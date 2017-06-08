package com.paddy.edcastdemo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.utils.UserSharePreference;

public class SplashScreenActivity extends AppCompatActivity {
    private final static long MILLISECOND = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (new UserSharePreference(SplashScreenActivity.this).isLoggedIn()) {
                    moveToNextActivity(SplashScreenActivity.this, HomeScreenActivity.class);
                } else {
                    moveToNextActivity(SplashScreenActivity.this, SocialLoginActivity.class);
                }
            }
        }, MILLISECOND);
    }

    /**
     * move to next activity by finishing current activity
     *
     * @param activity
     * @param target
     */
    public void moveToNextActivity(Activity activity, Class target) {
        startActivity(new Intent(activity, target));
        finish();
    }
}
