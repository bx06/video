package com.ops.www.center.web.websocket;

import javax.websocket.Session;

/**
 * @author wangzr
 */
public interface WebSocketHandle {

    /**
     * 关闭
     *
     * @param session 会话
     */
    void closeHandle(Session session);

    /**
     * 打开
     *
     * @param session 会话
     * @param uuid    唯一ID
     */
    void openHandle(Session session, String uuid);

    /**
     * 信息
     *
     * @param message 信息
     * @param session 会话
     */
    void onMessageHandle(String message, Session session);
}
