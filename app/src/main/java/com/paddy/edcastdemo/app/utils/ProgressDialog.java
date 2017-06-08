package com.paddy.edcastdemo.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.paddy.edcastdemo.app.R;

import timber.log.Timber;

/**
 * Created by Prashant on 08/06/17.
 */
public class ProgressDialog {
    private static Dialog progressDialog;

    /**
     * call method to show progress dialog
     */
    public static void showProgress(final Context context, boolean isCancelable) {
        if (context != null) {
            progressDialog = new Dialog(context);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(isCancelable);
            progressDialog.setContentView(R.layout.layout_progress);
            progressDialog.show();
        }
    }

    /**
     * call method to close progress dialog
     */
    public static void closeProgress() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final Exception e) {
            Timber.d(e.getMessage());
        } finally {
            progressDialog = null;
        }
    }
}
