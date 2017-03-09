package org.flywind.cms.pages.admin.sys;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.Organization;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.cms.base.AppBase;

public class OrganizationAddChild extends AppBase {

	@Property
	@PageActivationContext
	private Long treeNodeId;
	// 父节点名字
	@Property
	private String parentNodeName;
	
	@InjectComponent("appendChildForm")
	private Form appendChildForm;
	
	@Property
	private Organization organization;

	@Property
	private Organization parentOrganization;
	@Inject
	private OrganizationService organizationService;

	public void onPrepare() {
		if (organization == null) {
			organization = new Organization();
		}
	}

	public void setupRender() {
		parentOrganization = organizationService.findOne(treeNodeId);
		this.parentNodeName = parentOrganization.getName();
	}

	public void onValidateFromAppendChildForm() {
		organization.setParentId(treeNodeId);
		parentOrganization = organizationService.findOne(treeNodeId);
		organization.setParentIds(parentOrganization.getParentIds() + treeNodeId + "/");
		organization.setCustomerCode(state.getSysInfo().getUser().getCustomerCode());
		organizationService.createOrganization(organization);
	}

	public Object onSuccess() {
		return Success.class;
	}
}
