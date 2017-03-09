package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.exception.FException;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("user:delete")
public class UpdateInitPwd extends AppBase {
	
	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(UpdateInitPwd.class);
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private User user;
	
	@Property
	private String username;
	
	@Property
	private String newPassword;
	
	@Property
	private String confirmPassword;
	
	@InjectComponent("updateInitPwdForm")
	private Form updateInitPwdForm;
	
	@Inject
	private UserService userService;
	
	public void onPrepare() {
		if(null == user){
			user = new User();
		}
	}
	
	public void setupRender() {
		user = state.getSysInfo().getUser();
		this.username = user.getUsername();
	}
	
	/**
	 * 修改密码
	 */
	public void onValidateFromUpdateInitPwdForm() {
		String errorInfo = FBaseConstants.EMPTY_STRING;
		try {
			user.setLastUpdatePerson(username);
			user.setLastUpdateTime(new Date());
			userService.updatePassword(user, newPassword, confirmPassword);
		} catch (FException e) {
			errorInfo = messages.get(e.getTypeKey());
			logger.error("修改用户密码失败，失败原因：" + errorInfo, e);
			alertManager.error(errorInfo);
			updateInitPwdForm.recordError(errorInfo);
		} catch (Exception e) {
			errorInfo = messages.get("update-password-fail");
			logger.error(errorInfo, e);
			alertManager.error(errorInfo);
			updateInitPwdForm.recordError(errorInfo);
		}
		
		
	}
	
	public void onSuccessFromUpdateInitPwdForm() {
		alertManager.success(messages.get("update-success"));
	}

}
