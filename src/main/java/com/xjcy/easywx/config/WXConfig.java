package com.xjcy.easywx.config;

import javax.net.ssl.SSLSocketFactory;

/**
 * 微信配置
 * @author YYDF
 * 2018-03-26
 */
public class WXConfig {
	private String appId;
	private String appSecret;
	private String mchId;
	private String key;
	private String notifyUrl;
	private boolean isMiniProgram;
	private SSLSocketFactory sslSocket;

	public WXConfig(String appId, String appSecret, boolean isMiniProgram) {
		this.appId = appId;
		this.appSecret = appSecret;
		this.isMiniProgram = isMiniProgram;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public boolean isMiniProgram() {
		return isMiniProgram;
	}

	public SSLSocketFactory getSslSocket() {
		return sslSocket;
	}

	public void setSslSocket(SSLSocketFactory sslSocket) {
		this.sslSocket = sslSocket;
	}
}
