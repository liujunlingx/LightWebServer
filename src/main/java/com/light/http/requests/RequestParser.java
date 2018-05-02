package com.light.http.requests;

import com.light.http.HttpMethod;
import com.light.http.HttpVersion;
import com.light.http.exceptions.IllegalRequestException;
import com.light.util.BytesUtil;
import com.light.util.Static;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created on 2018/4/23.
 */
@Slf4j
public class RequestParser {

    public static Request parseRequest(SocketChannel channel) throws IOException {
        //assume size of (requestline + headers) <= 1024kb
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        buffer.flip();
        int remaining = buffer.remaining();
        if(remaining == 0){
            return null;
        }
        byte[] bytes = new byte[remaining];
        buffer.get(bytes);
        int position1 = BytesUtil.indexOf(bytes, "\r\n");
        int position2 = BytesUtil.indexOf(bytes, "\r\n\r\n");
        if(position1 == -1 || position2 == -1){
            throw new IllegalRequestException("Illegal request format");
        }

        //RequestLine
        byte[] firstLine = Arrays.copyOfRange(bytes, 0, position1);
        RequestLine requestLine = parseRequestLine(firstLine);

        //RequestHeader
        byte[] head = Arrays.copyOfRange(bytes, position1 + 2, position2);
        RequestHeader requestHeader = parseRequestHeader(head);

        //RequestBody
        buffer.position(position2 + 4);
        int contentLength = requestHeader.getContentLength();
        ByteBuffer bodyBuffer = ByteBuffer.allocate(contentLength);
        bodyBuffer.put(buffer);
        while (bodyBuffer.hasRemaining()){
            channel.read(bodyBuffer);
        }
        byte[] body = bodyBuffer.array();
        RequestBody requestBody = parseRequestBody(body,requestHeader);

        //Request
        return new Request(requestLine,requestHeader,requestBody);
    }

    private static RequestLine parseRequestLine(byte[] src){
        RequestLine requestLine = new RequestLine();
        try {
            BufferedReader reader = new BufferedReader(new StringReader(new String(src, Static.DEFAULT_CHARSET)));
            String[] line = reader.readLine().split(" ");
            HttpMethod method = HttpMethod.parseMethod(line[0]);
            String requestURI = line[1];
            String queryString = StringUtils.EMPTY;
            MultiValuedMap<String,String> queryMap = new ArrayListValuedHashMap<>();
            HttpVersion httpVersion = HttpVersion.parseHttpVersion(line[2]);
            int index = requestURI.indexOf("?");
            if(index != -1){
                queryString = URLDecoder.decode(requestURI.substring(index + 1),Static.DEFAULT_CHARSET);
                parseQueryParameters(queryString, queryMap);
                requestURI = requestURI.substring(0,index);
            }
            requestLine.setMethod(method);
            requestLine.setRequestURI(requestURI);
            requestLine.setQueryString(queryString);
            requestLine.setQueryMap(queryMap);
            requestLine.setHttpVersion(httpVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestLine;
    }

    private static void parseQueryParameters(String queryString, MultiValuedMap<String,String> queryMap){
        String[] keyValuePairs = queryString.split("&");
        for(String keyValuePair : keyValuePairs){
            String[] split =  keyValuePair.split("=");
            if(split.length != 2){
                continue;
            }
            queryMap.put(split[0],split[1]);
        }
    }

    private static RequestHeader parseRequestHeader(byte[] src){
        RequestHeader requestHeader = new RequestHeader();
        Map<String,String> headers = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new StringReader(new String(src, Static.DEFAULT_CHARSET)));
            String line;
            while ((line = reader.readLine()) != null){
                String[] split = line.split(":");
                if(split.length != 2)
                    continue;
                headers.put(split[0].trim().toLowerCase(),split[1].trim());
            }
            requestHeader.setHeaders(headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestHeader;
    }

    private static RequestBody parseRequestBody(byte[] src,RequestHeader header){
        if(src.length == 0){
            return new RequestBody();
        }
        MultiValuedMap<String,String> formMap = new ArrayListValuedHashMap<>();
        Map<String,MimeData> mimeMap = Collections.emptyMap();

        String contentType = header.getContentType();
        if(contentType.contains("application/x-www-form-urlencoded")){
            try {
                String body = new String(src,Static.DEFAULT_CHARSET);
                parseQueryParameters(body,formMap);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(contentType.contains("multipart/form-data")){
            int boundaryValueIndex = contentType.indexOf("boundary=");
            String boundary = contentType.substring(boundaryValueIndex + "boundary=".length());
            mimeMap = parseMimeMap(src,boundary);
        }
        return new RequestBody(formMap,mimeMap);
    }

    private static Map<String,MimeData> parseMimeMap(byte[] src,String boundary){
        boundary = "--" + boundary;

        Map<String,MimeData> map = new HashMap<>();
        int startIndex,endIndex;
        List<Integer> allBoundaryIndexes = BytesUtil.findAll(src, boundary);
        //process empty form
        if(allBoundaryIndexes.size() == 0)
            return map;

        //process each segment
        for(int i = 0;i < allBoundaryIndexes.size() - 1;i++){ //there are allBoundaryIndexes.size() - 1 segments to process
            startIndex = allBoundaryIndexes.get(i);
            endIndex = allBoundaryIndexes.get(i+1);
            byte[] segment = Arrays.copyOfRange(src, startIndex + boundary.length() + 2, endIndex);//去掉\r\n

            int lineEndIndex = BytesUtil.indexOf(segment, "\r\n");
            byte[] firstLine = Arrays.copyOfRange(segment, 0, lineEndIndex);

            String control;
            String contentType = null;
            String filename = null;
            byte[] data = null;

            int dataStartIndex;
            List<Integer> allQuotationIndexes = BytesUtil.findAll(firstLine, "\"");
            control = new String(Arrays.copyOfRange(firstLine,allQuotationIndexes.get(0) + 1,allQuotationIndexes.get(1)));
            if(allQuotationIndexes.size() == 2){
                dataStartIndex = lineEndIndex + 4;
                data = Arrays.copyOfRange(segment,dataStartIndex,segment.length-2);
            }
            if(allQuotationIndexes.size() == 4){
                filename = new String(Arrays.copyOfRange(firstLine,allQuotationIndexes.get(2) + 1,allQuotationIndexes.get(3)));
                int headEndIndex = BytesUtil.indexOf(segment,"\r\n\r\n");
                //find contentType, if not found, default is "text/plain"
                contentType = headEndIndex == lineEndIndex ? "text/plain" :
                        new String(Arrays.copyOfRange(segment,lineEndIndex + 16,headEndIndex));
                dataStartIndex = headEndIndex + 4;
                data = Arrays.copyOfRange(segment,dataStartIndex,segment.length);
            }

            map.put(control,new MimeData(contentType,filename,data));
        }
        return map;
    }

    /**
     * https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2
     * standard form format
     */
//    <FORM action="http://server.com/cgi/handle"
//          enctype="multipart/form-data"
//          method="post">
//    <P>
//    What is your name? <INPUT type="text" name="submit-name"><BR>
//    What files are you sending? <INPUT type="file" name="files"><BR>
//    <INPUT type="submit" value="Send"> <INPUT type="reset">
//    </FORM>

//    Content-Type: multipart/form-data; boundary=AaB03x
//
//    --AaB03x
//    Content-Disposition: form-data; name="submit-name"
//
//    Larry
//    --AaB03x
//    Content-Disposition: form-data; name="files"; filename="file1.txt"
//    Content-Type: text/plain
//
//    ... contents of file1.txt ...
//    --AaB03x--

    /**
     * empty form format
     */
//    POST http://localhost:9000/testpost HTTP/1.1
//    Host: localhost:9000
//    Connection: keep-alive
//    Content-Length: 44
//    Cache-Control: max-age=0
//    Origin: http://localhost:9000
//    Upgrade-Insecure-Requests: 1
//    Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryUHzb1hRumHCWpU0G
//    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
//    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
//    Referer: http://localhost:9000/
//    Accept-Encoding: gzip, deflate, br
//    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,es;q=0.6
//
//    ------WebKitFormBoundaryUHzb1hRumHCWpU0G--

    /**
     * form with one input
     *
     */
//    <form action="/testpost"
//    enctype="multipart/form-data"
//    method="post">
//    <input type="text" name="submit-name">
//    <input type="submit" value="Send">
//    </form>
//
//    POST http://localhost:9000/testpost HTTP/1.1
//    Host: localhost:9000
//    Connection: keep-alive
//    Content-Length: 145
//    Cache-Control: max-age=0
//    Origin: http://localhost:9000
//    Upgrade-Insecure-Requests: 1
//    Content-Type: multipart/form-data; boundary=----WebKitFormBoundarywAVMkIySLJR8L8ko
//    User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
//    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
//    Referer: http://localhost:9000/
//    Accept-Encoding: gzip, deflate, br
//    Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,es;q=0.6
//
//    ------WebKitFormBoundarywAVMkIySLJR8L8ko
//    Content-Disposition: form-data; name="submit-name"
//
//    abc
//    ------WebKitFormBoundarywAVMkIySLJR8L8ko--


}
