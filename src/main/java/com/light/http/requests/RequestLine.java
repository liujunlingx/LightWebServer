package com.light.http.requests;

import com.light.http.HttpMethod;
import com.light.http.HttpVersion;
import lombok.Data;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.Map;

/**
 * Created on 2018/4/23.
 */
@Data
public class RequestLine {

    /**
     * Request-Line = Method SP Request-URI SP HTTP-Version CRLF
     */

    private HttpMethod method;
    private String requestURI;
    private String queryString;
    private MultiValuedMap<String,String> queryMap;
    private HttpVersion httpVersion;//now only support http/1.1
}
