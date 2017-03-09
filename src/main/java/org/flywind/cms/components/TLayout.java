package org.flywind.cms.components;

import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
import org.flywind.business.entities.base.SysParam;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.services.base.SysParamService;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.cms.base.AppBase;

public class TLayout extends AppBase {
	
	@Property
	private String title;
	
	@Inject
	@Path("context:assets/tlayout/styles/t5base.css")
	private Asset t5baseCss;
	
	/***
	 * All skins start
	 * 
	 * */
	//Skin type a
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-coffee.css")
	private Asset themeacoffee;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-dark.css")
	private Asset themeadark;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-dust.css")
	private Asset themeadust;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-light.css")
	private Asset themealight;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-lime.css")
	private Asset themealime;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-mint.css")
	private Asset themeamint;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-navy.css")
	private Asset themeanavy;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-ocean.css")
	private Asset themeaocean;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-prickly-pear.css")
	private Asset themeapricklypear;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-purple.css")
	private Asset themeapurple;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-well-red.css")
	private Asset themeawellred;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-a-yellow.css")
	private Asset themeayellow;
	
	//Skin type b
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-coffee.css")
	private Asset themebcoffee;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-dark.css")
	private Asset themebdark;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-dust.css")
	private Asset themebdust;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-light.css")
	private Asset themeblight;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-lime.css")
	private Asset themeblime;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-mint.css")
	private Asset themebmint;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-navy.css")
	private Asset themebnavy;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-ocean.css")
	private Asset themebocean;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-prickly-pear.css")
	private Asset themebpricklypear;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-purple.css")
	private Asset themebpurple;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-well-red.css")
	private Asset themebwellred;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-b-yellow.css")
	private Asset themebyellow;
	
	//Skin type c
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-coffee.css")
	private Asset themeccoffee;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-dark.css")
	private Asset themecdark;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-dust.css")
	private Asset themecdust;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-light.css")
	private Asset themeclight;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-lime.css")
	private Asset themeclime;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-mint.css")
	private Asset themecmint;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-navy.css")
	private Asset themecnavy;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-ocean.css")
	private Asset themecocean;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-prickly-pear.css")
	private Asset themecpricklypear;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-purple.css")
	private Asset themecpurple;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-well-red.css")
	private Asset themecwellred;
	
	@Inject
	@Path("context:assets/tlayout/styles/themes/theme-c-yellow.css")
	private Asset themecyellow;
	
	@Property
	private String currentStyle;
	
	@Property
	private SystemSeting systemSeting;

	@Inject
	private SystemSetingService systemSetingService;

	@Inject
	private SysParamService sysParamService;
	
	@Property
    protected String pageName = componentResources.getPageName();
	
	@Property
	private String navCss, boxedlayoutCss;
	
	public void setupRender(){
		javaScriptSupport.importStylesheet(t5baseCss);

		String customerCode = state.getSysInfo().getCustomerCode();
		SystemSeting  querySysSeting = systemSetingService.querySysSetingByCustomerCode(customerCode);
		
		if(querySysSeting.isNavToggle() == true){
			navCss = "mainnav-sm";
		}else{
			navCss = "mainnav-lg";
		}
		
		if(querySysSeting.isBoxedLayout() == true){
			boxedlayoutCss= "boxed-layout";
		}else{
			boxedlayoutCss= "";
		}
		
    	if(null!=querySysSeting){
    		int styleNum = Integer.parseInt(querySysSeting.getStyle());
    		List<SysParam>  sysParams=sysParamService.getAllParamByBusinessType(4);
    		for(SysParam p : sysParams){
    			if(p.getParamKey() == styleNum){
    				currentStyle = p.getDescription();
    				break;
    			}
    		}
    		
    		if("theme-a-coffee".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeacoffee);
    		}else if("theme-a-dark".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeadark);
    		}else if("theme-a-dust".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeadust);
    		}else if("theme-a-light".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themealight);
    		}else if("theme-a-lime".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themealime);
    		}else if("theme-a-mint".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeamint);
    		}else if("theme-a-navy".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeanavy);
    		}else if("theme-a-ocean".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeaocean);
    		}else if("theme-a-prickly-pear".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeapricklypear);
    		}else if("theme-a-purple".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeapurple);
    		}else if("theme-a-well-red".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeawellred);
    		}else if("theme-a-yellow".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeayellow);
    		}else if("theme-b-coffee".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebcoffee);
    		}else if("theme-b-dark".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebdark);
    		}else if("theme-b-dust".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebdust);
    		}else if("theme-b-light".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeblight);
    		}else if("theme-b-lime".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeblime);
    		}else if("theme-b-mint".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebmint);
    		}else if("theme-b-navy".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebnavy);
    		}else if("theme-b-ocean".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebocean);
    		}else if("theme-b-prickly-pear".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebpricklypear);
    		}else if("theme-b-purple".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebpurple);
    		}else if("theme-b-well-red".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebwellred);
    		}else if("theme-b-yellow".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themebyellow);
    		}else if("theme-c-coffee".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeccoffee);
    		}else if("theme-c-dark".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecdark);
    		}else if("theme-c-dust".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecdust);
    		}else if("theme-c-light".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeclight);
    		}else if("theme-c-lime".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themeclime);
    		}else if("theme-c-mint".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecmint);
    		}else if("theme-c-navy".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecnavy);
    		}else if("theme-c-ocean".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecocean);
    		}else if("theme-c-prickly-pear".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecpricklypear);
    		}else if("theme-c-purple".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecpurple);
    		}else if("theme-c-well-red".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecwellred);
    		}else if("theme-c-yellow".equalsIgnoreCase(currentStyle)){
    			javaScriptSupport.importStylesheet(themecyellow);
    		}
    		
    		//javaScriptSupport.importStylesheet(new StylesheetLink(nonsupportCss, new StylesheetOptions().withCondition("lt IE 10")));
    		
    		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
    			title = querySysSeting.getSystemMainTitle();
    		}else{
    			title = querySysSeting.getSystemMainTitleEn();
    		}
    	}
	}
	
	public void afterRender(){
		javaScriptSupport.require("inits/admin/init-layout").invoke("init");
	}
}
