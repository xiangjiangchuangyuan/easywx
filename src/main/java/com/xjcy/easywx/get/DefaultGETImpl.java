package com.xjcy.easywx.get;

import com.xjcy.easywx.AbstractGET;
import com.xjcy.easywx.config.WXConfig;
import com.xjcy.util.JSONUtils;
import com.xjcy.util.StringUtils;
import com.xjcy.util.http.WebClient;

public class DefaultGETImpl extends AbstractGET {

	private static final String URL_OPENID = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
	private static final String URL_PROJRAM_OPENID = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
	private static final String URL_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	private static final String URL_JSTICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

	private static final int TIME = 7100 * 1000;

	public DefaultGETImpl(WXConfig wxConfig) {
		this._appId = wxConfig.getAppId();
		this._appSecret = wxConfig.getAppSecret();
		this._isMiniProgram = wxConfig.isMiniProgram();
	}

	@Override
	public String getOpenId(String code) {
		String json;
		if(_isMiniProgram)
			json = WebClient.downloadString(String.format(URL_PROJRAM_OPENID, _appId, _appSecret, code));
		else
			json = WebClient.downloadString(String.format(URL_OPENID, _appId, _appSecret, code));
		if (isSuccessful(json))
			return JSONUtils.getString(json, "openid");
		return null;
	}

	@Override
	public synchronized String getAccessToken() {
		if (StringUtils.isEmpty(_token) || (System.currentTimeMillis() - _tokenTime) > TIME) {
			String json = WebClient.downloadString(String.format(URL_TOKEN, _appId, _appSecret));
			if (isSuccessful(json)) {
				_token = JSONUtils.getString(json, "access_token");
				_tokenTime = System.currentTimeMillis();
			}
		}
		return _token;
	}

	@Override
	public synchronized String getJsapiTicket() {
		if (StringUtils.isEmpty(_ticket) || (System.currentTimeMillis() - _ticketTime) > TIME) {
			String json = WebClient.downloadString(String.format(URL_JSTICKET, getAccessToken()));
			if (json != null && json.contains("\"ticket\"")) {
				_ticket = JSONUtils.getString(json, "ticket");
				_ticketTime = System.currentTimeMillis();
			}
		}
		return _ticket;
	}
}
