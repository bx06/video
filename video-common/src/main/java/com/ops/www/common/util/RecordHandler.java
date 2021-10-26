package com.ops.www.common.util;

/**
 * @author wangzr
 */
public interface RecordHandler {

    /**
     * 错误处理
     *
     * @param t 异常
     */
    void handleError(Throwable t);
}
