package com.ops.www.center.service;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.dto.ResponseResult;

/**
 * @author 作者 cp
 * @version 创建时间：2020年8月26日 上午9:45:04
 */
public interface HttpService {

    /**
     * 播放视频
     *
     * @param playConfig 播放配置
     * @return 播放结果
     */
    PlayResult playVideo(PlayConfig playConfig);

    /**
     * 关闭
     *
     * @param ip       IP地址
     * @param port     端口
     * @param clientId 客户端ID
     * @param protocol 协议
     * @return 关闭结果
     */
    ResponseResult close(String ip, int port, String clientId, byte protocol);

    /**
     * 关闭
     *
     * @param ip         IP地址
     * @param port       端口
     * @param callbackId 回调ID
     * @param clientId   客户端ID
     * @param theme      样式
     * @param protocol   协议
     * @return 关闭结果
     */
    ResponseResult close(String ip, int port, String callbackId, String clientId, String theme, byte protocol);

    /**
     * 关闭
     *
     * @param ip   IP地址
     * @param port 端口
     * @return 关闭结果
     */
    ResponseResult closeAll(String ip, int port);
}
