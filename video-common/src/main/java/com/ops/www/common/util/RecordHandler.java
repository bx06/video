package com.ops.www.common.util;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月14日 下午4:16:48
 */
public interface RecordHandler {

    /**
     * 错误处理
     *
     * @param t 异常
     */
    void handleError(Throwable t);
}
