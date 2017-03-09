package org.flywind.cms.pages.admin.sys;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.flywind.business.entities.base.FSysInfo;
import org.flywind.business.entities.sys.VersionLog;
import org.flywind.business.services.sys.VersionLogService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;

@RequiresPermissions("versionlog:view")
public class VersionLogList extends AppBase {

	//private final static Logger logger = Logger.getLogger(VersionLogList.class);
	
	@Property
	@Persist
	private VersionLog log;
	
	
	@Property
	private FPage paging;
	
	@Property
	private List<VersionLog> versionLogs;
	
	@Property
	private String editUrl;
	
	@Property
	private String delUrl;
	
	@Inject
	private VersionLogService logService;
	
	@InjectComponent
	private Zone versionLogListZone;
	
	@Property
	private Long uid = 1L;
	
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
		if (null == log) {
			log = new VersionLog();
		}
	}
	
	public void setupRender() {
		lang = getCurrentLanguage();
		delUrl = componentResources.createEventLink("delete").toURI();
	}
	
	public void afterRender(){
		JSONObject spec = new JSONObject();
		spec.put("editMsg", messages.get("edit-btn"));
		spec.put("editAlertTitleMsg", messages.get("dialog-title"));
		spec.put("editAlertMsg", messages.get("edit-alert-msg"));
		spec.put("editSelectOneMsg", messages.get("select-one-msg"));
		javaScriptSupport.require("init-versionloglist").invoke("update").with(spec);
	}
	
	@OnEvent(component = "versionLogListId", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		paging = (FPage) request.getAttribute("page");
		FSysInfo session = state.getSysInfo();
		if (null == log) {
			log = new VersionLog();
		}
		log.setHide(-1);
		versionLogs = logService.findAll(log, paging, session);
	}
	
	public void onSuccessFromSearchForm() {
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(versionLogListZone);
		}
	}	
	
	public Object onReset() {
		log = null;
		return this;
	}
	
	/*
	 * **
	 * ** Delete log**
	 * */
	@RequiresPermissions("versionlog:delete")
	public void onDelete(List<String> ids) {
		for(String id :ids){
			System.out.println(id);
			logService.delete(Long.parseLong(id));
		}
		if(request.isXHR()){
			ajaxResponseRenderer.addRender(versionLogListZone);
		}
	}
	
}

