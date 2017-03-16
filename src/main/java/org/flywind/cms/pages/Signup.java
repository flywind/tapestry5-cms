package org.flywind.cms.pages;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.utils.FBaseUtil;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
import org.springframework.util.StringUtils;
import org.tynamo.security.services.SecurityService;

@Import(stylesheet={"context:assets/tlayout/styles/animate.css"})
public class Signup extends AppBase {
	
	@Property
	private User user;
	
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
	
	private String loginMessage;
	
	@Inject
	private UserService userService;
	
	@Inject
	private SecurityService securityService;
	
	public void setupRender(){
		//生成6位的随机密码
		dynamicPwd = FBaseUtil.generateInitPasword();
		dynamicPwdForSession = dynamicPwd;
		System.out.println(contextPath);
		System.out.println(componentResources.getPageName());
	}
	
	public void afterRender(){
		JSONObject spec = new JSONObject();
		spec.put("msg", messages.get("validate-message"));
		spec.put("userRequiredMsg", messages.get("user-required-message"));
		spec.put("userValidateMsg", messages.get("user-validate-message"));
		spec.put("userHasMsg", messages.get("user-has-message"));
		spec.put("userStringLengthMsg", messages.get("user-string-length-message"));
		spec.put("emailRequiredMsg", messages.get("email-required-message"));
		spec.put("emailValidateMsg", messages.get("email-validate-message"));
		spec.put("emailHasMsg", messages.get("email-has-message"));
		spec.put("passwordRequiredMsg", messages.get("password-required-message"));
		spec.put("confirmpwdRequiredMsg", messages.get("confirmpwd-required-message"));
		spec.put("noSameMsg", messages.get("no-same-message"));
		spec.put("checkUser", componentResources.createEventLink("checkUser").toAbsoluteURI());
		spec.put("create", componentResources.createEventLink("createUser").toAbsoluteURI());
		if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
			spec.put("index", contextPath + "/zh_CN/Start");
		}else{
			spec.put("index", contextPath + "/en/Start");
		}
		javaScriptSupport.require("inits/signup").invoke("init").with(spec);
	}
	
	public JSONObject onCheckUser(@RequestParameter(value = "name") final String userName){
		JSONObject reulut = new JSONObject();
		if (userName != null) {
			boolean userNameExist = userService.checkUserExist(userName.toLowerCase(), "0755");
			if(!userNameExist){
				reulut.put("valid", true);
				return reulut;
			}else{
				reulut.put("valid", false);
				return reulut;
			}
		}
		return null;
	} 
	
	public JSONObject onCreateUser(@RequestParameter(value = "name") final String username,
			@RequestParameter(value = "email") final String email, @RequestParameter(value = "password") final String password, @RequestParameter(value = "ep") final String ep){
		
		JSONObject result = new JSONObject();
		
		try {
			
			String defaultImg = getRequest().getContextPath() + "/uploadImages/head/head.png";
			
			user = new User();
			user.setUsername(username.toLowerCase());
			user.setEmail(email);
			user.setPassword(password);
			user.setCreater("admin");
			user.setCompanyId(Long.parseLong("1"));
			user.setCustomerCode("0755");
			user.setInitPwd(Boolean.FALSE);
			user.setLocked(Boolean.FALSE);
			user.setParentId("0/1/");
			user.setCreateTime(new Date());
			user.setPicUrl(defaultImg);
			
			Long userId = userService.createUser(user);
			userService.createUserAndRole(userId, "4");
			
			//User Login
			Subject currentUser = securityService.getSubject();
			
			if (currentUser == null)
			{
				throw new IllegalStateException("Subject can`t be null");
			};
			
			Session session = currentUser.getSession();
			
			session.setAttribute(FBaseConstants.DOMAIN_NAME, request.getServerName());
			session.setAttribute(FBaseConstants.CUSTOMER_CODE, "0755");
			session.setAttribute(FBaseConstants.DYNAMIC_PWD_FOR_SESSION, dynamicPwdForSession);
			
			HttpServletRequest req = requestGlobals.getHTTPServletRequest();
			String ip = FBaseUtil.getClientIP(req);
			
			session.setAttribute(FBaseConstants.IP_STRING, ip);
			session.setAttribute("messages", messages);
			
			UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), ep);
			
			try
			{
				currentUser.login(token);
			} catch (UnknownAccountException e)
			{
				loginMessage = messages.get("AccountDoesNotExists");
				//logger.error(messages.get("AccountDoesNotExists"));
	            
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
			    //throw new ValidationException(loginMessage);
				userService.deleteUser(user);
				result.put("error", messages.get("create-user-error"));
				result.put("loginMessage", loginMessage);
			}
			
			result.put("success", messages.get("create-user-success"));
			return result;
			
		} catch (Exception e) {
			result.put("error", messages.get("create-user-error"));
			return result;
		}
		
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
