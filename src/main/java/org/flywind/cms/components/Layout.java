package org.flywind.cms.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.cms.base.AppBase;

public class Layout extends AppBase {

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String title;
	
	@Property
	private SystemSeting systemSeting;

	@Inject
	private SystemSetingService systemSetingService;
	
	@Inject
	@Path("context:assets/tlayout/styles/t5base.css")
	private Asset t5baseCss;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-light2.css")
	private Asset themeclight;
	
	@Property
	private String pagetitle;
	
	public void setupRender(){
		javaScriptSupport.importStylesheet(t5baseCss);
		javaScriptSupport.importStylesheet(themeclight);
		
		SystemSeting  querySysSeting = systemSetingService.querySysSetingByCustomerCode("0755");
		
		if(querySysSeting != null){
			if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
				pagetitle = title +" - " + querySysSeting.getSystemMainTitle();
			}else{
				pagetitle = title +" - " + querySysSeting.getSystemMainTitleEn();
			}
		} else {
			pagetitle = title;
		}
		
	}
	
	public void afterRender(){
		javaScriptSupport.require("inits/layout").invoke("init");
	}
}
