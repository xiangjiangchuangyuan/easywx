package com.xjcy.easywx.post;

import com.xjcy.easywx.AbstractPOST;
import com.xjcy.easywx.config.WXConfig;
import com.xjcy.util.http.WebClient;

public class DefaultPOSTImpl extends AbstractPOST {
	
	private static final String JSON_QRCODE_ID = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %s}}}";
	private static final String URL_CREATEQRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
	
	public DefaultPOSTImpl(WXConfig wxConfig) {
		this._appId = wxConfig.getAppId();
		this._appSecret = wxConfig.getAppSecret();
	}

	@Override
	public String createQrcode(Integer sceneId) {
		String postStr = String.format(JSON_QRCODE_ID, sceneId);
		String json = WebClient.uploadString(String.format(URL_CREATEQRCODE, getAccessToken()), postStr);
		if (!json.contains("\"errcode\""))
			return json;
		return null;
	}

}
