package com.ops.www.common.dto;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月31日 上午11:15:26
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
