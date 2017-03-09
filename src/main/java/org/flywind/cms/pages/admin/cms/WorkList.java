package org.flywind.cms.pages.admin.cms;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.base.SysParam;
import org.flywind.business.entities.cms.Work;
import org.flywind.business.services.base.SysParamService;
import org.flywind.business.services.cms.WorkService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

@RequiresPermissions("work:view")
public class WorkList extends AppBase {

	@Property
	@Persist
	private Work item;
	
	@Property
	private List<Work> items;
	
	@Property
	private String searchTitle;
	
	@InjectComponent
	private Zone listZone;
	
	private final static char EQUAL = '=';
	
	private final static char COMMA = ',';
	
	@Property
	@Persist
	private int menutype;
	
	@Property
	private String types;
	
	@Inject
	private WorkService itemService;
	
	@Inject
	private SysParamService sysParamService;
	
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
		delUrl = componentResources.createEventLink("del").toURI();
		lang = getCurrentLanguage();
	}
	
	public void onPrepare(){
		if(item == null){
			item = new Work();
		}
		
		if(StringUtils.isEmpty(types)){
			StringBuffer s = new StringBuffer();
			s.append(0).append(EQUAL).append(messages.get("please-select-label")).append(COMMA);
			List<SysParam> sysParams = sysParamService.getAllParamByBusinessType(3);
			if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
				for(SysParam param : sysParams){
					s.append(param.getParamKey()).append(EQUAL).append(param.getParamValue()).append(COMMA);
				}
			}else{
				for(SysParam param : sysParams){
					s.append(param.getParamKey()).append(EQUAL).append(param.getParamValueEn()).append(COMMA);
				}
			}
			
			types = s.toString();
		}
	}
	
	@OnEvent(component = "gridData", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		FPage paging = (FPage) request.getAttribute("page");
		if(item == null){
			item = new Work();
		}
		item.setType(menutype);
		paging.setSortOrder("DESC");
		paging.setSortName("id");
		
		items = itemService.findAll(item, paging, state.getSysInfo(), getCurrentLanguage());
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
	
	public void onReset() {
		menutype = 0;
		item = null;
		ajaxResponseRenderer.addRender(listZone);
	}
	
	@RequiresPermissions("work:delete")
	public void onDel(List<String> ids){
		
		try {
			for(String id : ids){
				itemService.deleteById(Long.parseLong(id));
			}
			ajaxResponseRenderer.addRender(listZone);
		} catch (Exception e) {
			String errorMsg = messages.get("delete-fail");
			alertManager.error(errorMsg);
		}
	}

}
