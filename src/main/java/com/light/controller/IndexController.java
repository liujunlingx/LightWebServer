package com.light.controller;

import com.light.http.HttpStatus;
import com.light.http.requests.Request;
import com.light.http.responses.FileResponse;
import com.light.http.responses.Response;
import com.light.mvc.annotations.Controller;
import com.light.mvc.annotations.WebPath;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created on 2018/4/27.
 */
@Controller
public class IndexController {

    @WebPath("/")
    public Response indexController(Request request) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("pages/index.html");
        File index = Files.createTempFile("index", ".html").toFile();
        FileOutputStream os = new FileOutputStream(index);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        return new FileResponse(HttpStatus.OK_200, index);
    }

}
