package com.ops.www.center.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ops.www.center.service.CenterService;
import com.ops.www.center.util.OrderConstants;
import com.ops.www.center.web.websocket.BaseDefaultWebsocketHandle;
import com.ops.www.common.dto.*;
import com.ops.www.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author wangzr
 */
@Slf4j
@ServerEndpoint("/video/{uuid}")
@Component
public class VideoController extends BaseDefaultWebsocketHandle implements OnCloseCallBack {

    private static CenterService centerService;

    private byte protocol = PlayConfig.PROTOCOL_TRSP;

    @Override
    public void closeHandle(Session session) {
        String clientId = getUuid(session);
        if (clientId != null) {
            centerService.close(clientId, protocol);
            centerService.deleteCallBack(clientId);
            log.debug("Close {}.", clientId);
        }
    }

    @Override
    public void openHandle(Session session, String uuid) {
        centerService.registerCallBack(uuid, this);
        log.debug("New ClientId [{}].", uuid);
    }

    @Override
    public void doOnClose(PlayConfig playConfig, String lines) {
        String clientId = playConfig.getClientId();
        Session session = getSession(clientId);
        if (session == null) {
            return;
        }
        ResponseResult resultModel = new ResponseResult("视频流关闭", ResponseResult.CODE_SUCCESS, playConfig)
                .setOrder(OrderConstants.CLOSE_CALL);
        resultModel.setLines(lines);
        sendMsg(resultModel, session);
    }

    private void doPlay(JSONObject parse, Session session) {
        String callbackId = parse.getString("callbackId");
        JSONObject object = (JSONObject) parse.get("payload");
        PlayConfig playConfig = object.toJavaObject(PlayConfig.class);
        playConfig.setProtocol(protocol);
        String url = playConfig.getUrl();
        String rtsp = "rtsp:";
        if (StringUtils.isBlank(url) || !url.contains(rtsp)) {
            ResponseResult resultModel =
                    new ResponseResult("URL非法", ResponseResult.CODE_SERVER_ERROR, null).setOrder(OrderConstants.PLAY)
                            .setCallbackId(callbackId);
            sendMsg(resultModel, session);
            return;
        }
        PlayResult result = centerService.playVideo(callbackId, playConfig);
        if (result == null) {
            ResponseResult resultModel =
                    new ResponseResult("调用失败", ResponseResult.CODE_SERVER_ERROR, null).setOrder(OrderConstants.PLAY)
                            .setCallbackId(callbackId);
            sendMsg(resultModel, session);
            return;
        }
        ResponseResult resultModel =
                new ResponseResult("调用成功", ResponseResult.CODE_SUCCESS, result).setOrder(OrderConstants.PLAY)
                        .setCallbackId(callbackId);
        sendMsg(resultModel, session);
    }

    private void doClose(JSONObject parse, Session session) {
        String callbackId = parse.getString("callbackId");
        JSONObject jsonObject = (JSONObject) parse.get("payload");
        String clientId = jsonObject.getString("clientId");
        String theme = jsonObject.getString("theme");
        boolean flag = centerService.close(callbackId, clientId, theme, protocol);
        int code = flag ? ResponseResult.CODE_SUCCESS : ResponseResult.CODE_SERVER_ERROR;
        ResponseResult resultModel =
                new ResponseResult("调用成功", code, theme).setOrder(OrderConstants.CLOSE).setCallbackId(callbackId);
        sendMsg(resultModel, session);
    }

    private void doOther(JSONObject parse, Session session) {
        String callbackId = parse.getString("callbackId");
        ResponseResult resultModel = new ResponseResult("未定义的方法", false, null).setOrder(OrderConstants.UNDEFINED)
                .setCallbackId(callbackId);
        sendMsg(resultModel, session);
    }

    @Override
    public void onMessageHandle(String message, Session session) {
        JSONObject parse = (JSONObject) JSONObject.parse(message);
        int order = parse.getIntValue("order");
        switch (order) {
            case Order.ORDER_PLAY:
                doPlay(parse, session);
                return;
            case Order.ORDER_CLOSE:
                doClose(parse, session);
                return;
            case Order.ORDER_SET_PROTOCOL:
                setProtocol(parse);
                return;
            default:
                doOther(parse, session);
        }
    }

    private void setProtocol(JSONObject parse) {
        String protocol = parse.getString("protocol");
        if (protocol == null) {
            return;
        }
        this.protocol = Byte.parseByte(protocol);
    }

    @Autowired
    public void setCenterService(CenterService centerService) {
        VideoController.centerService = centerService;
    }

    private void sendMsg(ResponseResult resultModel, Session session) {
        sendTextMsg(session, JSON.toJSONString(resultModel, SerializerFeature.DisableCircularReferenceDetect));
    }
}
