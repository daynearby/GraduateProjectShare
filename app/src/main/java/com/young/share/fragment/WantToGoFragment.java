package com.young.share.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.widget.TextView;

import com.young.share.R;
import com.young.share.base.BaseFragment;

import java.util.List;

/**
 * 想去区显示的内容
 * Created by Nearby Yang on 2016-03-19.
 */
public class WantToGoFragment extends BaseFragment {
    private TextView avatar;
private List<String> wantUserId;

    public WantToGoFragment() {
    }

    @SuppressLint("ValidFragment")
    public WantToGoFragment(Context context, List<String> wantUserId) {
        super(context);
        this.wantUserId = wantUserId;
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
        avatar = $(R.id.tv_want_to_go_avatar);
    }

    @Override
    public void bindData() {



    }

    @Override
    public void handler(Message msg) {

    }
}
