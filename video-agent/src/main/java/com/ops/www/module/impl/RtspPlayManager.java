package com.ops.www.module.impl;

import com.ops.www.common.dto.Config2Result;
import com.ops.www.common.dto.PlayConfig;
import com.ops.www.common.dto.PlayResult;
import com.ops.www.common.dto.ResponseResult;
import com.ops.www.common.util.*;
import com.ops.www.common.util.ProcessUtil.ProcessInstance;
import com.ops.www.module.PlayManager;
import com.ops.www.service.CenterService;
import com.ops.www.util.PidUtil;
import com.ops.www.util.cmd.PlayCmdRtsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangzr
 */
@Slf4j
@Component("rtspPlayManager")
public class RtspPlayManager implements PlayManager {

    @Value(value = "${system.play.service.port}")
    public int servicePort;

    @Value(value = "${system.play.ws.ip}")
    public String wsIp;

    @Value(value = "${system.play.ws.port}")
    public int wsPort;

    @Value(value = "${system.play.ws.secret}")
    private String superSecret;

    @Value(value = "${system.ffmpeg.timeOut}")
    private int timeOut;

    @Value(value = "${server.host}")
    private String localHost;

    @Value(value = "${server.port}")
    private int localPort;

    private final Map<String, CachePlay> caches = new ConcurrentHashMap<>();

    private CenterService centerService;

    @Autowired
    private void setService(CenterService centerService) {
        this.centerService = centerService;
    }

    @Override
    public void start() {
        Objects.requireNonNull(ProcessUtil.doCmd("kill node", PidUtil.killProcessCmd("node"), null, null, 0)).waitClose();
        Objects.requireNonNull(ProcessUtil.doCmd("kill ffmpeg", PidUtil.killProcessCmd("ffmpeg"), null, null, 0)).waitClose();
        String path = PathUtil.getProjectPath() + "video-agent/src/main/resources/play/websocket.js";
        String cmd = "node " + path + " " + superSecret + " " + servicePort + " " + wsPort;
        ProcessUtil.doCmd("playService", cmd, (args, result) -> log.info((String) result), null, 0);
    }

    @Override
    public PlayResult playVideo(PlayConfig playConfig) {
        String url = playConfig.getUrl();
        if (StringUtils.isBlank(url)) {
            return new PlayResult(wsIp, wsPort, null, localHost, localPort);
        }
        String key = playConfig.getKey();
        CachePlay cachePlay = caches.get(key);
        String clientId = playConfig.getClientId();
        if (cachePlay != null) {
            Set<String> clientIds = cachePlay.clientIds;
            if (clientIds == null) {
                clientIds = new HashSet<>();
                cachePlay.clientIds = clientIds;
            }
            clientIds.add(clientId);
            return cachePlay.playResult;
        }
        cachePlay = buildCache(playConfig);
        cachePlay.clientIds = new HashSet<>();
        cachePlay.clientIds.add(clientId);
        caches.put(key, cachePlay);
        log.info("Open New Url:{}.", url);
        return cachePlay.playResult;
    }

    private CachePlay buildCache(PlayConfig playConfig) {
        int width = playConfig.getWidth();
        int height = playConfig.getHeight();
        String theme = "play_" + IdFactory.buildId();
        CallBack onClose = (args, result) -> {
            close(playConfig.getClientId(), theme);
            if (StringUtils.isBlank(result)) {
                return;
            }
            ResponseResult model = centerService.onClose(playConfig, result.toString());
            log.info("onClose call ret:{}.", model.isOk());
        };
        String cmd = PlayCmdRtsp.playCmd(playConfig.getType(), playConfig.getUrl(), playConfig.getUserName(),
                playConfig.getPassWord(), width + "x" + height, wsIp, servicePort, superSecret, theme, timeOut);
        ProcessInstance process = ProcessUtil.doCmd(theme, cmd, (args, result) -> {
            // 改成info查看ffmpeg回显
            log.info((String) result);
        }, onClose, 0);
        String url = playConfig.getUrl();
        PlayResult playResult = new PlayResult(wsIp, wsPort, theme, localHost, localPort);
        return new CachePlay(url, process, playConfig, playResult);
    }

    private void closeProcess(CachePlay cache) {
        ProcessInstance process = cache.process;
        if (process != null) {
            long pid = PidUtil.getPid(process.getProcess());
            PidUtil.killPid(pid);
            process.close();
        }
    }

    @Override
    public boolean close(String clientId, String theme) {
        Set<String> keySet = caches.keySet();
        for (String key : keySet) {
            CachePlay cache = caches.get(key);
            String themeTemp = cache.playResult.getTheme();
            if (!theme.equals(themeTemp)) {
                continue;
            }
            Set<String> clientIds = cache.clientIds;
            if (clientIds == null) {
                continue;
            }
            clientIds.remove(clientId);
            if (!clientIds.isEmpty()) {
                continue;
            }
            closeProcess(cache);
            caches.remove(key);
            log.info("Close :{}.", cache.url);
        }
        return true;
    }

    @Override
    public boolean close(String clientId) {
        Set<String> keySet = caches.keySet();
        for (String key : keySet) {
            CachePlay cache = caches.get(key);
            Set<String> clientIds = cache.clientIds;
            if (clientIds == null) {
                continue;
            }
            if (clientIds.contains(clientId)) {
                close(clientId, cache.playResult.getTheme());
            }
        }
        return true;
    }

    @Override
    public List<Config2Result> selectConfig2Result() {
        Collection<CachePlay> values = caches.values();
        List<Config2Result> ret = new ArrayList<>();
        for (CachePlay cachePlay : values) {
            ret.add(new Config2Result(cachePlay.playConfig, cachePlay.playResult));
        }
        return ret;
    }
}
