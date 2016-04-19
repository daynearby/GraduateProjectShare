package com.young.share.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.BaiduMapActivity;
import com.young.share.BigPicActivity;
import com.young.share.R;
import com.young.share.RankListActivity;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DateUtils;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.NetworkUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.MultiImageView.MultiImageView;
import com.young.share.views.PopupWinUserInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 商家优惠信息适配器
 * <p/>
 * Created by Nearby Yang on 2015-12-07.
 */
public class DiscountAdapter extends CommAdapter<DiscountMessage_HZ> {
    private String contentString = null;
    private String imageUrl = null;//图片签名后的地址
    private List<String> imagesUri;


    public DiscountAdapter(Context context) {
        super(context);
    }

    /**
     * 复制内容
     *
     * @return
     */
    public String getContentString() {
        return contentString;
    }

    /**
     * 获取图片的下载地址
     *
     * @return
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 获取点击的全部图片的uri
     *
     * @return
     */
    public List<String> getImagesUri() {
        return imagesUri;
    }

    @Override
    public void convert(ViewHolder holder, final DiscountMessage_HZ discountMessage_hz, int position) {
        MyUser myUser = discountMessage_hz.getMyUserId();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        TextView location = holder.getView(R.id.tv_item_share_main_location);//分享信息的位置
        MultiImageView multiImageView = holder.getView(R.id.miv_share_iamges);
//        WrapHightGridview myGridview = holder.getView(R.id.id_gv_shareimg);
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        RelativeLayout tagLayout = holder.getView(R.id.rl_head_tag_layout);
        LinearLayout bottomBar = holder.getView(R.id.ll_bottom_option_bar);
        holder.getView(R.id.rel_indicador_layout).setVisibility(View.GONE);

        ViewGroup.LayoutParams lp = multiImageView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels((Activity) ctx) / 3 * 2;//设置宽度
        bottomBar.setBackgroundColor(Color.WHITE);
        holder.getView(R.id.id_tx_comment).setVisibility(View.GONE);//评论数量

        ((TextView) holder.getView(R.id.tv_item_share_main_created_at)).setText(DateUtils.convertDate2Str(discountMessage_hz.getCreatedAt()));//创建时间


//************************************************初始化数据********************************************

//        StringBuilder sb = new StringBuilder(discountMessage_hz.getShContent());
        // 特殊文字处理,将表情等转换一下
        if (!TextUtils.isEmpty(discountMessage_hz.getDtContent())) {
            content_tv.setText(StringUtils.getEmotionContent(
                    ctx,  discountMessage_hz.getDtContent()));
            content_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    contentString = discountMessage_hz.getDtContent();
                    return false;
                }
            });
            ((Activity) ctx).registerForContextMenu(content_tv);
            content_tv.setOnCreateContextMenuListener(new OnContextMenuCreat());
        } else {
            content_tv.setVisibility(View.GONE);
        }
        nickname_tv.setText(TextUtils.isEmpty(myUser.getNickName()) ? ctx.getString(R.string.user_name_defual) : myUser.getNickName());

        //地理信息的显示。显示了可以点击查看详细
        if (!TextUtils.isEmpty(discountMessage_hz.getDtLocation())) {
            location.setVisibility(View.VISIBLE);
            location.setText(discountMessage_hz.getDtLocation());

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Contants.INTENT_BMOB_GEOPONIT, discountMessage_hz.getGeographic());
                    startActivity(BaiduMapActivity.class, bundle);
                }
            });

        }

        ImageLoader.getInstance().displayImage(
                TextUtils.isEmpty(myUser.getAvatar()) ? Contants.DEFAULT_AVATAR :
                        NetworkUtils.getRealUrl(ctx, myUser.getAvatar()), avatar);

        if (TextUtils.isEmpty(discountMessage_hz.getDtTag())) {
            tagLayout.setVisibility(View.INVISIBLE);
        } else {
            tagLayout.setVisibility(View.VISIBLE);
            tag_tv.setText(discountMessage_hz.getDtTag());
        }


        String wanto;
        if (discountMessage_hz.getDtWantedNum() > 0) {
            wanto = String.valueOf(discountMessage_hz.getDtWantedNum());
        } else {
            wanto = ctx.getString(R.string.tx_wantogo);
        }

        String hadgo;
        if (discountMessage_hz.getDtVisitedNum()  > 0) {
            hadgo = String.valueOf(discountMessage_hz.getDtVisitedNum());
        } else {
            hadgo = ctx.getString(R.string.hadgo);
        }

        wanto_tv.setText(wanto);
        hadgo_tv.setText(hadgo);


        if (cuser != null) {
            CommonFunctionUtils.leftDrawableWantoGO(wanto_tv, discountMessage_hz.getDtWanted(), cuser.getObjectId());//设置图标
            CommonFunctionUtils.leftDrawableVisited(hadgo_tv, discountMessage_hz.getDtVisited(), cuser.getObjectId());//设置图标
        }


        //图片显示
//        gridViewAdapter.setDatas(DataFormateUtils.formateStringInfoList(ctx,discountMessage_hz.getDtImgs()));
        setupImage(multiImageView, discountMessage_hz);

//添加监听事件
        nickname_tv.setOnClickListener(new click(myUser));
        avatar.setOnClickListener(new click(myUser));
        wanto_tv.setOnClickListener(new click(discountMessage_hz));
        hadgo_tv.setOnClickListener(new click(discountMessage_hz));
        tag_tv.setOnClickListener(new click(discountMessage_hz.getDtTag()));


    }

    /**
     * 设置图片显示
     *
     * @param multiImageView
     */
    private void setupImage(final MultiImageView multiImageView, final DiscountMessage_HZ discountMessage_hz) {

        multiImageView.setRegisterForContextMenu(true);
        multiImageView.setList(DataFormateUtils.thumbnailList(ctx, discountMessage_hz.getDtImgs()));
        multiImageView.setBigImagesList(DataFormateUtils.bigImagesList(ctx, discountMessage_hz.getDtImgs()));

        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(ctx, discountMessage_hz.getDtImgs());

                EvaluateUtil.setupCoords(ctx, (ImageView) view, pictureInfoList, position);
                Intent intent = new Intent(ctx, BigPicActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable(Contants.INTENT_IMAGE_INFO_LIST, (Serializable) pictureInfoList);
                intent.putExtras(bundle);
                intent.putExtra(Contants.INTENT_CURRENT_ITEM, position);

                ctx.startActivity(intent);
                ((Activity) ctx).overridePendingTransition(0, 0);
            }
        });
        //长按，为了获取文件地址
        multiImageView.setOnItemLongClickListener(new MultiImageView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                imageUrl = multiImageView.getBigImagesList().get(position);
                imagesUri =multiImageView.getBigImagesList();
            }
        });

    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_discount;
    }

    /**
     * context menu 创建
     */
    private class OnContextMenuCreat implements View.OnCreateContextMenuListener {

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            ((Activity) ctx).getMenuInflater().inflate(R.menu.menu_context_content, contextMenu);
        }
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
                    MyUser u = (MyUser) o;
                    userInfo = new PopupWinUserInfo(ctx, u);
                    userInfo.onShow(v);
//                    LogUtils.logD("用户资料 = " + u.toString());
                    break;

                case R.id.id_userName:

                    u = (MyUser) o;
                    userInfo = new PopupWinUserInfo(ctx, u);
                    userInfo.onShow(v);
                    break;

                case R.id.id_tx_wantogo://想去--数量

                    //防止请求尚未完成，再次点击。防止重复点击
                    v.setClickable(false);

                    getUser();

                    if (cuser != null) {//用户是否登陆

                        discountMessage = (DiscountMessage_HZ) o;
                        List<String> wantedNum = discountMessage.getDtWanted();

                        CommonFunctionUtils.discountWanto(ctx, cuser, discountMessage,
                                UserUtils.isHadCurrentUser(wantedNum, cuser.getObjectId()), (TextView) v, new CommonFunctionUtils.Callback() {
                                    @Override
                                    public void onSuccesss() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });

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
                        List<String> shVisitedNum = discountMessage.getDtVisited();
                        CommonFunctionUtils.discountVisit(ctx, cuser, discountMessage,
                                UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()), (TextView) v, new CommonFunctionUtils.Callback() {
                                    @Override
                                    public void onSuccesss() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                });

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
