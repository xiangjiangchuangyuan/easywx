package com.xjcy.easywx;

public abstract class AbstractGET {

	protected String _appId;
	protected String _appSecret;
	protected String _mchId;
	protected String _key;
	protected String _notifyUrl;
	protected boolean _isMiniProgram;
	
	protected String _token;
	protected Long _tokenTime = 0L;
	protected String _ticket;
	protected Long _ticketTime = 0L;
	
	public abstract String getOpenId(String code);

	public abstract String getAccessToken();

	public abstract String getJsapiTicket();
	
	protected static boolean isSuccessful(String json)
	{
		return !json.contains("\"errcode\"");
	}
}
