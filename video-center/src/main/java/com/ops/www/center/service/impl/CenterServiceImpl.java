package com.ops.www.center.service.impl;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ops.www.center.module.CenterCallbackService;
import com.ops.www.center.module.VideoService;
import com.ops.www.center.service.CenterService;
import com.ops.www.center.service.HttpService;
import com.ops.www.common.dto.OnCloseCallBack;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.dto.ResultModel;
import com.ops.www.common.util.RecordHandler;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月13日 下午4:20:23
 */
@Service
public class CenterServiceImpl extends BaseResultCacheService implements CenterService {

    private final Logger logger = LogManager.getLogger();

    private final Object lock = new Object();

    private VideoService videoService;

    private CenterCallbackService centerCallbackService;

    private RecordHandler recordHandler;

    private HttpService httpService;

    @Autowired
    private void setService(VideoService videoService, CenterCallbackService centerCallbackService,
                            RecordHandler recordHandler, HttpService httpService) {
        this.videoService = videoService;
        this.centerCallbackService = centerCallbackService;
        this.recordHandler = recordHandler;
        this.httpService = httpService;
    }

    @Override
    public PlayResult playVideo(String callbackId, PlayConfig playConfig) {
        synchronized (lock) {
            try {
                PlayResult ret = selectRetFromCache(playConfig);
                if (ret != null) {
                    logger.info("Play from Cache IP:{}.", ret.getLocalHost());
                    pushCache(callbackId, playConfig, ret);
                    return ret;
                }
                PlayResult playResult = videoService.playVideo(playConfig);
                if (playResult == null) {
                    return null;
                }
                pushCache(callbackId, playConfig, playResult);
                pushIp2Result(playConfig, playResult);
                logger.info("Play from new IP:{}.", playResult.getLocalHost());
                return playResult;
            } catch (Exception e) {
                recordHandler.handleError(e);
                return null;
            }
        }
    }

    @Override
    public boolean close(String clientId, byte protocol) {
        synchronized (lock) {
            try {
                Set<PlayResult> results = super.closeByClientId(clientId);
                if (results.isEmpty()) {
                    logger.info("Play close From Cache by ClientId:[{}].", clientId);
                    return true;
                }
                for (PlayResult playResult : results) {
                    String ip = playResult.getLocalHost();
                    ResultModel model = httpService.close(ip, playResult.getLocalPort(), clientId, protocol);
                    logger.info("Play close by ClientId:[{}] in Ip:[{}],ret:{}.", clientId, ip, model.isOk());
                }
                return true;
            } catch (Exception e) {
                recordHandler.handleError(e);
                return false;
            }
        }
    }

    @Override
    public boolean close(String callbackId, String clientId, String theme, byte protocol) {
        synchronized (lock) {
            PlayResult playResult;
            try {
                playResult = super.closeById(callbackId);
                if (playResult == null) {
                    return true;
                }
                if (hasSubsTheme(playResult)) {
                    logger.info("Play close From Cache by ClientId:[{}] And theme:[{}].", clientId, theme);
                    return true;
                }
                String ip = playResult.getLocalHost();
                ResultModel model = httpService.close(ip, playResult.getLocalPort(), callbackId, clientId, theme,
                        protocol);
                logger.info("Play close by ClientId:[{}] And theme:[{}] in Ip:[{}],ret:{}.", clientId, theme, ip,
                        model.isOk());
                super.close(ip, clientId, theme, protocol);
                return true;
            } catch (Exception e) {
                recordHandler.handleError(e);
                return false;
            }
        }
    }

    @Override
    public void closeByPlayConfig(PlayConfig playConfig) {
        super.closeByPlayConfig(playConfig);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void registerCallBack(String key, OnCloseCallBack callBack) {
        try {
            centerCallbackService.registerCallBack(key, callBack);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void deleteCallBack(String key) {
        try {
            centerCallbackService.deleteCallBack(key);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
