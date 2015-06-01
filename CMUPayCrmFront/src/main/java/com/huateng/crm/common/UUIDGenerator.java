package com.huateng.crm.common;

import java.util.UUID;

/**
 * <p>
 * 主键生成器 UUID含义是通用唯一识别码 (Universally Unique Identifier)，这 是一个软件建构的标准，也是被开源软件基金会
 * (Open Software Foundation, OSF) 的组织在分布式计算环境 (Distributed Computing
 * Environment, DCE) 领域的一部份。UUID
 * 的目的，是让分布式系统中的所有元素，都能有唯一的辨识资讯，而不需要透过中央控制端来做辨识资讯的指定。如此一来，每个人都可以建立不与其它人冲突的
 * UUID。在这样的情况下，就不需考虑数据库建立时的名称重复问题。目前最广泛应用的 UUID，即是微软的 Microsoft's Globally
 * Unique Identifiers (GUIDs)，而其他重要的应用，则有 Linux ext2/ext3 档案系统、LUKS
 * 加密分割区、GNOME、KDE、Mac OS X 等等。
 * </p>
 * 
 * @author Gary<br>
 *         <p>
 *         UUID由以下几部分的组合：<br>
 *         （1）当前日期和时间，UUID的第一个部分与时间有关，如果你在生成一个UUID之后，过几秒又生成一个UUID，则第一个部分不同，
 *         其余相同。<br>
 *         （2）时钟序列<br>
 *         （3）全局唯一的IEEE机器识别号，如果有网卡，从网卡MAC地址获得，没有网卡以其他方式获得。<br>
 *         </p>
 */
public class UUIDGenerator {
	/**
	 * 默认构造函数
	 */
	private UUIDGenerator() {

	}

	/**
	 * 获取自动生成的UUID字符串
	 * 
	 * @return
	 */
	public static String generateUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// 去掉"-"符号
		String temp = str.replaceAll("-", "");
		return temp;
	}

	public static void main(String[] args) {
		System.out.println(UUIDGenerator.generateUUID());
	}

}
