package com.light.http.responses;

import com.light.http.HttpStatus;
import com.light.http.MimeTypes;
import com.light.http.exceptions.ServerInternalException;
import com.light.util.Static;
import com.light.util.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created on 2018/4/23.
 */
public class FileResponse extends Response {

    public FileResponse(HttpStatus status, File file) {
        super(status);
        if(file == null){
            throw new ServerInternalException("file 对象为空");
        }
        if(!file.isFile() || !file.canRead()){
            statusLine.setStatus(HttpStatus.NOT_FOUND_404);
        }
        long l = file.lastModified();
        headers.put("Last-Modified", TimeUtil.toRFC822(ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())));
        String path = file.getAbsolutePath();
        String contentType = MimeTypes.getContentType(path);
        if(contentType.startsWith("text")){
            contentType += "; charset=" + Static.DEFAULT_CHARSET;
        }
        headers.put("Content-Type",contentType);
        try {
            body = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            statusLine.setStatus(HttpStatus.NOT_FOUND_404);
        }

    }
}
