package com.ops.www.center.web.websocket;

import com.ops.www.common.util.RecordHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cp
 * @version 创建时间：2020年1月6日 上午11:03:32
 */
@Slf4j
public abstract class BaseDefaultWebsocketHandle implements WebSocketHandle {

	private static final Map<String, Session> ID2SESSION = new ConcurrentHashMap<>();

	private static RecordHandler recordHandler;

	public static Map<String, Session> selectSessions() {
		return ID2SESSION;
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("uuid") String uuid) {
		ID2SESSION.put(uuid, session);
		log.debug("WebSocket onOpen:{}.", session.getId());
		openHandle(session, uuid);
	}

	@OnError
	public void onError(Session session, Throwable thr) {
		onClose(session);
		log.warn("WebSocket Error Close:{}.", session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		log.debug("WebSocket onClose:{}.", session.getId());
		closeHandle(session);
		String uuid = getUuid(session);
		if (uuid != null) {
			ID2SESSION.remove(uuid);
		}
	}

	public static String getUuid(Session session) {
		Set<String> keySet = ID2SESSION.keySet();
		for (String uuid : keySet) {
			Session temp = ID2SESSION.get(uuid);
			if (temp.getId().equals(session.getId())) {
				return uuid;
			}
		}
		return null;
	}

	public static Session getSession(String uuid) {
		return ID2SESSION.get(uuid);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		onMessageHandle(message, session);
	}

	public synchronized void sendTextMsg(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			recordHandler.handleError(e);
		}
	}

	@Autowired
	public static void setRecordHandler(RecordHandler recordHandler) {
		BaseDefaultWebsocketHandle.recordHandler = recordHandler;
	}
}
