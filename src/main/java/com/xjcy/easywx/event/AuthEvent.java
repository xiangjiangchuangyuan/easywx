package com.xjcy.easywx.event;

import java.util.Map;

public interface AuthEvent {

	void doXml(String authXml);

	void doText(Map<String, Object> message);

	void doSubscribe(String eventKey, Map<String, Object> message);

	void doUnSubscribe(Map<String, Object> message);

	void doScan(Map<String, Object> message);

	void doView(Map<String, Object> message);

}
