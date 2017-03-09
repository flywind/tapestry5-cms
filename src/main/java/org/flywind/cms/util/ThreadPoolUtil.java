package org.flywind.cms.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>Spring 线程池util类</p>
 * 
 * @author flywind(飞风)
 * @date 2016年2月1日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class ThreadPoolUtil
{
	//获取spring线程池
	private static ThreadPoolTaskExecutor taskExecutor;
	
	public static ThreadPoolTaskExecutor getPoolTaskExecutor(){
		
		if(taskExecutor == null){
			WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
			taskExecutor =(ThreadPoolTaskExecutor)wac.getBean("taskExecutor");
		}
		return taskExecutor;
		
	}
}
