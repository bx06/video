package com.ops.www.common.dto;

import java.io.Serializable;

/**
 * 返回结果集
 *
 * @author wangzr
 */
public class ResponseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CODE_SUCCESS = 200;

    public static final int CODE_CLIENT_ERROR = 404;

    public static final int CODE_SERVER_ERROR = 500;

    /**
     * 状态码 200:成功 500:错误 404:未找到
     */
    private int code;

    /**
     * 描述信息
     */
    private String msg;

    /**
     * 返回对象
     */
    private Object obj;

    /**
     * 命令方式 0：播放 1:关闭
     */
    private byte order = 0;

    /**
     * 方法调用唯一标识
     */
    private String callbackId;
    private String lines;

    public ResponseResult(String msg, int code, Object obj) {
        this.msg = msg;
        this.code = code;
        this.obj = obj;
    }

    public ResponseResult(String msg, boolean ok, Object obj) {
        new ResponseResult(msg, ok ? CODE_SUCCESS : CODE_SERVER_ERROR, obj);
    }

    public boolean isOk() {
        return code == CODE_SUCCESS;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ResponseResult setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getObj() {
        return obj;
    }

    public ResponseResult setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    public byte getOrder() {
        return order;
    }

    public ResponseResult setOrder(byte order) {
        this.order = order;
        return this;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public String getLines() {
        return lines;
    }

    public ResponseResult setLines(String lines) {
        this.lines = lines;
        return this;
    }

    public ResponseResult setCallbackId(String callbackId) {
        this.callbackId = callbackId;
        return this;
    }
}