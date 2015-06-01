package com.huateng.core.common;

public class CoreCommon {
////	private static ResourceBundle bundle = ResourceBundle
////			.getBundle("property/jms");
////	
////	private static ResourceBundle ccbBundle = ResourceBundle
////			.getBundle("property/ccb");
////	
//	private static final String ALL = "all";
//
//	@Value("${CHECK_STATUS}")
//	private  String checkStatus;
//	
//	@Value("${BANK_CHECK_STATUS}")
//	private  String bankCheckStatus;
//	
//	@Value("${HTTP_TIME_OUT}")
//	private  String httpTimeOut;
//	
//	@Value("${CCB.B2C.PAY.BANKFRONT_TEST}")
//	private  String bankfrontTest;
//	
//	@Value("${PROVICE_LIST}")
//	private  String proviceList;
//	
//	@Value("${BANK_OPEN_LIST}")
//	private  String bankOpenList;
//	
//	private static CoreCommon coreCommon=null;
//	
//	public static CoreCommon getCoreCommon(){
//		if(coreCommon==null){
//			coreCommon=new CoreCommon();
//		}
//		
//		return coreCommon;
//	}
//	/**
//	 * 省端获取验签开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public  String getCheckSwitch() {
//		System.out.println("==============checkStatus=========================="+checkStatus);
//		System.out.println("==============bankCheckStatus=========================="+bankCheckStatus);
//		System.out.println("==============httpTimeOut=========================="+httpTimeOut);
//		System.out.println("==============bankfrontTest=========================="+bankfrontTest);
//		System.out.println("==============proviceList=========================="+proviceList);
//		System.out.println("==============bankOpenList=========================="+bankOpenList);
//		return checkStatus;//bundle.getString("CHECK_STATUS");
//	}
//
//	/**
//	 * 银行端获取验签开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public  String getBankCheckSwitch() {
//		System.out.println("=========bankCheckStatus========="+bankCheckStatus);
//		return bankCheckStatus;//bundle.getString("BANK_CHECK_STATUS");
//	}
//
//	/**
//	 * 银行端获取验签开关(open:开,close:关)
//	 * 
//	 * @return
//	 */
//	public  int getHttpTimeOut() {
//		System.out.println("=========httpTimeOut========="+httpTimeOut);
//		String timeOut=httpTimeOut;//bundle.getString("HTTP_TIME_OUT");
//		return Integer.parseInt(timeOut);
//	}
//
////	/**
////	 * 获取建设银行的密钥
////	 * 
////	 * @return
////	 */
////	public static String getCCBPayKey() {
////		return ccbBundle.getString("CCB.B2C.PAY.KEY");
////	}
//
//	/**
//	 * 获取测试银行地址
//	 * 
//	 * @return
//	 */
//	public  String getTestPayUrl() {
//		System.out.println("=========bankfrontTest========="+bankfrontTest);
//		return bankfrontTest;//ccbBundle.getString("CCB.B2C.PAY.BANKFRONT_TEST");
//	}
//
//	/**
//	 * 
//	 * 根据省份判断是否加密、解密 (all 全部打开,部分打开 4位省份代码用逗号隔开)
//	 * 
//	 * @return true 需要加密
//	 */
//	public  boolean isCheckProArea(String sysProCode) {
//		System.out.println("=========proviceList========="+proviceList);
//		String proAreas = proviceList;//bundle.getString("PROVICE_LIST");
//		if (StringUtils.equals(proAreas, ALL)) {
//			return true;
//		} else {
//			List<String> proAreaList = new ArrayList<String>();
//			String[] proArea = proAreas.split(",");
//			boolean isCheckCode = false;
//			if (proArea.length == 1) {
//				proAreaList.add(proAreas);
//			} else {
//				for (String proCode : proArea) {
//					proAreaList.add(proCode);
//				}
//			}
//			for (String sysCode : proAreaList) {
//				if (StringUtils.equals(sysProCode, sysCode)) {
//					isCheckCode = true;
//					break;
//				}
//			}
//			return isCheckCode;
//		}
//	}
//
//	/**
//	 * 
//	 * 根据省份判断是否加密、解密 (all 全部打开,部分打开 4位省份代码用逗号隔开)
//	 * 
//	 * @return true 需要加密
//	 */
//	public  boolean isCheckProBank(String sysProCode) {
//		System.out.println("=========bankOpenList========="+bankOpenList);
//		String proBanks = bankOpenList;// bundle.getString("BANK_OPEN_LIST");
//		if (StringUtils.equals(proBanks, ALL)) {
//			return true;
//		} else {
//			List<String> proBankList = new ArrayList<String>();
//			String[] proBank = proBanks.split(",");
//			boolean isCheckCode = false;
//			if (proBank.length == 1) {
//				proBankList.add(proBanks);
//			} else {
//				for (String proCode : proBank) {
//					proBankList.add(proCode);
//				}
//			}
//			for (String sysCode : proBankList) {
//				if (StringUtils.equals(sysProCode, sysCode)) {
//					isCheckCode = true;
//					break;
//				}
//			}
//			return isCheckCode;
//		}
//	}

}
