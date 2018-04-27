package com.light.http.responses;

import com.light.http.HttpStatus;
import com.light.http.HttpVersion;
import com.light.util.Static;
import com.light.util.TimeUtil;
import lombok.Data;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created on 2018/4/23.
 */
@Data
public class Response implements HttpServletResponse{

    protected StatusLine statusLine;
    protected Map<String,String> headers;
    protected byte[] body;//response body
    private ByteBuffer responseBuffer;//response

    public Response(HttpStatus status){
        statusLine = new StatusLine(HttpVersion.HTTP1_1,status);
        headers = new HashMap<>();
        body = new byte[0];
        headers.put("Date", TimeUtil.toRFC822(ZonedDateTime.now()));
        headers.put("Server", "LightWebServer");//TODO 看Server规范
        headers.put("Connection", "Close"); // TODO keep-alive
    }

    public ByteBuffer getResponseBuffer(){
        if(responseBuffer == null){
            headers.put("Content-Length",String.valueOf(body.length));
            StringBuilder sb = new StringBuilder();
            String httpVersion = statusLine.getHttpVersion().getName();
            String statusCode = String.valueOf(statusLine.getStatus().getCode());
            String reasonPhrase = statusLine.getStatus().getMessage();

            sb.append(httpVersion).append(" ").append(statusCode).append(" ").append(reasonPhrase).append("\r\n");

            for(Map.Entry<String,String> entry : headers.entrySet()){
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\r\n");
            }

            sb.append("\r\n");

            byte[] statusLineAndHeaders = new byte[0];
            try {
                statusLineAndHeaders = sb.toString().getBytes(Static.DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            responseBuffer = ByteBuffer.allocate(statusLineAndHeaders.length + body.length + 2);
            responseBuffer.put(statusLineAndHeaders);

            responseBuffer.put(body);
            responseBuffer.put((byte)'\r');
            responseBuffer.put((byte)'\n');
            responseBuffer.flip();
        }
        return responseBuffer;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    @Deprecated
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    @Deprecated
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    @Override
    @Deprecated
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return statusLine.getStatus().getCode();
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
