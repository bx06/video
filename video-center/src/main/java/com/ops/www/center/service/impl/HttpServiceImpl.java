package com.ops.www.center.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ops.www.center.service.HttpService;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.dto.ResultModel;
import com.ops.www.common.util.StringUtils;

/**
 * @author 作者 cp
 * @version 创建时间：2020年8月26日 上午9:47:08
 */
@Service
public class HttpServiceImpl implements HttpService {

    private final Logger logger = LogManager.getLogger();

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
    public ResultModel close(String ip, int port, String clientId, byte protocol) {
        try {
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("clientId", clientId);
            paramMap.add("protocol", protocol);
            return restTemplate.postForObject("http://" + ip + ":" + port + "/agent/close", paramMap,
                    ResultModel.class);
        } catch (Exception e) {
            return new ResultModel(e.getMessage(), false, null);
        }
    }

    @Override
    public ResultModel close(String ip, int port, String callbackId, String clientId, String theme, byte protocol) {
        try {
            MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
            paramMap.add("clientId", clientId);
            paramMap.add("theme", theme);
            paramMap.add("protocol", protocol);
            return restTemplate.postForObject("http://" + ip + ":" + port + "/agent/closeByTheme",
                    paramMap, ResultModel.class);
        } catch (Exception e) {
            return new ResultModel(e.getMessage(), false, null);
        }

    }

    @Override
    public ResultModel closeAll(String ip, int port) {
        try {
            return restTemplate.postForObject("http://" + ip + ":" + port + "/agent/closeAll", null,
                    ResultModel.class);
        } catch (Exception e) {
            return new ResultModel(e.getMessage(), false, null);
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
            logger.error("playVideo error:{}.", e.getMessage());
            return null;
        }
    }
}
