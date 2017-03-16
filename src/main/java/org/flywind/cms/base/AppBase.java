package org.flywind.cms.base;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.MultipartDecoder;
import org.flywind.cms.services.IState;

/**
 * <p>基础页面</p>
 * 
 * @author flywind(飞风)
 * @date 2015年10月13日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class AppBase {

	/**
	 * 应用程序全局参数
	 */
	@Inject
    protected ApplicationGlobals applicationGlobals;
	
	@Inject
	protected PageRenderLinkSource pageRenderLinkSource;
	
	/**
	 * 请求全局参数
	 */
	@Inject
	protected RequestGlobals requestGlobals;
	
	/**
	 * tapestry IOC请求，如获取http的请求
	 */
	@Inject
    protected Request request;
	
	/**
	 * javascript 提供者，用来view层与control层的js交互
	 */
	@Inject
	protected JavaScriptSupport javaScriptSupport;
	
	/**
	 * 组件资源，使用组件的资源触发器
	 */
	@Inject
	protected ComponentResources componentResources;
	
	/**
	 * ajax请求渲染器
	 */
	@Inject
    protected AjaxResponseRenderer ajaxResponseRenderer;
	
	/**
	 * 客户端元素、多用来获取dmo元素属性，如ID
	 */
	@InjectContainer
	protected ClientElement clientElement;
	
	/**
	 * 获得context目录
	 */
	@Property
	@Inject
	@Symbol(SymbolConstants.CONTEXT_PATH)
	protected String contextPath;
	
	/**
	 * 自定义的tapestry session
	 */
	@SessionState
	protected IState state;
	
	/**
	 * 本地化信息源
	 */
	@Inject
	protected Messages messages;
	
	/**
	 * tapestry alert
	 */
	@Inject
	protected AlertManager alertManager;
	
	@Inject
	protected MultipartDecoder multipartDecoder;
	
	/**
	 * 持久化本地语言触发器
	 */
	@Inject
	protected PersistentLocale persistentLocale;
	
	/**
	 * 当前语言
	 */
	@Inject
	protected Locale currentLocale;

	/**
	 * 原语言
	 */
	/*@Property
	@Persist
	protected String oldLanguage;*/
	
	/**
	 * 国际化信息的Map
	 */
	/*@Property
	@Persist
	protected Map<String, String> i18n;*/
	
	public Format getDateFormat(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	
	public Format getYFormat(){
		return new SimpleDateFormat("yyyy-MM-dd");
	}
	
	public Format getMdFormat(){
		return new SimpleDateFormat("MM-dd HH:mm");
	}
	
	public Format getHourFormat(){
		return new SimpleDateFormat("HH:mm");
	}
	
	public Format getDFormat(){
		return new SimpleDateFormat("MM.dd.yyyy");
	}
	
	/**
	 * 获取当前环境的语言
	 * 
	 * @return
	 *        语言字符串
	 */
	protected Locale toLocale(String shortName) {
		String[] result = shortName.split("_");
		if (result.length == 1) {
			return new Locale(result[0]);
		} else {
			return new Locale(result[0], result[1]);
		}
	}
	
	/**
	 * 获取当前环境的语言
	 * 
	 * @return
	 *        语言字符串
	 */
	protected String getCurrentLanguage() {
		return currentLocale.toLanguageTag();
	}
	
	/**
	 * 获得文件存储的目录
	 * @param catalog 目录,如:E:/project
	 * @return
	 */
	protected String getSavePath(String catalog){
		return applicationGlobals.getServletContext().getRealPath(catalog)+ "\\";
	}
	
	/**
	 * 获得Url存储路径
	 * @param catalog 路径 /hello/Index
	 * @return
	 */
	protected String getSaveUrl(String catalog){
		return getRequest().getContextPath() + catalog;
	}
	
	/**
	 * 覆盖HttpServletRequest
	 * @return
	 */
	protected final HttpServletRequest getRequest() {
		return requestGlobals.getHTTPServletRequest();
	}
	
	/**
	 * 判断当前语言是否中文
	 * 覆盖HttpServletResponse
	 * @return boolean
	 */
	protected final HttpServletResponse getResponse() {
		return requestGlobals.getHTTPServletResponse();
	}
	
	/**
	 * 系统国际化信息
	 */
	/*protected Map<String, Map<String, String>> getI18nResource() {
		return I18NCache.getResource(state.getSysInfo().getCustomerCode(), currentLocale.toLanguageTag());
	}*/
	
	/**
	 * 获取指定"表名"的国际化资源，包括了公共的国际化资源
	 * 
	 * @param tableName
	 *        国际化资源表名
	 * @return
	 *        国际化资源的Map
	 */
	/*protected Map<String, String> getI18nResource(String tableName) {
		Map<String, String> resMap = I18NCache
				.getResource(state.getSysInfo().getCustomerCode(), getCurrentLanguage()).get(tableName);
		resMap.putAll(this.getI18nResource().get(FBusinessConstants.I18N_BASE));
		return resMap;
	}*/
	
	
	/**
	 * 响应切换语言
	 */
	/*protected void responseChangeLanguage() {
		if (!getCurrentLanguage().equals(oldLanguage)) {
			i18n = null;
			oldLanguage = getCurrentLanguage();
		}
	}*/
	
	/**
	 * 是否通过配置管理页面修改了页面国际化资源，如果修改了则更新i18n对象
	 * 
	 * @param sessionKey
	 *        Session键
	 * @return 
	 *        0.未修改，1.修改
	 */
	/*protected void isModifiedProp(String sessionKey) {
		Object obj = state.getSysInfo().getAttribute(sessionKey);
		int isModified = FSysUtil.objectToInt(obj);
		//如果发生了变化，则清空i18n中的国际化资源
		if (isModified == SysConfigConstants.MODIFIED) {
			i18n = null;
			state.getSysInfo().removeAttribute(sessionKey);
			return;
		}
		
		//响应切换语言
		responseChangeLanguage();
	}*/
}
