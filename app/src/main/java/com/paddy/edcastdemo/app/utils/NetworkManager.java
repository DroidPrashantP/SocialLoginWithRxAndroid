package com.paddy.edcastdemo.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Prashant on 08/06/17.
 */
public class NetworkManager {
    /**
     * Check available internet connection
     *
     * @param ctx
     * @return
     */
    public static boolean isConnectedToInternet(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }
}
