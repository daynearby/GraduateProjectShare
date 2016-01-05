package com.young.share.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.young.share.BaiduMapActivity;
import com.young.share.MessageDetail;
import com.young.share.R;
import com.young.share.RankListActivity;
import com.young.share.adapter.CommonAdapter.CommAdapter;
import com.young.share.adapter.CommonAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.User;
import com.young.share.utils.DateUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.PopupWinUserInfo;
import com.young.share.views.WrapHightGridview;

import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * 实例化
 * <p/>
 * 父类中setdata并且刷新
 * <p/>
 * <p/>
 * Created by yangfujing on 15/10/10.
 */
public class DiscoverAdapter extends CommAdapter<ShareMessage_HZ> {

    //    private int strId;

    /**
     * 实例化对象
     *
     * @param context
     */
    public DiscoverAdapter(Context context) {
        super(context);
    }


    @Override
    public void convert(ViewHolder holder, ShareMessage_HZ shareMessage, int position) {
//        this.shareMessage = shareMessage;
        User user = shareMessage.getUserId();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        TextView location = holder.getView(R.id.tv_item_share_main_location);//分享信息的位置
        WrapHightGridview myGridview = holder.getView(R.id.id_gv_shareimg);//图片
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        TextView comment_tv = holder.getView(R.id.id_tx_comment);//评论数量

        ((TextView) holder.getView(R.id.tv_item_share_main_created_at))
                .setText(DateUtils.convertDate2Str(shareMessage.getCreatedAt()));//创建时间

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//************************************************初始化数据********************************************

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());
        // 特殊文字处理,将表情等转换一下
        content_tv.setText(StringUtils.getEmotionContent(
                ctx, content_tv, TextUtils.isEmpty(shareMessage.getShContent()) ? "" : shareMessage.getShContent()));

        nickname_tv.setText(TextUtils.isEmpty(user.getNickName()) ? "" : user.getNickName());

        ImageHandlerUtils.loadIamgeThumbnail(ctx,
                TextUtils.isEmpty(user.getAvatar()) ? Contants.DEFAULT_AVATAR : user.getAvatar(), avatar);

        tag_tv.setText(shareMessage.getShTag());

        String wanto;
        if (shareMessage.getShWantedNum() != null && shareMessage.getShWantedNum().size() > 0) {
            wanto = String.valueOf(shareMessage.getShWantedNum().size());
        } else {
            wanto = ctx.getString(R.string.tx_wantogo);
        }

        String hadgo;
        if (shareMessage.getShVisitedNum() != null && shareMessage.getShVisitedNum().size() > 0) {
            hadgo = String.valueOf(shareMessage.getShVisitedNum().size());
        } else {
            hadgo = ctx.getString(R.string.hadgo);
        }

        wanto_tv.setText(wanto);
        hadgo_tv.setText(hadgo);

        if (cuser != null) {
            LocationUtils.leftDrawableWantoGO(wanto_tv, shareMessage.getShWantedNum(), cuser.getObjectId());//设置图标
            LocationUtils.leftDrawableVisited(hadgo_tv, shareMessage.getShVisitedNum(), cuser.getObjectId());//设置图标
        }

        comment_tv.setText(shareMessage.getShCommNum() != null && shareMessage.getShCommNum() > 0 ?
                String.valueOf(shareMessage.getShCommNum()) : ctx.getString(R.string.tx_comment));

        //地理信息的显示。显示了可以点击查看详细
        if (!TextUtils.isEmpty(shareMessage.getShLocation())) {
            location.setVisibility(View.VISIBLE);
            location.setText(shareMessage.getShLocation());
            LinkBuilder.on(location).addLink(setLocationInfoLink(shareMessage.getShLocation(), shareMessage.getGeographic())).build();
        }
//        location.setText();
        //图片显示
        gridViewAdapter.setDatas(shareMessage.getShImgs(), false);
        myGridview.setOnItemClickListener(new LocationUtils.itemClick(ctx, shareMessage.getShImgs()));

//添加监听事件
        nickname_tv.setOnClickListener(new click(user));
        avatar.setOnClickListener(new click(user));
        wanto_tv.setOnClickListener(new click(shareMessage));
        hadgo_tv.setOnClickListener(new click(shareMessage));
        comment_tv.setOnClickListener(new click(shareMessage));
        tag_tv.setOnClickListener(new click(shareMessage.getShTag()));

    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_share_main;
    }

    /**
     * 地理位置的点击事件
     *
     * @param linkWhat
     * @param geoPoint 位置信息
     * @return
     */
    private Link setLocationInfoLink(String linkWhat, final BmobGeoPoint geoPoint) {
        Link link = new Link(linkWhat);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {

                Bundle bundle = new Bundle();
                bundle.putSerializable(Contants.INTENT_BMOB_GEOPONIT, geoPoint);
                startActivity(BaiduMapActivity.class, bundle);
            }
        });

        return link;
    }

    /**
     * 点击事件
     */
    private class click implements View.OnClickListener {

        private Object o;
        private ShareMessage_HZ shareMessage;

        public click(Object o) {
            this.o = o;


//            LogUtils.logD("dian ji ");
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.id_im_userH://用户资料
                    User u = (User) o;
                    PopupWinUserInfo userInfo = new PopupWinUserInfo(ctx, u);
                    userInfo.onShow(v);
//                    LogUtils.logD("用户资料 = " + u.toString());
                    break;

                case R.id.id_tx_wantogo://想去--数量
                    //防止请求尚未完成，再次点击。防止重复点击
                    v.setClickable(false);

                    getUser();
                    if (cuser != null) {//用户是否登陆

                        shareMessage = (ShareMessage_HZ) o;
                        List<String> shWantedNum = shareMessage.getShWantedNum();

                        LocationUtils.wantToGo(ctx, cuser, UserUtils.isHadCurrentUser(shWantedNum, cuser.getObjectId()), shareMessage, (TextView) v);
                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }

                    break;

                case R.id.id_hadgo://去过--数量

                    v.setClickable(false);

                    getUser();//用户是否登陆

                    if (cuser != null) {
                        shareMessage = (ShareMessage_HZ) o;
                        List<String> shVisitedNum = shareMessage.getShVisitedNum();
                        LocationUtils.visit(ctx, cuser, UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()), shareMessage, v);

                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }
                    break;

                case R.id.id_tx_comment://评论数量
                    getUser();
                    if (cuser != null) {//用户已登录
                        shareMessage = (ShareMessage_HZ) o;
                        comment(shareMessage);
                    } else {
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }
                    break;
                case R.id.id_tx_tab://标签


                    Bundle bundle = new Bundle();
                    bundle.putCharSequence(Contants.INTENT_RANK_TYPE, o.toString());
                    startActivity(RankListActivity.class, bundle);

                    break;

            }
        }
    }

    /**
     * 编辑发送评论
     *
     * @param shareMessage
     */
    private void comment(ShareMessage_HZ shareMessage) {
        Bundle bundle = new Bundle();

        bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_DISCOVER_ACTIVITY);
        bundle.putInt(Contants.EXPEND_OPTION_ONE, Contants.EXPEND_START_INPUT);
        bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, shareMessage);

        startActivity(MessageDetail.class, bundle);

    }

}
