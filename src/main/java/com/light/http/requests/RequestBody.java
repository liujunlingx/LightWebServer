package com.light.http.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.Map;

/**
 * Created on 2018/4/23.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBody {

    private MultiValuedMap<String,String> formMap;
    /**
     * key      name_of_control
     * value    MimeData
     */
    private Map<String,MimeData> mimeMap;
}
