package com.light.http.responses;

import com.light.http.HttpStatus;
import com.light.http.HttpVersion;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created on 2018/4/23.
 */
@Data
@AllArgsConstructor
public class StatusLine {

    /**
     * Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
     */

    private HttpVersion httpVersion;
    private HttpStatus status;
}
