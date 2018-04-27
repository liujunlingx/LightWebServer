package com.light.http.requests;

import com.light.http.HttpHeader;
import lombok.Data;

import javax.servlet.http.Cookie;
import java.util.Map;

/**
 * Created on 2018/4/23.
 */
@Data
public class RequestHeader {

    private Map<String,String> headers;
    //TODO process cookies
    private Cookie[] cookies;

    public String getContentType(){
        return headers.get(HttpHeader.Content_Type.getName().toLowerCase());
    }

    public int getContentLength(){
        return Integer.valueOf(headers.getOrDefault(HttpHeader.Content_Length.getName().toLowerCase(),"0"));
    }
}
