package org.flywind.cms.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
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
	@Path("context:assets/tlayout/styles/index.css")
	private Asset indexCss;
	
	@Inject
	@Path("context:assets/tlayout/styles/animate.css")
	private Asset animateCss;
	
	@Inject
	@Path("context:assets/tlayout/styles/nonsupport.css")
	private Asset nonsupportCss;
	
	@Property
	private String pagetitle;
	
	public void setupRender(){
		javaScriptSupport.importStylesheet(t5baseCss);
		javaScriptSupport.importStylesheet(indexCss);
		javaScriptSupport.importStylesheet(animateCss);
		javaScriptSupport.importStylesheet(new StylesheetLink(nonsupportCss, new StylesheetOptions().withCondition("lt IE 10")));
		
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
