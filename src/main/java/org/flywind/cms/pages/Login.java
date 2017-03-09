package org.flywind.cms.pages;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.base.SysParamService;
import org.flywind.business.services.sys.LogService;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.cms.base.AppBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.components.LoginForm;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

public class Login extends AppBase {

	private static final Logger logger = LoggerFactory.getLogger(LoginForm.class);

	private static final String LOGIN_FORM_ID = "tynamoLoginForm";

	@Property
	private String tynamoLogin;

	@Property
	private String tynamoPassword;
	
	@Property
	private String customerCode;

	@Property
	private boolean tynamoRememberMe;

	private String loginMessage;

	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String successURL;

	@Inject
	private SecurityService securityService;

	@Inject
	private LoginContextService loginContextService;

	@Inject
	@Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
	private boolean redirectToSavedUrl;
	
	/**
	 * 动态密码(用于前台页面)
	 */
	@Property
	private String dynamicPwd;
	
	/**
	 * 动态密码(保存于后台session)
	 */
	@Property
	@Persist
	private String dynamicPwdForSession;
	
	@Property
	private SystemSeting systemSeting;

	@Inject
	private SystemSetingService systemSetingService;

	@Inject
	private SysParamService sysParamService;
	
	@Inject
	private LogService logService;
	
	public void setupRender(){
		
		//根据ip查出客户代码
		customerCode = "0755";
		
		//生成6位的随机密码
		dynamicPwd = FBaseUtil.generateInitPasword();
		dynamicPwdForSession = dynamicPwd;
		
	}

	@OnEvent(value = EventConstants.VALIDATE, component = LOGIN_FORM_ID)
	public void attemptToLogin() throws ValidationException
	{

		Subject currentUser = securityService.getSubject();
		
		if (currentUser == null)
		{
			throw new IllegalStateException("Subject can`t be null");
		};
		
		Session session = currentUser.getSession();
		//根据域名查询数据customerCode，之后setAttribute到shiro安全框架session
		//System.out.println(request.getServerName());//获得服务器域名
		session.setAttribute(FBaseConstants.DOMAIN_NAME, request.getServerName());
		session.setAttribute(FBaseConstants.CUSTOMER_CODE, customerCode);
		session.setAttribute(FBaseConstants.DYNAMIC_PWD_FOR_SESSION, dynamicPwdForSession);
		
		HttpServletRequest req = requestGlobals.getHTTPServletRequest();
		String ip = FBaseUtil.getClientIP(req);
		
		session.setAttribute(FBaseConstants.IP_STRING, ip);
		session.setAttribute("messages", messages);

		//String pwd = DigestUtils.md5Hex(tynamoPassword);
		System.out.println("e10adc3949ba59abbe56e057f20f883e");
		
		UsernamePasswordToken token = new UsernamePasswordToken(tynamoLogin, tynamoPassword);
		token.setRememberMe(tynamoRememberMe);

		try
		{
			currentUser.login(token);
		} catch (UnknownAccountException e)
		{
			loginMessage = messages.get("AccountDoesNotExists");
			logger.error(messages.get("AccountDoesNotExists"));
            
		} catch (IncorrectCredentialsException e)
		{
			loginMessage = messages.get("WrongPassword");
		} catch (LockedAccountException e)
		{
			loginMessage = messages.get("AccountLocked");
		} catch (AuthenticationException e)
		{
			loginMessage = messages.get("AuthenticationError");
		}

		if (loginMessage != null)
		{
		    throw new ValidationException(loginMessage);
		}
	}

	@OnEvent(value = EventConstants.SUCCESS, component = LOGIN_FORM_ID)
	public Object onSuccessfulLogin() throws IOException
	{
		if (StringUtils.hasText(successURL)) {
			if ("^".equals(successURL))
				return pageRenderLinkSource.createPageRenderLink(componentResources.getPage().getClass());
			return new URL(successURL);
		}

		if (redirectToSavedUrl) {
			String requestUri = loginContextService.getSuccessPage();
			if (!requestUri.startsWith("/") && !requestUri.startsWith("http")) {
				String language = currentLocale.toLanguageTag();
				if("zh-cn".equalsIgnoreCase(language)){
					requestUri = "/zh_CN/" + requestUri;
				}else if("en".equalsIgnoreCase(language)){
					requestUri = "/en/" + requestUri;
				}else{
					requestUri = "/" + requestUri;
				}  
			}
			
			loginContextService.redirectToSavedRequest(requestUri);
			return null;
		}
		return loginContextService.getSuccessPage();
	}
	
	public void setLoginMessage(String loginMessage)
	{
		this.loginMessage = loginMessage;
	}

	public String getLoginMessage()
	{
		if (StringUtils.hasText(loginMessage))
		{
			return loginMessage;
		} else
		{
			return " ";
		}
	}
}
