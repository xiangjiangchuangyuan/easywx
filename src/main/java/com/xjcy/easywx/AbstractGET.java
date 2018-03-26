package com.xjcy.easywx;

public abstract class AbstractGET {

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;
	
	protected static String _token;
	protected static Long _startTime = 0L;
	
	public abstract String getOpenId(String code);

	public abstract String getAccessToken();
}
