package com.xjcy.easywx;

public abstract class AbstractPOST {

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;
	
	public abstract String createQrcode(Integer sceneId);
	
	public abstract String createQrcode(String sceneStr);

	protected String getAccessToken()
	{
		return WXUtil.get().getAccessToken();
	}
	
	protected boolean isSuccessful(String json)
	{
		return !json.contains("\"errcode\"");
	}
}
