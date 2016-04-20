package com.young.share.fragment;

import android.content.Intent;
import android.os.Bundle;
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
 * <p>
 * Created by Nearby Yang on 2015-12-09.
 */
public class RankFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private RankAdapter rankAdapter;
    //瀑布式
    private StaggeredGridLayoutManager layoutManager;
    private List<RankBean> dataList = new ArrayList<>();
    private List<String> tagList;

    private static final String tag = "tank";
    private static final int GET_DATA = 101;
// TODO: 2016-04-15 打开新的activity闪烁

    public RankFragment() {

    }

    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }



    @Override
    public int getLayoutId() {
        return R.layout.fragment_rank;
    }

    @Override
    public void initData() {

        getDatas();

    }

    @Override
    public void initView() {

        recyclerView = $(R.id.recv_rank);
        rankAdapter = new RankAdapter(context);

            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);


    }


    @Override
    public void bindData() {

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

        rankAdapter.setDataList(dataList);
        recyclerView.setAdapter(rankAdapter);
    }

    @Override
    public void handler(Message msg) {

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
