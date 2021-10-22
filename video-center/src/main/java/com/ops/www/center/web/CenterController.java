package com.ops.www.center.web;

import java.util.Map;

import com.ops.www.common.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ops.www.center.module.CenterCallbackService;
import com.ops.www.common.dto.PlayConfig;

/**
 * @author wangzr
 */
@Controller
@RequestMapping("/center")
public class CenterController {

    private CenterCallbackService centerCallbackService;

    @Autowired
    private void setService(CenterCallbackService centerCallbackService) {
        this.centerCallbackService = centerCallbackService;
    }

    @RequestMapping(value = "/onClose")
    @ResponseBody
    public ResponseResult onClose(@RequestParam Map<String, Object> paramMap) {
        PlayConfig playConfig = JSONObject.parseObject(paramMap.get("playConfig").toString(), PlayConfig.class);
        String lines = paramMap.get("lines").toString();
        centerCallbackService.onClose(playConfig, lines);
        return new ResponseResult(null, true, null);
    }
}
