package org.flywind.cms.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;

@Path("/thirdparty")
public class OtherLogin {

	@Inject
	private SecurityService securityService;
	
	@POST
	@Produces({ "application/xml", "application/json" })
	public String post() {
		Subject currentUser = securityService.getSubject();
		
		if (currentUser == null)
		{
			throw new IllegalStateException("Subject can`t be null");
		};
		
		UsernamePasswordToken token = new UsernamePasswordToken("admin", "123456");
		
		currentUser.login(token);
		
		System.out.println(111);
		
		return null;
	}
}
