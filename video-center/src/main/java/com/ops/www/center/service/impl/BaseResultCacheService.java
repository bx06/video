package com.ops.www.center.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.ops.www.center.module.VideoService;
import com.ops.www.common.dto.Config2Result;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;

/**
 * @author wangzr
 */
public abstract class BaseResultCacheService {

    @Autowired
    private VideoService videoService;

    private final Map<String, Config2Result> id2Cache = new ConcurrentHashMap<>();

    private final Map<String, Map<PlayConfig, PlayResult>> ip2Result = new ConcurrentHashMap<>();

    public Map<String, Config2Result> selectAllCache() {
        return id2Cache;
    }

    protected synchronized void closeByPlayConfig(PlayConfig playConfig) {
        Set<String> idSet = id2Cache.keySet();
        for (String id : idSet) {
            Config2Result result = id2Cache.get(id);
            PlayConfig playConfigTemp = result.getPlayConfig();
            if (playConfigTemp.equals(playConfig)) {
                id2Cache.remove(id);
            }
        }
        Set<String> ipSet = ip2Result.keySet();
        for (String ip : ipSet) {
            Map<PlayConfig, PlayResult> c2r = ip2Result.get(ip);
            Set<PlayConfig> keySet = c2r.keySet();
            for (PlayConfig next : keySet) {
                if (next.equals(playConfig)) {
                    c2r.remove(next);
                }
            }
            if (c2r.isEmpty()) {
                ip2Result.remove(ip);
            }
        }
    }

    protected synchronized boolean hasSubsTheme(PlayResult playResult) {
        Collection<Config2Result> values = id2Cache.values();
        for (Config2Result config2Result : values) {
            if (config2Result.getPlayResult().equals(playResult)) {
                return true;
            }
        }
        return false;
    }

    protected synchronized Set<PlayResult> closeByClientId(String clientId) {
        Iterator<String> iterator = id2Cache.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Config2Result config2Result = id2Cache.get(key);
            if (config2Result.getPlayConfig().getClientId().equals(clientId)) {
                id2Cache.remove(key);
            }
        }
        iterator = ip2Result.keySet().iterator();
        Set<PlayResult> ret = new HashSet<>();
        while (iterator.hasNext()) {
            String ip = iterator.next();
            Map<PlayConfig, PlayResult> map = ip2Result.get(ip);
            for (PlayConfig next : map.keySet()) {
                if (next.getClientId().equals(clientId)) {
                    PlayResult playResult = map.get(next);
                    ret.add(playResult);
                    map.remove(next);
                }
            }
        }
        return ret;
    }

    protected synchronized boolean close(String ip, String clientId, String theme, byte protocol) {
        Map<PlayConfig, PlayResult> map = ip2Result.get(ip);
        if (map == null) {
            return false;
        }
        Set<PlayConfig> keySet = map.keySet();
        for (PlayConfig playConfig : keySet) {
            PlayResult playResult = map.get(playConfig);
            if (playConfig.getClientId().equals(clientId) && playResult.getTheme().equals(theme)
                    && protocol == playConfig.getProtocol()) {
                map.remove(playConfig);
                return true;
            }
        }

        return false;
    }

    protected synchronized PlayResult closeById(String callbackId) {
        Config2Result result = id2Cache.get(callbackId);
        if (result == null) {
            return null;
        }
        id2Cache.remove(callbackId);
        return result.getPlayResult();
    }

    protected synchronized void pushIp2Result(PlayConfig playConfig, PlayResult playResult) {
        String ip = playResult.getLocalHost();
        Map<PlayConfig, PlayResult> map = ip2Result.get(ip);
        if (map == null) {
            map = new ConcurrentHashMap<>(4);
            ip2Result.put(ip, map);
        }
        map.put(playConfig, playResult);
    }

    protected synchronized PlayResult selectRetFromCache(PlayConfig playConfig) {
        Set<String> ipKey = ip2Result.keySet();
        for (String ip : ipKey) {
            Map<PlayConfig, PlayResult> map = ip2Result.get(ip);
            Set<PlayConfig> configSet = map.keySet();
            for (PlayConfig config : configSet) {
                if (config.equals(playConfig)) {
                    return map.get(config);
                }
            }
        }
        return null;
    }

    protected synchronized void pushCache(String callbackId, PlayConfig playConfig, PlayResult playResult) {
        Config2Result config2Result = new Config2Result(playConfig, playResult);
        id2Cache.put(callbackId, config2Result);
    }

    /**
     * 全部调用一遍关闭
     */
    protected void start() {
        videoService.closeAll();
    }
}
