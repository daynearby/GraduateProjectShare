package com.young.share.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.young.share.R;
import com.young.share.RankListActivity;
import com.young.share.adapter.RankAdapter;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
import com.young.share.model.RankBean;
import com.young.share.utils.XmlUtils;
import com.young.share.views.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜
 * <p/>
 * Created by Nearby Yang on 2015-12-09.
 */
@SuppressLint("ValidFragment")
public class RankFragment extends BaseFragment {

    private RankAdapter rankAdapter;
    private List<RankBean> dataList = new ArrayList<>();
    private List<String> tagList;

    private static final String tag = "tank";
    private static final int GET_DATA = 101;

    public RankFragment() {
    }

    public RankFragment(Context context) {
        super(context);

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_rank;
    }

    @Override
    public void initData() {

        getDatas();

    }

    // TODO: 2016-03-06 修复拖动多次出现崩溃
    @Override
    public void initView() {
        RecyclerView recyclerView = $(R.id.recv_rank);
        rankAdapter = new RankAdapter(context);
        //瀑布式
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //item之间距离
        recyclerView.addItemDecoration(new SpacesItemDecoration(16));
        // 设置ItemAnimator
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        rankAdapter.setOnItemClickListener(new RankAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {

//                Toast.makeText(context, "点击 = " + position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, RankListActivity.class);
                intent.putExtra(Contants.INTENT_RANK_TYPE, tagList.get(position));
                startActivity(intent);

            }
        });

        recyclerView.setAdapter(rankAdapter);

    }


    @Override
    public void bindData() {

    }

    @Override
    public void handler(Message msg) {

        switch (msg.what) {
            case GET_DATA:
                rankAdapter.setDataList(dataList);
                break;
        }
    }

    /**
     * 从资源文件中获取数据
     */
    public void getDatas() {

        RankBean rankBean;
        tagList = XmlUtils.getSelectTag(context);
        List<Integer> colorList = XmlUtils.getSelectRankBackgroundColor(context);
        List<Integer> iconList = XmlUtils.getSelectRankIcon(context);

        if (tagList != null && tagList.size() > 0
                && colorList != null && colorList.size() > 0
                && iconList != null && iconList.size() > 0) {
            for (int i = 0; i < tagList.size(); i++) {
                rankBean = new RankBean();

                rankBean.setImageText(tagList.get(i));
                rankBean.setBackgroundColor(colorList.get(i));
                rankBean.setIconId(iconList.get(i));

                dataList.add(rankBean);
            }
        }

        mhandler.sendEmptyMessage(GET_DATA);

    }



}
