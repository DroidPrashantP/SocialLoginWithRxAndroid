package com.paddy.edcastdemo.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.db.UserDatabaseManager;
import com.paddy.edcastdemo.app.model.User;
import com.paddy.edcastdemo.app.utils.CircularNetworkImageView;
import com.paddy.edcastdemo.app.utils.StringUtils;
import com.paddy.edcastdemo.app.utils.UpdateUserInfo;
import com.paddy.edcastdemo.app.utils.UserSharePreference;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


public class UserInfoFragment extends Fragment implements UpdateUserInfo {
    private static final String TAG = "UserInfoFragment";
    @BindView(R.id.pictureView)
    CircularNetworkImageView mPicture;
    @BindView(R.id.nameTextView)
    TextView mNameTextView;
    @BindView(R.id.emailTextView)
    TextView mEmailTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        updateProfileCard();
    }

    /**
     * get user info from local database and display
     */
    private void updateProfileCard() {
        new UserDatabaseManager(getActivity()).getUserData(new UserSharePreference(getActivity()).getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(User user) {
                        if (StringUtils.isNotEmpty(user.picture)) {
                            Picasso.with(getActivity()).load(user.picture).into(mPicture);
                        }
                        mNameTextView.setText(user.name);
                        mEmailTextView.setText(user.email);
                        ((HomeScreenActivity) getActivity()).setUserSession(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        // Called when the observable has no more data to emit
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void update() {
        updateProfileCard();
    }

}
