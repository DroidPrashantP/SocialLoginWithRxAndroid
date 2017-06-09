package com.paddy.edcastdemo.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.utils.UserSharePreference;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Observable observable = Observable.timer(400, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        observable.subscribe(new Observer<Long>() {
            @Override
            public void onError(Throwable e) {
                Timber.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long s) {
                if (new UserSharePreference(SplashScreenActivity.this).isLoggedIn()) {
                    moveToNextActivity(SplashScreenActivity.this, HomeScreenActivity.class);
                } else {
                    moveToNextActivity(SplashScreenActivity.this, SocialLoginActivity.class);
                }
            }
        });
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
