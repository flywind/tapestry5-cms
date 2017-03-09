package org.flywind.cms.pages.admin.sys;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.exception.FException;
import org.flywind.business.entities.sys.Role;
import org.flywind.business.services.sys.RoleService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

@RequiresPermissions("role:view")
public class RoleList extends AppBase {

	private final static Logger logger = Logger.getLogger(RoleList.class);
	
	@Property
	@Persist
	private Role role;
	
	@Property
	private List<Role> roles;
	
	@Property
	private String roleName;
	
	@Inject
	private RoleService roleService;
	
	@InjectComponent("searchForm")
	private Form searchForm;
	
	@InjectComponent
	private Zone listZone;
	
	@PageActivationContext
	private Long roleId;
	
	@Property
	@Persist
	private String delUrl;
	
	@Property
	@Persist
	private String lang;
	
	public void setupRender(){
		lang = getCurrentLanguage();
		delUrl = componentResources.createEventLink("del").toURI();
	}
	
	public void onPrepare(){
		if(role == null){
			role = new Role();
		}
	}
	
	@RequiresPermissions("role:delete")
	public void onDel(List<String> ids){
		try {
			for(String id : ids){
				Role role = roleService.getRoleById(Long.parseLong(id));
				roleService.deleteRole(role);
			}
			
		} catch (FException de) {
			String errorMsg = messages.get(de.getTypeKey());
			logger.error(errorMsg, de);
			alertManager.error(errorMsg);
		} catch (Exception e) {
			String errorMsg = messages.get("role-delete-fail");
			logger.error(errorMsg, e);
			alertManager.error(errorMsg);
		}
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
	
	@OnEvent(component = "gridData", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		FPage paging = (FPage) request.getAttribute("page");
		if(role == null){
			role = new Role();
		}
		roles = roleService.findAll(role, paging, state.getSysInfo());
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
	
	public void onReset() {
		role = null;
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
}
