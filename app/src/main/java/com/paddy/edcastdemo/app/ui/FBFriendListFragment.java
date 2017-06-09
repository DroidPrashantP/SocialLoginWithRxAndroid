package com.paddy.edcastdemo.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.model.User;
import com.paddy.edcastdemo.app.utils.Constants;
import com.paddy.edcastdemo.app.utils.JSONUtil;
import com.paddy.edcastdemo.app.utils.ProgressDialog;
import com.paddy.edcastdemo.app.utils.UserSharePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FBFriendListFragment extends Fragment {
    private static final String TAG = "FBFriendListFragment";
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.emptyView)
    View mEmptyView;
    private FriendListCustomAdapter mFriendListCustomAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fbfriend_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setRecyclerView();
        getRequestForFriendList();

    }

    /**
     * setup list view
     */
    private void setRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mFriendListCustomAdapter = new FriendListCustomAdapter(new ArrayList<User>(), getActivity());
        mRecyclerView.setAdapter(mFriendListCustomAdapter);
    }

    /**
     * get friend List
     */
    private void getRequestForFriendList() {
        ProgressDialog.showProgress(getActivity(), true);
        final String url = "/" + new UserSharePreference(getActivity()).getUserId() + "/friends";
        Observable<GraphResponse> fetchFBFriendList = Observable.create(new ObservableOnSubscribe<GraphResponse>() {
            @Override
            public void subscribe(final ObservableEmitter<GraphResponse> emitter) throws Exception {
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(), url
                        ,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                ProgressDialog.closeProgress();
                                emitter.onNext(response);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString(Constants.FIELDS, Constants.FACEBOOK_PARAM_KEYS);
                request.setParameters(parameters);
                request.executeAsync();
            }
        });

        fetchFBFriendList.subscribeOn(Schedulers.newThread()) // Create a new Thread
                .observeOn(AndroidSchedulers.mainThread()) // Use the UI thread
                .subscribe(new Observer<GraphResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(GraphResponse response) {
                        ProgressDialog.closeProgress();
                        parseFriendsListData(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.getMessage());
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * parse friends list data
     *
     * @param response
     */
    private void parseFriendsListData(GraphResponse response) {
        try {
            JSONObject jsonObject = response.getJSONObject();
            if (jsonObject != null) {
                JSONArray data = JSONUtil.getJSONArray(jsonObject, Constants.DATA);
                ArrayList<User> userArrayList = new ArrayList<>();
                if (data != null && data.length() > 0) {
                    isDataAvailable(true);
                    for (int i = 0; i < data.length(); i++) {
                        User user = new User(data.getJSONObject(i));
                        userArrayList.add(user);
                    }
                    // update list
                    if (mFriendListCustomAdapter != null) {
                        mFriendListCustomAdapter.updateList(userArrayList);
                    }

                } else {
                    isDataAvailable(false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Timber.e(e.getMessage());
        }
    }

    /**
     * show and hide view
     *
     * @param isDataPresent
     */
    public void isDataAvailable(boolean isDataPresent) {
        if (isDataPresent) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }
}
