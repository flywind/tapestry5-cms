package org.flywind.cms.pages.admin.sys;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.Contact;
import org.flywind.business.services.sys.ContactService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

@RequiresPermissions("contact:view")
@Import(stylesheet={"${widget.plugins.assets.path}/artdialog/skin/default.css"})
public class ContactList extends AppBase {

	@Property
	@Persist
	private Contact item;
	
	@Property
	private List<Contact> items;
	
	@InjectComponent
	private Zone itemListZone;
	
	@Inject
	private ContactService contactService;
	
	@Property
	private String delUrl;
	
	@Property
	@Persist
	private String lang;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void setupRender(){
		lang = getCurrentLanguage();
		delUrl = componentResources.createEventLink("del").toURI();
	}
	
	public void onPrepare(){
		if(item == null){
			item = new Contact();
		}
	}
	
	@OnEvent(component = "itemGrid", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		FPage paging = (FPage) request.getAttribute("page");
		paging.setSortName("id");
		paging.setSortOrder("DESC");
		if(item == null){
			item = new Contact();
		}
		items = contactService.findAll(item, paging, state.getSysInfo());
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(itemListZone);
		}
	}
	
	public void onReset() {
		item = null;
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(itemListZone);
		}
	}
	
	@RequiresPermissions("contact:delete")
	public void onDel(List<String> ids){
		
		try {
			for(String id : ids){
				contactService.delete(Long.parseLong(id));
			}
			
		} catch (Exception e) {
			String errorMsg = messages.get("delete-error");
		}
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(itemListZone);
		}
	}
}
