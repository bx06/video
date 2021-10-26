package com.ops.www.center.service.impl;

import com.ops.www.center.service.HttpService;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.dto.ResponseResult;
import com.ops.www.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author wangzr
 */
@Slf4j
@Service
public class HttpServiceImpl implements HttpService {

    private String agentService;

    @Qualifier("loadBalanced")
    private RestTemplate loadBalanced;

    @Qualifier("restTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private void setTemplate(RestTemplate loadBalanced, RestTemplate restTemplate) {
        this.loadBalanced = loadBalanced;
        this.restTemplate = restTemplate;
    }

    @Value(value = "${server.port.ssl.key-store:}")
    public void setProtocol(String protocol) {
		agentService = (StringUtils.isBlank(protocol) ? "http://" : "https://") + "video-agent/";
    }

    @Override
    public ResponseResult close(String ip, int port, String clientId, byte protocol) {
        try {
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("clientId", clientId);
            paramMap.add("protocol", protocol);
            return restTemplate.postForObject("http://" + ip + ":" + port + "/agent/close", paramMap,
                    ResponseResult.class);
        } catch (Exception e) {
            return new ResponseResult(e.getMessage(), false, null);
        }
    }

    @Override
    public ResponseResult close(String ip, int port, String callbackId, String clientId, String theme, byte protocol) {
        try {
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("clientId", clientId);
            paramMap.add("theme", theme);
            paramMap.add("protocol", protocol);
            return restTemplate.postForObject("http://" + ip + ":" + port + "/agent/closeByTheme", paramMap,
                    ResponseResult.class);
        } catch (Exception e) {
            return new ResponseResult(e.getMessage(), false, null);
        }

    }

    @Override
    public ResponseResult closeAll(String ip, int port) {
        try {
            return restTemplate.postForObject("http://" + ip + ":" + port + "/agent/closeAll", null,
                    ResponseResult.class);
        } catch (Exception e) {
            return new ResponseResult(e.getMessage(), false, null);
        }
    }

    @Override
    public PlayResult playVideo(PlayConfig playConfig) {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("playConfig", playConfig);
        try {
            return loadBalanced.postForObject(agentService + "/agent/playVideo", paramMap,
                    PlayResult.class);
        } catch (Exception e) {
            log.error("playVideo error:{}.", e.getMessage());
            return null;
        }
    }
}
