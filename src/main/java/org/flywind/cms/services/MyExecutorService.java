package org.flywind.cms.services;

import java.util.concurrent.ExecutorService;

/**
 * <p>java ExecutorService接口</p>
 * 
 * @author flywind(飞风)
 * @date 2016年2月1日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public interface MyExecutorService {
	
	public ExecutorService getPool();
	
}
