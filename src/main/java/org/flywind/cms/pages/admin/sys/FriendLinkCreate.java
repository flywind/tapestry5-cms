package org.flywind.cms.pages.admin.sys;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.FriendLink;
import org.flywind.business.services.sys.FriendLinkService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("friendlink:add")
public class FriendLinkCreate extends AppBase{

	@Property
	private FriendLink link;
	
	@Inject
	private FriendLinkService friendLinkService;
	
	@InjectComponent
	private Form meForm;
	
	public void onPrepare(){
		if(link == null){
			link = new FriendLink();
		}
	}
	
	public void onValidateFromMeForm(){
		if(meForm.getHasErrors()){
			return;
		}
		try {
			link.initBaseInfo(state.getSysInfo().getUser().getUsername(), state.getSysInfo().getCustomerCode());
			friendLinkService.create(link);
		} catch (Exception e) {
			String error = messages.get("link-create-error");
			alertManager.error(error);
			meForm.recordError(error);
		}
	}
	
	public Object onSuccess(){
		return FriendLinkList.class;
	}
}
