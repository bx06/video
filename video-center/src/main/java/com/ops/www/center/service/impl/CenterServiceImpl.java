package com.ops.www.center.service.impl;

import com.ops.www.center.module.CenterCallbackService;
import com.ops.www.center.module.VideoService;
import com.ops.www.center.service.CenterService;
import com.ops.www.center.service.HttpService;
import com.ops.www.common.dto.OnCloseCallBack;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.dto.ResponseResult;
import com.ops.www.common.util.RecordHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author 作者 cp
 * @version 创建时间：2020年7月13日 下午4:20:23
 */
@Slf4j
@Service
public class CenterServiceImpl extends BaseResultCacheService implements CenterService {

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
                    log.info("Play from Cache IP:{}.", ret.getLocalHost());
                    pushCache(callbackId, playConfig, ret);
                    return ret;
                }
                PlayResult playResult = videoService.playVideo(playConfig);
                if (playResult == null) {
                    return null;
                }
                pushCache(callbackId, playConfig, playResult);
                pushIp2Result(playConfig, playResult);
                log.info("Play from new IP:{}.", playResult.getLocalHost());
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
                    log.info("Play close From Cache by ClientId:[{}].", clientId);
                    return true;
                }
                for (PlayResult playResult : results) {
                    String ip = playResult.getLocalHost();
                    ResponseResult model = httpService.close(ip, playResult.getLocalPort(), clientId, protocol);
                    log.info("Play close by ClientId:[{}] in Ip:[{}],ret:{}.", clientId, ip, model.isOk());
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
                    log.info("Play close From Cache by ClientId:[{}] And theme:[{}].", clientId, theme);
                    return true;
                }
                String ip = playResult.getLocalHost();
                ResponseResult model = httpService.close(ip, playResult.getLocalPort(), callbackId, clientId, theme,
                        protocol);
                log.info("Play close by ClientId:[{}] And theme:[{}] in Ip:[{}],ret:{}.", clientId, theme, ip,
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
            log.error(e.getMessage());
        }
    }

    @Override
    public void deleteCallBack(String key) {
        try {
            centerCallbackService.deleteCallBack(key);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
