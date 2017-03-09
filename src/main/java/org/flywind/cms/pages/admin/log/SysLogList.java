package org.flywind.cms.pages.admin.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.constants.FLogConstants;
import org.flywind.business.entities.sys.SysLog;
import org.flywind.business.services.sys.LogService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;


/**
 * <p>系统日志查看页面类</p>
 * 
 * @author flywind(飞风)
 * @date 2016年1月19日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
@Import(stylesheet={"${widget.plugins.assets.path}/artdialog/skin/default.css"})
public class SysLogList extends AppBase {
	
	/**
	 * 记录日志
	 */
	private final static Logger logger = Logger.getLogger(SysLogList.class);
	
	/**
	 * 等于号
	 */
	private final static char EQUAL = '=';
	
	/**
	 * 逗号
	 */
	private final static char COMMA = ',';
	
	@Property
	@Persist
	private SysLog sysLog;
	
	@Property
	private FPage paging;
	
	@Property
	private List<SysLog> sysLogs;
	
	@Property
	private int type;
	
	@Property
	private int result;
	
	@Property
	private String selectOpType;
	
	@Property
	private String selectOpResult;
	
	@Property
	private String startTime;
	
	@Property
	private String endTime;
	
	@Inject
	private LogService logService;
	
	@InjectComponent
	private Zone sysLogListZone;
	
	@Property
	@Persist
	private String lang;

	
	/**
	 * 类初始化时执行
	 */
	public void onPrepare() {
		sysLog = new SysLog();
		if (null == paging) {
			paging = new FPage();
			paging.setSortName(FLogConstants.OPERATION_TIME_SORT_NAME);
		}
		
		//初始化页面操作类型选择框
		if (null == selectOpType || selectOpType.isEmpty()) {
			StringBuilder opType = new StringBuilder();
			opType.append(0).append(EQUAL).append(messages.get("please-select-operation-type")).append(COMMA);
			String info = messages.get(FLogConstants.OPERATION_TYPE_LOGIN_LOG);
			opType.append(FLogConstants.LOGIN).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_LOGOFF_LOG);
			opType.append(FLogConstants.LOGOFF).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_PASSWORD_UPDATE_LOG);
			opType.append(FLogConstants.PASSWORD_UPDATE).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_FIND_PASSWORD_LOG);
			opType.append(FLogConstants.FIND_PASSWORD).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_RESET_PASSWORD_LOG);
			opType.append(FLogConstants.RESET_PASSWORD).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_CREATE_LOG);
			opType.append(FLogConstants.CREATE).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_DELETE_LOG);
			opType.append(FLogConstants.DELETE).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_UPDATE_LOG);
			opType.append(FLogConstants.UPDATE).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_TYPE_SELECT_LOG);
			opType.append(FLogConstants.SELECT).append(EQUAL).append(info).append(COMMA);
			selectOpType = opType.toString();
		}
		
		//初始化页面操作结果选择框
		if (null == selectOpResult || selectOpResult.isEmpty()) {
			StringBuilder opResult = new StringBuilder();
			opResult.append(0).append(EQUAL).append(messages.get("please-select-operation-result")).append(COMMA);
			String info = messages.get(FLogConstants.OPERATION_RESULT_SUCCESS_LOG);
			opResult.append(FLogConstants.SUCCESS_LOG).append(EQUAL).append(info).append(COMMA);
			info = messages.get(FLogConstants.OPERATION_RESULT_FAIL_LOG);
			opResult.append(FLogConstants.FAIL_LOG).append(EQUAL).append(info).append(COMMA);
			selectOpResult = opResult.toString();
		}
	}
	
	@OnEvent(component = "sysLogListId", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll() {
		try {
			paging = (FPage) request.getAttribute(FBaseConstants.PAGE_STRING);
			paging.setSortName(FLogConstants.OPERATION_TIME_SORT_NAME);
			paging.setSortOrder("DESC");
			if (null == sysLog) {
				sysLog = new SysLog();
			}
			sysLogs = logService.findSysLog(sysLog, paging, state.getSysInfo());
		} catch (Exception e) {
			logger.error("获取系统日志系列失败。", e);
		}
	}
	
	/**
	 * 高级搜索
	 * 
	 * @return
	 */
	public void onSuccessFromSearchForm() {
		sysLog.setType(type);
		sysLog.setResult(result);
		if (StringUtils.isNotEmpty(startTime)) {
			sysLog.setStartTime(startTime);
		}
		if (StringUtils.isNotEmpty(endTime)) {
			sysLog.setEndTime(endTime);
		}
		ajaxResponseRenderer.addRender(sysLogListZone);
	}
	
	/**
	 * 重置
	 * @return
	 */
	public void onReset() {
		sysLog = null;
		ajaxResponseRenderer.addRender(sysLogListZone);
	}
	
	/**
	 * 格式化页面的日期带时分
	 * 
	 * @return
	 */
	public JSONObject getOptions(){
		JSONObject opts = new JSONObject();
		opts.put("timepicker", true);
		opts.put("datepicker", true);
		opts.put("format", "Y-m-d H:i");
		return opts;
	}
	
	public void setupRender(){
		lang = getCurrentLanguage();
		String deleteUrl = componentResources.createEventLink("delete").toAbsoluteURI();
		JSONObject spec = new JSONObject();
		spec.put("deleteUrl", deleteUrl);
		spec.put("dialogTitle", messages.get("dialog-title"));
		spec.put("noSelectMsg", messages.get("select-one-msg"));
		javaScriptSupport.require("init-sysloglist").invoke("init").with(spec);
	}
	
	public JSONObject onDelete(){
		String opTime = request.getParameter("opTime");
		String customerCode = request.getParameter("customerCode");
		
		JSONObject result = new JSONObject();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("opTime", opTime);
			params.put("customerCode", customerCode);
			System.out.println("params");
			logService.deteleSysLog(params);
			result.put("success", messages.get("delete-success"));
			return result;
		} catch (Exception e) {
			result.put("error", messages.get("delete-error"));
			result.put("errorMsg", e.getMessage());
			return result;
		}
	}
	
}
