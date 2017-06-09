package com.paddy.edcastdemo.app.utils;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;

/**
 * Created by prashant on 8/6/17.
 */

public class CommonUse {

    /***
     * show SnackBar with message
     */
    public static void showSnackBar(final View view, String message) {
        if (view != null && StringUtils.isNotEmpty(message)) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    /**
     * set error enabled to TextInputLayout
     *
     * @param inputLayout TextInputLayout
     * @param error       error message
     */
    public static void setInputLayoutError(TextInputLayout inputLayout, String error) {
        inputLayout.requestFocus();
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(error);
    }

    /**
     * set error disabled to TextInputLayout
     *
     * @param inputLayout TextInputLayout
     */
    public static void setErrorDisabled(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
