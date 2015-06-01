package com.huateng.quartz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.log.XmlUtil;
import com.huateng.log.vo.Bomc;
import com.huateng.log.vo.Data;
import com.huateng.log.vo.Rcd;

/**
 * 日志工作类
 * 
 * @author Gary
 * 
 */
public class LogJob {
	private Logger logger = LoggerFactory.getLogger(getClass());
	//public static String FILE_TYPE = "UPAY1";
	public static String FILE_TYPE = "UPAY";
	//public static String LOG_PROVINCE = "0001";
	public static String LOG_PROVINCE = "000";
	//public static String LOG_INTVAL = "15MI";
	public static String ENCODING = "UTF-8";
	private String logPath;
	private String logXmlPath;
	/**
	 * 日志文件生成频率(单位:分钟)
	 * */
	private int logGenerateInterval;	
	/**
	 * 日志文件编号(取值范围:A~P)
	 * */
	private String logFileNo;

	/**
	 * 写日志
	 */
	public void doLog() {
		logger.debug("......定时器启动，开始进行日志记录......");
		Bomc bomc = this.readText(logPath);
		if (bomc == null) {
			logger.debug("......日志文件[" + logPath + "]不存在......");
			return;
		}
		File xmlPath = new File(logXmlPath);
		if (!xmlPath.exists()) {
			logger.debug("......XML日志路径[" + logXmlPath + "]不存在......");
			return;
		}
		bomc.setCreatetime(this.getDateTime());
		String xml = XmlUtil.toXml(bomc, Bomc.class, true);
		logger.debug("......转换好的日志内容:" + xml);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(ENCODING);// 设置XML文件的编码格式
		XMLWriter writer = null;
		BufferedWriter bw = null;
		try {
			logger.debug("......开始写文件[" + logXmlPath + this.getXmlFileName() + "]......");
			Document doc = DocumentHelper.parseText(xml);
			writer = new XMLWriter(new FileWriter(logXmlPath + this.getXmlFileName()), format);
			writer.write(doc);
			logger.debug("......文件[" + logXmlPath + this.getXmlFileName() + "]写入成功!");
			/*
			 * 成功之后清空文件
			 */
			bw = new BufferedWriter(new FileWriter(logPath));
			bw.write("");
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.debug("......文件[" + logXmlPath + this.getXmlFileName() + "]写入失败!");
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("......文件[" + logXmlPath + this.getXmlFileName() + "]写入失败!");
		} finally {
			logger.debug("......文件[" + logXmlPath + this.getXmlFileName() + "]写入完成!");
			try {
				if (writer != null) {
					writer.close();
				}
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("......定时器停止，日志记录结束......");
	}

	/**
	 * 
	 * @param filePathAndName
	 * @return
	 */
	public Bomc readText(String filePathAndName) {
		logger.debug("......读取日志文件[" + filePathAndName + "]......");
		Bomc bomc = new Bomc();
		File file = new File(filePathAndName);
		if (!file.exists()) {
			return null;
		}

		Rcd rcd = null;
		List<Rcd> listRcd = new ArrayList<Rcd>();

		FileInputStream fs = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fs = new FileInputStream(filePathAndName);
			isr = new InputStreamReader(fs, ENCODING);
			br = new BufferedReader(isr);
			String data = "";
			int seq = 0;
			while ((data = br.readLine()) != null) {
				seq++;
				logger.debug("......读取第[" + seq + "]行......");
				rcd = new Rcd();
				List<String> strList = this.splitToList(data, "#");
				this.assemlyRcd(seq, rcd, strList);
				listRcd.add(rcd);

			}
			logger.debug("......共[" + seq + "]行......");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (fs != null) {
					fs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		bomc.setProvince(LOG_PROVINCE);
		bomc.setType(FILE_TYPE+logFileNo);
		bomc.setSum(listRcd.size());
		bomc.setBegintime(this.getBeginTime());
		bomc.setEndtime(this.getEndTime());
		Data data = new Data();
		data.setRcd(listRcd);
		bomc.setData(data);
		return bomc;
	}

	private void assemlyRcd(int seq, Rcd rcd, List<String> strList) {
		int length = strList.size();
		rcd.setSeq(String.valueOf(seq));
		switch (length) {
		case 1:
			rcd.setLogcode(strList.get(0));//
			break;
		case 2:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			break;
		case 3:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			break;
		case 4:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			break;
		case 5:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			break;
		case 6:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			rcd.setAsys(strList.get(5));//
			break;
		case 7:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			rcd.setAsys(strList.get(5));//
			rcd.setZsys(strList.get(6));//
		case 8:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			rcd.setAsys(strList.get(5));//
			rcd.setZsys(strList.get(6));//
			rcd.setProvcode(strList.get(7));//
			break;
		case 9:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			rcd.setAsys(strList.get(5));//
			rcd.setZsys(strList.get(6));//
			rcd.setProvcode(strList.get(7));//
			rcd.setCalltime(strList.get(8));//
			break;
		case 10:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			rcd.setAsys(strList.get(5));//
			rcd.setZsys(strList.get(6));//
			rcd.setProvcode(strList.get(7));//
			rcd.setCalltime(strList.get(8));//
			rcd.setDealcode(strList.get(9));//
			break;
		case 11:
			rcd.setLogcode(strList.get(0));//
			rcd.setOrderno(strList.get(1));//
			rcd.setIntertype(strList.get(2));//
			rcd.setOpertype(strList.get(3));//
			rcd.setInterno(strList.get(4));//
			rcd.setAsys(strList.get(5));//
			rcd.setZsys(strList.get(6));//
			rcd.setProvcode(strList.get(7));//
			rcd.setCalltime(strList.get(8));//
			rcd.setDealcode(strList.get(9));//
			rcd.setErrinfo(strList.get(10));//
			break;
		default:
			break;
		}

	}

	/**
	 * 计算XML文件名称 E2ELG_100_15MI_20121021_001_000.xml
	 * 
	 * @return
	 */
	private String getXmlFileName() {
		String YYYY_MM_DD = this.getDateForLong();
		String fileSeq = this.getFileSeq();
//		String fileName = FILE_TYPE + "_" + LOG_PROVINCE + "_" + LOG_INTVAL + "_" + YYYY_MM_DD + "_" + fileSeq
//				+ "_000.xml";
		String fileName = FILE_TYPE +logFileNo+ "_" + LOG_PROVINCE + "_0" + logGenerateInterval+"MI" + "_" + YYYY_MM_DD + "_" + fileSeq
				+ "_000.xml";
		return fileName;
	}

	/**
	 * 获取文件序列号 3位数字；
	 * 对于15分钟文件，内容为当日0点至0点15分统计数据的文件作为当日001号文件；之后步长为1，序列号依次递增，序列号范围：001-096；
	 * 
	 * @return
	 */
	public String getFileSeq() {
		int hour = this.getHour();
		int minute = hour * 60 + this.getMinute();
//		long seq = (minute / 15) + 1;
		long seq = (minute / logGenerateInterval) + 1;
		return String.format("%03d", seq);
	}

	/**
	 * 获得统计开始时间 00-15 15-30 30-45 45-00
	 * 
	 * @return
	 */
	public String getBeginTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCountDate());
		int minute = this.getMinute();
		
		int t = 60/logGenerateInterval;
		for(int i=0;i<t;i++){	
			if(minute <logGenerateInterval*(i+1) && minute>=logGenerateInterval*i){
				if(i==0){
					minute = 00;
				}else{
					minute = logGenerateInterval*i;
				}					 
				break;
			} 
		 }
		
//		if (minute < 15 && minute >= 0) {
//			minute = 00;
//		} else if (minute < 30 && minute >= 15) {
//			minute = 15;
//		} else if (minute < 45 && minute >= 30) {
//			minute = 30;
//		} else if (minute >= 45) {
//			minute = 45;
//		}
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 00);
		return this.getDateFormat(cal.getTime());
	}

	/**
	 * 获得统计结束时间
	 * 
	 * @return
	 */
	public String getEndTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCountDate());
		int minute = this.getMinute();
		int hour = this.getHour();
		
		int t = 60/logGenerateInterval;
		for(int i=0;i<t;i++){	
			if(minute <logGenerateInterval*(i+1) && minute>=logGenerateInterval*i){
			   if(i==t-1){
				   minute = 00;
				   hour = hour + 1;
				}else{
					minute = logGenerateInterval*(i+1);
				}					 
				break;
			} 
		 }
		
//		if (minute < 15 && minute >= 0) {
//			minute = 15;
//		} else if (minute < 30 && minute >= 15) {
//			minute = 30;
//		} else if (minute < 45 && minute >= 30) {
//			minute = 45;
//		} else if (minute >= 45) {
//			minute = 00;
//			hour = hour + 1;
//		}
		
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 00);
		return this.getDateFormat(cal.getTime());
	}

	public Date getCountDate() {
		Calendar cal = Calendar.getInstance();
		// cal.set(Calendar.HOUR_OF_DAY, 1);
		// cal.set(Calendar.MINUTE, 00);
//		cal.add(Calendar.MINUTE, -15);
		cal.add(Calendar.MINUTE, -logGenerateInterval);
		return cal.getTime();
	}

	public int getHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCountDate());
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinute() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getCountDate());
		return cal.get(Calendar.MINUTE);
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getLogXmlPath() {
		return logXmlPath;
	}

	public void setLogXmlPath(String logXmlPath) {
		this.logXmlPath = logXmlPath;
	}

	/**
	 * 功能：取应用服务器日期并以"yyyy-MM-dd HH:mm:ss"格式返回
	 */
	public String getDateTime() {
		return getCurrentDateByFormat("yyyy-MM-dd") + "T" + getCurrentDateByFormat("HH:mm:ss");
	}

	public String getDateFormat(Date date) {
		return (new SimpleDateFormat("yyyy-MM-dd").format(date)) + "T"
				+ (new SimpleDateFormat("HH:mm:ss").format(date));
	}

	/**
	 * 功能：取当前服务器时间并以参数指定的格式返回
	 */
	public String getCurrentDateByFormat(String formatStr) {
		long currentTime = System.currentTimeMillis();
		java.util.Date date = new java.util.Date(currentTime);
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(formatStr);
		return formatter.format(date);
	}

	/**
	 * 功能：取应用服务器时间并以"yyyyMMdd"格式返回
	 */
	public String getDateForLong() {
		return getCurrentDateByFormat("yyyyMMdd");
	}

	/**
	 * 
	 * @param str
	 * @param separatorChar
	 * @return
	 */
	public List<String> splitToList(String str, String separatorChar) {
		//String[] sArray = StringUtils.split(str, separatorChar);
		String[] sArray = str.split(separatorChar);
		List<String> result = new ArrayList<String>(11);
		for (String s : sArray) {
			result.add(s);
		}
		return result;
	}

	public int getLogGenerateInterval() {
		return logGenerateInterval;
	}

	public void setLogGenerateInterval(int logGenerateInterval) {
		this.logGenerateInterval = logGenerateInterval;
	}

	public String getLogFileNo() {
		return logFileNo;
	}

	public void setLogFileNo(String logFileNo) {
		this.logFileNo = logFileNo;
	}	
}
