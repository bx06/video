package com.ops.www.center.system.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ops.www.center.service.CenterService;
import com.ops.www.center.system.SystemBoot;

/**
 * @author wangzr
 */
@Component
public class DefaultSystemBoot implements SystemBoot {

    private CenterService centerService;

    @Autowired
    private void setService(CenterService centerService) {
        this.centerService = centerService;
    }

    private void start() {
        centerService.start();
    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }

}
