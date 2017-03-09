package org.flywind.cms.components;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.flywind.business.entities.sys.SystemSeting;
import org.flywind.business.entities.sys.User;
import org.flywind.business.services.sys.SystemSetingService;
import org.flywind.business.services.sys.UserService;
import org.flywind.cms.base.AppBase;
import org.tynamo.security.services.SecurityService;

public class THeader extends AppBase {

	private static Logger logger = Logger.getLogger(THeader.class);

	@Inject
	@Property
	private SecurityService securityService;
	
	@Property
	private User user;
	
	@Property
    private  SystemSeting  systemSeting;
	
	@Property
	private String companyName;
	
	@Inject
	private SystemSetingService systemSetingService;
	
	@Inject
	private UserService userService;
	
	public void setupRender() {
		if (null == user) {
			User oldUser = state.getSysInfo().getUser();
			user = userService.findOne(oldUser.getId());
			String OldpicUrl = user.getPicUrl();
			// 如果没有头像，给一个默认头像
			if (null == OldpicUrl || "".endsWith(OldpicUrl)) {
				//OldpicUrl = contextPath + SEPARATOR + FSysConstants.ASSETS + SEPARATOR + FSysConstants.IMAGES + SEPARATOR + FSysConstants.DEFAULT_IMAGE;
				user.setPicUrl(OldpicUrl);
			} else {
				// 新头像url
				user.setPicUrl(OldpicUrl);
				logger.info("新头像url");
			}
		}

		if (null == systemSeting) {
			systemSeting = systemSetingService.querySysSetingByCustomerCode(user.getCustomerCode());
			if("zh-cn".equalsIgnoreCase(getCurrentLanguage())){
				companyName = systemSeting.getCompanyName();
			}else{
				companyName = systemSeting.getCompanyNameEn();
			}
			String logoPath = systemSeting.getSystemLogoPath();
			if (null == logoPath || "".equals(logoPath)) {
				//logoPath = contextPath + SEPARATOR + FSysConstants.ASSETS + SEPARATOR + FSysConstants.IMAGES + SEPARATOR + FSysConstants.MAX_LOGO_IMAGE;
				systemSeting.setSystemLogoPath(logoPath);
			} else {
				systemSeting.setSystemLogoPath(logoPath);
			}

		}
	}
}
