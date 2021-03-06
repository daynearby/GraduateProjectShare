package com.young.share.fragment;

import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.young.share.R;
import com.young.share.base.BaseFragment;
import com.young.share.config.Contants;
import com.young.share.interfaces.AsyncListener;
import com.young.share.model.Comment_HZ;
import com.young.share.model.gson.CommentList;
import com.young.share.network.BmobApi;
import com.young.share.utils.LogUtils;
import com.young.share.views.CommentListView.CommentAdapter;
import com.young.share.views.CommentListView.CommentListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论区显示的内容
 * Created by Nearby Yang on 2016-03-19.
 */

public class CommentFragment extends BaseFragment {

    private String shareMessageId;
    private CommentListView commentListView;
    private CommentAdapter commentAdapter;
    private List<Comment_HZ> dataList = new ArrayList<>();//数据
    private CommentListView.OnItemClickListener onItemClickListener;
    private static final String CACHE_KEY = "fragment_get_comment";

    private static final int GET_MESSAGE = 0x01;//格式化数据
    public static final String BUNLDE_SHARE_MESSAGE_ID = "bunlde_share_message_id";//shareMessageId
    public static final int MESSAGE_LOAD_DATA_FAILURE =0x002;//加载数据失败

    public CommentFragment() {

    }
    @Override
    protected void getDataFromBunlde(Bundle bundle) {
        shareMessageId = bundle.getString(BUNLDE_SHARE_MESSAGE_ID);
    }

    /**
     * 初始化当前fragment的数据
     * 初始化shareid
     */
    private void initFragment() {
        shareMessageId = getArguments().getString(BUNLDE_SHARE_MESSAGE_ID);

    }

    public CommentAdapter getCommentAdapter() {
        return commentAdapter;
    }

    @Override
    protected void onSaveState(Bundle outState) {

    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_comment;
    }

    @Override
    public void initData() {
  /*初始化数据*/
//        initFragment();
        //提示
//        SVProgressHUD.showWithStatus(context, getString(R.string.tips_loading));
        dataList = (List<Comment_HZ>) app.getCacheInstance().getAsObject(CACHE_KEY + shareMessageId);

/*获取评论内容*/
//        app.getThreadInstance().startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
//            @Override
//            public void running() {
//
//            }
//        }));

        initDataByThread();
    }

    /**
     *  获取远程数据，通过线程
     */
    private void initDataByThread(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getComment(shareMessageId);
//            }
//        }).start();

        getComment(shareMessageId);
    }

    @Override
    public void initView() {
        commentListView = $(R.id.clv_frament_com_list);
        commentAdapter = new CommentAdapter(context);

    }

    @Override
    public void bindData() {

        commentAdapter.bindListView(commentListView);
        commentListView.setOnItemClick(new CommentListView.OnItemClickListener() {
            @Override
            public void onItemClick(Comment_HZ comment, int position) {
                onItemClickListener.onItemClick(comment, position);
            }
        });

        if (dataList != null && dataList.size() > 0) {
            commentAdapter.setDatas(dataList);
            commentAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void handler(Message msg) {

        switch (msg.what) {
            case GET_MESSAGE:
                /*评论数据，显示到linearLayout*/
                //关闭显示的框
//                processDialogDismisson();

                commentAdapter.setDatas(dataList);
                commentAdapter.notifyDataSetChanged();

                break;
            case MESSAGE_LOAD_DATA_FAILURE://加载数据失败

//                processDialogDismisson();
                Toast.makeText(context, R.string.tips_loading_faile, Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * 点击评论的回调
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(CommentListView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 获取评论数据
     *
     * @param messageId
     */
    private void getComment(String messageId) {

        JSONObject params = new JSONObject();

        try {
            params.put(Contants.PARAMS_MESSAGE_ID, messageId);
        } catch (JSONException e) {
            LogUtils.d("get comment add params failure" + e.toString());
        }

        BmobApi.AsyncFunction(context, params, BmobApi.GET_MESSAGE_COMMENTS, CommentList.class, new AsyncListener() {
            @Override
            public void onSuccess(Object object) {
                CommentList commentList = (CommentList) object;

                if (dataList == null) {
                    dataList = new ArrayList<>();
                }

                if (commentList.getCommentList().size() > 0) {

                    dataList = commentList.getCommentList();
                }

                if (dataList.size() > 0) {
                    app.getCacheInstance().put(CACHE_KEY + shareMessageId, (Serializable) dataList);
                }
//刷新评论列表
                mhandler.sendEmptyMessage(GET_MESSAGE);


            }

            @Override
            public void onFailure(int code, String msg) {
                mhandler.sendEmptyMessage(MESSAGE_LOAD_DATA_FAILURE);

                LogUtils.d("get comment add params failure.  code = " + code + " message =  " + msg);
            }
        });
    }

//    /**
//     * 关闭进度条提示
//     */
//    private void processDialogDismisson() {
////        LogUtils.d(" isshow" + SVProgressHUD.isShowing(mActivity));
//        if (SVProgressHUD.isShowing(context)) {
//
//            //提示
//            SVProgressHUD.dismiss(context);
//        }
//    }

    /**
     * 发送评论之后进行刷新数据
     */
    public void refreshData() {
        getComment(shareMessageId);
    }
}
