package com.ops.www.center.web.websocket;

import javax.websocket.Session;

/**
 * @author wangzr
 */
public interface WebsocketListen {

    /**
     * 开启
     *
     * @param session 会话
     * @param uuid    唯一ID
     */
    void open(Session session, String uuid);

    /**
     * 关闭
     *
     * @param session 会话
     * @param uuid    唯一ID
     */
    void close(Session session, String uuid);
}
