package org.flywind.cms.pages.admin.tcms;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.exception.FException;
import org.flywind.business.entities.cms.Category;
import org.flywind.business.entities.cms.Discuss;
import org.flywind.business.entities.cms.Posts;
import org.flywind.business.services.cms.CategoryService;
import org.flywind.business.services.cms.DiscussService;
import org.flywind.business.services.cms.PostsService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

public class DiscussListSupper extends AppBase {

	@Property
	@Persist
	private Discuss discuss;
	
	@Property
	private List<Discuss> discusses;
	
	@Inject
	private DiscussService discussService;
	
	
	@InjectComponent
	private Zone listZone;
	
	@Property
	private String types;
	
	@Property
	@Persist
	private String delUrl;
	
	@Property
	@Persist
	private int menutype;
	
	@Property
	@Persist
	private String lang;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void onPrepare(){
		if(discuss == null){
			discuss = new Discuss();
		}
	}
	
	@OnEvent(component = "gridData", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		
		FPage page = (FPage) request.getAttribute("page");
		if(discuss == null){
			discuss = new Discuss();
		}
		page.setSortOrder("DESC");
		page.setSortName("lastUpdateTime");
		
		discusses = discussService.getAllDiscuss(discuss, page);
	}
	
	public void setupRender(){
		delUrl = componentResources.createEventLink("del").toURI();
		lang = getCurrentLanguage();
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
	
	public void onReset() {
		menutype = 0;
		discuss = null;
		ajaxResponseRenderer.addRender(listZone);
	}
	
	public void onDel(List<String> ids) throws Exception{
		try {
			discussService.deleteDsicussByIds(ids);
			ajaxResponseRenderer.addRender(listZone);
		} catch (FException e) {
			String errorMsg = messages.get(e.getTypeKey());
			alertManager.error(errorMsg);
		}
	}
}
