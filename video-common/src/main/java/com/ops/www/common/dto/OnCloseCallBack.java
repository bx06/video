package com.ops.www.common.dto;

/**
 * @author wangzr
 */
public interface OnCloseCallBack {

    /**
     * 关闭
     *
     * @param playConfig 播放配置
     * @param lines      轨道
     */
    void doOnClose(PlayConfig playConfig, String lines);
}
