package com.young.share.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;

import com.young.share.R;
import com.young.share.base.BaseFragment;

import java.util.List;

/**
 *
 * 点赞区显示的内容
 * Created by Nearby Yang on 2016-03-19.
 */

public class LikeFragment extends BaseFragment {
    private List<String> userIdList;

    public LikeFragment() {
    }

    @SuppressLint("ValidFragment")
    public LikeFragment(Context context, List<String> userIdList) {
        super(context);
        this.userIdList = userIdList;

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_want_to_go_like;
    }

    @Override
    public void initData() {



    }

    @Override
    public void initView() {

    }

    @Override
    public void bindData() {

    }

    @Override
    public void handler(Message msg) {

    }
}
