package com.young.share.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.young.share.MessageDetail;
import com.young.share.R;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.dbmodel.CommRemoteModel;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.User;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DateUtils;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.PopupWinUserInfo;
import com.young.share.views.WrapHightGridview;

import java.util.List;

/**
 * 排行榜列表的adapter
 * Created by Nearby Yang on 2015-12-26.
 */
public class RankListAdapter extends CommAdapter<CommRemoteModel> {


    public RankListAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(ViewHolder holder, CommRemoteModel commRemoteModel, int position) {

        User user = commRemoteModel.getUser();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        WrapHightGridview myGridview = holder.getView(R.id.id_gv_shareimg);//分享的照片
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        TextView comment_tv = holder.getView(R.id.id_tx_comment);//评论数量
        ((TextView) holder.getView(R.id.tv_item_share_main_created_at))
                .setText(DateUtils.convertDate2Str(commRemoteModel.getMcreatedAt()));//创建时间
        RelativeLayout tagLayout = holder.getView(R.id.rl_head_tag_layout);
        ViewGroup.LayoutParams lp = myGridview.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels((Activity) ctx) / 3 * 2;//设置宽度
        myGridview.setLayoutParams(lp);

        GridviewAdapter gridViewAdapter = new GridviewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//************************************************初始化数据********************************************

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());
        // 特殊文字处理,将表情等转换一下
        if (!TextUtils.isEmpty(commRemoteModel.getContent())) {
            content_tv.setText(StringUtils.getEmotionContent(
                    ctx, content_tv, commRemoteModel.getContent()));
        } else {
            content_tv.setVisibility(View.GONE);
        }


        nickname_tv.setText(TextUtils.isEmpty(user.getNickName()) ? "" : user.getNickName());

        ImageHandlerUtils.loadIamgeThumbnail(ctx,
                TextUtils.isEmpty(user.getAvatar()) ? Contants.DEFAULT_AVATAR : user.getAvatar(), avatar);
        if (TextUtils.isEmpty(commRemoteModel.getTag())) {
            tagLayout.setVisibility(View.INVISIBLE);
        } else {
            tagLayout.setVisibility(View.VISIBLE);
            tag_tv.setText(commRemoteModel.getTag());
        }

        tag_tv.setText(commRemoteModel.getTag());

        String wanto;
        if (commRemoteModel.getWanted() != null && commRemoteModel.getWanted().size() > 0) {
            wanto = String.valueOf(commRemoteModel.getWanted().size());
        } else {
            wanto = ctx.getString(R.string.tx_wantogo);
        }

        String hadgo;
        if (commRemoteModel.getVisited() != null && commRemoteModel.getVisited().size() > 0) {
            hadgo = String.valueOf(commRemoteModel.getVisited().size());
        } else {
            hadgo = ctx.getString(R.string.hadgo);
        }

        wanto_tv.setText(wanto);
        hadgo_tv.setText(hadgo);

        if (cuser != null) {
            LocationUtils.leftDrawableWantoGO(wanto_tv, commRemoteModel.getWanted(), cuser.getObjectId());//设置图标
            LocationUtils.leftDrawableVisited(hadgo_tv, commRemoteModel.getVisited(), cuser.getObjectId());//设置图标
        }

        comment_tv.setVisibility(commRemoteModel.getType() == Contants.DATA_MODEL_SHARE_MESSAGES ? View.VISIBLE : View.GONE);
        comment_tv.setText(commRemoteModel.getComment() > 0 ?
                String.valueOf(commRemoteModel.getComment()) : ctx.getString(R.string.tx_comment));

        //图片显示
        gridViewAdapter.setDatas(DataFormateUtils.formateStringInfoList(ctx, commRemoteModel.getImages()), false);

//添加监听事件
        nickname_tv.setOnClickListener(new click(user));
        avatar.setOnClickListener(new click(user));
        wanto_tv.setOnClickListener(new click(commRemoteModel));
        hadgo_tv.setOnClickListener(new click(commRemoteModel));
        comment_tv.setOnClickListener(new click(commRemoteModel));
        tag_tv.setOnClickListener(new click(commRemoteModel.getTag()));


    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_share_main;
    }


    /**
     * 点击事件
     */
    private class click implements View.OnClickListener {

        private Object o;
        private CommRemoteModel commModel;

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

                        commModel = (CommRemoteModel) o;

                        List<String> wantedNum = commModel.getWanted();

                        if (commModel.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息

                            ShareMessage_HZ shareMessage = new ShareMessage_HZ();
                            shareMessage.setObjectId(commModel.getObjectId());
                            shareMessage.setShWantedNum(commModel.getWanted());

                            LocationUtils.wantToGo(ctx, cuser,
                                    UserUtils.isHadCurrentUser(wantedNum, cuser.getObjectId()), shareMessage,
                                    (TextView) v);

                        } else {//优惠信息

                            DiscountMessage_HZ discountMessage = new DiscountMessage_HZ();
                            discountMessage.setObjectId(commModel.getObjectId());
                            discountMessage.setDtWantedNum(commModel.getWanted());

                            LocationUtils.discountWanto(ctx, cuser, discountMessage,
                                    UserUtils.isHadCurrentUser(wantedNum, cuser.getObjectId()),
                                    (TextView) v);

                        }


                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }

                    break;

                case R.id.id_hadgo://去过--数量

                    v.setClickable(false);

                    getUser();//用户是否登陆

                    if (cuser != null) {
                        commModel = (CommRemoteModel) o;
                        List<String> visitedNum = commModel.getVisited();

                        if (commModel.getType() == Contants.DATA_MODEL_SHARE_MESSAGES) {//分享信息

                            ShareMessage_HZ shareMessage = new ShareMessage_HZ();
                            shareMessage.setObjectId(commModel.getObjectId());
                            shareMessage.setShWantedNum(commModel.getWanted());


                            LocationUtils.visit(ctx, cuser,
                                    UserUtils.isHadCurrentUser(visitedNum, cuser.getObjectId()),
                                    shareMessage, v);

                        } else {//优惠信息

                            DiscountMessage_HZ discountMessage = new DiscountMessage_HZ();
                            discountMessage.setObjectId(commModel.getObjectId());
                            discountMessage.setDtWantedNum(commModel.getWanted());

                            LocationUtils.discountVisit(ctx, cuser, discountMessage,
                                    UserUtils.isHadCurrentUser(visitedNum, cuser.getObjectId()),
                                    (TextView) v);

                        }


                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }

                    break;

                case R.id.id_tx_comment://评论数量

                    getUser();

                    if (cuser != null) {//用户已登录

                        comment((CommRemoteModel) o);//评价

                    } else {

                        Dialog4Tips.loginFunction((Activity) ctx);

                    }

                    break;

                case R.id.id_tx_tab://标签


                    break;

                //地理信息

            }


        }
    }

    /**
     * 编辑发送评论
     *
     * @param commModel
     */
    private void comment(CommRemoteModel commModel) {
        Bundle bundle = new Bundle();

        bundle.putCharSequence(Contants.CLAZZ_NAME, Contants.CLAZZ_RANK_LIST_ACTIVITY);
        bundle.putInt(Contants.EXPEND_OPTION_ONE, Contants.EXPEND_START_INPUT);
        bundle.putSerializable(Contants.CLAZZ_DATA_MODEL, commModel);

        startActivity(MessageDetail.class, bundle);

    }

}
