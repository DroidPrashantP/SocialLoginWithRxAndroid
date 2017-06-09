package com.paddy.edcastdemo.app.model;

import com.paddy.edcastdemo.app.utils.Constants;
import com.paddy.edcastdemo.app.utils.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prashant on 6/7/17.
 */

public class User {

    public String id;
    public String name;
    public String email;
    public String picture;

    public User(JSONObject jsonObject) {
        try {
            id = JSONUtil.getString(jsonObject, Constants.FACEBOOK_ID);
            name = JSONUtil.getString(jsonObject, Constants.FACEBOOK_NAME);
            email = JSONUtil.getString(jsonObject, Constants.FACEBOOK_EMAIL_ID);
            picture = jsonObject.optJSONObject(Constants.FACEBOOK_PROFILE_PIC).optJSONObject(Constants.DATA).getString(Constants.URL_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User() {

    }
}
