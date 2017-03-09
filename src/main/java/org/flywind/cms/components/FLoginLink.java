package org.flywind.cms.components;

import org.apache.log4j.Logger;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.services.sys.LogService;
import org.flywind.cms.base.AppBase;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

/**
 * <p>登录连接</p>
 * @author flywind(飞风)
 * @date 2015年9月18日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class FLoginLink extends AppBase {
	
	/**
	 * 记录日志
	 */
	private static final Logger logger = Logger.getLogger(FLoginLink.class);

	@Inject
	@Property
	private SecurityService securityService;

	@Inject
	private LoginContextService loginContextService;
	
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String cls;
	
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String loginCls;
	
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String logoutCls;

	@Inject
	private LogService logService;

	public String onActionFromTynamoLoginLink()
	{
		loginContextService.removeSavedRequest();
		return loginContextService.getLoginPage();
	}

	public void onActionFromTynamoLogoutLink()
	{
		logger.info("User name: " + state.getSysInfo().getUser().getName() + " log out...");
		securityService.getSubject().logout();	
		
	}
	
}
