package org.flywind.cms.services;

import org.flywind.business.entities.base.FSysInfo;

/**
 * <p>session接口类</p>
 * 
 * @author flywind(飞风)
 * @date 2015年10月13日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public interface IState {


	/**
	 * 获得系统session
	 * @return FSysInfo
	 */
	public FSysInfo getSysInfo();

}
