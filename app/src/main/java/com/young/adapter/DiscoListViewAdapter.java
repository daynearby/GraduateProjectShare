package com.young.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.young.adapter.CommentAdapter.CommAdapter;
import com.young.adapter.CommentAdapter.ViewHolder;
import com.young.config.Contants;
import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.share.R;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.StringUtils;
import com.young.views.PopupWinImageBrowser;
import com.young.views.PopupWinUserInfo;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 实例化
 * <p/>
 * 父类中setdata并且刷新
 * <p/>
 * <p/>
 * Created by yangfujing on 15/10/10.
 */
public class DiscoListViewAdapter extends CommAdapter<ShareMessage_HZ> {

    private GridView myGridview;
    //    private ShareMessage shareMessage;
    private PopupWinUserInfo userInfo;
    private User user;
    private int strId;

    /**
     * 实例化对象
     *
     * @param context
     */
    public DiscoListViewAdapter(Context context) {
        super(context);
        user = BmobUser.getCurrentUser(ctx, User.class);
    }


    @Override
    public void convert(ViewHolder holder, ShareMessage_HZ shareMessage) {
//        this.shareMessage = shareMessage;
        User user = shareMessage.getUserId();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        myGridview = holder.getView(R.id.id_gv_shareimg);//分享的照片
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        TextView comment_tv = holder.getView(R.id.id_tx_comment);//评论数量
        ((TextView) holder.getView(R.id.tv_item_share_main_created_at)).setText(shareMessage.getCreatedAt());//创建时间

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());
        // 特殊文字处理,将表情等转换一下
        content_tv.setText(StringUtils.getEmotionContent(
                ctx, content_tv, TextUtils.isEmpty(shareMessage.getShContent()) ? "" : shareMessage.getShContent()));

        nickname_tv.setText(TextUtils.isEmpty(user.getNickName()) ? "" : user.getNickName());

        ImageHandlerUtils.loadIamge(ctx,
                TextUtils.isEmpty(user.getAvatar()) ? Contants.DEFAULT_AVATAR : user.getAvatar(), avatar, false);

        tag_tv.setText(shareMessage.getShTag());
        wanto_tv.setText(shareMessage.getShWantedNum() == null ? "0" : String.valueOf(shareMessage.getShWantedNum().size()));
        hadgo_tv.setText(shareMessage.getShVisitedNum() == null ? "0" : String.valueOf(shareMessage.getShVisitedNum().size()));
        comment_tv.setText(String.valueOf(shareMessage.getShCommNum()));
        gridViewAdapter.setDatas(shareMessage.getShImgs(), false);
        myGridview.setOnItemClickListener(new itemClick(shareMessage.getShImgs()));

        nickname_tv.setOnClickListener(new click(user));
        avatar.setOnClickListener(new click(user));
        wanto_tv.setOnClickListener(new click(shareMessage));
        hadgo_tv.setOnClickListener(new click(shareMessage));
        comment_tv.setOnClickListener(new click(shareMessage));
        tag_tv.setOnClickListener(new click(shareMessage.getShTag()));
    }

    @Override
    public int getlayoutid() {
        return R.layout.item_share_main;
    }

    private class click implements View.OnClickListener {

        private Object o;
        private ShareMessage_HZ shareMessage;

        public click(Object o) {
            this.o = o;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.id_im_userH://用户资料
                    User u = (User) o;
                    userInfo = new PopupWinUserInfo(ctx, u);
                    userInfo.onShow(v);
                    LogUtils.logD("用户资料 = " + u.toString());
                    break;

                case R.id.id_tx_wantogo://想去--数量
                    boolean hadWant = false;
                    shareMessage = (ShareMessage_HZ) o;
                    List<String> shWantedNum = shareMessage.getShWantedNum();
                    for (String userId : shWantedNum) {
                        hadWant = user.getObjectId().equals(userId);
                    }
                    wantToGo(hadWant, shareMessage, v);


                    break;

                case R.id.id_hadgo://去过--数量
                    boolean hadGo = false;
                    shareMessage = (ShareMessage_HZ) o;
                    List<String> shVisitedNum = shareMessage.getShVisitedNum();
                    for (String userId : shVisitedNum) {
                        hadGo = user.getObjectId().equals(userId);
                    }

                    visit(hadGo, shareMessage, v);
                    break;

                case R.id.id_tx_comment://评论数量

                    break;
                case R.id.id_tx_tab://标签

                    break;

            }
        }
    }

    /**
     * 去过的逻辑处理
     *
     * @param hadGo
     * @param shareMessage
     * @param v
     */
    private void visit(boolean hadGo, ShareMessage_HZ shareMessage, View v) {
        if (hadGo) {
            strId = R.string.not_visit;
            shareMessage.getShVisitedNum().remove(user.getObjectId());
        } else {
            strId = R.string.cancel_collect_success;
            shareMessage.getShVisitedNum().add(user.getObjectId());
        }


        ((TextView) v).setText(String.valueOf(shareMessage.getShVisitedNum() == null ?
                0 : shareMessage.getShVisitedNum().size()));

        shareMessage.update(ctx, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    /**
     * 想去逻辑处理
     *
     * @param hadWant
     * @param shareMessage
     * @param v
     */
    private void wantToGo(boolean hadWant, ShareMessage_HZ shareMessage, View v) {

        if (hadWant) {
            strId = R.string.cancel_collect_success;
            shareMessage.getShWantedNum().remove(user.getObjectId());
        } else {
            strId = R.string.collect_success;
            shareMessage.getShWantedNum().add(user.getObjectId());
        }

        ((TextView) v).setText(String.valueOf(shareMessage.getShWantedNum() == null ?
                0 : shareMessage.getShWantedNum().size()));

        shareMessage.update(ctx, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                mToast(strId);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    private void updateShMs() {
//shareMessage

    }

    /**
     * item click listener
     */
    private class itemClick implements AdapterView.OnItemClickListener {

        private PopupWinImageBrowser popupView = new PopupWinImageBrowser(ctx);

        public itemClick(List<String> list) {
            if (list != null) {
                popupView.setData(list);
            }

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            popupView.setCurrentPager(position);
            popupView.onShow(view);
        }
    }

    /**
     * toast 提示
     *
     * @param strResId 提示文字 - 资源
     */
    private void mToast(int strResId) {
        Toast.makeText(ctx, strResId, Toast.LENGTH_LONG).show();
    }
}
