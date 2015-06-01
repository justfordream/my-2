package com.huateng.cmupay.controller.task;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

public class ThreadPoolManager {
	//private static ThreadPoolManager tpm = new ThreadPoolManager();
	//redis template
	private RedisTemplate template;
	// 线程池维护线程的最少数量
	private int minThreadNum = DEFAULT_MIN_THREAD_NUM;
	// 线程池维护线程的最大数量
	private  int maxThreadNum = DEFAULT_MAX_THREAD_NUM;
	// 线程池维护线程所允许的空闲时间
	private  int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
	// 线程池所使用的缓冲队列大小
	private  int workQueueSize = DEFAULT_WORK_QUEUE_SIZE;

	private static final int DEFAULT_MIN_THREAD_NUM = 4;
	private static final int DEFAULT_MAX_THREAD_NUM = 8;
	private static final int DEFAULT_KEEP_ALIVE_TIME =0;
	private static final int DEFAULT_WORK_QUEUE_SIZE = 2046;
	
	// 消息缓冲队列
//	private BlockingQueue<Runnable> msgQueue = new LinkedBlockingQueue<Runnable>();

	// 访问消息缓存的调度线程
//	private final Runnable accessBufferThread = new Runnable() {
//
//		public void run(){
//			System.out.println("in backup queue,Thread name is " + Thread.currentThread().getName());
//			// 查看是否有待定请求，如果有，则创建一个新的AccessDBThread，并添加到线程池中
//			if (hasMoreAcquire()) {
//				Runnable task = msgQueue.poll();
//				threadPool.execute(task);
//			}
//		}
//	};
//	
	/***
	 * 该策略失败后继续让线程执行
	 */
	private final RejectedExecutionHandler handler =new ThreadPoolExecutor.CallerRunsPolicy();
	/**
	 * 该策略失败后讲线程丢到此class的消息队列中
	 */
//	private final RejectedExecutionHandler handler = new RejectedExecutionHandler() {
//
//		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor){
//			//若线程池已满，将消息放入此管理类的队列中等候
//			//System.out.println("main thread pool is full,in the rejectedExecution method,Thread name is " + Thread.currentThread().getName());
//			msgQueue.offer(r);
//		}
//
//	};

	// 线程池
	private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
			minThreadNum, maxThreadNum, keepAliveTime, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(workQueueSize), handler);

	// 调度线程池
//	private final ScheduledExecutorService scheduler = Executors
//			.newScheduledThreadPool(1);

//	private final ScheduledFuture taskHandler = scheduler.schedule(
//			accessBufferThread, 0, TimeUnit.SECONDS);

//	public static ThreadPoolManager newInstance() {
//
//		return tpm;
//
//	}
//
//	private ThreadPoolManager() {
//	}

//	private boolean hasMoreAcquire() {
//
//		return !msgQueue.isEmpty();
//
//	}
	/**
	 * 增加消息
	 * @param key
	 * @param fieldMap
	 */
	public void addMessage(String key,Map fieldMap) {
		
		Runnable task = new AcctWriterThread(template,key,fieldMap);

		threadPool.execute(task);
	}
	/**
	 * 批量增加
	 * @param map
	 */
	public void addMessage(Map<String,Map<Integer,String>> map){
		Runnable task = new BatchAcctWriterThread(template,map);

		threadPool.execute(task);
	}
	
	public void addMessage(RedisRunnable run){
		if(run==null){
			return;
		}
		run.setTemplate(template);
		threadPool.execute(run);
	}
	
	/**
	 * @return the template
	 */
	public RedisTemplate getTemplate() {
		return template;
	}
	/**
	 * @param template the template to set
	 */
	public void setTemplate(RedisTemplate template) {
		this.template = template;
	}
	/**
	 * @return the minThreadNum
	 */
	public int getMinThreadNum() {
		return minThreadNum;
	}
	/**
	 * @param minThreadNum the minThreadNum to set
	 */
	public void setMinThreadNum(int minThreadNum) {
		this.minThreadNum = minThreadNum;
	}
	/**
	 * @return the maxThreadNum
	 */
	public int getMaxThreadNum() {
		return maxThreadNum;
	}
	/**
	 * @param maxThreadNum the maxThreadNum to set
	 */
	public void setMaxThreadNum(int maxThreadNum) {
		this.maxThreadNum = maxThreadNum;
	}
	/**
	 * @return the keepAliveTime
	 */
	public int getKeepAliveTime() {
		return keepAliveTime;
	}
	/**
	 * @param keepAliveTime the keepAliveTime to set
	 */
	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}
	/**
	 * @return the workQueueSize
	 */
	public int getWorkQueueSize() {
		return workQueueSize;
	}
	/**
	 * @param workQueueSize the workQueueSize to set
	 */
	public void setWorkQueueSize(int workQueueSize) {
		this.workQueueSize = workQueueSize;
	}
//	/**
//	 * @return the msgQueue
//	 */
//	public BlockingQueue<Runnable> getMsgQueue() {
//		return msgQueue;
//	}
//	/**
//	 * @param msgQueue the msgQueue to set
//	 */
//	public void setMsgQueue(BlockingQueue<Runnable> msgQueue) {
//		this.msgQueue = msgQueue;
//	}
	
	
	
}
