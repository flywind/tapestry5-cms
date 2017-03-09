package org.flywind.cms.pages.admin.sys;

import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.common.utils.FEmailThread;
import org.flywind.business.common.utils.FMessageThread;
import org.flywind.business.common.utils.FMessageUtil;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;


public class ResetPwd extends AppBase {
	
	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(ResetPwd.class);
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private User user;
	
	@Property
	@PageActivationContext
	private Long id;
	
	@Property
	private int sendPwd;
	
	@Inject
	private UserService userService;
	
	public void onPrepare() {
		if (null == user) {
			user = new User();
		}
	}
	
	public void setupRender() {
		user = userService.findOne(id);
	}
	
	public Object onSuccess() {
		try {
			String initPwd = FBaseUtil.generateInitPasword();
			user.setPassword(DigestUtils.md5Hex(initPwd));
			user.setLastUpdatePerson(state.getSysInfo().getUser().getUsername());
			user.setLastUpdateTime(new Date());
			user.setInitPwd(true);
			
			//发送初始密码到邮箱或手机
			String title = messages.format(FSysConstants.SEND_EMAIL_TITLE, user.getUsername());
			String content = messages.format(FSysConstants.SEND_EMAIL_CONTENT, initPwd);
			String sendTo = user.getEmail();
			if (sendPwd == 2) {
				new FMessageThread(content, FMessageUtil.F_SYSTEM, user.getMobile(), state.getSysInfo().getCustomerCode()).start();
				sendTo = user.getMobile();
			} else {
				new FEmailThread(user.getEmail(), title, content).start();
			}
			
			userService.updateUser(user);
			return UserList.class;
		} catch (Exception e) {
			String errorInfo = messages.get("reset-password-fail");
			logger.error(errorInfo, e);
			alertManager.error(errorInfo);
		}
		return this;
	}

}
