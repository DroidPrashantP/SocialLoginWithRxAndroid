package com.paddy.edcastdemo.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.model.User;
import com.paddy.edcastdemo.app.utils.Constants;
import com.paddy.edcastdemo.app.utils.ProgressDialog;
import com.paddy.edcastdemo.app.utils.UserSharePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FBFriendListFragment extends Fragment {
    private static final String TAG = "FBFriendListFragment";
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
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
        //setUpList();
        getFriendList();

    }

    /**
     * get friend List
     */
    private void getFriendList() {
        ProgressDialog.showProgress(getActivity(), true);
        Observable<List<User>> fetchFBFriendList = Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<User>> emitter) throws Exception {
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + new UserSharePreference(getActivity()).getUserId() + "/friends",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                ProgressDialog.closeProgress();
                                // Insert your code here
                                try {
                                    JSONObject jsonObject = response.getJSONObject();
                                    if (jsonObject != null) {
                                        JSONArray data = jsonObject.getJSONArray(Constants.DATA);
                                        if (data != null && data.length() > 0) {
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d(TAG, "getFriendList: " + response.getJSONObject());
                            }
                        });
                request.executeAsync();
            }
        });

        fetchFBFriendList.subscribeOn(Schedulers.newThread()) // Create a new Thread
                .observeOn(AndroidSchedulers.mainThread()) // Use the UI thread
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setUpList() {
        ArrayList<User> list = new ArrayList<>();

        User user1 = new User();
        user1.id = "1";
        user1.name = "Prashant";
        user1.email = "1abc@gmail.com";
        user1.picture = "https://www.planwallpaper.com/static/images/desktop-year-of-the-tiger-images-wallpaper.jpg";

        User user2 = new User();
        user2.name = "Patil";
        user2.id = "2";
        user2.email = "2abc@gmail.com";
        user2.picture = "https://www.planwallpaper.com/static/images/desktop-year-of-the-tiger-images-wallpaper.jpg";

        User user3 = new User();
        user3.name = "Parola";
        user3.id = "3";
        user3.email = "3abc@gmail.com";
        user3.picture = "https://www.planwallpaper.com/static/images/desktop-year-of-the-tiger-images-wallpaper.jpg";

        list.add(user1);
        list.add(user2);
        list.add(user3);

        mFriendListCustomAdapter.updateList(list);

    }

    private void setRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mFriendListCustomAdapter = new FriendListCustomAdapter(new ArrayList<User>(), getActivity());
        mRecyclerView.setAdapter(mFriendListCustomAdapter);
    }
}
