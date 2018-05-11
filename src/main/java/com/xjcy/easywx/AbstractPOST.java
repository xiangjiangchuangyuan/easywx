package com.xjcy.easywx;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLSocketFactory;

import com.xjcy.easywx.post.JsapiTicket;
import com.xjcy.easywx.post.RefundResult;
import com.xjcy.easywx.post.UnifiedOrder;
import com.xjcy.util.MD5;

public abstract class AbstractPOST {

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;
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
	public abstract RefundResult refundOrder(String out_trade_no, String refund_fee);

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
	
	protected String getSign(Map<String, Object> map)
	{
		StringBuffer sb = new StringBuffer();
		SortedMap<String, Object> sortmap = new TreeMap<>(map);
		Object obj;
		for (String key : sortmap.keySet())
		{
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

	protected static String getValue(Map<String, Object> mapreturn, String key)
	{
		if (mapreturn == null)
			return null;
		if (mapreturn.containsKey(key)) { return mapreturn.get(key).toString(); }
		return null;
	}
}
