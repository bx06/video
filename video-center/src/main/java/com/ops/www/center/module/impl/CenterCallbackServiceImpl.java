package com.ops.www.center.module.impl;

import com.ops.www.center.module.CenterCallbackService;
import com.ops.www.center.service.CenterService;
import com.ops.www.common.dto.OnCloseCallBack;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangzr
 */
@Slf4j
@Service
public class CenterCallbackServiceImpl implements CenterCallbackService {

    private final Map<String, OnCloseCallBack> id2callBack = new ConcurrentHashMap<>();

    private CenterService centerService;

    @Autowired
    private void setService(CenterService centerService) {
        this.centerService = centerService;
    }

    @Override
    public void onClose(PlayConfig playConfig, String lines) {
        if (StringUtils.isBlank(playConfig.getClientId())) {
            return;
        }
        centerService.closeByPlayConfig(playConfig);
        OnCloseCallBack callBack = id2callBack.get(playConfig.getClientId());
        if (callBack != null) {
            callBack.doOnClose(playConfig, lines);
            log.info("\n" + lines);
        }
    }

    @Override
    public void registerCallBack(String key, OnCloseCallBack callBack) {
        id2callBack.put(key, callBack);
    }

    @Override
    public void deleteCallBack(String key) {
        id2callBack.remove(key);
    }
}
