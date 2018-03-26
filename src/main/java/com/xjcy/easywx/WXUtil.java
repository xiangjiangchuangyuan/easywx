package com.xjcy.easywx;

import com.xjcy.easywx.get.DefaultGETImpl;
import com.xjcy.easywx.post.DefaultPOSTImpl;

public class WXUtil {

	private static AbstractGET getImpl = null;
	private static AbstractPOST postImpl = null;
	
	public static void register(String appId, String appSecret) {
		// TODO Auto-generated method stub
		
	}

	public static AbstractPOST post() {
		if(postImpl == null)
			postImpl = new DefaultPOSTImpl();
		return postImpl;
	}
	
	public static AbstractGET get() {
		if(getImpl == null)
			getImpl = new DefaultGETImpl();
		return getImpl;
	}

}
