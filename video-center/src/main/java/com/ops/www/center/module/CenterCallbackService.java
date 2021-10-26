package com.ops.www.center.module;

import com.ops.www.common.dto.OnCloseCallBack;
import com.ops.www.common.dto.PlayConfig;

/**
 * @author wangzr
 */
public interface CenterCallbackService {

    /**
     * 关闭
     *
     * @param playConfig 播放配置
     * @param lines      轨道
     */
    void onClose(PlayConfig playConfig, String lines);

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
}
