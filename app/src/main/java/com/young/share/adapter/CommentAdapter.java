package com.young.share.adapter;

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
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.BaseModel;
import com.young.share.model.CommRemoteModel;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.User;
import com.young.share.myInterface.GotoAsyncFunction;
import com.young.share.network.BmobApi;
import com.young.share.R;
import com.young.share.utils.DateUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.PopupWinImageBrowser;
import com.young.share.views.PopupWinUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * 详细信息的通用界面
 * <p/>
 * Created by Nearby Yang on 2015-11-27.
 */
public class CommentAdapter extends CommAdapter<CommRemoteModel> {

    private TextView content_txt;
    private ToReply toReply;
    private boolean isClick = false;

//    private int strId;//提示文字资源id

    private CommRemoteModel commRemoteModel;

    private static final int ITEM_TYPE_SIZE = 2;
    private static final int ITEM_TYPE_BODY = 1;
    private static final int ITEM_TYPE_HEAD = 0;
    private static final int CLICK_TYPE_USER_INFO = 3;
    private static final int CLICK_TYPE_REPLY = 4;

    public CommentAdapter(Context context) {
        super(context);


    }

    public CommentAdapter(Context context, ToReply toReply) {
        this(context);
        this.toReply = toReply;
    }


    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_SIZE;
    }

    @Override
    public int getItemViewType(int position) {

        switch (beanList.get(position).getType()) {

            case Contants.DATA_MODEL_HEAD:
                return ITEM_TYPE_HEAD;

            case Contants.DATA_MODEL_BODY://评论回复
                return ITEM_TYPE_BODY;

        }

        return 10;
    }

    @Override
    public void convert(ViewHolder holder, CommRemoteModel commRemoteModel, int position) {

        if (getItemViewType(position) == ITEM_TYPE_BODY) {
            listviewBody(holder, commRemoteModel);//body，评论回复
        } else {
            listviewHead(holder, commRemoteModel);//信息主题内容
        }


    }

    /**
     * body，评论回复
     * sender receiver
     *
     * @param holder
     * @param commRemoteModel
     */
    private void listviewBody(ViewHolder holder, CommRemoteModel commRemoteModel) {

        content_txt = holder.getView(R.id.tv_message_detail_comment);
        TextView createdAt = holder.getView(R.id.tv_message_detail_reply);

        User sender = commRemoteModel.getSender();
        User receiver = commRemoteModel.getReceiver();

        String senderNickname = TextUtils.isEmpty(sender.getNickName()) ?
                ctx.getString(R.string.user_name_defual) : sender.getNickName();

        String receiverNickname = TextUtils.isEmpty(receiver.getNickName()) ?
                ctx.getString(R.string.user_name_defual0) : receiver.getNickName();

        String content = senderNickname +
                Contants.DATA_SINGEL_COLON +
                Contants.DATA_SINGEL_AT +
                receiverNickname +
                Contants.DATA_SINGEL_ENTER;

        String created = DateUtils.convertDate2Str(commRemoteModel.getMcreatedAt()) +
                Contants.DATA_SINGEL_SAPCE +
                ctx.getString(R.string.txt_replay);

        content_txt.setText(content);

        LinkBuilder.on(content_txt)
                .addLinks(getLinks(commRemoteModel))
                .build();
//表情
        content_txt.append(StringUtils.getEmotionContent(ctx, content_txt, commRemoteModel.getContent()));

        createdAt.setText(created);

        //回复
        Link reply = new Link(ctx.getString(R.string.txt_replay));
        reply.setOnClickListener(new linkOnclick(commRemoteModel.getUser(), CLICK_TYPE_REPLY));

        LinkBuilder.on(createdAt)
                .addLink(reply)
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

        String senderNickname = TextUtils.isEmpty(commRemoteModel.getSender().getNickName()) ?
                ctx.getString(R.string.user_name_defual) : commRemoteModel.getSender().getNickName();

        String receiverNickname = TextUtils.isEmpty(commRemoteModel.getReceiver().getNickName()) ?
                ctx.getString(R.string.user_name_defual0) : commRemoteModel.getReceiver().getNickName();

        //用户名
        Link sender = new Link(senderNickname);
        sender.setOnClickListener(new linkOnclick(commRemoteModel.getSender(), CLICK_TYPE_USER_INFO));
        links.add(sender);

        //收到评论者
        Link receiver = new Link(receiverNickname);
        receiver.setOnClickListener(new linkOnclick(commRemoteModel.getReceiver(), CLICK_TYPE_USER_INFO));
        links.add(receiver);


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

//        holder.getView(R.id.view_bottom_bar_divilier).setVisibility(View.GONE);
        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.tx_message_detail_content);//分享的文本内容
        GridView myGridview = holder.getView(R.id.gv_message_detailshareimg);
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        TextView comment_tv = holder.getView(R.id.id_tx_comment);//评论数量
        ((TextView) holder.getView(R.id.tv_item_message_detail_createdat)).setText(commRemoteModel.getMcreatedAt());//创建时间

        ThumGridViewAdapter gridViewAdapter = new ThumGridViewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());
        // 特殊文字处理,将表情等转换一下
        content_tv.setText(StringUtils.getEmotionContent(
                ctx, content_tv, TextUtils.isEmpty(commRemoteModel.getContent()) ? "" : commRemoteModel.getContent()));

        nickname_tv.setText(TextUtils.isEmpty(user.getNickName()) ? "" : user.getNickName());

        ImageHandlerUtils.loadIamgeThumbnail(ctx,
                TextUtils.isEmpty(user.getAvatar()) ? Contants.DEFAULT_AVATAR : user.getAvatar(), avatar);

        tag_tv.setText(commRemoteModel.getTag());
        wanto_tv.setText(commRemoteModel.getWanted() == null ?
                ctx.getString(R.string.tx_wantogo) : String.valueOf(commRemoteModel.getWanted().size()));

        hadgo_tv.setText(commRemoteModel.getVisited() == null ?
                ctx.getString(R.string.hadgo) : String.valueOf(commRemoteModel.getVisited().size()));
        //判断当前用户是否点赞
        if (cuser != null) {
            LocationUtils.leftDrawableWantoGO(wanto_tv, commRemoteModel.getWanted(), cuser.getObjectId());//设置图标
            LocationUtils.leftDrawableVisited(hadgo_tv, commRemoteModel.getVisited(), cuser.getObjectId());
        }

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

        return getItemViewType(position) == ITEM_TYPE_HEAD ?
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
                    showUserInfo(v);
//                    LogUtils.logD("用户资料 = " + u.toString());
                    break;
                case R.id.id_userName:
                    showUserInfo(v);
                    break;

                case R.id.id_tx_wantogo://想去--数量
                    v.setClickable(false);
                    getUser();

                    if (cuser != null) {

                        List<String> shWantedNum = commRemoteModel.getWanted();

                        wantToGo(UserUtils.isHadCurrentUser(shWantedNum, cuser.getObjectId()), v);
                        isClick = true;
                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }

                    break;

                case R.id.id_hadgo://去过--数量
                    v.setClickable(false);
                    getUser();

                    if (cuser != null) {

                        List<String> shVisitedNum = commRemoteModel.getVisited();

                        visit(UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()), v);

                        isClick = true;

                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }
                    break;

                case R.id.id_tx_comment://评论数量
                    getUser();
                    if (cuser != null && toReply != null) {
                        toReply.reply(commRemoteModel.getUser().getObjectId());
                    }


                    break;
                case R.id.id_tx_tab://标签


                    break;

            }
        }
    }

    /**
     * 查看用户资料
     *
     * @param v
     */
    private void showUserInfo(View v) {

        PopupWinUserInfo userInfo = new PopupWinUserInfo(ctx, commRemoteModel.getUser());
        userInfo.onShow(v);
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
     * 超链接 点击事件监听
     * 评论内容的监听
     */
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


            LogUtils.logD("reply user not null.clickType = " + clickType + " clickText = " + clickedText);

            switch (clickType) {

                case CLICK_TYPE_USER_INFO://查看用户数据
                    userInfoWindow = new PopupWinUserInfo(ctx, user);
                    userInfoWindow.onShow(content_txt);

                    break;

                case CLICK_TYPE_REPLY://回复

                    isClick = true;

                    getUser();

                    if (cuser != null) {
                        if (toReply != null) {
                            toReply.reply(cuser.getObjectId());
                        }
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
    private void visit(boolean hadGo, final View v) {

        int leftDrawID;//提示图片资源id
        if (hadGo) {
//            strId = R.string.not_visit;
            leftDrawID = R.drawable.icon_bottombar_hadgo;

            commRemoteModel.getVisited().remove(cuser.getObjectId());
        } else {
//            strId = R.string.cancel_collect_success;
            leftDrawID = R.drawable.icon_hadgo;
            commRemoteModel.getVisited().add(cuser.getObjectId());
        }


        ((TextView) v).setText(String.valueOf(commRemoteModel.getVisited() == null ?
                0 : commRemoteModel.getVisited().size()));
        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(leftDrawID, 0, 0, 0);

        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        shareMessage.setShVisitedNum(commRemoteModel.getVisited());
        shareMessage.setUserId(commRemoteModel.getUser());

        shareMessage.update(ctx, commRemoteModel.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                mToast(strId);
                v.setClickable(true);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
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
    private void wantToGo(boolean hadWant, final View v) {

        int leftDrawID;//提示图片资源id
        JSONObject jsonObject = new JSONObject();//参数
        ShareMessage_HZ shareMessage = new ShareMessage_HZ();
        try {
            jsonObject.put("message", "1");
            jsonObject.put("userid", cuser.getObjectId());
            jsonObject.put("collectionid", commRemoteModel.getObjectId());

        } catch (JSONException e) {
//            e.printStackTrace();
            LogUtils.logD("云端代码 添加参数失败 " + e.toString());
        }

        if (hadWant) {
//            strId = R.string.cancel_collect_success;
            leftDrawID = R.drawable.icon_wantogo;

            commRemoteModel.getWanted().remove(cuser.getObjectId());

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
//            strId = R.string.collect_success;
            leftDrawID = R.drawable.icon_wantogo_light;

            commRemoteModel.getWanted().add(cuser.getObjectId());
            shareMessage.setObjectId(commRemoteModel.getObjectId());
            shareMessage.setUserId(commRemoteModel.getUser());

            BmobApi.saveCollectionShareMessage(ctx, cuser, shareMessage, Contants.MESSAGE_TYPE_SHAREMESSAGE);

        }

        ((TextView) v).setText(String.valueOf(commRemoteModel.getWanted() == null ?
                0 : commRemoteModel.getWanted().size()));
        ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(leftDrawID, 0, 0, 0);

        shareMessage.setShVisitedNum(commRemoteModel.getVisited());
        shareMessage.update(ctx, shareMessage.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
//                mToast(strId);
                v.setClickable(true);
            }

            @Override
            public void onFailure(int i, String s) {
                v.setClickable(true);
                LogUtils.logD(" wantToGo faile  erro code = " + i + " erro message = " + s);
            }
        });

    }

    public void setToReply(ToReply toReply) {
        this.toReply = toReply;
    }

    /**
     * 点击回复打开输入框、输入法
     */
    public interface ToReply {
        void reply(String objId);
    }

    public boolean isClick() {
        return isClick;
    }
}
