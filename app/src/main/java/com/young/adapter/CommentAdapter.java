package com.young.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.young.adapter.CommonAdapter.CommAdapter;
import com.young.adapter.CommonAdapter.ViewHolder;
import com.young.config.Contants;
import com.young.model.BaseModel;
import com.young.model.CommRemoteModel;
import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.myInterface.GotoAsyncFunction;
import com.young.network.BmobApi;
import com.young.share.R;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.StringUtils;
import com.young.views.Dialog4Tips;
import com.young.views.PopupWinImageBrowser;
import com.young.views.PopupWinUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 详细信息的通用界面
 * <p/>
 * Created by Nearby Yang on 2015-11-27.
 */
public class CommentAdapter extends CommAdapter<CommRemoteModel> {

    private TextView content_txt;
    private ToReply toReply;
    private User user;
    private int strId;
    private CommRemoteModel commRemoteModel;

    private static final int ITEM_TYPE_SIZE = 2;
    private static final int ITEM_TYPE_HEAD = 1;
    private static final int CLICK_TYPE_USER_INFO = 3;
    private static final int CLICK_TYPE_REPLY = 4;

    public CommentAdapter(Context context) {
        super(context);
        user = BmobUser.getCurrentUser(ctx, User.class);

    }


    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_SIZE;
    }

    @Override
    public int getItemViewType(int position) {

        switch (beanList.get(position).getType()) {
            case Contants.DATA_MODEL_HEAD:
                return 0;
            case Contants.DATA_MODEL_BODY:
                return 1;
        }

        return 1;
    }

    @Override
    public void convert(ViewHolder holder, CommRemoteModel commRemoteModel, int position) {

        if (getItemViewType(position) != ITEM_TYPE_HEAD) {
            listviewBody(holder, commRemoteModel);//body，评论回复
        } else {
            listviewHead(holder, commRemoteModel);//信息主题内容
        }


    }

    /**
     * body，评论回复
     *
     * @param holder
     * @param commRemoteModel
     */
    private void listviewBody(ViewHolder holder, CommRemoteModel commRemoteModel) {
        content_txt = holder.getView(R.id.tv_message_detail_comment);
        User user = commRemoteModel.getUser();
        String nickname = TextUtils.isEmpty(user.getNickName()) ?
                ctx.getString(R.string.user_name_defual) : user.getNickName();

        String content = nickname + Contants.DATA_SINGEL_COLON + commRemoteModel.getContent() + Contants.DATA_SINGEL_ENTER +
                commRemoteModel.getCreatedAt() + Contants.DATA_SINGEL_SAPCE + ctx.getString(R.string.txt_replay);

        content_txt.setText(StringUtils.getEmotionContent(ctx, content_txt, content));

        LinkBuilder.on(content_txt)
                .addLinks(getLinks(commRemoteModel))
                .build();


    }

    /**
     * 超链接效果
     *
     * @param commRemoteModel
     * @return
     */
    private List<Link> getLinks(CommRemoteModel commRemoteModel) {
        List<Link> links = new ArrayList<>();

        //用户名
        Link nickname = new Link(commRemoteModel.getUser().getNickName());

        nickname.setOnClickListener(new linkOnclick(commRemoteModel.getUser(), CLICK_TYPE_USER_INFO));

        links.add(nickname);
//回复
        Link reply = new Link(ctx.getString(R.string.txt_replay));

        reply.setOnClickListener(new linkOnclick(commRemoteModel.getUser(), CLICK_TYPE_REPLY));
        links.add(reply);

        return links;
    }

    /**
     * 信息主题内容
     *
     * @param holder
     * @param commRemoteModel
     */
    private void listviewHead(ViewHolder holder, CommRemoteModel commRemoteModel) {

        this.commRemoteModel = commRemoteModel;
        User user = commRemoteModel.getUser();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.tx_message_detail_content);//分享的文本内容
        GridView myGridview = holder.getView(R.id.gv_message_detailshareimg);
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        TextView comment_tv = holder.getView(R.id.id_tx_comment);//评论数量
        ((TextView) holder.getView(R.id.tv_item_message_detail_createdat)).setText(commRemoteModel.getMcreatedAt());//创建时间

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());
        // 特殊文字处理,将表情等转换一下
        content_tv.setText(StringUtils.getEmotionContent(
                ctx, content_tv, TextUtils.isEmpty(commRemoteModel.getContent()) ? "" : commRemoteModel.getContent()));

        nickname_tv.setText(TextUtils.isEmpty(user.getNickName()) ? "" : user.getNickName());

        ImageHandlerUtils.loadIamgeThumbnail(ctx,
                TextUtils.isEmpty(user.getAvatar()) ? Contants.DEFAULT_AVATAR : user.getAvatar(), avatar);

        tag_tv.setText(commRemoteModel.getTag());
        wanto_tv.setText(commRemoteModel.getWanted() == null ? "0" : String.valueOf(commRemoteModel.getWanted().size()));
        hadgo_tv.setText(commRemoteModel.getVisited() == null ? "0" : String.valueOf(commRemoteModel.getVisited().size()));
        comment_tv.setText(String.valueOf(commRemoteModel.getComment()));
        gridViewAdapter.setDatas(commRemoteModel.getImages(), false);
        myGridview.setOnItemClickListener(new itemClick(commRemoteModel.getImages()));

        nickname_tv.setOnClickListener(new click());
        avatar.setOnClickListener(new click());
        wanto_tv.setOnClickListener(new click());
        hadgo_tv.setOnClickListener(new click());
        comment_tv.setOnClickListener(new click());
        tag_tv.setOnClickListener(new click());

    }

    @Override
    public int getlayoutid(int position) {

        return position == 0 ?
                R.layout.item_message_detail_head : R.layout.item_message_detail;

    }


    /**
     * 对应控件的点击事件
     */
    private class click implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.id_im_userH://用户资料

                    PopupWinUserInfo userInfo = new PopupWinUserInfo(ctx, commRemoteModel.getUser());
                    userInfo.onShow(v);
//                    LogUtils.logD("用户资料 = " + u.toString());
                    break;

                case R.id.id_tx_wantogo://想去--数量

                    getUser();
                    if (user != null) {


                        boolean hadWant = false;
                        List<String> shWantedNum = commRemoteModel.getWanted();
                        for (String userId : shWantedNum) {
                            hadWant = user.getObjectId().equals(userId);
                        }
                        wantToGo(hadWant, v);
                    } else {
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }

                    break;

                case R.id.id_hadgo://去过--数量

                    getUser();
                    if (user != null) {
                        boolean hadGo = false;
                        List<String> shVisitedNum = commRemoteModel.getVisited();
                        for (String userId : shVisitedNum) {
                            hadGo = user.getObjectId().equals(userId);
                        }

                        visit(hadGo,  v);
                    } else {
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }
                    break;

                case R.id.id_tx_comment://评论数量
                    getUser();


                    break;
                case R.id.id_tx_tab://标签


                    break;

            }
        }
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


    private class linkOnclick implements Link.OnClickListener {

        private User user;
        private int clickType;
        private PopupWinUserInfo userInfoWindow;

        public linkOnclick(User user, int clickType) {
            this.user = user;
            this.clickType = clickType;

        }

        @Override
        public void onClick(String clickedText) {

            switch (clickType) {

                case CLICK_TYPE_USER_INFO://查看用户数据
                    userInfoWindow = new PopupWinUserInfo(ctx, user);
                    userInfoWindow.onShow(content_txt);

                    break;

                case CLICK_TYPE_REPLY://回复

                    if (toReply != null) {
                        toReply.reply(user.getObjectId());
                    }

                    break;

            }

        }
    }

    /**
     * 去过的逻辑处理
     *
     * @param hadGo
     * @param v
     */
    private void visit(boolean hadGo, View v) {

        if (hadGo) {
            strId = R.string.not_visit;
            commRemoteModel.getVisited().remove(user.getObjectId());
        } else {
            strId = R.string.cancel_collect_success;
            commRemoteModel.getVisited().add(user.getObjectId());
        }


        ((TextView) v).setText(String.valueOf(commRemoteModel.getVisited() == null ?
                0 : commRemoteModel.getVisited().size()));

        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        shareMessage.setShVisitedNum(commRemoteModel.getVisited());
        shareMessage.update(ctx, commRemoteModel.getObjectId(), new UpdateListener() {
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
     * @param v
     */
    private void wantToGo(boolean hadWant,  View v) {

        JSONObject jsonObject = new JSONObject();//参数
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        try {
            jsonObject.put("message", "1");
            jsonObject.put("userid", user.getObjectId());
            jsonObject.put("collectionid", commRemoteModel.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.logD("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {
            strId = R.string.cancel_collect_success;
            commRemoteModel.getWanted().remove(user.getObjectId());

//操作收藏表
            BmobApi.AsyncFunction(ctx, jsonObject, BmobApi.REMOVE_COLLECTION, new GotoAsyncFunction() {
                @Override
                public void onSuccess(Object object) {

                    BaseModel baseModel = (BaseModel) object;

                    if (baseModel.getCode() == BaseModel.SUCCESS) {
//                        mToast(R.string.operation_success);
                        LogUtils.logD("删除收藏记录 成功  data = " + baseModel.getData());
                    } else {

                        LogUtils.logD("删除收藏记录 失败  data = " + baseModel.getData());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        } else {
            strId = R.string.collect_success;
            commRemoteModel.getWanted().add(user.getObjectId());
            shareMessage.setObjectId(commRemoteModel.getObjectId());

            BmobApi.saveCollectionShareMessage(ctx, user, shareMessage, Contants.MESSAGE_TYPE_SHAREMESSAGE);

        }

        ((TextView) v).setText(String.valueOf(commRemoteModel.getWanted() == null ?
                0 : commRemoteModel.getWanted().size()));


        shareMessage.setShVisitedNum(commRemoteModel.getVisited());
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
     * 再获取当前用户是否存在
     */
    private void getUser() {
        if (user == null) {
            user = BmobUser.getCurrentUser(ctx, User.class);
        }
    }

    /**
     * 恢复按钮回调
     *
     * @param toReply
     */
    public void setToReply(ToReply toReply) {
        this.toReply = toReply;
    }

    /**
     * 点击回复打开输入框、输入法
     */
    public interface ToReply {
        void reply(String objId);
    }


}