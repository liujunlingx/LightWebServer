package com.light.http.exceptions;

/**
 * Created on 2018/4/23.
 */
public class ServerInternalException extends RuntimeException{
    public ServerInternalException(String msg) {
        super(msg);
    }
}
