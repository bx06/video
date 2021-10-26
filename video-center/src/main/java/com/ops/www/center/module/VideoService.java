package com.ops.www.center.module;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;

/**
 * @author wangzr
 */
public interface VideoService {

    /**
     * 播放视频
     *
     * @param playConfig 播放配置
     * @return 播放结果
     */
    PlayResult playVideo(PlayConfig playConfig);

    /**
     * 关闭
     */
    void closeAll();
}
