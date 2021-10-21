package com.ops.www.util;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author wangzr
 */
public interface Kernel32 extends Library {

    Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

    /**
     * 获取进程ID
     *
     * @param hProcess 进程
     * @return 进程ID
     */
    long getProcessId(Long hProcess);
}
