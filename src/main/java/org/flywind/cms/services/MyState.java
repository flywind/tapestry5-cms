package org.flywind.cms.services;

import org.apache.shiro.SecurityUtils;
import org.flywind.business.common.constants.FSysConstants;
import org.flywind.business.entities.base.FSysInfo;
import org.springframework.stereotype.Service;

/**
 * <p>自定义的session类</p>
 * 
 * @author flywind(飞风)
 * @date 2015年10月13日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
@Service
public class MyState implements IState {
	
	public FSysInfo getSysInfo(){
		return (FSysInfo) SecurityUtils.getSubject().getSession().getAttribute(FSysConstants.SESSION);
	}

}
