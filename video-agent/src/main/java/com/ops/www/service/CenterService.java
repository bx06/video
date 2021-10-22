package com.ops.www.service;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.ResponseResult;

/**
 * @author wangzr
 */
public interface CenterService {

    /**
     * 关闭播放
     *
     * @param playConfig 播放配置
     * @param lines      轨道
     * @return 关闭结果
     */
    ResponseResult onClose(PlayConfig playConfig, String lines);
}
