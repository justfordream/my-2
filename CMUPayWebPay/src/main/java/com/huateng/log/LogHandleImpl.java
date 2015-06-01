package com.huateng.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.huateng.log.vo.LogVO;

/**
 * 全网监控日志采集接口
 * 
 * @author Gary
 * 
 */
@Component
public class LogHandleImpl implements LogHandle {
	private Logger logger = LoggerFactory.getLogger(getClass());

	

	private String getCurrentDatetime() {
		SimpleDateFormat localDate = new SimpleDateFormat("yyyy-MM-dd");
		
		SimpleDateFormat localDate2 = new SimpleDateFormat("HH:mm:ss");
		return localDate.format(new Date())+"T"+localDate2.format(new Date());
	}

	private String getCurrentDatetimeWithoutMis() {
		SimpleDateFormat localDate = new SimpleDateFormat("yyyyMMddHHmm");
		return localDate.format(new Date());
	}

	@Override
	public void info(boolean isClient,String code, String tradeCode,String operType,String provCode, String otherSystem, String desc) {
		LogVO logVO = new LogVO();
		logVO.setLogCode(this.getCurrentDatetimeWithoutMis());
		logVO.setOrderNo(tradeCode);
		logVO.setProvCode(provCode);
		if (isClient) {
			logVO.setInterType("客户端");
		} else {
			logVO.setInterType("服务端");
			
		}
		logVO.setOperType(operType);
		logVO.setInterNo(tradeCode);
		logVO.setAsys("UPSS");
		logVO.setZsys(otherSystem);
		logVO.setCallTime(this.getCurrentDatetime());
		logVO.setDealCode(code);
		logVO.setErrinfo(desc);
		logVO.setStatus("SUCCESS");
		logger.debug(logVO.format());
	}
}
