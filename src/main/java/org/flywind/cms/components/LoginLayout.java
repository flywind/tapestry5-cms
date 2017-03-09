package org.flywind.cms.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.cms.base.AppBase;

public class LoginLayout extends AppBase {

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String title;
	
	@Inject
	@Path("context:assets/tlayout/styles/t5base.css")
	private Asset t5baseCss;
	
	@Inject
	@Path("context:assets/tlayout/styles/magic-check.min.css")
	private Asset magicCss;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-light.css")
	private Asset themeclight;
	
	@Inject
	@Path("context:assets/tlayout/styles/login.css")
	private Asset loginCss;
	
	public void setupRender(){
		javaScriptSupport.importStylesheet(t5baseCss);
		javaScriptSupport.importStylesheet(magicCss);
		javaScriptSupport.importStylesheet(themeclight);
		javaScriptSupport.importStylesheet(loginCss);
	}
	
	public void afterRender(){
		javaScriptSupport.require("inits/loginLayout").invoke("init");
	}
}
