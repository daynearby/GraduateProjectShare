package com.young.share;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.young.share.adapter.WantToGoAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.interfaces.ListViewRefreshListener;
import com.young.share.model.Collection_HZ;
import com.young.share.model.RemoteModel;
import com.young.share.model.gson.CollectionList;
import com.young.share.network.BmobApi;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 想去的类
 * 也就是用户的收藏记录
 * <p/>
 * Created by Nearby Yang on 2016-04-02.
 */
public class WantToGoActivity extends BaseAppCompatActivity {

    @InjectView(R.id.lv_record_comm)
    private ListView listview;
    @InjectView(R.id.sw_record_comm_refresh)
    private SwipeRefreshLayout swipeRefresh;
    @InjectView(R.id.im_record_content_empty)
    private ImageView contentEmpty;

    private WantToGoAdapter wantAdapter;
    private List<RemoteModel> dataList = new ArrayList<>();

    private int starIndex = 0;
    private int endIndex = 20;
    private int PUSH_TIMES = 1;//上拉次数
    private boolean isGetMore = false;//从远程数据库获取更多数据
    private boolean isEmpty = false;//内容是否为空

    private int RECORD_TYPE;//记录类型
    private int Skip = 0;//跳过的数量

    private static final int MESSAFE_TYPE_MODEL = 0x01;//通用的数据类型

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_comm;
    }

    @Override
    protected void initData() {
        initialiToolbar();
        setTitle(R.string.txt_wantto_go);
        dataList = (List<RemoteModel>) app.getCacheInstance().getAsObject(getString(R.string.collection_record) + cuser.getObjectId());

        getCollectionRec();

    }

    @Override
    protected void findviewbyid() {
        wantAdapter = new WantToGoAdapter(mActivity);
        listview.setAdapter(wantAdapter);
        listview.setOnItemClickListener(new itemClick());


    }

    @Override
    protected void bindData() {
        swipeRefresh.setColorSchemeResources(R.color.red_light,
                R.color.green_light,
                R.color.blue_bright,
                R.color.orange_light);
//列表刷新
        new ListViewRefreshListener(listview, swipeRefresh, new ListViewRefreshListener.RefreshListener() {
            @Override
            public void pushToRefresh() {//上拉
                Skip += Contants.RECORD_LENGHT;

                //分享信息
                if (dataList.size() > Contants.PAGE_SIZE * PUSH_TIMES) {

                    endIndex = dataList.size() < endIndex + PUSH_TIMES * Contants.PAGE_SIZE ? dataList.size() :
                            endIndex + PUSH_TIMES * Contants.PAGE_SIZE;

                    wantAdapter.setData(dataList.subList(starIndex, endIndex));

                    PUSH_TIMES++;

                } else {
                    isGetMore = true;
                    Skip = dataList.size();
                    //获取数据
                    getCollectionRec();
                }

                swipeRefresh.setRefreshing(false);
                LogUtils.d("上拉刷新");

            }

            @Override
            public void pullToRefresh() {//下拉
                Skip = 0;
//                dataList.clear();
                isGetMore = false;
                //获取分享记录
                getCollectionRec();

            }
        });

        if (dataList != null && dataList.size() > 0) {
            mHandler.sendEmptyMessage(MESSAFE_TYPE_MODEL);
        }

    }

    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_record_com);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });

    }

    @Override
    protected void handerMessage(Message msg) {
        swipeRefresh.setRefreshing(false);
        if (!isEmpty) {
            refreshUI();
        } else {

            contentEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * setdata &&  notification
     */
    private void refreshUI() {

        if (isGetMore) {
            endIndex = dataList.size() < (PUSH_TIMES + 1) * Contants.PAGE_SIZE ?
                    dataList.size()
                    : (PUSH_TIMES + 1) * Contants.PAGE_SIZE;
        } else {
            endIndex = dataList.size() < Contants.PAGE_SIZE ? dataList.size() : endIndex;
        }
        //刷新UI
        wantAdapter.setData(dataList.subList(starIndex, endIndex));
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

    /**
     * 收藏记录
     */
    public void getCollectionRec() {
        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAM_USERID, cuser.getObjectId());
            params.put(Contants.PARAM_SKIP, String.valueOf(Skip));
        } catch (JSONException e) {
            LogUtils.d("add params failure 　" + e.toString());
        }

        BmobApi.AsyncFunction(mActivity, params, BmobApi.GET_RECOLLECTION_RECORD, CollectionList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                CollectionList collectionList = (CollectionList) object;

                if (isGetMore) {
                    if (collectionList.getCollecList().size() > 0) {

                        dataList.addAll(formateData(collectionList.getCollecList()));

                    } else {
                        toast(R.string.no_more_messages);
                    }
                } else {

                    if (dataList == null) {
                        dataList = new ArrayList<>();
                    }
                    if (collectionList.getCollecList().size() > 0) {
//                格式化数据
                        dataList = formateData(collectionList.getCollecList());

                    } else {
                        isEmpty = dataList.size() <= 0;

                    }

                }
/**
 * 保存数据到本地
 */
                if (dataList.size() > 0) {
                    app.getCacheInstance().put(getString(R.string.collection_record) + cuser.getObjectId(), (Serializable) dataList);
                }

                mHandler.sendEmptyMessage(MESSAFE_TYPE_MODEL);
            }

            @Override
            public void onFailure(int code, String msg) {
                isEmpty = dataList.size() <= 0;
                //提示框处理
                CommonFunctionUtils.processDialog(mActivity);
                toast(R.string.tips_loading_faile);
                LogUtils.d("get share messages failure. code = " + code + " message = " + msg);

            }
        });

    }

    /**
     * 格式化数据，将分享信息、商家优惠信息区分开
     *
     * @param collectionList
     * @return
     */
    private List<RemoteModel> formateData(List<Collection_HZ> collectionList) {
        List<RemoteModel> remoteModelList = new ArrayList<>();
        for (Collection_HZ collection : collectionList) {
            if (collection.getShMsgId() != null) {
/*分享,格式化成通用的类型*/
                remoteModelList.add(DataFormateUtils.formateDataDiscover(collection.getShMsgId()));

            } else {
      /*优惠,格式化*/
                remoteModelList.add(DataFormateUtils.formateDataDiscount(collection.getDtMsgId()));

            }


        }
        return remoteModelList;
    }

    /**
     * item的点击事件
     */
    private class itemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            RemoteModel remoteModel = dataList.get(position);
            bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, remoteModel);
            bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_PERSONAL_ACTIVITY);//shareMessage

            if (remoteModel.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息
                mStartActivity(MessageDetailActivity.class, bundle);
            } else {//折扣信息
                mStartActivity(DiscoutDetailActivity.class, bundle);
            }


        }
    }

}
