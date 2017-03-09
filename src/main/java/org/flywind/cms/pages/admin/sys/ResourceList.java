package org.flywind.cms.pages.admin.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.common.exception.FException;
import org.flywind.business.entities.sys.Resource;
import org.flywind.business.entities.sys.Role;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.ResourceService;
import org.flywind.business.services.sys.RoleService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
@RequiresPermissions("resource:view")
@Import(stylesheet={"${plugins.path}/treeTable/stylesheets/jquery.treetable.css","${plugins.path}/treeTable/stylesheets/jquery.treetable.theme.default.css"})
public class ResourceList extends AppBase {

	private static final Logger logger = Logger.getLogger(ResourceList.class);

	@Property
	private Resource resource;
	
	@Property
	private List<Resource> resources;
	
	@Inject
	private ResourceService resourceService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private RoleService roleService;
	
	@Property
	@PageActivationContext
	private Long resourceId;
	
	public boolean isCn(){
		String language = getCurrentLanguage();
		if("zh-cn".equalsIgnoreCase(language)){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 判断是否为菜单
	 * 
	 * @return
	 *        true菜单，false非菜单
	 */
	public boolean isAddNode(){
		if(FSysConstants.RESOURCE_TYPE_MENU.equals(resource.getType())){
			return true;
		}
		return false;
	}
	
	/**
	 * 页面渲染之前执行
	 */
	public void setupRender(){
		System.out.println(getCurrentLanguage());
		String customerCode = state.getSysInfo().getCustomerCode();
		Set<String> permissions = userService.findPermissions(state.getSysInfo().getUser().getUsername(), customerCode);
		Resource r = resourceService.findAllForTree(customerCode, permissions);
		if (null == resources) {
			resources = new ArrayList<Resource>();
		}
		resources.add(r);
		treeToList(resources, r.getChildResource());
	}
	
	public void afterRender(){
		javaScriptSupport.require("init-resourceList").invoke("init");
	}
	
	/**
	 * 点击"删除"时执行
	 */
	@RequiresPermissions("resource:delete")
	public void onDel(Long resourceId){
		try {
			Resource res = resourceService.getResourceById(resourceId);
			isAdmin(res);
			
			resourceService.deleteResource(res);
		} catch (FException e) {
			String errorMsg = messages.get(e.getTypeKey());
			logger.error(errorMsg, e);
			alertManager.error(errorMsg);
		} catch (Exception e) {
			String errorMsg = messages.get("resource-delete-fail");
			logger.error(errorMsg, e);
			alertManager.error(errorMsg);
		}
	}

	/**
	 * 是否超级管理员角色
	 * 
	 * @param res
	 */
	private void isAdmin(Resource res) {
		User user = state.getSysInfo().getUser();
		List<Role> roles = roleService.getRoleByUser(user.getId());
		if (null != roles && !roles.isEmpty()) {
			for (int i = 0; i < roles.size(); i++) {
				Role r = roles.get(i);
				if (FSysConstants.ROLE_SYS.equals(r.getName())) {
					res.setAdministrator(true);
					break;
				}
			}
		}
	}
	
	/**
	 * 将页面上的资源类型进行国际化处理
	 * 
	 * @return
	 *        国际化后的资源类型信息
	 */
	public String getMenuType(){
		if(FSysConstants.RESOURCE_TYPE_MENU.equals(resource.getType())){
			return messages.get("resource-type-menu");
		} else if (FSysConstants.RESOURCE_TYPE_BUTTON.equals(resource.getType())) {
			return messages.get("resource-type-button");
		}
		return FBaseConstants.EMPTY_STRING;
	}
	
	/**
	 * 以递归的方式将tree型的资源转换成List类型的资源
	 * @param rList
	 * @param rTree
	 */
	private void treeToList(List<Resource> rList, List<Resource> rTree) {
		for (Resource r : rTree) {
			rList.add(r);
			if (null != r.getChildResource()) {
				treeToList(rList, r.getChildResource());
			}
		}
	}
}
