package com.ops.www.system.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ops.www.module.PlayManager;
import com.ops.www.system.SystemBoot;

/**
 * @author wangzr
 */
@Component
public class DefaultSystemBoot implements SystemBoot {

    @Qualifier("rtspPlayManager")
    private PlayManager rtspPlayManager;

    @Autowired
    private void setManager(PlayManager rtspPlayManager) {
        this.rtspPlayManager = rtspPlayManager;
    }

    private void start() {
        rtspPlayManager.start();
    }

    @Override
    public void run(String... args) {
        start();
    }
}
