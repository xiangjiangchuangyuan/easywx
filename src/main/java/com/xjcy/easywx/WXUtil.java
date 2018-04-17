package com.xjcy.easywx;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xjcy.easywx.config.WXConfig;
import com.xjcy.easywx.event.AuthEvent;
import com.xjcy.easywx.get.DefaultGETImpl;
import com.xjcy.easywx.post.DefaultPOSTImpl;
import com.xjcy.util.STR;
import com.xjcy.util.XMLUtils;

/**
 * 微信开发工具
 * 
 * @author YYDF 2018-03-26
 */
public class WXUtil {

	private static final Logger logger = Logger.getLogger(WXUtil.class);

	private static AbstractGET getImpl = null;
	private static AbstractPOST postImpl = null;
	private static WXConfig wxConfig;

	/**
	 * 注册公众号服务
	 * 
	 * @param appId
	 * @param appSecret
	 */
	public static void register(String appId, String appSecret) {
		wxConfig = new WXConfig(appId, appSecret);
		logger.debug("Register success with appId => " + appId);
	}

	public static AbstractPOST post() {
		if (postImpl == null)
			postImpl = new DefaultPOSTImpl(wxConfig);
		return postImpl;
	}

	public static AbstractGET get() {
		if (getImpl == null)
			getImpl = new DefaultGETImpl(wxConfig);
		return getImpl;
	}

	public static void auth(HttpServletRequest request, HttpServletResponse response, AuthEvent authEvent) {
		try {
			if (request.getMethod() == "POST") {
				String authXml = XMLUtils.deserialize(request.getReader());
				authEvent.doXml(authXml);
				Map<String, Object> map = XMLUtils.doXMLParse(authXml);
				String toUserName = map.get("ToUserName") + "";
				final String fromUserName = map.get("FromUserName") + "";
				String msgType = map.get("MsgType") + "";
				Map<String, Object> message = new HashMap<>();
				message.put("FromUserName", toUserName);
				message.put("ToUserName", fromUserName);
				// 对文本消息进行处理
				if ("text".equals(msgType)) {
					authEvent.doText(message);
				} else if ("event".equals(msgType)) {
					String event = map.get("Event") + "";
					String eventKey = map.get("EventKey") + "";
					if (event.equals("subscribe")) {
						authEvent.doSubscribe(eventKey, message);
					} else if (event.equals("unsubscribe")) {
						authEvent.doUnSubscribe(message);
					} else if (event.equals("SCAN")) {
						authEvent.doScan(message);
					} else if (event.equals("VIEW")) {
						authEvent.doView(message);
					}
				}
				message.put("CreateTime", new Date().getTime());
				response.setContentType(STR.CONTENT_TYPE_XML);
				PrintWriter out = response.getWriter();
				out.print(XMLUtils.toXML(message)); // 将回应发送给微信服务器
				out.close();
			} else {
				String echostr = request.getParameter("echostr");
				PrintWriter out = response.getWriter();
				out.print(echostr);
				out.close();
			}
		} catch (Exception e) {
			logger.error("处理微信auth失败", e);
		}
	}
}
