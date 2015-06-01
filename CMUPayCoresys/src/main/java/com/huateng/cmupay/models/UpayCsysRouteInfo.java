package com.huateng.cmupay.models;

import java.io.Serializable;

/** 
 * @author cmt  
 * @version 创建时间：2013-3-8 下午6:12:57 
 * 类说明 路由信息表
 * 对应的数据库表：UPAY_CSYS_ROUTE_INFO
 */
public class UpayCsysRouteInfo implements Serializable{
  
	private static final long serialVersionUID = 1L;

	//流水号
    private Long seqId;
    //机构编码
    private String orgId;
    //上下文根路径
    private String reqPath;
    //路由编号
    private String routeInfo;
    //请求协议
    private String protocol;
    //请求地址
    private String reqIp;
    //端口号
    private String reqPort;
    //MQ请求队列号
    private String reqMqId;
    //MQ接收队列号
    private String resMqId;
    //状态
    private String status;
    //权重
    private Integer weight;
    //备注
    private String misc;
    //前置机ip
    private String hostIp;
    //前置机端口
    private String hostPort;
    //前置机协议
    private String hostProtocol;
    //前置机路径
    private String hostPath;
    //超时时间
    private String timeout;
    public String getHostIp() {
		return hostIp;
	}
    //最后修改时间
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}

	public String getHostProtocol() {
		return hostProtocol;
	}

	public void setHostProtocol(String hostProtocol) {
		this.hostProtocol = hostProtocol;
	}

	public String getHostPath() {
		return hostPath;
	}

	public void setHostPath(String hostPath) {
		this.hostPath = hostPath;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	private String lastUpdOprid;

    private String lastUpdTime;
    //保留域1
    private String reserved1;
    //保留域2
    private String reserved2;
    //保留域3
    private String reserved3;
    //是否历史
    private String isHistory;
    //日有效日期范围
    private String dateScale;
    //月有效日期范围
    private String mouthScale;

    public String getReqPath() {
		return reqPath;
	}

	public void setReqPath(String reqPath) {
		this.reqPath = reqPath;
	}

	public String getIsHistory() {
		return isHistory;
	}

	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}

	public String getDateScale() {
		return dateScale;
	}

	public void setDateScale(String dateScale) {
		this.dateScale = dateScale;
	}

	public String getMouthScale() {
		return mouthScale;
	}

	public void setMouthScale(String mouthScale) {
		this.mouthScale = mouthScale;
	}

	public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

   

    public String getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(String routeInfo) {
        this.routeInfo = routeInfo;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getReqIp() {
        return reqIp;
    }

    public void setReqIp(String reqIp) {
        this.reqIp = reqIp;
    }

    public String getReqPort() {
        return reqPort;
    }

    public void setReqPort(String reqPort) {
        this.reqPort = reqPort;
    }

    public String getReqMqId() {
        return reqMqId;
    }

    public void setReqMqId(String reqMqId) {
        this.reqMqId = reqMqId;
    }

    public String getResMqId() {
        return resMqId;
    }

    public void setResMqId(String resMqId) {
        this.resMqId = resMqId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

   
    public Long getSeqId() {
		return seqId;
	}

	public void setSeqId(Long seqId) {
		this.seqId = seqId;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getLastUpdOprid() {
        return lastUpdOprid;
    }

    public void setLastUpdOprid(String lastUpdOprid) {
        this.lastUpdOprid = lastUpdOprid;
    }

    public String getLastUpdTime() {
        return lastUpdTime;
    }

    public void setLastUpdTime(String lastUpdTime) {
        this.lastUpdTime = lastUpdTime;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public String getReserved3() {
        return reserved3;
    }

    public void setReserved3(String reserved3) {
        this.reserved3 = reserved3;
    }
}