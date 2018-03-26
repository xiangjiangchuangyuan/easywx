package com.xjcy.easywx;

import com.xjcy.easywx.config.WXConfig;
import com.xjcy.easywx.get.DefaultGETImpl;
import com.xjcy.easywx.post.DefaultPOSTImpl;

public class WXUtil {

	private static AbstractGET getImpl = null;
	private static AbstractPOST postImpl = null;
	private static WXConfig wxConfig;
	
	public static void register(String appId, String appSecret) {
		wxConfig = new WXConfig(appId, appSecret);
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
