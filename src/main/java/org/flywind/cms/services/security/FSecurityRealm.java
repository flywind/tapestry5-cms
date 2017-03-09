package org.flywind.cms.services.security;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.common.constants.FBaseConstants;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.entities.base.FSysInfo;
import org.flywind.business.entities.sys.Organization;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.OrganizationService;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.business.services.sys.UserService;
import org.tynamo.security.services.SecurityService;


/**
 * <p>自定义Shiro realm</p>
 * 
 * @author flywind(飞风)
 * @date 2015年10月13日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class FSecurityRealm extends AuthorizingRealm{
	
	@Inject
	private UserService userService;
	
	@Inject
	@Property
	private SecurityService securityService;
	
	@Inject
	private SystemSetingService setService;
	
	@Inject
	private OrganizationService orgService;
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
			throws AuthenticationException
	{
		String username = (String)token.getPrincipal();
		
		//获取框架session对象
		Session session = securityService.getSubject().getSession();
		//获取登录IP地址
        String ip = (String) session.getAttribute(FBaseConstants.IP_STRING);
		String customerCode = (String)securityService.getSubject().getSession().getAttribute("customerCode");
		String dynamicPwdForSession = (String) session.getAttribute(FBaseConstants.DYNAMIC_PWD_FOR_SESSION);
		//取出后清除session中的动态密码
		session.removeAttribute(FBaseConstants.DYNAMIC_PWD_FOR_SESSION);
		//String password = new String((char[])token.getCredentials()); //得到密码  

        User user = userService.findByUsername(username,customerCode);
        
        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }

        if(Boolean.TRUE.equals(user.getLocked())) {
            throw new LockedAccountException(); //帐号锁定
        }

        String pwd = DigestUtils.md5Hex(user.getPassword() + dynamicPwdForSession);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
        		user.getUsername(), //用户名
        		pwd, //密码
                getName()  //realm name
        );
        
        //实例化基础信息
        FSysInfo fsysInfo = new FSysInfo(ip, customerCode, user);
        
        Organization o = orgService.findOne(user.getCompanyId());
        fsysInfo.setCompany(o);
        
        String domainName = (String) session.getAttribute(FBaseConstants.DOMAIN_NAME);
        fsysInfo.setDomainName(domainName);
        
        /*SystemSeting seting = setService.querySysSetingByCustomerCode(customerCode);
        boolean writeFindLog = seting.isWriteFindLog();
        fsysInfo.setWriteFindLog(writeFindLog);*/
        
        session.setAttribute(FSysConstants.SESSION, fsysInfo);
        
        Messages messages = (Messages) session.getAttribute("messages");
        fsysInfo.setMessages(messages);
        return authenticationInfo;
	}
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
	{
		String username = (String)principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        
        String customerCode = (String)securityService.getSubject().getSession().getAttribute("customerCode");
        
        authorizationInfo.setRoles(userService.findRoles(username,customerCode));
        System.out.println("角色+++++++++++"+userService.findRoles(username,customerCode));
        authorizationInfo.setStringPermissions(userService.findPermissions(username,customerCode));
        System.out.println("权限+++++++++++"+userService.findPermissions(username,customerCode));
        return authorizationInfo;
	}

}
