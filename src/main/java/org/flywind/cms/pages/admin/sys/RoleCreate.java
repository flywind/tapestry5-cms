package org.flywind.cms.pages.admin.sys;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.URLEncoder;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.result.Json;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.entities.sys.Role;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.ResourceService;
import org.flywind.business.services.sys.RoleService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("role:add")
@Import(stylesheet={"context:assets/styles/zTreeStyle/zTreeStyle.css"})
public class RoleCreate extends AppBase {

	private Logger logger = Logger.getLogger(RoleCreate.class);
	
	@Property
	private Role role;
	
	@Inject
	private ResourceService resourceService;
	
	@Inject
	private RoleService roleService;
	
	@Property
	private String resources;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json json;
	
	@InjectComponent("createForm")
	private Form createForm;
	
	@Inject
    private AlertManager alertManager;
	
	@Property
    @Persist(PersistenceConstants.FLASH)
    private Boolean showDismissAll;
	
	@Inject
	private Messages messages;
	
	@Property
	private String resourceIds;
	
	@InjectComponent
	private Zone roleCheckZone;
	
	@Inject
	private URLEncoder uRLEncoder;
	
	public void onPrepareForRender() throws Exception{
		if(createForm.isValid()){
			role = new Role();
		}
	}
	
	void onPrepareForSubmit() throws Exception {
		role = new Role();
    }
	
	public void onValidateFromCreateForm(){
		try {
			role.initBaseInfo(state.getSysInfo().getUser().getUsername(),state.getSysInfo().getCustomerCode());
			Long roleId = roleService.createRole(role);
			roleService.createRoleResources(resourceIds, roleId);
		} catch (Exception e) {
			String error = messages.get("role-create-error");
			alertManager.error(error);
			createForm.recordError(error);
		}
	}
	
	public void setupRender(){
		User loginUser = state.getSysInfo().getUser();
		json = resourceService.findResourceByLoginUserToJson(loginUser);
		if (showDismissAll == null) {
            showDismissAll = false;
        }
	}
	
	public void afterRender(){
		Json selectIds = new Json();
		selectIds.setObj(FBaseUtil.idsToList(resourceIds));
		javaScriptSupport.require("init-roleCreate").invoke("init").with(json.toString(), selectIds.toString());
	 }
	
	/**
	 * 检查角色名是否重复
	 * 
	 * @param roleName
	 * @throws Exception
	 */
	public void onRoleCheck(@RequestParameter(value = "param", allowBlank = true) final String roleName) throws Exception {
		if (null != roleName) {
			try {	
				boolean roleExist = roleService.checkRoleExist(roleName,state.getSysInfo().getCustomerCode());
				
				//角色名已经存在，不允许提交，并在前台进行提示
				if (roleExist && request.isXHR()) {
					ajaxResponseRenderer.addRender(roleCheckZone).addCallback(new JavaScriptCallback() {
						@Override
						public void run(JavaScriptSupport javascriptSupport) {
							JSONObject json = new JSONObject();
							json.put(FBaseConstants.ID_STRING, FBaseConstants.NAME_STRING);
							json.put(FBaseConstants.INFO_STRING, messages.format("role-already-exist", roleName));
							javascriptSupport.require("roleCheck").invoke(FBaseConstants.ERROR_STRING).with(json);
						}
					});
				} else {
					//角色名不存在，清除之前的提示信息
					ajaxResponseRenderer.addRender(roleCheckZone).addCallback(new JavaScriptCallback() {
						@Override
						public void run(JavaScriptSupport javascriptSupport) {
							javascriptSupport.require("roleCheck").invoke(FBaseConstants.SUCCESS_STRING);
						}
					});
				}
			} catch (Exception e) {
				logger.error(messages.get("role-exist-check"), e);
			}
		}
	}
	
	public Object onSuccess(){
		return RoleList.class;
	}
}
