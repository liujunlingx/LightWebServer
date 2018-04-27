package com.light.http;

/**
 * Created on 2018/4/23.
 */
public enum  HttpVersion {

    HTTP1_0("HTTP/1.0"),
    HTTP1_1("HTTP/1.1"),
    HTTP2_0("HTTP/2.0");

    private String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpVersion parseHttpVersion(String version){
        for(HttpVersion httpVersion : HttpVersion.values()){
            if(version.toLowerCase().equals(httpVersion.getName().toLowerCase()))
                return httpVersion;
        }
        return null;
    }
}
