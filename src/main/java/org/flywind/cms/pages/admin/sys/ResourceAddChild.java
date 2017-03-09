package org.flywind.cms.pages.admin.sys;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.entities.sys.Resource;
import org.flywind.business.entities.sys.Role;
import org.flywind.business.entities.sys.RoleResource;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.ResourceService;
import org.flywind.business.services.sys.RoleService;
import org.flywind.cms.base.AppBase;
@RequiresPermissions("resource:add")
public class ResourceAddChild extends AppBase {

	private static final Logger logger = Logger.getLogger(ResourceAddChild.class);

	@Property
	@PageActivationContext
	private Long parentId;
	
	/**
	 * 父资源对象
	 * {@code @Persist(PersistenceConstants.FLASH)表示页面缓存 }
	 */
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Resource parent;
	
	/**
	 * 当前资源对象
	 */
	@Property
	private Resource resource;
	
	@Inject
	private ResourceService resourceService;
	
	@Inject
	private RoleService roleService;
	
	@InjectComponent("resourceForm")
	private Form resourceForm;
	
	/**
	 * 资源类型
	 */
	@Property
	private String type;
	
	/**
	 * 类初始化时执行
	 */
	public void onPrepare(){
		if(resource == null){
			resource = new Resource();
		}
	}
	
	/**
	 * 页面渲染前执行
	 */
	public void setupRender(){
		if(parent == null){
			parent = resourceService.getResourceById(parentId);
		}
	}
	
	public void onValidateFromResourceForm() throws Exception{
		if(resourceForm.getHasErrors()){
			return;
		}
		try {
			User user = state.getSysInfo().getUser();
			resource.initBaseInfo(user.getUsername(), state.getSysInfo().getCustomerCode());
			resource.setParentId(parent.getId());
			resource.setLevel(parent.getLevel()+1);
			resource.setType(type);
			resource.setParentIds(parent.makeSelfAsParentIds());
	        Long resId = resourceService.createResource(resource);
	        
	        //创建资源与角色的关联关系
	        List<Role> roles = roleService.getRoleByUser(user.getId());
	        if (null != roles && !roles.isEmpty()) {
	        	Role role = roles.get(FBaseConstants.FIRST_INDEX);
	        	RoleResource rr = new RoleResource(role.getId(), resId);
	        	resourceService.createRoleRes(rr);
	        }
		} catch (Exception e) {
			String errorInfo = messages.get("create-error");
			logger.error(errorInfo, e);
			alertManager.error(errorInfo);
			resourceForm.recordError(errorInfo);	
		}
	}
	
	/**
	 * 表单提交时执行
	 * 
	 * @return
	 *        目录页面对象
	 */
	public Object onSuccess(){
        return ResourceList.class;
	}
}
