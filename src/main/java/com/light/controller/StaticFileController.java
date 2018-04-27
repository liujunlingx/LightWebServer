package com.light.controller;

import com.light.http.HttpHeader;
import com.light.http.HttpStatus;
import com.light.http.requests.Request;
import com.light.http.responses.NotFoundResponse;
import com.light.http.responses.FileResponse;
import com.light.http.responses.Response;
import com.light.mvc.annotations.Controller;
import com.light.mvc.annotations.WebPath;
import com.light.util.PropertiesUtil;
import com.light.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created on 2018/4/27.
 */
@Controller
public class StaticFileController {

    private static final String prefix = "/s/";
    private static String staticPath;
    private static String root;

    static {
        staticPath = PropertiesUtil.getProperty("static_file_path","static");
        root = StaticFileController.class.getClassLoader().getResource("").getPath();
    }

    @WebPath(prefix + ".*")
    public Response staticFile(Request request){
        String filePath = root + staticPath + File.separator + request.getRequestURI().replaceAll(prefix,"");
        File file = new File(filePath);
        if(!file.exists() || !file.isFile() || !file.canRead()){
            return new NotFoundResponse();
        }

        /**
         * conditional get
         * https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01#GET
         */
        String ifModifiedSinceStr = request.getHeader(HttpHeader.If_Modified_Since.getName().toLowerCase());
        if(StringUtils.isNotEmpty(ifModifiedSinceStr)){
            long ifModifiedSince = TimeUtil.parseRFC822(ifModifiedSinceStr).toInstant().toEpochMilli();
            if(file.lastModified() <= ifModifiedSince){
                return new Response(HttpStatus.NOT_MODIFIED_304);
            }
        }

        return new FileResponse(HttpStatus.OK_200,file);
    }

    public static void main(String[] args) {
        System.out.println(StaticFileController.class.getClassLoader().getResource("").getPath());
        System.out.println(StaticFileController.class.getResource("").getPath());
        System.out.println(StaticFileController.class.getResource("/").getPath());
    }
}
