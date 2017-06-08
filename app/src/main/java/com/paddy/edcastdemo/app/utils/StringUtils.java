package com.paddy.edcastdemo.app.utils;

/**
 * Created by prashant on 6/7/17.
 */

public class StringUtils {

    /**
     * Check whether a string is not NULL, empty or "NULL", "null", "Null"
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        boolean flag = true;
        if (str != null) {
            str = str.trim();
            if (str.length() == 0 || str.equalsIgnoreCase("null")) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

}
