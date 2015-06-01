/*package com.huateng.core.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.huateng.tmall.bean.TMallRequestMessage;

//import com.huateng.haobai.ppcore.model.BillProvinceSwitch;
//import com.huateng.haobai.ppcore.service.db.IBusiDBService;
*//**
 * 
 * @author JGQ
 * 2013-05-08
 *//*
public class SgwThreadPoolUtil {
	private static  Logger logger = Logger.getLogger("FILE");
	//private IBusiDBService busiDbService;
	private static Map<String, ThreadPoolExecutor> excutorMap = new ConcurrentHashMap<String, ThreadPoolExecutor>();
	
	private static ThreadPoolExecutor  executor  = null;

	*//**
	 * 各省线程池初始化方法
	  *//*
	public void init() {
		//取各省参数
//		List<BillProvinceSwitch> list = busiDbService.selectBillProvinceSwitch();
//		for (int i = 0; i < list.size(); i++) {
//			BillProvinceSwitch provinceSwitch = list.get(i);
//			ThreadPoolExecutor executor = new ThreadPoolExecutor(provinceSwitch.getMinSize(), 
//					provinceSwitch.getMaxSize(), 
//					500, 
//					TimeUnit.MILLISECONDS,
//					new ArrayBlockingQueue<Runnable>(provinceSwitch.getQueueSize()),
//					new ThreadPoolExecutor.AbortPolicy());
//			excutorMap.put(provinceSwitch.getTeleCode(), executor);
//			logger.info("省线程池初始化teleCode="+provinceSwitch.getTeleCode()+"，min="+provinceSwitch.getMinSize()
//					+ ",max="+provinceSwitch.getMaxSize()+",queueSize="+provinceSwitch.getQueueSize());
//		}
		
		executor = new ThreadPoolExecutor(80,1000,500,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(1000),new ThreadPoolExecutor.AbortPolicy());
		
	}
	
	*//**
	 * 各省线程池参数更新方法
	  *//*
	public  void updateProvinceSwitch() {
		//取各省参数
//		List<BillProvinceSwitch> list = busiDbService.selectBillProvinceSwitch();
//		for (int i = 0; i < list.size(); i++) {
//			BillProvinceSwitch provinceSwitch = list.get(i);
//			ThreadPoolExecutor executor = excutorMap.get(provinceSwitch.getTeleCode());
//			if (executor == null) {	//省线程池不存在，创建新的
//				logger.info("省线程池更新重建！teleCode="+provinceSwitch.getTeleCode()+"，min="+provinceSwitch.getMinSize()
//						+ ",max="+provinceSwitch.getMaxSize()+",queueSize="+provinceSwitch.getQueueSize());
//				executor = new ThreadPoolExecutor(provinceSwitch.getMinSize(), 
//						provinceSwitch.getMaxSize(), 
//						500, 
//						TimeUnit.MILLISECONDS,
//						new ArrayBlockingQueue<Runnable>(provinceSwitch.getQueueSize()),
//						new ThreadPoolExecutor.AbortPolicy());
//				excutorMap.put(provinceSwitch.getTeleCode(), executor);
//			}else {//	更新省线程池
//				executor.setCorePoolSize(provinceSwitch.getMinSize());
//				executor.setMaximumPoolSize(provinceSwitch.getMaxSize());
//				logger.info("省线程池更新！teleCode="+provinceSwitch.getTeleCode()+"，min="+provinceSwitch.getMinSize()
//						+ ",max="+provinceSwitch.getMaxSize()+",queueSize="+provinceSwitch.getQueueSize());
//			}
//		}
	}
	
	*//**
	 * 任务加入省线程池
	 * @author JGQ
	 * @param teleCode 省代码
	 * @param runnable 请求集团SGW充值任务
	 * @param reqlog   订单参数信息
	  *//*
	public static String putReqestQueue(String teleCode, Runnable runnable, String reqlog){
//		ThreadPoolExecutor executor = excutorMap.get(teleCode);
//		if (executor == null) {
//			logger.error("没有找到省线程队列！teleCode = "+teleCode + reqlog);
//			return "未找到省线程队列！";
//		}
//		try {
//			executor.execute(runnable);
//			logger.debug("teleCode="+teleCode +"加入省线程池成功！活动线程数--->"
//					+ executor.getActiveCount() + "|| 队列实际数---->"
//					+ executor.getQueue().size() + "|| 最小线程数---->"
//					+ executor.getCorePoolSize() +"||最大线程数---->"
//					+ executor.getMaximumPoolSize()+ "||最大线程峰值---->"
//					+ executor.getLargestPoolSize()+"||"+reqlog);
//			logger.info("teleCode="+teleCode +"加入省线程池成功！活动线程数--->"
//					+ executor.getActiveCount() + "|| 队列实际数---->"
//					+ executor.getQueue().size() + "||最大线程峰值---->"
//					+ executor.getLargestPoolSize()+"||"+reqlog);
//		} catch (RejectedExecutionException e) {
//			logger.error("teleCode="+teleCode +"省资源忙！活动线程数--->"
//					+ executor.getActiveCount() + "|| 队列实际数---->"
//					+ executor.getQueue().size() + "|| 最小线程数---->"
//					+ executor.getCorePoolSize() +"||最大线程数---->"
//					+ executor.getMaximumPoolSize()+ "||最大线程峰值---->"
//					+ executor.getLargestPoolSize()+"||"+reqlog);
//			return "省资源忙！";
//		}
//		return null;
		
		
		return null;
	}

	
	public static String getRequestQueue(TMallRequestMessage tmallMsg){
		executor = new ThreadPoolExecutor(80,1000,500,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(1000),new ThreadPoolExecutor.AbortPolicy());
		
		executor.execute(tmallMsg);
						
		System.out.println("省线程池成功！活动线程数--->"
				+ executor.getActiveCount() + "|| 队列实际数---->"
				+ executor.getQueue().size() + "|| 最小线程数---->"
				+ executor.getCorePoolSize() +"||最大线程数---->"
				+ executor.getMaximumPoolSize()+ "||最大线程峰值---->"
				+ executor.getLargestPoolSize()+"||");
		return null;
	}
	
	
//	public IBusiDBService getBusiDbService() {
//		return busiDbService;
//	}
//
//	public void setBusiDbService(IBusiDBService busiDbService) {
//		this.busiDbService = busiDbService;
//	}
//	
	
	public static void main(String[] args){
		
	}
	
	
}*/