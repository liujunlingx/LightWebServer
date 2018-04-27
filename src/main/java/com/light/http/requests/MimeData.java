package com.light.http.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created on 2018/4/25.
 */
@Data
@AllArgsConstructor
public class MimeData {

    private String contentType;
    private String filename;
    private byte[] data;
}
