package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.VersionLog;
import org.flywind.business.services.sys.VersionLogService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("versionlog:edit")
public class VersionLogUpdate extends AppBase {
	
	private static final Logger logger = Logger.getLogger(VersionLogUpdate.class);
	
	@Property
	@PageActivationContext
	private Long id;
	
	@InjectComponent
	private Form editForm;
	
	@Property
	private VersionLog log;

	@Inject
	private VersionLogService logService;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void onPrepare(){
		
		if(null == log){
			log = logService.getById(id);
		}
	}
	
	public void onValidateFromEditForm(){
		if(editForm.getHasErrors()){
			return;
		}
		try {
			//VersionLog log = logService.getById(id);
			if(StringUtils.isNotBlank(log.getContent())){
				log.setContent(log.getContent());
			}
			if(StringUtils.isNotBlank(log.getContentEn())){
				log.setContentEn(log.getContentEn());
			}
			if(StringUtils.isNotBlank(log.getTitle())){
				log.setTitle(log.getTitle());
			}
			if(StringUtils.isNotBlank(log.getTitleEn())){
				log.setTitleEn(log.getTitleEn());
			}
			log.setHide(log.getHide());
			log.setTime(new Date());
			log.setVersion(log.getVersion());
			logService.update(log);
		} catch (Exception e) {
			String error = messages.get("update-error");
			logger.error(error + e);
			alertManager.error(error);
			editForm.recordError(error);
		}
	}
	
	public Object onSuccessFromEditForm(){
		return VersionLogList.class;
	}
}
