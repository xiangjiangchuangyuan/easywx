package com.xjcy.easywx.post;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xjcy.easywx.AbstractPOST;
import com.xjcy.easywx.config.WXConfig;
import com.xjcy.util.JSONUtils;
import com.xjcy.util.StringUtils;
import com.xjcy.util.XMLUtils;
import com.xjcy.util.http.WebClient;

public class DefaultPOSTImpl extends AbstractPOST {
	private static final Logger logger = Logger.getLogger(DefaultPOSTImpl.class);

	private static final String URL_CREATEQRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
	private static final String URL_WXACODE = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s";
	private static final String URL_CUSTOM_SEND = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	private static final String URL_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
	private static final String URL_SEND_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	private static final String URL_CREATE_UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static final String URL_REFUND_ORDER = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	private static final String POST_SCENE_ID = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %s}}}";
	private static final String POST_SCENE_STR = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"%s\"}}}";
	private static final String POST_WXACODE_STR = "{\"scene\": \"%s\", \"path\": \"%s\", \"width\": 430}";
	private static final String POST_SEND_TEXT = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";

	public DefaultPOSTImpl(WXConfig wxConfig) {
		this._appId = wxConfig.getAppId();
		this._appSecret = wxConfig.getAppSecret();
		this._mchId = wxConfig.getMchId();
		this._key = wxConfig.getKey();
		this._notifyUrl = wxConfig.getNotifyUrl();
		this._sslSocket = wxConfig.getSslSocket();
		this._isMiniProgram = wxConfig.isMiniProgram();
	}

	@Override
	public String createQrcode(Integer sceneId) {
		String postStr = String.format(POST_SCENE_ID, sceneId);
		String json = WebClient.uploadString(String.format(URL_CREATEQRCODE, getAccessToken()), postStr);
		return isSuccessful(json) ? json : null;
	}

	@Override
	public String createQrcode(String sceneStr) {
		String postStr = String.format(POST_SCENE_STR, sceneStr);
		String json = WebClient.uploadString(String.format(URL_CREATEQRCODE, getAccessToken()), postStr);
		return isSuccessful(json) ? json : null;
	}

	@Override
	public byte[] createWxacode(String sceneStr, String path) {
		byte[] postData = String.format(POST_WXACODE_STR, sceneStr, path).getBytes();
		return WebClient.uploadData(String.format(URL_WXACODE, getAccessToken()), postData);
	}

	@Override
	public JsapiTicket signRequestUrl(String url) {
		String nonceStr = getRandamStr();
		long time = getTimestamp();
		String jsapiTicket = getJsapiTicket();
		StringBuffer signStr = new StringBuffer();
		signStr.append("jsapi_ticket=").append(jsapiTicket);
		signStr.append("&noncestr=").append(nonceStr);
		signStr.append("&timestamp=").append(time);
		signStr.append("&url=").append(url);
		return new JsapiTicket(_appId, nonceStr, time, signStr.toString());
	}

	@Override
	public boolean sendText(String openId, String text) {
		String postStr = String.format(POST_SEND_TEXT, openId, text);
		String json = WebClient.uploadString(String.format(URL_CUSTOM_SEND, getAccessToken()), postStr);
		return isSuccessful(json);
	}

	@Override
	public boolean createMenu(String json) {
		String result = WebClient.uploadString(String.format(URL_MENU_CREATE, getAccessToken()), json);
		return JSONUtils.getInteger(result, "errcode") == 0;
	}

	@Override
	public boolean sendTemplate(String json) {
		String result = WebClient.uploadString(String.format(URL_SEND_TEMPLATE, getAccessToken()), json);
		return isSuccessful(result);
	}

	@Override
	public Map<String, Object> createUnifiedOrder(UnifiedOrder order) {
		if (_mchId == null || _key == null || _notifyUrl == null) {
			logger.error("请调用WXUtil.registerPay，注册微信支付必要参数");
			return null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("appid", _appId);// 应用ID
		map.put("mch_id", _mchId);// 商户号
		// map.put("device_info", "");//设备号
		map.put("nonce_str", getRandamStr());// 随机字符串
		if (order.body != null && order.body.length() > 64)
			map.put("body", order.body.substring(0, 64));// 商品描述
		else
			map.put("body", order.body);// 商品描述
		// map.put("detail", "");//商品详情
		// map.put("attach", "");//附加数据
		map.put("out_trade_no", order.out_trade_no);// 商户订单号
		// map.put("fee_type", "");//货币类型
		map.put("total_fee", order.total_fee);// 总金额(单位分)
		map.put("spbill_create_ip", order.spbill_create_ip);// 客户端IP
		// map.put("time_start", "");//交易起始时间
		// map.put("time_expire", "");//交易结束时间
		// map.put("goods_tag", "");//商品标记
		map.put("notify_url", _notifyUrl);// 通知地址
		if (StringUtils.isNotBlank(order.sub_mch_id))
			map.put("sub_mch_id", order.sub_mch_id);// 子商户订单号

		if (StringUtils.isEmpty(order.openid))
			map.put("trade_type", "APP");// 交易类型
		else {
			map.put("trade_type", "JSAPI");// 交易类型
			map.put("openid", order.openid);
		}

		map.put("sign", getSign(map));// 签名
		// map.put("limit_pay", "");// 指定支付方式

		// post调取方法
		String return_xml = WebClient.uploadString(URL_CREATE_UNIFIEDORDER, XMLUtils.toXML(map));
		Map<String, Object> result = XMLUtils.doXMLParse(return_xml);
		String returnCode = getValue(result, "return_code");
		String resultCode = getValue(result, "result_code");
		if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
			String prepayid = getValue(result, "prepay_id");
			Map<String, Object> maplast = new HashMap<>();
			if (StringUtils.isEmpty(order.openid)) {
				maplast.put("appid", _appId);
				maplast.put("noncestr", getRandamStr());
				maplast.put("partnerid", _mchId);
				maplast.put("prepayid", prepayid);
				maplast.put("timestamp", getTimestamp());
				maplast.put("package", "Sign=WXPay");
				maplast.put("sign", getSign(maplast));
			} else {
				maplast.put("appId", _appId);
				maplast.put("nonceStr", getRandamStr());
				maplast.put("package", "prepay_id=" + prepayid);
				maplast.put("timeStamp", getTimestamp());
				maplast.put("signType", "MD5");
				maplast.put("paySign", getSign(maplast));
			}
			return maplast;
		}
		logger.error("Create faild:" + result.toString());
		return null;
	}

	@Override
	public RefundResult refundOrder(String out_trade_no, String refund_fee, String total_fee) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("appid", _appId);// 应用ID
			map.put("mch_id", _mchId);// 商户号
			map.put("nonce_str", getRandamStr());// 随机字符串
			map.put("out_trade_no", out_trade_no);// 商户订单号
			map.put("out_refund_no", out_trade_no);// 商户退款单号
			map.put("total_fee", total_fee); // 订单总金额
			map.put("refund_fee", refund_fee); // 退款总金额
			map.put("notify_url", this._notifyUrl); // 退款通知

			// 增加签名
			map.put("sign", getSign(map));// 签名

			// post调取方法
			byte[] data = XMLUtils.toXML(map).getBytes();
			String return_xml = WebClient.uploadData(URL_REFUND_ORDER, data, this._sslSocket);
			logger.debug("申请退款结果 => " + return_xml);
			Map<String, Object> result = XMLUtils.doXMLParse(return_xml);
			String returnCode = getValue(result, "return_code");
			String resultCode = getValue(result, "result_code");
			if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
				RefundResult refund = new RefundResult();
				refund.appid = getValue(result, "appid");
				refund.mch_id = getValue(result, "mch_id");
				refund.transaction_id = getValue(result, "transaction_id"); // 微信订单号
				refund.out_trade_no = getValue(result, "out_trade_no");
				refund.refund_id = getValue(result, "refund_id"); // 微信退款单号
				refund.refund_fee = getValue(result, "refund_fee");// 退款金额
				refund.total_fee = getValue(result, "total_fee"); // 订单总金额
				refund.cash_fee = getValue(result, "cash_fee"); // 现金支付金额
				refund.cash_refund_fee = getValue(result, "cash_refund_fee"); // 现金退款金额
				return refund;
			}
			logger.error("Refund faild:" + result.toString());
		} catch (Exception e) {
			logger.error("申请退款失败", e);
		}
		return null;
	}
}
