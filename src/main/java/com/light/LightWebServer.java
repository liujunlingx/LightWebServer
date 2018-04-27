package com.light;

import com.light.io.Server;

import java.io.IOException;

/**
 * Created on 2018/4/27.
 */
public class LightWebServer {

    public static void main(String[] args) throws IOException {
        Server.run(args,LightWebServer.class.getPackage().getName());
    }
}
