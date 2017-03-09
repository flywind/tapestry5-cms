package org.flywind.cms.pages.admin.sys;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.result.Json;
import org.flywind.business.entities.sys.Organization;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.cms.base.AppBase;

public class OrganizationMove extends AppBase {

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Organization source;
	
	@Property
	private Organization target;
	
	@Property
	private String targetName;
	
	@Property
	@PageActivationContext
	private Long sourceId;
	
	@Property
	private Long targetId;
	
	@Inject
	private OrganizationService organizationService;
	
	public void setupRender(){
		if(source == null){
			source = organizationService.findOne(sourceId);
		}
		Json targetList = organizationService.findAllWithExcludeToJson(source);
		javaScriptSupport.require("init-move").invoke("init").with(targetList.toString());
	}
	
	public Object onSuccess(){
        Organization target = organizationService.findOne(targetId);
        organizationService.move(source, target);
		return Success.class;
	}
}
