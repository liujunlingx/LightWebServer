package com.light.http;

import com.light.mvc.annotations.Controller;

/**
 * Created on 2018/4/23.
 */
public enum  HttpMethod {

    GET("GET"),
    POST("POST"),
    HEAD("HEAD"), //TODO HEAD method
    PUT("PUT"), //TODO PUT method
    DELETE("DELETE"), //TODO DELETE method
    TRACE("TRACE"), //TODO TRACE method
    CONNECT("CONNECT"), //TODO CONNECT method
    UNRECOGNIZED(null);

    private String name;

    HttpMethod(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static HttpMethod parseMethod(String method){
        for(HttpMethod httpMethod : HttpMethod.values()){
            if(method.toLowerCase().equals(httpMethod.getName().toLowerCase()))
                return httpMethod;
        }
        return null;
    }
}
