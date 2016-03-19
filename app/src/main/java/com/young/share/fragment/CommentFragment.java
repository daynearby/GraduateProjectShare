package com.young.share.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;

import com.young.share.R;
import com.young.share.base.BaseFragment;

/**
 *
 * 评论区显示的内容
 * Created by Nearby Yang on 2016-03-19.
 */

public class CommentFragment extends BaseFragment {

    private String shareMessageId;

    public CommentFragment() {
    }

    @SuppressLint("ValidFragment")
    public CommentFragment(Context context, String shareMessageId) {
        super(context);
        this.shareMessageId = shareMessageId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_comment;
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
