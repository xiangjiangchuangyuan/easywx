package com.xjcy.easywx;

import java.util.UUID;

import com.xjcy.easywx.post.JsapiTicket;

public abstract class AbstractPOST {

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;

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
}
