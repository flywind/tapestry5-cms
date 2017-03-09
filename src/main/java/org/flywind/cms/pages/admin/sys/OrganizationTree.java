package org.flywind.cms.pages.admin.sys;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.result.Json;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.cms.base.AppBase;

public class OrganizationTree extends AppBase {
	
	@Inject
	private OrganizationService organizationService;
	
	@Property
    private String  organizationName;
	
	void afterRender(){
		 User loginUser  = state.getSysInfo().getUser();
		 Json json  = organizationService.findAllByCustomerCode(loginUser.getCustomerCode());
	     javaScriptSupport.require("init-tree").invoke("initTree").with(json.toString()); 
	     javaScriptSupport.require("init-tree").invoke("searchOrg").with();
	}
}
