package com.paddy.edcastdemo.app.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.model.User;
import com.paddy.edcastdemo.app.db.UserDatabaseManager;
import com.paddy.edcastdemo.app.utils.CommonUse;
import com.paddy.edcastdemo.app.utils.UserSharePreference;
import com.paddy.edcastdemo.app.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = "HomeScreenActivity";
    private List<String> mTitlesList;
    private List<Fragment> mFragments;
    private BottomSheetBehavior mBehavior;
    private UserSharePreference mUserSharePreference;

    @BindView(R.id.bottom_sheet)
    CardView mBottomSheet;
    @BindView(R.id.viewPagerId)
    ViewPager mViewPager;

    private User mUserSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        ButterKnife.bind(this);

        setToolBar(getString(R.string.my_profile_text));
        inti();
        setTabListData();
        setViewPager();
        setBottomSheet();
    }

    private void inti() {
        mUserSharePreference = new UserSharePreference(this);
    }

    /**
     * set tab list data
     */
    private void setTabListData() {
        mTitlesList = new ArrayList<>();
        mFragments = new ArrayList<>();

        mTitlesList.add(getString(R.string.profile_text));
        mTitlesList.add(getString(R.string.friends_text));

        mFragments.add(new UserInfoFragment());
        mFragments.add(new FBFriendListFragment());
    }

    /**
     * Set viewpager layout
     */
    private void setViewPager() {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), mFragments, mTitlesList);
        mViewPager.setAdapter(viewPagerAdapter);
        tabLayout.setTabMode(mTitlesList.size() <= 3 ? TabLayout.MODE_FIXED : TabLayout.MODE_SCROLLABLE);
        if (mTitlesList.size() == 1) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        mViewPager.setOffscreenPageLimit(mTitlesList.size());
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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

    /**
     * set bottomSheet layout
     */
    private void setBottomSheet() {
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBehavior.setHideable(false);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    /**
     * set User local session data
     *
     * @param user
     */
    public void setUserSession(User user) {
        mUserSession = user;
    }

    /**
     * show update profile dialog
     */
    public void showUpdateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_update_dialog, null);
        dialog.setContentView(view);
        final TextInputLayout nameTextViewLayout = ButterKnife.findById(view, R.id.nameTextInputLayout);
        final TextInputLayout emailTextViewLayout = ButterKnife.findById(view, R.id.emailTextInputLayout);
        if (mUserSession != null) {
            nameTextViewLayout.getEditText().setText(mUserSession.name);
            emailTextViewLayout.getEditText().setText(mUserSession.email);
        }
        Button updateProfile = ButterKnife.findById(view, R.id.updateProfile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData(nameTextViewLayout, emailTextViewLayout)) {
                    updateProfileData(nameTextViewLayout.getEditText(), emailTextViewLayout.getEditText(), dialog);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /***
     * update profile data
     * @param nameTextView
     * @param emailTextView
     * @param dialog
     */
    private void updateProfileData(EditText nameTextView, EditText emailTextView, Dialog dialog) {
        User user = new User();
        user.id = mUserSharePreference.getUserId();
        user.name = nameTextView.getText().toString();
        user.email = emailTextView.getText().toString();

        new UserDatabaseManager(HomeScreenActivity.this).updateUserInfo(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(User user1) {
                        Log.d(TAG, "onNext: " + user1.email);
                        mViewPager.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * Show user logout alert dialog with two action
     */
    private void showLogoutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(getString(R.string.yes_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mUserSharePreference.clearPreferences();
                                com.facebook.login.LoginManager.getInstance().logOut();
                                new UserDatabaseManager(HomeScreenActivity.this).deleteAll();
                                dialogInterface.dismiss();
                                finish();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
    }

    @OnClick(R.id.myProfile)
    public void profileTextClick() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @OnClick(R.id.updateProfile)
    public void updateProfileClick() {
        showUpdateDialog();
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.logout)
    public void logoutBtnClick() {
        showLogoutDialog();
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    /**
     * validating input fields
     */
    private boolean validateData(TextInputLayout nameTextInputLayout, TextInputLayout emailTextInputLayout) {
        CommonUse.setErrorDisabled(nameTextInputLayout);
        CommonUse.setErrorDisabled(emailTextInputLayout);

        if (!StringUtils.isNotEmpty(nameTextInputLayout.getEditText().getText().toString())) {
            CommonUse.setInputLayoutError(nameTextInputLayout, getString(R.string.name_empty_error));
            return false;
        } else if (!StringUtils.isNotEmpty(emailTextInputLayout.getEditText().getText().toString())) {
            CommonUse.setInputLayoutError(emailTextInputLayout, getString(R.string.email_empty_error));
            return false;
        } else if (!CommonUse.isValidEmail(emailTextInputLayout.getEditText().getText().toString())) {
            CommonUse.setInputLayoutError(emailTextInputLayout, getString(R.string.email_empty_error));
            return false;
        }
        return true;
    }

}
