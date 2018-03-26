package com.xjcy.easywx;

import org.apache.log4j.Logger;

import com.xjcy.easywx.config.WXConfig;
import com.xjcy.easywx.get.DefaultGETImpl;
import com.xjcy.easywx.post.DefaultPOSTImpl;

/**
 * 微信开发工具
 * @author YYDF
 * 2018-03-26
 */
public class WXUtil {

	private static final Logger logger = Logger.getLogger(WXUtil.class);
	
	private static AbstractGET getImpl = null;
	private static AbstractPOST postImpl = null;
	private static WXConfig wxConfig;
	
	/**
	 * 注册公众号服务
	 * @param appId
	 * @param appSecret
	 */
	public static void register(String appId, String appSecret) {
		wxConfig = new WXConfig(appId, appSecret);
		logger.debug("Register success with appId => " + appId);
	}

	public static AbstractPOST post() {
		if(postImpl == null)
			postImpl = new DefaultPOSTImpl(wxConfig);
		return postImpl;
	}
	
	public static AbstractGET get() {
		if(getImpl == null)
			getImpl = new DefaultGETImpl(wxConfig);
		return getImpl;
	}

}
