package org.flywind.cms.components;

import java.util.List;
import java.util.Set;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.Resource;
import org.flywind.business.services.sys.ResourceService;
import org.flywind.business.services.sys.RoleService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;

public class TNav extends AppBase {

	@Inject
	private ResourceService resourceService;
	
	@Inject
	private UserService userService;
	
	@Inject 
	private RoleService roleService;
	
	@Property
	private Resource menu;
	
	@Property Resource imenu;
	
	@Property
	private List<Resource> menus;
	 
	@Property
	private int count;
	
	@Property
	private String webRootPath;
	
	@Property
	private String currentPageName;

	public boolean isCn(){
		String l = getCurrentLanguage();
		if("zh-cn".equalsIgnoreCase(l)){
			return true;
		}
		
		return false;
	}
	
	public void setupRender(){
		webRootPath = contextPath;
		
		currentPageName = componentResources.getPageName().toLowerCase();
		
		if (null == menus) {
			Set<String> permissions = userService.findPermissions(state.getSysInfo().getUser().getUsername(), state.getSysInfo().getCustomerCode());
			Resource r = resourceService.findAllForTree(state.getSysInfo().getCustomerCode(), permissions);
			menus = r.getChildResource();
			
			for(Resource rs : menus){

				if(!"".equals(rs.getChildResource())){
					if(null != rs.getChildResource()){
						for(Resource k : rs.getChildResource()){
							String page = k.getUrl().toLowerCase().substring(1);
							if(page.equals(currentPageName)){
								k.setActiveStyle("active-link");
								rs.setActiveStyle("active-sub active");
							}
						}
					}
				}
			}
		}
		
		
		
	}
	
	public boolean isHomePage(){
		if("admin/index".equals(componentResources.getPageName().toLowerCase())){
			return true;
		}
		return false;
	}
	
	public String getUpath(){
		String language = currentLocale.toLanguageTag();
		String newLanguage = "";
		if(language != null){
			if(language.indexOf("-")>0){
				newLanguage = language.replaceAll("-","_");
				return "/"+newLanguage;
			}
			return "/"+language;
		}
		
		return "";
	}
	
}
