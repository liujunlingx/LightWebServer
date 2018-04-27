package com.light.http.exceptions;

/**
 * Created on 2018/4/24.
 */
public class IllegalRequestException extends RuntimeException {
    public IllegalRequestException(String msg) {
        super(msg);
    }
}
