package com.light.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Be careful!
 * Created by hason on 15/9/30.
 */
public class BytesUtil {

    private BytesUtil() {
    }

    public static int indexOf(byte[] source, String str) {
        return indexOf(source, str, 0);
    }

    public static int indexOf(byte[] source, String str, int fromIndex) {
        int sourceCount = source.length;
        int targetCount = str.length();
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = str.charAt(0);
        int max = (sourceCount - targetCount);

        for (int i = fromIndex; i <= max; i++) {
            if (source[i] != first) {
                while (++i <= max && source[i] != first) ;
            }
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = 1; j < end && source[j] == str.charAt(k); j++, k++) ;
                if (j == end) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int lastIndexOf(byte[] bytes, String str) {
        int sourceCount = bytes.length;
        int targetCount = str.length();
        int fromIndex = bytes.length;

        int rightIndex = sourceCount - targetCount;


        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        if (targetCount == 0) {
            return fromIndex;
        }


        int strLastIndex = targetCount - 1;
        char strLastChar = str.charAt(strLastIndex);
        int min = targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && bytes[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (bytes[j--] != str.charAt(k--)) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start + 1;
        }
    }

    public static List<Integer> findAll(byte[] source, String str){
        List<Integer> result = new ArrayList<>();
        for(int start = 0;start < source.length - str.length();){
            int i = indexOf(source,str,start);
            if(i != -1){
                result.add(i);
                start = i+1;
            }else{
                break;
            }
        }
        return result;
    }
}
