package com.huateng.core.common;



import java.util.concurrent.Semaphore;
import javax.jms.Message;



/**
 * @author cmt
 *
 */
public class RecvMessageBean {
	private Semaphore recvsemap = new Semaphore(0);

	private Message recvmsg;

	public Semaphore getRecvsemap() {
		return recvsemap;
	}

	public void setRecvsemap(Semaphore recvsemap) {
		this.recvsemap = recvsemap;
	}

	public Message getRecvmsg() {
		return recvmsg;
	}

	public void setRecvmsg(Message recvmsg) {
		this.recvmsg = recvmsg;
	}

}
