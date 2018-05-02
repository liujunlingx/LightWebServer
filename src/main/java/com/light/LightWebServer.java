package com.light;

import com.light.io.Server;

import java.io.IOException;

/**
 * Created on 2018/4/27.
 */
public class LightWebServer {

    public static void main(String[] args) throws IOException {
        //run方法第二个参数是要扫描@Controller的包名，可以是多个
        Server.run(args);
    }
}
