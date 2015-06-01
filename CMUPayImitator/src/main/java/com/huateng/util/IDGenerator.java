package main.java.com.huateng.util;

/**
 * 产生唯一的ID
 * 
 * @author zeng.j
 * 
 */
public class IDGenerator {

	/**
	 * 取"000001"部分，6位交易代码
	 * 
	 * @param fileName
	 * 返回30位
	 * @return
	 */
	public static String getReqTransID(String fileName) {
		String reqTransID = "";
		String sequence = fileName.substring(0, 6);
		reqTransID = UUIDGenerator.randomNum(26);
		return sequence +	reqTransID;
	}

	/**
	 * 取"0001-"部分
	 * 
	 * @param fileName
	 *  格式如：BIAP32002_T0012011请求报文.xml
	 * @return
	 */
	public static String getTransIDO(String fileName) {
//		System.out.println("fileName ==== " + fileName);
		String sequence = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("_")+9);
		return  sequence + UUIDGenerator.randomNum(22);
	}

	/**
	 * 产生报文体中的 transactionId
	 * 
	 * @return
	 */
	public static String genBodyTransactionId() {
		return UUIDGenerator.generateUUID();
	}
	/**
	 * 产生报文体中的 transactionId
	 * 
	 * @return
	 */
	public static String getTransactionId() {
		StringBuffer sb = new StringBuffer();
		sb.append("10").append("200").append(UUIDGenerator.randomNum(30));
		return sb.toString().substring(0, 32);
	}
	/**
	 * crm返回报文头中落地方交易流水
	 * @return
	 */
	public static String genTransIDH() {
//		return UUIDGenerator.getDateAndUUID(30);
		return UUIDGenerator.randomNum(30);
	}
	
	/**
	 * 银行返回报文头中接收方交易流水
	 * @return
	 */
	public static String genRcvTransId(){
		return UUIDGenerator.generateUUID();
	}

	/**
	 * 生成sessionId
	 * @return
	 */
	public static String getSessionID() {
		String sessionID = "";
		sessionID = UUIDGenerator.randomNum(20);
		return sessionID;
	}

	/**
	 * 生成SubId(银行端)
	 * @return
	 */
	public static String getSubIdForBank(String systemId,String bankId) {
		StringBuffer sb = new StringBuffer();
		sb.append(systemId).append(bankId).append(UUIDGenerator.randomNum(30));
		return sb.toString().substring(0, 22);
	}

	/**
	 * 生成SubId(银行端)
	 * @return
	 */
	public static String getSubIdForBank(String bankId) {
		StringBuffer sb = new StringBuffer();
		sb.append("01").append(bankId).append(UUIDGenerator.randomNum(30));
		return sb.toString().substring(0, 22);
	}
	/**
	 * 生成OrderId
	 * @return
	 */
	public static String getOrderId() {
		StringBuffer sb = new StringBuffer();
		sb.append("FFFF").append(UUIDGenerator.generateUUID().substring(4, 32));
		return sb.toString();
	}
	public static void main(String[] ages) {
//		System.out.println(getSessionID());
		
		for (int i = 0; i < 100; i++) {
			System.out.println(IDGenerator.getOrderId());
			//System.out.println();
		}
	}

}
