package org.flywind.cms.pages.admin.cms;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.flywind.business.entities.base.SysParam;
import org.flywind.business.entities.cms.Technology;
import org.flywind.business.services.base.SysParamService;
import org.flywind.business.services.cms.TechnologyService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;
@RequiresPermissions("content:view")
public class ContentList extends AppBase {

	@Property
	@Persist
	private Technology technology;
	
	@Property
	private List<Technology> technologys;
	
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
	private TechnologyService technologyService;
	
	@Inject
	private SysParamService sysParamService;
	
	@Property
	@Persist
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
		if(technology == null){
			technology = new Technology();
		}
		
		if(StringUtils.isEmpty(types)){
			StringBuffer s = new StringBuffer();
			s.append(0).append(EQUAL).append(messages.get("please-select-label")).append(COMMA);
			List<SysParam> sysParams = sysParamService.getAllParamByBusinessType(2);
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
		//System.out.println(arr[0]);
		
		//sleep(5000000);
		FPage paging = (FPage) request.getAttribute("page");
		if(technology == null){
			technology = new Technology();
		}
		paging.setSortOrder("DESC");
		paging.setSortName("id");
		technology.setTechnologyType(menutype);
		
		technologys = technologyService.findAll(technology, paging, state.getSysInfo(), getCurrentLanguage());
	}
	
	public void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(listZone);
		}
	}
	
	public void onReset() {
		menutype = 0;
		technology = null;
		ajaxResponseRenderer.addRender(listZone);
	}
	
	@RequiresPermissions("content:delete")
	public void onDel(List<String> ids){
		try {
			for(String id : ids){
				technologyService.deleteById(Long.parseLong(id));
			}
			ajaxResponseRenderer.addRender(listZone);
		} catch (Exception e) {
			String errorMsg = messages.get("delete-error");
			alertManager.error(errorMsg);
		}
	}
	
	/*public SelectModel getTypes() {
		final List<OptionModel> options = new ArrayList<OptionModel>();

		List<SysParam> sysParams = sysParamService.getAllParamByBusinessType(2);
		// 循环添加到OptionModel
		for (SysParam param : sysParams) {
			options.add(new OptionModelImpl(param.getParamValue(), param.getParamKey() + ""));
		}
		// 返回SelectModel
		return new SelectModelImpl(null, options);
	}*/

}
