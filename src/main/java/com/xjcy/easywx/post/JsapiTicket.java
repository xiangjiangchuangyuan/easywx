package com.xjcy.easywx.post;

import com.xjcy.util.ObjectUtils;

/**
 * 微信页面签名所用对象
 * @author YYDF
 * 2018-04-13
 */
public class JsapiTicket {
	public JsapiTicket(String _appId, String nonce, long time, String signStr) {
		this.appId = _appId;
		this.nonceStr = nonce;
		this.timestamp = time;
		this.signature = ObjectUtils.SHA1(signStr);
	}

	/**
	 * 微信公众号唯一ID
	 */
	public String appId;
	
	/**
	 * 时间戳
	 */
	public long timestamp;
	
	/**
	 * 随机字符串
	 */
	public String nonceStr;
	
	/**
	 * 加密后字符串
	 */
	public String signature;
}
