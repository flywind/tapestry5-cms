package org.flywind.cms.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>java ExecutorService实现</p>
 * 
 * @author flywind(飞风)
 * @date 2016年2月1日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class MyExecutorServiceImpl implements MyExecutorService {

	private static ExecutorService pool;
	
	public ExecutorService getPool(){
		if(null==pool){
			/* newSingleThreadExecutor()：单线程
			 * newCachedThreadPool()：无界线程池，可以进行自动线程回收
			 * newFixedThreadPool (int nThreads):固定大小线程池
			 */
			pool = Executors.newFixedThreadPool(2);
		}
		return pool;
	}
}
