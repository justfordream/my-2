package com.huateng.log.vo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("data")
public class Data {
	@XStreamImplicit
	private List<Rcd> rcd;

	public List<Rcd> getRcd() {
		return rcd;
	}

	public void setRcd(List<Rcd> rcd) {
		this.rcd = rcd;
	}

}
