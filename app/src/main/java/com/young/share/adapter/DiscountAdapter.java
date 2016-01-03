package com.young.share.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.share.adapter.CommonAdapter.CommAdapter;
import com.young.share.adapter.CommonAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.User;
import com.young.share.R;
import com.young.share.RankListActivity;
import com.young.share.utils.DateUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.PopupWinUserInfo;
import com.young.share.views.WrapHightGridview;

import java.util.List;

/**
 * 商家优惠信息适配器
 * <p/>
 * Created by Nearby Yang on 2015-12-07.
 */
public class DiscountAdapter extends CommAdapter<DiscountMessage_HZ> {


    public DiscountAdapter(Context context) {
        super(context);
    }

    @Override
    public void convert(ViewHolder holder, DiscountMessage_HZ discountMessage_hz, int position) {
        User user = discountMessage_hz.getUserId();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        WrapHightGridview myGridview = holder.getView(R.id.id_gv_shareimg);
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量

        holder.getView(R.id.id_tx_comment).setVisibility(View.GONE);//评论数量

        ((TextView) holder.getView(R.id.tv_item_share_main_created_at)).setText(DateUtils.convertDate2Str(discountMessage_hz.getCreatedAt()));//创建时间

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//************************************************初始化数据********************************************

//        StringBuilder sb = new StringBuilder(discountMessage_hz.getShContent());
        // 特殊文字处理,将表情等转换一下
        content_tv.setText(StringUtils.getEmotionContent(
                ctx, content_tv, TextUtils.isEmpty(discountMessage_hz.getDtContent()) ? "" : discountMessage_hz.getDtContent()));

        nickname_tv.setText(TextUtils.isEmpty(user.getNickName()) ? ctx.getString(R.string.user_name_defual) : user.getNickName());

        ImageHandlerUtils.loadIamgeThumbnail(ctx,
                TextUtils.isEmpty(user.getAvatar()) ? Contants.DEFAULT_AVATAR : user.getAvatar(), avatar);

        tag_tv.setText(discountMessage_hz.getDtTag());


        String wanto;
        if (discountMessage_hz.getDtWantedNum() != null && discountMessage_hz.getDtWantedNum().size() > 0) {
            wanto = String.valueOf(discountMessage_hz.getDtWantedNum().size());
        } else {
            wanto = ctx.getString(R.string.tx_wantogo);
        }

        String hadgo;
        if (discountMessage_hz.getDtVisitedNum() != null && discountMessage_hz.getDtVisitedNum().size() > 0) {
            hadgo = String.valueOf(discountMessage_hz.getDtVisitedNum().size());
        } else {
            hadgo = ctx.getString(R.string.hadgo);
        }

        wanto_tv.setText(wanto);
        hadgo_tv.setText(hadgo);


        if (cuser != null) {
            LocationUtils.leftDrawableWantoGO(wanto_tv, discountMessage_hz.getDtWantedNum(), cuser.getObjectId());//设置图标
            LocationUtils.leftDrawableVisited(hadgo_tv, discountMessage_hz.getDtVisitedNum(), cuser.getObjectId());//设置图标
        }


        //图片显示
        gridViewAdapter.setDatas(discountMessage_hz.getDtImgs(), false);
        myGridview.setOnItemClickListener(new LocationUtils.itemClick(ctx, discountMessage_hz.getDtImgs()));

//添加监听事件
        nickname_tv.setOnClickListener(new click(user));
        avatar.setOnClickListener(new click(user));
        wanto_tv.setOnClickListener(new click(discountMessage_hz));
        hadgo_tv.setOnClickListener(new click(discountMessage_hz));
        tag_tv.setOnClickListener(new click(discountMessage_hz.getDtTag()));


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
        private PopupWinUserInfo userInfo;
        private DiscountMessage_HZ discountMessage;

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
//                    LogUtils.logD("用户资料 = " + u.toString());
                    break;

                case R.id.id_userName:

                    u = (User) o;
                    userInfo = new PopupWinUserInfo(ctx, u);
                    userInfo.onShow(v);
                    break;

                case R.id.id_tx_wantogo://想去--数量

                    //防止请求尚未完成，再次点击。防止重复点击
                    v.setClickable(false);

                    getUser();

                    if (cuser != null) {//用户是否登陆

                        discountMessage = (DiscountMessage_HZ) o;
                        List<String> wantedNum = discountMessage.getDtWantedNum();

                        LocationUtils.discountWanto(ctx, cuser, discountMessage,
                                UserUtils.isHadCurrentUser(wantedNum, cuser.getObjectId()), (TextView) v);

                    } else {
                        v.setClickable(true);
                        Dialog4Tips.loginFunction((Activity) ctx);
                    }

                    break;

                case R.id.id_hadgo://去过--数量

                    v.setClickable(false);

                    getUser();//用户是否登陆

                    if (cuser != null) {
                        discountMessage = (DiscountMessage_HZ) o;
                        List<String> shVisitedNum = discountMessage.getDtVisitedNum();
                        LocationUtils.discountVisit(ctx, cuser, discountMessage,
                                UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()), (TextView) v);

                    } else {
                        v.setClickable(true);
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


}
