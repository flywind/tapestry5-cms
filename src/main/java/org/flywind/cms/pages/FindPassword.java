package org.flywind.cms.pages;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.common.exception.FExceptionKey;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.common.utils.FEmailThread;
import org.flywind.business.common.utils.FMessageThread;
import org.flywind.business.common.utils.FMessageUtil;
import org.flywind.business.entities.base.SysParam;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.base.SysParamService;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
import org.tynamo.security.services.SecurityService;

/**
 * <p>找回密码</p>
 * 
 * @author flywind(飞风)
 * @date 2015年11月24日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class FindPassword extends AppBase {
	
	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(FindPassword.class);
	
	@Inject
	private UserService userService;
	
	@Inject
	private SecurityService securityService;
	
	@InjectComponent
	private Zone codeSendZone;
	
	@InjectComponent("identifyingCodeForm")
	private Form identifyingCodeForm;
	
	@Property
	@Persist
	private String username;
	
	@Property
	@Persist
	private String emailOrPhone;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private boolean showForm1;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private boolean showForm2;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private boolean showForm3;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String code;
	
	@Property
	@Persist
	private String customerCode;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String identifyingCode;
	
	@Property
	private String newPwd;
	
	@Property
	private String confirmPwd;
	
	public void setupRender(){
		if (!showForm2 && !showForm3) {
			showForm1 = true;
			showForm2 = false;
			showForm3 = false;
		}
		//可根据域名获取客户代码
		customerCode = "0755";
	}
	
	/**
	 * 校验用户名与邮箱或手机号的有效性
	 * 
	 * @return
	 *        当前页面对象
	 */
	public Object onSuccessFromCheckUsernameForm() {
		try {
			String info = messages.get(FSysConstants.PHONE_NUMBER);
			if (emailOrPhone.contains("@")) {
				info = messages.get(FSysConstants.EMAIL_ADDRESS);
			}
			User user = userService.findByUsername(username, emailOrPhone, customerCode);
			if (null == user) {
				String errorInfo = messages.format(FSysConstants.USERNAME_WITH_EMAIL_OR_PASSWORD_MATCH, info);
				//String logInfo = messages.format("find-back-password-error", request.getRemoteHost(), errorInfo);
				alertManager.error(errorInfo);
			} else {
				showForm2 = true;
				showForm1 = false;
				showForm3 = false;
			}
		} catch (Exception e) {
			logger.error("检查用户名与邮箱或手机号失败。", e);
		}
		return this;
	}
	
	/**
	 * 校验验证码
	 * 
	 * @return
	 *        当前页面对象
	 */
	public Object onSuccessFromIdentifyingCodeForm() {
		String key = emailOrPhone + "_" + username + "_" + customerCode;
		Session session = securityService.getSubject().getSession();
		Object value = session.getAttribute(key);
		if (null != value && value.toString().equals(identifyingCode)) {
			showForm3 = true;
			showForm1 = false;
			showForm2 = false;
		} else {
			String errorInfo = messages.get(FSysConstants.INDENTIFYING_CODE_INVALID);
			//String logInfo = messages.format("find-back-password-error", request.getRemoteHost(), errorInfo);
			alertManager.error(errorInfo);
			showForm2 = true;
			showForm1 = false;
			showForm3 = false;
		}
		return this;
	}
	
	/**
	 * 发送验证码
	 * 
	 * @param sendTarget
	 *        目标地址(邮箱或手机号)
	 */
	public void onActionFromSendCodeBut(String sendTarget, String account, String customerCode) {
		String key = sendTarget + "_" + account + "_" + customerCode;
		if (null != sendTarget && !sendTarget.isEmpty()) {
			Session session = securityService.getSubject().getSession();
			Object value = session.getAttribute(key);
			if (null == value) {
				//生成一个4位的随机数
				code = FBaseUtil.generateRandomNumber(4);
				//超时时间10分钟
				session.setTimeout(600000);
				session.setAttribute(key, code);
			} else {
				code = value.toString();
			}
			String content = messages.format(FSysConstants.IDENTIFYING_CODE_INFO, code);
			if (sendTarget.contains("@")) {
				String title = messages.get(FSysConstants.IDENTIFYING_CODE);
				//使用单独的线程来发送邮件，从而提高程序继续执行的效率
				new FEmailThread(sendTarget, title, content).start();
			} else {
				//使用单独的线程来发送短信，从而提高程序继续执行的效率
				new FMessageThread(content, FMessageUtil.F_SYSTEM, sendTarget, customerCode).start();
			}
			
			ajaxResponseRenderer.addRender(codeSendZone).addCallback(new JavaScriptCallback() {
				@Override
				public void run(JavaScriptSupport javascriptSupport) {
					javaScriptSupport.require("findBackPassword").invoke("sendCode");
				}
			});
		} else {
			String errorInfo = messages.get(FSysConstants.SEND_INDENTIFYING_CODE_ERROR);
			//String logInfo = messages.format("find-back-password-error", request.getRemoteHost(), errorInfo);
			logger.error(errorInfo);
			alertManager.error(errorInfo);
		}
	}
	
	/**
	 * 修改密码
	 * 
	 * @return
	 *        修改成功返回登录页面，失败则保留在当前页面
	 */
	public Object onSuccessFromUpdatePasswordForm() {
		if (!FBaseUtil.equals(newPwd, confirmPwd)) {
			String errorInfo = messages.get(FExceptionKey.NEW_PASSWORD_WITH_OLD_PASSWORD_THE_SAME);
			//String logInfo = messages.format("find-back-password-error", request.getRemoteHost(), errorInfo);
			alertManager.error(errorInfo);
			showForm1 = false;
			showForm2 = false;
			showForm3 = true;
			return this;			
		}
		User user = userService.findByUsername(username, emailOrPhone, customerCode);
		if (null != user) {
			String pwd = DigestUtils.md5Hex(newPwd);
			user.setPassword(pwd);
			user.setLastUpdatePerson(username);
			user.setLastUpdateTime(new Date());
			user.setInitPwd(false);
			userService.updateUser(user);
			//String logInfo = messages.format("find-back-password-success", user.getUsername(), emailOrPhone, request.getRemoteHost());
			return Login.class;
		} else {
			String errorInfo = messages.get(FSysConstants.FIND_BACK_PASSWORD_FAIL_USER_NOT_EXIST);
			//String logInfo = messages.format("find-back-password-error", request.getRemoteHost(), errorInfo);
			alertManager.error(errorInfo);
			return this;
		}
	}

}
