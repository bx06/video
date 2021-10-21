package com.ops.www.common.util;

import java.util.UUID;

/**
 * @author wangzr
 */
public final class IdFactory {

    private IdFactory() {
    }

    public static String buildId() {
        return UUID.randomUUID().toString().replaceAll("-", "c");
    }
}
