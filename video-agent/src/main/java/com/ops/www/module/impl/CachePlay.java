package com.ops.www.module.impl;

import java.util.Set;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.util.ProcessUtil.ProcessInstance;

/**
 * @author wangzr
 */
public class CachePlay {
    final String url;
    final ProcessInstance process;
    final PlayResult playResult;
    final PlayConfig playConfig;
    Set<String> clientIds;

    CachePlay(String url, ProcessInstance process, PlayConfig playConfig, PlayResult playResult) {
        this.url = url;
        this.process = process;
        this.playConfig = playConfig;
        this.playResult = playResult;
    }
}
