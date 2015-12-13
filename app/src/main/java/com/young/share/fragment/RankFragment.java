package com.young.share.fragment;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.young.base.BaseFragment;
import com.young.share.MainActivity;
import com.young.share.R;

/**
 * 排行榜
 * <p>
 * Created by Nearby Yang on 2015-12-09.
 */
public class RankFragment extends BaseFragment {

// TODO: 2015-12-07 pinterestView瀑布流布局，屏幕适配

    private static final String tag = "tank";

    public RankFragment(Context context) {
        super(context);
    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rank;
    }

    @Override
    public void initData() {
        Log.d(tag, "initdata");
    }

    @Override
    public void initView() {

    }


    @Override
    public void bindData() {
        Log.d(tag, "bindData");
    }

    @Override
    public void handler(Message msg) {

    }

}
