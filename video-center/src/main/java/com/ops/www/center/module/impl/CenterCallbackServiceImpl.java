package com.ops.www.center.module.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ops.www.center.module.CenterCallbackService;
import com.ops.www.center.service.CenterService;
import com.ops.www.common.dto.OnCloseCallBack;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.util.StringUtils;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月31日 上午10:34:42
 */
@Service
public class CenterCallbackServiceImpl implements CenterCallbackService {

    private final Map<String, OnCloseCallBack> id2callBack = new ConcurrentHashMap<>();

    private final Logger logger = LogManager.getLogger();

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
            logger.info("\n" + lines);
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
