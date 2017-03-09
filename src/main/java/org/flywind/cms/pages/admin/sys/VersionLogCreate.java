package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.VersionLog;
import org.flywind.business.services.sys.VersionLogService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("versionlog:add")
public class VersionLogCreate extends AppBase {
	
	private static final Logger logger = Logger.getLogger(VersionLogCreate.class);

	@Property
	private VersionLog log;
	
	@Inject
	private VersionLogService logService;
	
	@InjectComponent
	private Form createForm;
	
	public void onPrepare(){
		if (null == log) {
			log = new VersionLog();
		}
	}
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void onValidateFromCreateForm(){
		if(createForm.getHasErrors()){
			return;
		}
		try {
			log.setTime(new Date());
			log.setCustomerCode("0755");
			logService.create(log);
		} catch (Exception e) {
			String error = messages.get("create-error");
			logger.error(error + e);
			alertManager.error(error);
			createForm.recordError(error);
		}	
	}
	
	public Object onSuccessFromCreateForm(){
		return VersionLogList.class;
	}
}
