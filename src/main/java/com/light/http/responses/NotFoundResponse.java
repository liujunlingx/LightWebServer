package com.light.http.responses;

import com.light.http.HttpStatus;

import java.io.*;

/**
 * Created on 2018/4/23.
 */
//TODO NotFoundResponse
public class NotFoundResponse extends FileResponse {

    public NotFoundResponse() {
        super(HttpStatus.NOT_FOUND_404, new File("404.html"));
    }

    public static void main(String[] args) throws IOException {
        File file = new File("404.html");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while((line = bufferedReader.readLine()) != null){
            System.out.println(line);
        }
    }
}
