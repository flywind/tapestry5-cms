package org.flywind.cms.pages.admin.sys;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.exception.FException;
import org.flywind.business.common.result.Json;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.entities.sys.Resource;
import org.flywind.business.entities.sys.Role;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.ResourceService;
import org.flywind.business.services.sys.RoleService;
import org.flywind.cms.base.AppBase;
@RequiresPermissions("role:edit")
@Import(stylesheet={"context:assets/styles/zTreeStyle/zTreeStyle.css"})
public class RoleEdit extends AppBase {
	
	/**
	 * 记录日志
	 */
	private final static Logger logger = Logger.getLogger(RoleEdit.class);
	
	@Property
	private Role role;
	
	@InjectComponent("editForm")
	private Form editForm;
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private ResourceService resourceService;
	
	@Property
	private String resources;
	
	@Property
	private String resourceIds;
	
	/**
	 * 编辑前的资源id集，用于和编辑后的资源id做比对，判断是否有变化
	 */
	@Property
	private String oldResIds;
	
	/**
	 * 编辑前的角色描述，用于和编辑后的角色描述做比对，判断是否有变化
	 */
	@Property
	private String oldDesc;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json json;
	
	@PageActivationContext
	private Long id;
	
	/**
	 * 类初始化的时候执行
	 */
	public void onPrepare(){
		if(role == null){
			role = new Role();
		}
	}
	
	/**
	 * 页面渲染前执行
	 */
	public void setupRender() {
		//获取角色
		role = roleService.getRoleById(id);
		//记录旧的角色描述信息
		oldDesc = role.getDescription();
		List<Resource> resources = roleService.getResourcesByRoleId(id);
		idAndNameToString(resources);
		User loginUser = state.getSysInfo().getUser();
		json = resourceService.findResourceByLoginUserToJson(loginUser);
	}
	
	public void afterRender() {
		Json selectIds = new Json();
		selectIds.setObj(FBaseUtil.idsToList(oldResIds));
		javaScriptSupport.require("init-roleCreate").invoke("init").with(json.toString(), selectIds.toString());
	}
	
	/**
	 * 角色修改成功后，返回到角色列表页面
	 * 
	 * @return 
	 *        角色列表页面类的Class对象
	 */
	public Object onSuccessFromEditForm() {
		
		try {
			role.setId(id);
			role.setLastUpdatePerson(state.getSysInfo().getUser().getUsername());
			roleService.updateRole(role, oldDesc, resourceIds, oldResIds);
		}catch (FException ue) {
			String msg = messages.get(ue.getTypeKey());
			alertManager.warn(msg);
			return this;
		} catch (Exception e) {
			logger.error("", e);
		}
		
		return RoleList.class;
	}
	
	/**
	 * 将资源集合中的资源id和资源名称转换成以逗号分隔的字符串
	 * 
	 * @param resources
	 *        资源集合
	 */
	private void idAndNameToString(List<Resource> resourceList) {
		StringBuilder ids = new StringBuilder();
		StringBuilder names = new StringBuilder();
		for (Resource res : resourceList) {
			ids.append(res.getId()).append(",");
			names.append(res.getName()).append(",");
		}
		if (ids.length() > 0) {
			resourceIds = ids.substring(0, ids.length() - 1);
			//记录旧的资源id集
			oldResIds = resourceIds;			
			resources = names.substring(0, names.length() - 1);
		}
	}

}
