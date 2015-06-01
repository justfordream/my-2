package com.huateng.core.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qingxue.li 
 * 流量监控
 */
public class UpayThreadPoolUtil {

	private static Logger logger = LoggerFactory.getLogger(UpayThreadPoolUtil.class);
	private static ThreadPoolExecutor executor = null;

	/**
	 * 初始化线程池
	 */
	public void init() {
		logger.debug("......线程池初始化......");
//		executor = new ThreadPoolExecutor(10, 29, 80, TimeUnit.MILLISECONDS,
//				new ArrayBlockingQueue<Runnable>(100),
//				new ThreadPoolExecutor.AbortPolicy());
		executor = new ThreadPoolExecutor(10, 29, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(100),
				new ThreadPoolExecutor.AbortPolicy());
		executor.allowCoreThreadTimeOut(true);
		logger.debug(
				"线程池初始化成功！其中，最小线程数为:{},最大线程数为:{}",
				new Object[] { executor.getCorePoolSize(),
						executor.getMaximumPoolSize() });

	}

	/**
	 * 任务加入线程池
	 * 
	 * @param runnable
	 *            天猫交易
	 * @throws Exception 
	 */
	public static String putReqestQueue(Runnable runnable) throws Exception {
		if (executor == null) {
			logger.error("......线程队列为空.....");
			throw new Exception("......线程队列为空.....");
		}
		try {
			executor.execute(runnable);
			logger.debug("任务加入线程池成功！当前线程数为:{}", executor.getPoolSize());
		} catch (RejectedExecutionException e) {
			logger.error("任务加入线程池失败！当前线程数为:{}", executor.getPoolSize());
			throw new Exception("......任务加入线程池失败.....");
		}
		return null;
	}
	
	/**
	 * 获得当前线程数量
	 * @throws Exception 
	 */
	public static int getCurrentThreadCount() throws Exception{
		int threadCount = 0;
		if (executor == null) {
			logger.error("......线程队列为空.....");
			throw new Exception("......线程队列为空.....");
		}
		threadCount = executor.getPoolSize();		
		return threadCount;
	}

}
