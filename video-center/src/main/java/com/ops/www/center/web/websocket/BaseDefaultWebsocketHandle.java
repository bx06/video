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
 * @author wangzr
 */
@Slf4j
public abstract class BaseDefaultWebsocketHandle implements WebSocketHandle {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的连接会话对象。
     */
    private static final Map<String, Session> ID2SESSION = new ConcurrentHashMap<>();

    /**
     * 接收sid
     */
    private String uuid = "";

    private static RecordHandler recordHandler;

    @Autowired
    public static void setRecordHandler(RecordHandler recordHandler) {
        BaseDefaultWebsocketHandle.recordHandler = recordHandler;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uuid") String uuid) {
        this.uuid = uuid;
        // 加入map中
        ID2SESSION.put(uuid, session);
        // 在线数加1
        addOnlineCount();
        log.info("有新窗口开始监听:{},当前在线人数为{}", uuid, getOnlineCount());
        openHandle(session, uuid);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        closeHandle(session);
        String uuid = getUuid(session);
        if (uuid != null) {
            // 从map中删除
            ID2SESSION.remove(uuid);
            // 在线数减1
            subOnlineCount();
            log.info("有一连接({})关闭！当前在线人数为{}", session.getId(), getOnlineCount());
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

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口({})的信息:{}", uuid, message);
        onMessageHandle(message, session);
    }

    /**
     * 连接错误调用的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        onClose(session);
        log.warn("WebSocket连接({})发生错误:{}.", session.getId(), error.getMessage());
    }

    public static Session getSession(String uuid) {
        return ID2SESSION.get(uuid);
    }

    /**
     * 实现服务器主动推送
     *
     * @param message 消息
     */
    public synchronized void sendTextMsg(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            recordHandler.handleError(e);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        onlineCount--;
    }
}
