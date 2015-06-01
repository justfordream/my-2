/**
 * 
 */
package com.huateng.cmupay.constant;

/**
 * 日志类型常量
 * @author cmt
 *
 */
public enum LogTypeConstant {
	
	/**
	 * 供应商-登录
	 */
	MerchantLogin("merchant.login","供应商-登录"),
	/**
	 * 供应商-新增
	 */
	MerchantAdd("merchant.add","供应商-新增"),
	/**
	 * 供应商-修改
	 */
	MerchantModify("merchant.modify","供应商-修改"),	
	/**
	 * 供应商-删除
	 */
	MerchantDel("merchant.del","供应商-删除"),
	/**
	 * 供应商-密码修改
	 */
	MerchantModifyPassword("merchant.modifyPassword","供应商-密码修改"),
	/**
	 * 供应商-密码重置
	 */
	MerchantResetPassword("merchant.resetPassword","供应商-密码重置"),
	/**
	 * 机构-新增
	 */
	ChannelAdd("channel.add","机构-新增"),
	/**
	 * 机构-修改
	 */
	ChannelModify("channel.modify","机构-修改"),
	/**
	 * 机构-删除
	 */
	ChannelDel("channel.del","机构-删除"),
	/**
	 * 操作员-登录
	 */
	UserLogin("user.login","操作员-登录"),
	/**
	 * 操作员-新增
	 */
	UserAdd("user.add","操作员-新增"),
	/**
	 * 操作员-修改
	 */
	UserModify("user.modify","操作员-修改"),
	/**
	 * 操作员-删除
	 */

	/**
	 * 客户
	 */
	CustomerAdd("customer.add","客户-增加"),
	CustomerModify("customer.modify","客户-修改"),
	CustomerDel("customer.delete","客户-删除"),

	UserDel("user.del","操作员-删除"),
	/**
	 * 商品
	 */
	GoodsAdd("goods.add","商品-增加"),
	GoodsModify("goods.modify","商品-修改"),
	GoodsDel("goods.delete","商品-删除"),
	GoodsDictAdd("goodsDict.add","商品字典-增加"),
	GoodsDictModify("goodsDict.modify","商品字典-增加"),
	GoodsDictDel("goodsDict.delete","商品字典-删除"),
	GoodsPriceAdd("goodsPrice.add","商品价格-增加"),
	GoodsPriceModify("goodsPrice.modify","商品价格-修改"),
	GoodsPriceAudit("goodsPrice.audit","商品价格-审核"),
	/**库存*/
	GoodsStockAdd("goodsStock.add","商品库存-增加"),
	GoodsStockModify("goodsStock.modify","商品库存-修改"),
	
	/**
	 * 商品媒介
	 */
	GoodsExtMediaAdd("GoodsExtMedia.add","商品媒介-添加"),
	GoodsExtMediaModify("GoodsExtMedia.modify","商品媒介-修改"),
	GoodsExtMediaDel("GoodsExtMedia.del","商品媒介-删除"),
	
	/**
	 * 商品品牌
	 */
	GoodsBrandAdd("goodsBrand.add","品牌-添加"),
	GoodsBrandModify("goodsBrand.modify","品牌-修改"),
	GoodsBrandDel("goodsBrand.del","品牌-删除"),
	GoodsBrandUnDel("goodsBrand.undel","品牌-有效"),
	
	/**
	 * 商品类别
	 */
	GoodsCatAdd("goodsCat.add","类别-添加"),
	GoodsCatModify("goodsCat.modify","类别-修改"),
	GoodsCatDel("goodsCat.del","类别-删除"),
	
	/**
	 * 商品属性
	 */
	GoodsPropAdd("goodsProp.add","属性-添加"),
	GoodsPropModify("goodsProp.modify","属性-修改"),
	GoodsPropDel("goodsProp.del","属性-删除"),
	/**
	 * 商品属性
	 */
	GoodsSellTrue("GgoodsSell.true","属性-上架"),
	GoodsSellFalse("goodsSell.false","属性-下架"),
	GoodsSellFlag("GgoodsSell.Flag","属性-上下架"),

	
	/**
	 * 统计信息
	 */
	GoodsViewStatAdd("goodsViewStatAdd.add","商品统计-添加"),
	GoodsViewStatModify("goodsViewStatAdd.modify","商品统计-修改"),
	GoodsViewStatDel("goodsViewStatAdd.del","商品统计-删除"),

	/**
	 * 商品客户评论
	 */
	GoodsCustomerCommentAdd("customerComment.add","客户评论-添加"),
	
	/**
	 * 商品排行
	 */
	GoodsRankAdd("goodsRank.add","商品排行-添加"),
	GoodsRankModify("goodsRank.modify","商品排行--修改"),
	GoodsRankDel("goodsRank.del","商品排行--删除"),
	
	/**
	 * 订单
	 */
	OrderAdd("order.add","订单-增加"),
	OrderModify("order.modify","订单-修改"),
	OrderDel("order.delete","订单-删除"),
	Create("order.add", "订单-下单"), 
	Confirm("order.confirm", "订单-确认"),
	Pay("order.pay","订单-支付"),
	Delivery("order.delivery","订单-发货"),
	Finish("order.finish","订单-完成"),
	UserCancel("order.userCancel","订单-用户取消"),
	SystemCancel("order.systemCancel","订单-系统取消") ,
	
	txnFlowAdd("txnFlow.add","流水-增加"),
	txnFlowModify("txnFlow.modify","流水-修改"),
	
	/**
	 * 订单详情
	 */
	OrderDetailAdd("orderDetail.add","订单详情-增加"),
	OrderDetailModify("orderDetail.modify","订单详情-修改"),
	OrderDetailDel("orderDetail.delete","订单详情-删除"),
	
	/**
	 * 订单会员
	 */
	OrderExtCustomerAdd("orderExtCustomer.add","订单会员-增加"),
	OrderExtCustomerModify("orderExtCustomer.modify","订单会员-修改"),
	OrderExtCustomerDel("orderExtCustomer.delete","订单会员-删除"),
	
	/**
	 * 订单票号
	 */
	OrderDetailTicketAdd("orderDetailTicket.add","订单票号-增加"),
	OrderDetailTicketModify("orderDetailTicket.modify","订单票号-修改"),
	OrderDetailTicketDel("orderDetailTicket.delete","订单票号-删除"),
	
	/**
	 * 报表
	 */
	ReportAdd("report.add","报表-增加"),
	ReportModify("report.modify","报表-修改"),
	ReportDel("report.delete","报表-删除"),
	/**
	 * 日志
	 */
	LogSystemClear("logSystem.delete","日志-清空"),
	
	/**
	 * 角色
	 */
	SystemRoleAdd("systemRole.add","角色-添加"),
	SystemRoleModify("systemRole.modify","角色-修改"),
	SystemRoleDel("systemRole.del","角色-删除"),
	
	/**
	 * 角色菜单
	 */
	SystemRoleMenuModify("systemRoleMenu.modify","角色菜单-修改"),
	
	
	/**
	 * 操作员角色
	 */
	SystemUserRoleModify("systemUserRole.modify","操作员角色-修改"),
	
	/**
	 * 电影院 
	 */
	DictCinemalAdd("dictCinemal.add","电影院-添加"),
	DictCinemalModify("dictCinemal.modify","电影院-修改"),
	DictCinemalDel("dictCimemal.del","电影院-删除"),
	

	/**
	 * 商品 影院
	 */
	GoodsCinemaModify("goodsCinema.modify","商品影院-修改"),

	/**
	 * 影片
	 */
	FilmAdd("filmBasic.add","影片-添加"),
	FilmModify("filmBasic.modify","影片-修改"),
	FilmDel("filmBasic.del","影片-删除"),

	/**
	 * 影片
	 */
	FilmExtMeadiaAdd("filmExtMedia.add","影片-添加"),
	FilmExtMeadiaModify("filmExtMedia.modify","影片-修改"),
	FilmExtMeadiaDel("filmExtMedia.del","影片-删除"),

	/**
	 * 新闻
	 */
	NewsAdd("channelNewsBasic.add","新闻-增加"),
	NewsModif("channelNewsBasic.modify","新闻-修改"),
	NewsDel("channelNewsBasic.del","新闻-删除"),
	
	/**
	 * 商品定价
	 */
	GoodsPricing("goodsPricing.pring","商品-定价"),
	
	/**
	 * 大背景
	 */
	BackGroundAdd("backGround.add","背景图片-添加"),
	BackGroundModify("backGround.modify","背景图片-修改"),
	BackGroundDel("backGround.del","背景图片-删除"),
	
	/**
	 * 广告管理
	 */
	AdAdd("channelAdBasic.add","广告-添加"),
	AdModify("channelAdBasic.modify","广告-修改"),
	AdDel("channelAdBasic.del","广告-删除"),
	
	FilmCommentAdd("filmComment.add","电影评论-添加"),
	FilmCommentDel("filmComment.del","电影评论-删除")
	;
	
	String name;
	String value;
	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}

	LogTypeConstant(String name,String value){
		this.name=name;
		this.value=value;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static String getValue(String name){
		LogTypeConstant [] types=LogTypeConstant.values();
		for (LogTypeConstant logTypeConstant : types) {
			if (logTypeConstant.name.equals(name))
				return logTypeConstant.value;
		}
		return "";
	}
}
