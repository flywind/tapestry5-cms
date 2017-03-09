package org.flywind.cms.components;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.cms.base.AppBase;

public class TFooter extends AppBase {

	@Property
    private  SystemSeting  systemSeting;
	
	@Inject
	private SystemSetingService systemSetingService;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void setupRender(){
		if(systemSeting == null){
			systemSeting = systemSetingService.querySysSetingByCustomerCode(state.getSysInfo().getCustomerCode());
		}
	}
}
