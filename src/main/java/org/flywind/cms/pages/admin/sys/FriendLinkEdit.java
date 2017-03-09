package org.flywind.cms.pages.admin.sys;

import java.util.Date;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.FriendLink;
import org.flywind.business.services.sys.FriendLinkService;
import org.flywind.cms.base.AppBase;

@RequiresPermissions("friendlink:edit")
public class FriendLinkEdit extends AppBase {

	@Property
	private FriendLink link;
	
	@Inject
	private FriendLinkService friendLinkService;
	
	@Property
	@PageActivationContext
	private Long id;
	
	@InjectComponent
	private Form meForm;
	
	public void onPrepare(){
		if(link == null){
			link = new FriendLink();
		}
		if(meForm.isValid()){
			link = friendLinkService.getById(id);
		}
	}
	
	public Object onSuccess(){
		try {
			link.setLastUpdateTime(new Date());
			friendLinkService.update(link);
			return FriendLinkList.class;
		} catch (Exception e) {
			String error = messages.get("update-error");
			alertManager.error(error);
			meForm.recordError(error);
		}
		
		return this;
	}
}
