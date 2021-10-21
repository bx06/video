package com.ops.www.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.ResultModel;
import com.ops.www.common.util.StringUtils;
import com.ops.www.service.CenterService;

/**
 * @author wangzr
 */
@Service
public class CenterServiceImpl implements CenterService {

    private final Logger logger = LogManager.getLogger();

    private String centerService;

    private String host;

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

    private synchronized void ckConfig() {
        if (!StringUtils.isBlank(host)) {
            return;
        }
        String centerName = "video-center";
        List<ServiceInstance> instances = discoveryClient.getInstances(centerName);
        if (instances.isEmpty()) {
            return;
        }
        ServiceInstance instance = instances.get(0);
        host = instance.getHost();
        int port = instance.getPort();
        if (ssl) {
            centerService = "https://" + host + ":" + port + "/";
        } else {
            centerService = "http://" + host + ":" + port + "/";
        }
    }

    @Override
    public ResultModel onClose(PlayConfig playConfig, String lines) {
        try {
            ckConfig();
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("playConfig", playConfig);
            paramMap.add("lines", lines);
            RestTemplate temp = ssl ? restSslTemplate : restTemplate;
            return temp.postForObject(centerService + "/center/onClose", paramMap, ResultModel.class);
        } catch (Exception e) {
            logger.error("onClose call error:{}.", e.getMessage());
            return new ResultModel("调用失败", false, null);
        }
    }
}
