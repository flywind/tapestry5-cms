package org.flywind.cms.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

@Path("/test")
public class RestTest {
	
	@Inject
	private RequestGlobals requestGlobals;

	@POST
	@Produces({ "application/xml", "application/json" })
	public String post(@FormParam(value = "name") String n) {
		HttpServletRequest request = requestGlobals.getHTTPServletRequest();
		
		String name = request.getParameter("name");
		System.out.println(n);
		System.out.println(name);
		return null;
	}
}
