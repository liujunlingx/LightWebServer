package com.light.http.responses;

import com.alibaba.fastjson.JSON;
import com.light.http.HttpStatus;
import com.light.http.exceptions.ServerInternalException;
import com.light.util.Static;

import java.io.IOException;

/**
 * Created on 2018/4/23.
 */
public class JsonResponse extends Response{

    public JsonResponse(HttpStatus status, Object object) {
        super(status);
        if(object == null){
            throw new ServerInternalException("json对象为null");
        }

        String contentType = "application/json; charset=" + Static.DEFAULT_CHARSET;

        headers.put("Content-Type",contentType);
        try {
            body = JSON.toJSONString(object).getBytes(Static.DEFAULT_CHARSET);
        } catch (IOException ignored) {
        }
    }
}
