package org.flywind.cms.pages.admin.sys;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.common.result.Json;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
import org.flywind.widgets.WidgetSymbolConstants;
import org.flywind.widgets.core.dao.FPage;
@RequiresPermissions("user:view")
@Import(stylesheet={"context:assets/styles/zTreeStyle/zTreeStyle.css"})
public class UserList extends AppBase {

	@Property
	private User user;

	@Property
	private List<User> users;

	@Property
	private String roleName;

	@Inject
	private UserService userService;

	@Inject
	private OrganizationService organizationService;
	
	@Property
	@Persist
	private String delUrl;

	@Property
	@Persist
	private String username;
	
	@Property
	@Persist
	private String name;

	@InjectComponent
	private Zone listZone;

	@Property
	@Persist
	private String companyName;
	
	@Property
	@Persist
	private String 	companyId;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private Json json;
	
	@Property
	@Persist
	private String lang;
	
	/**
	 * 
	 * 分隔符
	 */
	private static final String SEPARATOR = System.getProperty("file.separator");
	
	
	public void setupRender() {
		lang = getCurrentLanguage();
		User loginUser = state.getSysInfo().getUser();
		json = organizationService.findAllToJsonByUser(loginUser);
		delUrl = componentResources.createEventLink("delete").toURI();
		
	}
	
	
	@OnEvent(component = "gridData", value = WidgetSymbolConstants.BOOTSTRAP_TABLE_LOAD_DATA)
	public void findAll(){
		User loginUser = state.getSysInfo().getUser();
		FPage paging = (FPage) request.getAttribute("page");
		if(null==username&&name==null&&null==companyName){
			users = userService.findAll(loginUser,paging);
		}else{
			if(null==companyName||"".equals(companyName)){
				companyId=null;
			}
			users = userService.findByUserNameAndReadName(loginUser,username, name,companyId, state.getSysInfo().getUser().getCustomerCode(),paging);
		}
		
	}

	/**
	 * 通过用户名或者用户真实姓名、组织机构 查询
	 */
	public void onSuccessFromSearchForm() {
		ajaxResponseRenderer.addRender(listZone);
	}

	/**
	 * 通过ID删除用户
	 * 
	 * @param id
	 */
	@RequiresPermissions("user:delete")
	public void onDelete(List<String> ids) {
        	//首先删除服务器中的头像资源，在删除数据库中的数据
		for(String id : ids){
			User user =userService.findOne(Long.parseLong(id));
            String picUrl = user.getPicUrl();
            if(null!=picUrl&&!"".endsWith(picUrl))
            {
            	 String[]  strs = picUrl.split("\\"+SEPARATOR);
                 String fileName=strs[strs.length-1];	
                 ServletContext servletContext = applicationGlobals.getServletContext();
     			 String realPath=servletContext.getRealPath(SEPARATOR + FSysConstants.ASSETS+SEPARATOR
     					+ FSysConstants.IMAGES);
                 String filePath=realPath+SEPARATOR+fileName;
                 File file  = new File(filePath);
                 file.delete();	
            }
        	userService.deleteUser(user);	
		}
        	
        ajaxResponseRenderer.addRender(listZone);
	}
	
	public void onReset(){
		username = null;
		name = null;
		companyName = null;
		ajaxResponseRenderer.addRender(listZone);
	}
	
	public void afterRender() {
		javaScriptSupport.require("init-userCreate").invoke("init").with(json.toString());		
	}
}
