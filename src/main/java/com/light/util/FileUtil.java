package com.light.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/4
 */
public class FileUtil {

    public static final char SEPARATOR_CHAR = '/';

    /**
     * 合并父子路径(处理路径分隔符)
     *
     * @param parent 父路径
     * @param child  子路径
     * @return 结果
     */
    public static String combinePath(String parent, String child) {
        if (StringUtils.isEmpty(parent)) return child;
        if (StringUtils.isEmpty(child)) return parent;
        int pn = parent.length();
        int cn = child.length();
        String c = child;
        int childStart = 0;
        int parentEnd = pn;

        if ((cn > 1) && (c.charAt(0) == SEPARATOR_CHAR)) {
            if (c.charAt(1) == SEPARATOR_CHAR) {
                childStart = 2;
            } else {
                childStart = 1;

            }
            if (cn == childStart) {
                if (parent.charAt(pn - 1) == SEPARATOR_CHAR)
                    return parent.substring(0, pn - 1);
                return parent;
            }
        }

        if (parent.charAt(pn - 1) == SEPARATOR_CHAR)
            parentEnd--;

        int strlen = parentEnd + cn - childStart;
        char[] theChars = null;
        if (child.charAt(childStart) == SEPARATOR_CHAR) {
            theChars = new char[strlen];
            parent.getChars(0, parentEnd, theChars, 0);
            child.getChars(childStart, cn, theChars, parentEnd);
        } else {
            theChars = new char[strlen + 1];
            parent.getChars(0, parentEnd, theChars, 0);
            theChars[parentEnd] = SEPARATOR_CHAR;
            child.getChars(childStart, cn, theChars, parentEnd + 1);
        }
        return new String(theChars);
    }
}
