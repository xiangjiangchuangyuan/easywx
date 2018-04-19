package com.xjcy.easywx.post;

import com.xjcy.easywx.AbstractPOST;
import com.xjcy.easywx.config.WXConfig;
import com.xjcy.util.JSONUtils;
import com.xjcy.util.http.WebClient;

public class DefaultPOSTImpl extends AbstractPOST {

	private static final String URL_CREATEQRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
	private static final String URL_CUSTOM_SEND = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	private static final String URL_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
	private static final String URL_SEND_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
	
	private static final String POST_SCENE_ID = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %s}}}";
	private static final String POST_SCENE_STR = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"%s\"}}}";
	private static final String POST_SEND_TEXT = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";

	public DefaultPOSTImpl(WXConfig wxConfig) {
		this._appId = wxConfig.getAppId();
		this._appSecret = wxConfig.getAppSecret();
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

}
