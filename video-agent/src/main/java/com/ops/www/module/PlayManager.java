package com.ops.www.module;

import java.util.List;

import com.ops.www.common.dto.Config2Result;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;

/**
 * @author wangzr
 */
public interface PlayManager {

    /**
     * 开始
     */
    void start();

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
     * @param clientId 客户端ID
     * @return 关闭结果
     */
    boolean close(String clientId);

    /**
     * 关闭
     *
     * @param clientId 客户端ID
     * @param theme    样式
     * @return 关闭结果
     */
    boolean close(String clientId, String theme);

    /**
     * 获取Config2Result
     *
     * @return Config2Result列表
     */
    List<Config2Result> selectConfig2Result();
}
