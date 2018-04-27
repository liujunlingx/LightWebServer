package com.light.http.responses;

import com.light.http.HttpStatus;

/**
 * Created on 2018/4/23.
 */
//TODO ServerInternalErrorResponse
public class ServerInternalErrorResponse extends Response {

    public ServerInternalErrorResponse(HttpStatus status) {
        super(status);
    }
}
