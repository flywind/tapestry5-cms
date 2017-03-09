package org.flywind.cms.pages.admin.sys;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.common.result.Json;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.common.utils.FEmailThread;
import org.flywind.business.common.utils.FMessageThread;
import org.flywind.business.common.utils.FMessageUtil;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("user:add")
@Import(stylesheet={"context:assets/styles/zTreeStyle/zTreeStyle.css"})
public class UserCreate extends AppBase {

	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(UserCreate.class);
	
	@Property
	private User user;

	@Inject
	private UserService userService;

	@InjectComponent("createForm")
	private Form createForm;

	@Inject
	private Messages messages;

	@InjectComponent
	private Zone userCheckZone;

	@Inject
	private OrganizationService organizationService;

	@Property
	private String companyName;
	
	/**
	 * 初始密码发送方式(邮件或手机)
	 */
	@Property
	private int sendPwd;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json json;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json roleJson;

	@Property
	private String roles, roleIds;

	void onPrepareForRender() throws Exception {

		if (createForm.isValid()) {
			user = new User();
		}
	}

	void onPrepareForSubmit() throws Exception {
		user = new User();
	}
	
	void onValidateFromCreateForm() {

		if (createForm.getHasErrors()) {
			return;
		}
		try {
			User loginUser = state.getSysInfo().getUser();
			String username = state.getSysInfo().getUser().getUsername();
			String customerCode = state.getSysInfo().getCustomerCode();
			user.initBaseInfo(username, customerCode);
			
			//生成初始密码
			String pwd = FBaseUtil.generateInitPasword();
			user.setPassword(DigestUtils.md5Hex(pwd));
			
			//发送初始密码到邮箱或手机
			String title = messages.format(FSysConstants.SEND_EMAIL_TITLE, user.getUsername());
			String content = messages.format(FSysConstants.SEND_EMAIL_CONTENT, pwd);
			if (sendPwd == 2) {
				new FMessageThread(content, FMessageUtil.F_SYSTEM, user.getMobile(), customerCode).start();
			} else {
				new FEmailThread(user.getEmail(), title, content).start();
			}
			
			//拼接父对象的id
			String parentId = loginUser.getParentId() + loginUser.getId() + FBaseConstants.SLASH;
			user.setParentId(parentId);
			
			Long userId = userService.createUser(user);
			if (!"".equals(roleIds) && null != roleIds) {
				userService.createUserAndRole(userId, roleIds);
			}

		} catch (Exception e) {
			logger.error(messages.get("create-error"), e);
			alertManager.error(messages.get("create-error"));
			
			//阻止表单提交
			createForm.recordError(messages.get("create-error"));
		}
	}

	void onSuccess() {
		alertManager.success(messages.get("create-success"));
	}

	void onUserCheck(@RequestParameter(value = "param", allowBlank = true) final String userName) throws Exception {
		if (userName != null) {
			boolean userNameExist = userService.checkUserExist(userName, state.getSysInfo().getUser().getCustomerCode());
			if (!userNameExist && request.isXHR()) {
				ajaxResponseRenderer.addRender(userCheckZone).addCallback(new JavaScriptCallback() {

					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						javascriptSupport.require("userCheck").invoke(FBaseConstants.SUCCESS_STRING);
					}
				});
			} else {
				ajaxResponseRenderer.addRender(userCheckZone).addCallback(new JavaScriptCallback() {

					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						JSONObject json = new JSONObject();
						json.put(FBaseConstants.ID_STRING, FSysConstants.USER_NAME);
						json.put(FBaseConstants.ERROR_STRING, messages.format("user-already-exist", userName));
						javascriptSupport.require("userCheck").invoke(FBaseConstants.ERROR_STRING).with(json);

					}
				});
			}
		}
	}

	public void setupRender() {
		User LoginUser = state.getSysInfo().getUser();
		json = organizationService.findAllToJsonByUser(LoginUser);
		roleJson = userService.findAllRolesToJson(LoginUser);
		
	}

	public void afterRender() {
		javaScriptSupport.require("init-userCreate").invoke("init").with(json.toString());
		javaScriptSupport.require("init-userCreate").invoke("initRole").with(roleJson.toString());
	}
}
