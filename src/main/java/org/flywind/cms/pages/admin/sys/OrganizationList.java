package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.flywind.business.common.result.Json;
import org.flywind.business.entities.sys.Organization;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.cms.base.AppBase;

@RequiresRoles("role_sys")
@Import(stylesheet={"context:assets/styles/zTreeStyle/zTreeStyle.css"})
public class OrganizationList extends AppBase {
	
	/**
	 * 记录日志
	 */
	private static Logger logger = Logger.getLogger(OrganizationList.class);
	
	@Property
    private String  organizationName;
	
	/**
	 * 添加单位的对象
	 */
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Organization organization;
	
	@InjectComponent
	private Zone formZone;
	
	@Inject
	private OrganizationService organizationService;
	
	public void onPrepare() {
		if (null == organization) {
			organization = new Organization();
		}
	}
	
	public void setupRender() {
		//i18n = super.getI18nResource().get(Organization.class.getSimpleName());
	}
	
	public void afterRender() {
		findTree();
		javaScriptSupport.require("init-tree").invoke("boundClickButton");
	}
	
	/**
	 * 查询出组织机构，并组装成树
	 */
	public void findTree() {
		/* 左边的树  */
		User loginUser  = state.getSysInfo().getUser();
		Json json  = organizationService.findAllByCustomerCode(loginUser.getCustomerCode());
		javaScriptSupport.require("init-tree").invoke("initTree").with(json.toString()); 
		javaScriptSupport.require("init-tree").invoke("searchOrg").with();
	}
	
	/**
	 * 添加下级机构
	 */
	public void onSuccessFromCreateChildForm() {
		try {
			if (null != organization) {
				String userName = state.getSysInfo().getUser().getUsername();
				String customerCode = state.getSysInfo().getCustomerCode();
				Long companyId = state.getSysInfo().getCompany().getId();
				organization.initBaseInfo(userName, customerCode, companyId);
				Organization parentOrg = organizationService.findOne(organization.getParentId());
				String parentIds = parentOrg.getParentIds() + parentOrg.getId() + "/";
				organization.setParentIds(parentIds);
				organizationService.createOrganization(organization);
				
				//通过回调刷新Zone
				ajaxResponseRenderer.addRender(formZone).addCallback(new JavaScriptCallback() {
					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						findTree();
					}
				});
			}
		} catch (Exception e) {
			//String errorMsg = i18n.get("createOrgFail");
			String errorMsg = messages.get("create-error");
			logger.error(errorMsg, e);
			alertManager.error(errorMsg);
		}
	}
	
	/**
	 * 编辑组织机构
	 */
	public void onSuccessFromEditForm() {
		try {
			if (null != organization && null != organization.getId()) {
				Organization o = organizationService.findOne(organization.getId());
				o.setName(organization.getName());
				o.setContactName(organization.getContactName());
				o.setContactNumber(organization.getContactNumber());
				o.setAddress(organization.getAddress());
				o.setLastUpdatePerson(state.getSysInfo().getUser().getUsername());
				o.setLastUpdateTime(new Date());
				organizationService.updateOrganization(o);
				
				//通过回调刷新Zone
				ajaxResponseRenderer.addRender(formZone).addCallback(new JavaScriptCallback() {
					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						findTree();
					}
				});
			}
		} catch (Exception e) {
			String errorMsg = messages.get("update-error");
			//String errorMsg = i18n.get("editOrgFail");
			logger.error(errorMsg, e);
			alertManager.error(errorMsg);
		}
	}
	
	/**
	 * 删除组织机构
	 */
	public void onSuccessFromDeleteForm() {
		try {
			if (null != organization && null != organization.getId()) {
				//先删除所有的下级机构
				Organization o = organizationService.findOne(organization.getId());
				organizationService.deleteChildren(o);
				
				organizationService.deleteOrganizationById(o);
				
				//通过回调刷新Zone
				ajaxResponseRenderer.addRender(formZone).addCallback(new JavaScriptCallback() {
					@Override
					public void run(JavaScriptSupport javascriptSupport) {
						findTree();
					}
				});
			}
		} catch (Exception e) {
			//String errorMsg = i18n.get("deleteOrgFail");
			String errorMsg = messages.get("delete-error");
			logger.error(errorMsg, e);
			alertManager.error(errorMsg);
		}
	}
}
