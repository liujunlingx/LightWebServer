package com.light.http;

/**
 * Created on 2018/4/23.
 */
public enum  HttpHeader {
    //https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01#Message-Headers

    /**
     * General Header Fields
     */
    Cache_Control("Cache-Control"),
    Connection("Connection"),
    Date("Date"),
    Forwarded("Forwarded"),
    Keep_Alive("Keep-Alive"),
    MIME_Version("MIME-Version"),
    Pragma("Pragma"),
    Upgrade("Upgrade"),

    /**
     * Request Header Fields
     */
    Accept("Accept"),
    Accept_Charset("Accept-Charset"),
    Accept_Encoding("Accept-Encoding"),
    Accept_Language("Accept-Language"),
    Authorization("Authorization"),
    From("From"),
    Host("Host"),
    If_Modified_Since("If-Modified-Since"),
    Proxy_Authorization("Proxy-Authorization"),
    Range("Range"),
    Referer("Referer"),
    Unless("Unless"),
    User_Agent("User-Agent"),

    /**
     * Response Header Fields
     */
    Location("Location"),
    Proxy_Authenticate("Proxy-Authenticate"),
    Public("Public"),
    Retry_After("Retry-After"),
    Server("Server"),
    WWW_Authenticate("WWW-Authenticate"),

    /**
     * Entity Header Fields
     */
    Allow("Allow"),
    Content_Encoding("Content-Encoding"),
    Content_Language("Content-Language"),
    Content_Length("Content-Length"),
    Content_MD5("Content-MD5"),
    Content_Range("Content-Range"),
    Content_Type("Content-Type"),
    Content_Version("Content-Version"),
    Derived_From("Derived-From"),
    Expires("Expires"),
    Last_Modified("Last-Modified"),
    Link("Link"),
    Title("Title"),
    Transfer_Encoding("Transfer-Encoding"),
    URI_header("URI-header");

    private String name;

    HttpHeader(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
