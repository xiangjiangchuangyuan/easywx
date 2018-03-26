package com.xjcy.easywx;

public abstract class AbstractPOST {

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;
	
	public abstract String createQrcode(Integer sceneId);

	protected static String getAccessToken()
	{
		return WXUtil.get().getAccessToken();
	}
}
