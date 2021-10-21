package com.ops.www.center.service;

import java.util.Map;

import com.ops.www.common.dto.Config2Result;
import com.ops.www.common.dto.OnCloseCallBack;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月13日 下午4:19:51
 */
public interface CenterService {

    /**
     * 播放视频
     *
     * @param callbackId 回调ID
     * @param playConfig 播放配置
     * @return 播放结果
     */
    PlayResult playVideo(String callbackId, PlayConfig playConfig);

    /**
     * 根据播放配置关闭
     *
     * @param playConfig 播放配置
     */
    void closeByPlayConfig(PlayConfig playConfig);

    /**
     * 关闭
     *
     * @param clientId 客户端ID
     * @param protocol 协议
     * @return 关闭结果
     */
    boolean close(String clientId, byte protocol);

    /**
     * 关闭
     *
     * @param callbackId 回调ID
     * @param clientId   客户端ID
     * @param theme      样式
     * @param protocol   协议
     * @return 关闭结果
     */
    boolean close(String callbackId, String clientId, String theme, byte protocol);

    /**
     * 启动
     */
    void start();

    /**
     * 注册回调
     *
     * @param key      主键
     * @param callBack 回调
     */
    void registerCallBack(String key, OnCloseCallBack callBack);

    /**
     * 删除回调
     *
     * @param key 主键
     */
    void deleteCallBack(String key);

    /**
     * 查找所有缓存
     *
     * @return Config2Result信息
     */
    Map<String, Config2Result> selectAllCache();
}
