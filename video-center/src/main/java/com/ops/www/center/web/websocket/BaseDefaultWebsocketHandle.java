package com.ops.www.center.web.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ops.www.common.util.RecordHandler;

/**
 *
 *
 * @author cp
 * @version 创建时间：2020年1月6日 上午11:03:32
 */
public abstract class BaseDefaultWebsocketHandle implements WebSocketHandle {

	protected Logger logger = LogManager.getLogger();

	private static final Map<String, Session> ID2SESSION = new ConcurrentHashMap<>();

	private static RecordHandler recordHandler;

	public static Map<String, Session> selectSessions() {
		return ID2SESSION;
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("uuid") String uuid) {
		ID2SESSION.put(uuid, session);
		logger.debug("WebSocket onOpen:{}.", session.getId());
		openHandle(session, uuid);
	}

	@OnError
	public void onError(Session session, Throwable thr) {
		onClose(session);
		logger.warn("WebSocket Error Close:{}.", session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		logger.debug("WebSocket onClose:{}.", session.getId());
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
