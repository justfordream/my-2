package com.huateng.cmupay.constant;

/**
 * 字典信息常量类
 * 
 * @author zeng.j
 */
public class DictConst {
	/**
	 * 字典主键
	 * 
	 * @author zeng.j
	 */
	public enum DictId {
		SettleDt("00001000", "清算日期      "), ChargeLimit("00002000", "缴费充值限额(分)"), InvoicePrtMax(
				"00003000", "发票打印最大条数 "), SubSignMax("00004000", "副号码签约最大个数"), RetryTimes(
				"00005000", "重发次数"), RevokeDaysMax("00006000", "允许退费最大天数  "), TestFlag(
				"00007000", "测试标记"), UnPrintCrm("00008000", "不允许打印发票的省"),
				InsertIntoHisTxnLogDay("00001300","流水表切换历史流水表天数"),TimeQuantum("00001400", "允许退费、冲正交易时间段");
		String value;
		String desc;

		private DictId(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public String getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}

	}

	/**
	 * 字典明细值主键
	 * 
	 * @author zeng.j
	 * 
	 */
	public enum CodeId {
		SettleDtCur("1", "当前清算日期      "), SettleDtPre("2", "前一清算日期"), ChargeLimit(
				"3", "缴费充值限额(分)"), InvoicePrtMax("4", "发票打印最大条数 "), SubSignMax(
				"5", "副号码签约最大个数"), RetryTimes("6", "重发次数"), RevokeDaysMax("7",
				"允许退费最大天数  "), TestFlag("8", "测试标记"), UnPrintCrm("9",
				"不允许打印发票的省"),ChangeHisTxnLog("13","流水表切换历史流水表天数"),TimeQuantum("14","允许退费、冲正交易时间段");
		String value;
		String desc;

		private CodeId(String value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		@Override
		public String toString() {
			return this.value.toString();
		}

		public String getValue() {
			return value;
		}

		public String getDesc() {
			return desc;
		}
	}
}
