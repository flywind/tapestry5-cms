package org.flywind.cms.pages.admin;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.cache.I18NCache;
import org.flywind.business.common.cache.util.CacheUtil;
import org.flywind.business.entities.sys.VersionLog;
import org.flywind.business.services.sys.VersionLogService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.core.dao.FPage;


public class Index extends AppBase {
	
	@Property
	private VersionLog versionLog;

	@Inject
	private VersionLogService versionLogService;
	
	@Property
	private List<VersionLog> versionLogs;
	
	@Property
	private String userName;
	
	public boolean isCn(){
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			return true;
		}
		return false;
	}
	
	public void setupRender(){
		FPage paging = new FPage();
		paging.setPageNumber(1);
		paging.setPageSize(30);
		if(versionLog == null){
			versionLog = new VersionLog();
		}
		versionLog.setHide(0);
		versionLogs = versionLogService.findAll(versionLog, paging, state.getSysInfo());
		
		userName = state.getSysInfo().getUser().getName();
	}
	
	public Format getYearFormat(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}
	
	/**
	 * 清除缓存
	 */
	public void onClearCache() {
		String customerCode = state.getSysInfo().getCustomerCode();
		String key = I18NCache.getCacheKey(customerCode, getCurrentLanguage());
		CacheUtil.clearCache(key);
	}
	
}
