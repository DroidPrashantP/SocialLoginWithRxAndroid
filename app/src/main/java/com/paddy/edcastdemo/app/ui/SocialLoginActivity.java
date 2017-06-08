package com.paddy.edcastdemo.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.model.User;
import com.paddy.edcastdemo.app.db.UserDatabaseManager;
import com.paddy.edcastdemo.app.utils.CommonUse;
import com.paddy.edcastdemo.app.utils.NetworkManager;
import com.paddy.edcastdemo.app.utils.UserSharePreference;
import com.paddy.edcastdemo.app.utils.Constants;
import com.paddy.edcastdemo.app.utils.ProgressDialog;
import com.paddy.edcastdemo.app.utils.StringUtils;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class SocialLoginActivity extends AppCompatActivity {
    private static final String TAG = "SocialLoginActivity";
    private CallbackManager mCallbackManager;
    private UserSharePreference mUserSharePreference;

    @BindView(R.id.mainLayoutWrapper)
    RelativeLayout mParentLayout;
    @BindView(R.id.login_button)
    LoginButton mFBLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        ButterKnife.bind(this);

        setToolBar(getString(R.string.login_text));
        intializeInstances();
        facebookBtnCallback();
    }

    /**
     * register callback on facebook login button
     */
    private void facebookBtnCallback() {
        Timber.d("Register FB callback");
        if (NetworkManager.isConnectedToInternet(this)) {
            mFBLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    ProgressDialog.showProgress(SocialLoginActivity.this, false);
                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            parseFbProfileResponse(response);
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString(Constants.FIELDS, Constants.FACEBOOK_PARAM_KEYS);
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Timber.e("onCancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    Timber.e(exception.getMessage());
                }
            });
        } else {
            CommonUse.showSnackBar(mParentLayout, getString(R.string.network_error));
        }

    }

    /**
     * parse Facebook profile response
     *
     * @param response
     */
    private void parseFbProfileResponse(GraphResponse response) {
        ProgressDialog.closeProgress();
        try {
            JSONObject json = response.getJSONObject();
            if (json != null) {
                Timber.d("Response : " + json.toString());
                if (StringUtils.isNotEmpty(json.optString(Constants.FACEBOOK_EMAIL_ID))) {
                    mUserSharePreference.setLoggedIn(true);
                    mUserSharePreference.setUserId(json.optString(Constants.FACEBOOK_ID));
                    User user = new User();
                    user.id = json.optString(Constants.FACEBOOK_ID);
                    user.name = json.optString(Constants.FACEBOOK_NAME);
                    user.email = json.optString(Constants.FACEBOOK_EMAIL_ID);
                    user.picture = json.optJSONObject(Constants.FACEBOOK_PROFILE_PIC).optJSONObject(Constants.DATA).getString(Constants.URL_KEY);
                    user.accessToken = AccessToken.getCurrentAccessToken().getToken();
                    insertUserDataIntoDB(user);
                    startActivity(new Intent(SocialLoginActivity.this, HomeScreenActivity.class));
                    finish();
                } else {
                    com.facebook.login.LoginManager.getInstance().logOut();
                    Timber.e("Facebook email not found");
                }
            }
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    /**
     * insert user profile data into database
     *
     * @param user
     */
    private void insertUserDataIntoDB(final User user) {
        new UserDatabaseManager(SocialLoginActivity.this).addUserIntoTable(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                        Timber.d("insertUserDataIntoDB : ", s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("insertUserDataIntoDB : ", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        // Called when the observable has no more data to emit
                    }
                });
    }

    /***
     * initialise classes
     */
    private void intializeInstances() {
        mCallbackManager = CallbackManager.Factory.create();
        mUserSharePreference = new UserSharePreference(this);
        mFBLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "read_custom_friendlists"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * set toolbar title
     */
    protected void setToolBar(String toolBarTitle) {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(StringUtils.isNotEmpty(toolBarTitle) ? toolBarTitle : "");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

