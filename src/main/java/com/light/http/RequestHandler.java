package com.light.http;

import com.light.http.exceptions.ServerInternalException;
import com.light.http.requests.Request;
import com.light.http.requests.RequestParser;
import com.light.http.responses.NotFoundResponse;
import com.light.http.responses.Response;
import com.light.mvc.ControllerMethod;
import com.light.mvc.ControllerScan;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created on 2018/4/21.
 */
@Slf4j
public class RequestHandler implements Runnable{

    private SocketChannel channel;
    private Selector selector;

    public RequestHandler(SocketChannel channel, Selector selector){
        this.channel = channel;
        this.selector = selector;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        Request request = null;
        Response response = null;
        try {
            //parseRequest
            request = RequestParser.parseRequest(channel);
            if(request == null)
                return;

            //find a method annotated with @WebPath(request.getRequestURI) to handle this request
            ControllerMethod controllerMethod = ControllerScan.findController(request);
            if(controllerMethod == null){
                response = new NotFoundResponse();
            } else if (!controllerMethod.containHttpMethod(request.getMethod())) {
                response = new Response(HttpStatus.METHOD_NOT_ALLOWED_405);
            } else {
                response = (Response) controllerMethod.handle(request);
                if (response == null) {
                    throw new ServerInternalException("controllerMethod.handle返回了一个null");
                }
            }
        } catch (IOException e) {
            response = new Response(HttpStatus.INTERNAL_SERVER_ERROR_500);
            log.error("parseRequest failed", e);
        } catch (IllegalAccessException | InvocationTargetException e) { //controllerMethod.handle出错
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        attachResponse(response);
        assert request != null;
        log.info("{} \"{}\" {} {} ms", request.getMethod(), request.getRequestURI(), response.getStatus(), System.currentTimeMillis() - start);
    }

    private void attachResponse(Response response){
        try{
            channel.register(selector, SelectionKey.OP_WRITE, response);
            selector.wakeup();
        } catch (ClosedChannelException e) {
            log.error("通道已关闭", e);
        }
    }
}
