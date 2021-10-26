package com.ops.www.service.impl;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.ResponseResult;
import com.ops.www.service.CenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author wangzr
 */
@Slf4j
@Service
public class CenterServiceImpl implements CenterService {

    private String centerService;

    @Value(value = "${system.ssl:false}")
    private boolean ssl;

    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Qualifier("restSslTemplate")
    private RestTemplate restSslTemplate;

    private DiscoveryClient discoveryClient;

    @Autowired
    private void setTemplate(RestTemplate restTemplate, RestTemplate restSslTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.restSslTemplate = restSslTemplate;
        this.discoveryClient = discoveryClient;
    }

    private synchronized void checkConfig() {
        String centerName = "video-center";
        List<ServiceInstance> instances = discoveryClient.getInstances(centerName);
        if (instances.isEmpty()) {
            return;
        }

        ServiceInstance instance = instances.get(0);
        centerService = ssl ? "https" : "http";
        centerService += "://" + instance.getHost() + ":" + instance.getPort() + "/";
    }

    @Override
    public ResponseResult onClose(PlayConfig playConfig, String lines) {
        checkConfig();
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("playConfig", playConfig);
        paramMap.add("lines", lines);
        RestTemplate temp = ssl ? restSslTemplate : restTemplate;

        try {
            return temp.postForObject(centerService + "/center/onClose", paramMap, ResponseResult.class);
        } catch (Exception e) {
            log.error("onClose call error:{}.", e.getMessage());
            return new ResponseResult("调用失败", false, null);
        }
    }
}
