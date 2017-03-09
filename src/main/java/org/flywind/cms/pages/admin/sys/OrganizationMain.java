package org.flywind.cms.pages.admin.sys;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.Organization;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.cms.base.AppBase;

public class OrganizationMain extends AppBase {
	
	@Property
	@PageActivationContext
	private Long treeNodeId;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private Organization organization;

	@Inject
	private OrganizationService organizationService;

	@InjectComponent("maintainForm")
	private Form maintainForm;

	public boolean isNodel() {
		if (!(organization.getParentId().equals(Long.parseLong("0")))) {
			return false;
		}
		return true;
	}

	public void onPrepareForRender() throws Exception {
		if (maintainForm.isValid()) {
			organization = organizationService.findOne(treeNodeId);
			if (organization == null) {
				throw new Exception(messages.get("organization-noid") + treeNodeId);
			}
		}

	}

	public void onPrepareForSubmit() {
		organization = organizationService.findOne(treeNodeId);
		if (organization == null) {
			maintainForm.recordError(messages.get("organization-no"));
			organization = new Organization();
		}
	}

	public void onValidateFromMaintainForm() {
		if (maintainForm.getHasErrors()) {
			return;
		}

		try {
			organizationService.updateOrganization(organization);
		} catch (Exception e) {
			maintainForm.recordError(messages.get("organization-addError"));
		}
	}

	public Object onSuccess() {
		return Success.class;
	}

	public Object onDel() {
		// 如果是顶级节点不给删除
		// 如果被管理用户不给删除
		Organization org = organizationService.findOne(treeNodeId);
		List<Long> orgIds = new ArrayList<Long>();
		orgIds.add(treeNodeId);
		List<User> users = organizationService.getUsersByOrgId(org.getCustomerCode(), orgIds);
		if (null != users && users.size() > 0) {
			alertManager.error(messages.get("has-users-no-delete"));
			return null;
		}
		// 如果有子集节点不给删除
		List<Long> childIds = organizationService.getOrganizationsByParentId(treeNodeId);
		if (null != childIds && childIds.size() > 0) {
			alertManager.error(messages.get("has-child-no-delete"));
			return null;
		}

		organizationService.deleteOrganizationById(org);

		return Success.class;
	}

	public void afterRender() {
		javaScriptSupport.require("init-maintain").invoke("init").with(treeNodeId);
	}
}
