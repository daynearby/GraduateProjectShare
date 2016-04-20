package com.young.share;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.adapter.RankListAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ComparatorImpl;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.RemoteModel;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.gson.RankList;
import com.young.share.network.BmobApi;
import com.young.share.network.NetworkReuqest;
import com.young.share.shareSocial.SocialShareByIntent;
import com.young.share.shareSocial.SocialShareManager;
import com.young.share.utils.CommonUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排行棒的列表信息
 * <p/>
 * Created by Nearby Yang on 2015-12-26.
 */
public class RankListActivity extends BaseAppCompatActivity {

    @InjectView(R.id.sw_ranklist_refresh)
    private SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.list_ranklist)
    private ListView listview;
    @InjectView(R.id.im_ranklist_tips)
    private ImageView bgLayout;

    private RankListAdapter rankAdapter;

    private String tag;
    private int key;
    private int skip = 0;
    private int startIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 0;//下拉次数
    private boolean isGetMore = false;
    private boolean isHadData = false;//本地是否有数据，false --> 没有

    private List<RemoteModel> remoteList;

    private static final int HANDLER_GET_DATA = 0x01;
    private static final int HANDLER_GET_NO_DATA = 0x02;//没有数据
    private static final int HANDLER_LOAD_DATA_FAILURE = 0x03;//加载数据失败
    private static final int HANDLER_LOAD_MORE_DATA_FAILURE = 0x04;//加载更多数据失败

    @Override
    public int getLayoutId() {
        return R.layout.activity_ranklist;
    }

    @Override
    public void initData() {
        initializeToolbar();

        //标志
        tag = getIntent().getStringExtra(Contants.INTENT_RANK_TYPE);
        key = getString(R.string.tag_manywanttogo).equals(tag) ? ComparatorImpl.COMPREHENSIVE : ComparatorImpl.COMPREHENSIVE_OTHERS;
        setTitle(tag);

        remoteList = (List<RemoteModel>) app.getCacheInstance().getAsObject(tag);
        if (!(isHadData = remoteList != null && remoteList.size() > 0))
            remoteList = new ArrayList<>();

//获取数据
        getDataFromRemote();

    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_rank);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();

            }
        });

    }


    @Override
    public void findviewbyid() {

        rankAdapter = new RankListAdapter(mActivity);

        listview.setAdapter(rankAdapter);

        new ListViewRefreshListener(listview, swipeRefreshLayout, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {//上拉

                if (CommonUtils.isNetworkAvailable()) {//有网络
//                            startRow += Contants.PAGER_NUMBER;

                    if (remoteList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                        endIndex = remoteList.size() < Contants.PAGE_SIZE +
                                Contants.PAGE_SIZE * PUSH_TIMES ? remoteList.size() :
                                Contants.PAGE_SIZE + Contants.PAGE_SIZE * PUSH_TIMES;

                        rankAdapter.setData(remoteList.subList(startIndex, endIndex));

                        PUSH_TIMES++;

                    } else {
                        isGetMore = true;
//                                Toast.makeText(ctx, R.string.no_more_messages, Toast.LENGTH_SHORT).show();
                        skip = remoteList.size();
                        getDataFromRemote();
                    }

                } else {//没有网络
                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.without_network));
                }

                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void pullToRefresh() {//下拉

                PUSH_TIMES = 1;
                skip = 0;
                isGetMore = false;

                if (CommonUtils.isNetworkAvailable()) {//有网络

                    getDataFromRemote();

                } else {

                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.without_network));

                }


            }
        });


    }

    @Override
    public void bindData() {

        swipeRefreshLayout.setColorScheme(getResources().getColor(android.R.color.holo_red_light),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_orange_light));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RemoteModel comm = remoteList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, comm);

                if (comm.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息

                    bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_RANK_LIST_ACTIVITY);

                    mStartActivity(MessageDetailActivity.class, bundle);

                } else {//商家优惠


                    mStartActivity(DiscoutDetailActivity.class, bundle);
                }
            }
        });

    }

    @Override
    public void handerMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_GET_DATA:

                refreshUI();
                break;

            case HANDLER_GET_NO_DATA:
                bgLayout.setVisibility(View.VISIBLE);
                bgLayout.setImageResource(R.drawable.icon_conten_empty);
                break;

            case HANDLER_LOAD_DATA_FAILURE://加载数据失败
                toast(R.string.tips_loading_faile);
                break;

            case HANDLER_LOAD_MORE_DATA_FAILURE://加载更多数据，为空
                toast(R.string.no_more_messages);
                break;
        }

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            mActivity.finish();
            return true;
        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_content_copy://复制文本
                StringUtils.CopyText(this, rankAdapter.getContentString());
                break;

            case R.id.menu_content_share://分享文本
                SocialShareManager.shareText(this, rankAdapter.getContentString());
                break;
            case R.id.menu_image_save://保存图片
                NetworkReuqest.call2(this, rankAdapter.getImageUrl());
                break;

            case R.id.menu_image_share_all://分享全部图片
//下载图片
                SocialShareByIntent.downloadImagesAndShare(mActivity, rankAdapter.getImagesUrl());
//                SocialShareManager.shareImage(context, discAdapter.getImageUrl());

                break;
            case R.id.menu_image_share_singal://分享打仗图片
                SocialShareByIntent.downloadImageAndShare(mActivity, rankAdapter.getImageUrl());
                break;
        }

        return super.onContextItemSelected(item);
    }



    /**
     * 刷新列表，最新数据
     */
    private void refreshUI() {
        if (isGetMore) {
            endIndex = remoteList.size() < (PUSH_TIMES + 1) * Contants.PAGE_SIZE ?
                    remoteList.size() : (PUSH_TIMES + 1) * Contants.PAGE_SIZE;
        } else {

            endIndex = remoteList.size() < Contants.PAGE_SIZE ? remoteList.size() : endIndex;

        }
        if (remoteList != null && remoteList.size() > 0) {
            rankAdapter.setData(remoteList.subList(startIndex, endIndex));
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 从远程数据库获取数据
     * 解析并且转换成remoteModel
     */
    private void getDataFromRemote() {

        JSONObject parameters = new JSONObject();
        String funcationName;

        if (key == ComparatorImpl.COMPREHENSIVE) {
            funcationName = BmobApi.GET_HEAT_MESSAGES;
        } else {
            funcationName = BmobApi.GET_RANK_DATA;
            try {
                parameters.put(Contants.PARAM_TAG, tag);
            } catch (JSONException e) {
                LogUtils.d("添加参数失败 " + e.toString());
            }
        }

        try {
            parameters.put(Contants.PARAM_SKIP, skip);

        } catch (JSONException e) {
            LogUtils.d("添加参数失败 " + e.toString());
        }


        BmobApi.AsyncFunction(mActivity, parameters, funcationName, RankList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                RankList rankLists = (RankList) object;

                List<ShareMessage_HZ> sharemessagesList = rankLists.getSharemessages();
                List<DiscountMessage_HZ> discountMessagesList = rankLists.getDiscountMessages();

                if (isGetMore) {
                    if (sharemessagesList.size() > 0 || discountMessagesList.size() > 0) {

                        for (ShareMessage_HZ share : sharemessagesList) {
                          /*格式化数据，通用格式*/
                            remoteList.add(DataFormateUtils.formateDataDiscover(share));
                        }

                        for (DiscountMessage_HZ discountMessage : discountMessagesList) {
                        /*格式化数据，通用格式*/
                            remoteList.add(DataFormateUtils.formateDataDiscount(discountMessage));
                        }

                        //排序
                        if (remoteList != null && remoteList.size() > 0) {
                            Collections.sort(remoteList, new ComparatorImpl(key));
                            app.getCacheInstance().put(tag, (Serializable) remoteList);
                            mHandler.sendEmptyMessage(HANDLER_GET_DATA);
                        }

                    } else {
                        mHandler.sendEmptyMessage(HANDLER_LOAD_MORE_DATA_FAILURE);
                    }


                } else {

/*分享信息的数据*/
                    if (sharemessagesList.size() > 0 || discountMessagesList.size() > 0) {
                        //清空原来的数据
                        if (remoteList != null && remoteList.size() > 0) {
                            remoteList.clear();
                        }


                        for (ShareMessage_HZ share : sharemessagesList) {
                          /*格式化数据，通用格式*/
                            remoteList.add(DataFormateUtils.formateDataDiscover(share));
                        }

                        for (DiscountMessage_HZ discountMessage : discountMessagesList) {
                        /*格式化数据，通用格式*/
                            remoteList.add(DataFormateUtils.formateDataDiscount(discountMessage));
                        }

//进行排序
                        if (remoteList != null && remoteList.size() > 0) {
                            Collections.sort(remoteList, new ComparatorImpl(key));
                            app.getCacheInstance().put(tag, (Serializable) remoteList);
                            mHandler.sendEmptyMessage(HANDLER_GET_DATA);
                        }
                    } else {
                        mHandler.sendEmptyMessage(HANDLER_GET_NO_DATA);
                    }
                }


            }

            @Override
            public void onFailure(int code, String msg) {
                mHandler.sendEmptyMessage(HANDLER_LOAD_DATA_FAILURE);
                LogUtils.d("get rank data failure. code = " + code + " message = " + msg);
            }
        });

    }
}
