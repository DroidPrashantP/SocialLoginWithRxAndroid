package com.paddy.edcastdemo.app.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

    /**
     * Check if the key exist and if exist returns the key, value
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static String getString(JSONObject jsonObject, String key) throws JSONException {
        if (jsonObject.has(key)) {
            return jsonObject.getString(key);
        } else {
            return "";
        }
    }

    /**
     * Get json array from json object for provided key
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
        return jsonObject != null ? jsonObject.optJSONArray(key) : null;
    }

    /**
     * Get json object from json object for provided key
     *
     * @param jsonArray
     * @param index
     * @return
     */
    public static Object get(JSONArray jsonArray, int index) {
        return jsonArray != null ? jsonArray.opt(index) : null;
    }

}
