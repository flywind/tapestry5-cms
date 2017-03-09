package org.flywind.cms.pages.admin.sys;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.FriendLink;
import org.flywind.business.services.sys.FriendLinkService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

@RequiresPermissions("friendlink:view")
public class FriendLinkList extends AppBase {

	@Property
	@Persist
	private FriendLink link;
	
	@Property
	private List<FriendLink> links;
	
	@InjectComponent
	private Zone linkListZone;
	
	@Inject
	private FriendLinkService friendLinkService;
	
	@Property
	private String delUrl;
	
	@Property
	@Persist
	private String lang;
	
	public void setupRender(){
		lang = getCurrentLanguage();
		delUrl = componentResources.createEventLink("del").toURI();
	}
	
	public void onPrepare(){
		if(link == null){
			link = new FriendLink();
		}
	}
	
	@OnEvent(component = "friendLinkGrid", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		FPage paging = (FPage) request.getAttribute("page");
		if(link == null){
			link = new FriendLink();
		}
		links = friendLinkService.findAll(link, paging, state.getSysInfo());
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(linkListZone);
		}
	}
	
	public void onReset() {
		link = null;
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(linkListZone);
		}
	}
	
	@RequiresPermissions("friendlink:delete")
	public void onDel(List<String> ids){
		try {
			for(String id : ids){
				friendLinkService.delete(Long.parseLong(id));
			}
			
		} catch (Exception e) {
			String errorMsg = messages.get("delete-fail");
			alertManager.error(errorMsg);
		}
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(linkListZone);
		}
	}

}
