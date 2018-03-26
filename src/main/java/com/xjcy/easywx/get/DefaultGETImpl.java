package com.xjcy.easywx.get;

import com.xjcy.easywx.AbstractGET;
import com.xjcy.easywx.config.WXConfig;
import com.xjcy.util.JSONUtils;
import com.xjcy.util.StringUtils;
import com.xjcy.util.http.WebClient;

public class DefaultGETImpl extends AbstractGET {

	private static final String URL_OPENID = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	private static final String URL_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

	private static final int TIME = 7100 * 1000;

	public DefaultGETImpl(WXConfig wxConfig) {
		this._appId = wxConfig.getAppId();
		this._appSecret = wxConfig.getAppSecret();
	}

	@Override
	public String getOpenId(String code) {
		String json = WebClient.downloadString(String.format(URL_OPENID, _appId, _appSecret, code));
		if (!json.contains("\"errcode\""))
			return JSONUtils.getString(json, "openid");
		return null;
	}

	@Override
	public synchronized String getAccessToken() {
		if (StringUtils.isEmpty(_token) || (System.currentTimeMillis() - _startTime) > TIME) {
			String json = WebClient.downloadString(String.format(URL_TOKEN, _appId, _appSecret));
			if (!json.contains("\"errcode\"")) {
				_token = JSONUtils.getString(json, "access_token");
				_startTime = System.currentTimeMillis();
			}
		}
		return _token;
	}
}
