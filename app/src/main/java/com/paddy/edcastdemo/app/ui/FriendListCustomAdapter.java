package com.paddy.edcastdemo.app.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paddy.edcastdemo.app.R;
import com.paddy.edcastdemo.app.model.User;
import com.paddy.edcastdemo.app.utils.CircularNetworkImageView;
import com.paddy.edcastdemo.app.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prashant on 6/8/17.
 */

public class FriendListCustomAdapter extends RecyclerView.Adapter<FriendListCustomAdapter.ViewHolder> {
    private static final String TAG = "FriendListCustomAdapter";
    private List<User> mUserList;
    private Context mContext;

    public FriendListCustomAdapter(List<User> mUserList, Context mContext) {
        this.mUserList = mUserList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_friend_list_custom_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUserList.get(position);
        Picasso.with(mContext).load(user.picture).into(holder.picture);
        holder.name.setText(user.name);
        holder.email.setText(user.email);
    }

    public void updateList(ArrayList<User> userArrayList) {
        mUserList.addAll(userArrayList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pictureView)
        CircularNetworkImageView picture;
        @BindView(R.id.nameTextView)
        TextView name;
        @BindView(R.id.emailTextView)
        TextView email;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
