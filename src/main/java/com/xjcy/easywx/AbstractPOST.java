package com.xjcy.easywx;

import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import com.xjcy.easywx.post.JsapiTicket;
import com.xjcy.easywx.post.PayResult;
import com.xjcy.easywx.post.RefundResult;
import com.xjcy.easywx.post.UnifiedOrder;
import com.xjcy.util.MD5;
import com.xjcy.util.ObjectUtils;
import com.xjcy.util.StringUtils;
import com.xjcy.util.XMLUtils;

public abstract class AbstractPOST {
	private static final Logger logger = Logger.getLogger(AbstractPOST.class);

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;
	protected boolean _isMiniProgram;
	protected SSLSocketFactory _sslSocket;

	public abstract String createQrcode(Integer sceneId);

	public abstract String createQrcode(String sceneStr);

	// 网页调用时签名url
	public abstract JsapiTicket signRequestUrl(String url);

	// 客服返回文本消息
	public abstract boolean sendText(String openId, String text);

	// 创建菜单
	public abstract boolean createMenu(String json);

	// 发送模板消息
	public abstract boolean sendTemplate(String json);

	// 创建预付单接口
	public abstract Map<String, Object> createUnifiedOrder(UnifiedOrder order);

	// 申请退款
	public abstract RefundResult refundOrder(String out_trade_no, String refund_fee, String total_fee);

	protected String getAccessToken() {
		return WXUtil.get().getAccessToken();
	}

	protected String getJsapiTicket() {
		return WXUtil.get().getJsapiTicket();
	}

	protected static boolean isSuccessful(String json) {
		return !json.contains("\"errcode\"");
	}

	protected synchronized String getRandamStr() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	protected synchronized long getTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	protected String getSign(Map<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		SortedMap<String, Object> sortmap = new TreeMap<>(map);
		Object obj;
		for (String key : sortmap.keySet()) {
			obj = sortmap.get(key);
			if (obj == null || "".equals(obj.toString()))
				continue;
			sb.append(key);
			sb.append("=");
			sb.append(obj);
			sb.append("&");
		}
		sb.append("key=");
		sb.append(_key);
		return MD5.encodeByMD5(sb.toString()).toUpperCase();
	}

	protected static String getValue(Map<String, Object> mapreturn, String key) {
		if (mapreturn == null)
			return null;
		if (mapreturn.containsKey(key)) {
			return mapreturn.get(key).toString();
		}
		return null;
	}

	public PayResult callback(Map<String, Object> result) {
		// 退款通知
		if (result.containsKey("req_info")) {
			byte[] data = Base64.getDecoder().decode(result.get("req_info").toString());
			byte[] key = MD5.encodeByMD5(_key).toLowerCase().getBytes();
			String str = ObjectUtils.decryptData(data, key);
			if (StringUtils.isNotBlank(str)) {
				Map<String, Object> result2 = XMLUtils.doXMLParse(str);
				PayResult msg = new PayResult(true);
				msg.mch_id = getValue(result, "mch_id");
				msg.appid = getValue(result, "appid");
				// 商户订单号
				msg.out_trade_no = getValue(result2, "out_trade_no");
				msg.success_time = getValue(result2, "success_time");
				msg.refund_fee = getValue(result2, "refund_fee");
				return msg;
			}
			return null;
		}
		String returnCode = getValue(result, "return_code");
		String resultCode = getValue(result, "result_code");
		if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode)) {
			String sign = getValue(result, "sign");
			// 签名验证
			result.remove("sign");
			if (getSign(result).equals(sign)) {
				PayResult msg = new PayResult(false);
				msg.appid = getValue(result, "appid");
				msg.mch_id = getValue(result, "mch_id");
				msg.openid = getValue(result, "openid");
				msg.trade_type = getValue(result, "trade_type");
				// 商户订单号
				msg.out_trade_no = getValue(result, "out_trade_no");
				msg.time_end = getValue(result, "time_end");
				msg.cash_fee = getValue(result, "cash_fee");
				return msg;
			}
			logger.error("Check sign faild");
		}
		return null;
	}
}
