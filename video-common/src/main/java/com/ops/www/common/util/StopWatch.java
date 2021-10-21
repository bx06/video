package com.ops.www.common.util;

import java.math.BigDecimal;

/**
 * @author wangzr
 */
public class StopWatch {
    private long start;
    private long end;

    public void start() {
        this.start = System.currentTimeMillis();
    }

    public long stop() {
        end = System.currentTimeMillis();
        return end - this.start;
    }

    public double timeSecond() {
        BigDecimal bg = BigDecimal.valueOf((end - start) / 1000d);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
