package com.ops.www.center.module.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import com.ops.www.center.module.VideoService;
import com.ops.www.center.service.HttpService;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;

/**
 * @author wangzr
 */
@Service
public class VideoServiceImpl implements VideoService {

    private DiscoveryClient discoveryClient;

    private HttpService httpService;

    @Autowired
    private void setService(DiscoveryClient discoveryClient, HttpService httpService) {
        this.discoveryClient = discoveryClient;
        this.httpService = httpService;
    }

    @Override
    public PlayResult playVideo(PlayConfig playConfig) {
        return httpService.playVideo(playConfig);
    }

    @Override
    public void closeAll() {
        String agentName = "video-agent";
        List<ServiceInstance> instances = discoveryClient.getInstances(agentName);
        for (ServiceInstance serviceInstance : instances) {
            httpService.closeAll(serviceInstance.getHost(), serviceInstance.getPort());
        }
    }
}
