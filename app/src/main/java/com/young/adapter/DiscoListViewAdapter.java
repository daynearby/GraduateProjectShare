package com.young.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.adapter.CommentAdapter.CommAdapter;
import com.young.adapter.CommentAdapter.ViewHolder;
import com.young.model.ShareMessage_HZ;
import com.young.model.User;
import com.young.share.R;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.StringUtils;

/**
 * 实例化
 * <p/>
 * 父类中setdata并且刷新
 * <p/>
 * <p/>
 * Created by yangfujing on 15/10/10.
 */
public class DiscoListViewAdapter extends CommAdapter<ShareMessage_HZ> implements View.OnClickListener {

    private GridView myGridview;
    private ShareMessage_HZ shareMessage;

    /**
     * 实例化对象
     *
     * @param context
     */
    public DiscoListViewAdapter(Context context) {
        super(context);
    }


    @Override
    public void convert(ViewHolder holder, ShareMessage_HZ shareMessage) {
        this.shareMessage = shareMessage;

        User user = shareMessage.getUserId();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        myGridview = holder.getView(R.id.id_gv_shareimg);//分享的照片
        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        holder.getView(R.id.id_hadgo);//去过数量
        holder.getView(R.id.id_tx_comment);//评论数量

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter((Activity) ctx, myGridview, false);
        myGridview.setAdapter(gridViewAdapter);

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());

        // 特殊文字处理,将表情等转换一下
        content_tv.setText(StringUtils.getEmotionContent(
                ctx, content_tv, shareMessage.getShContent()));
        nickname_tv.setText(shareMessage.getNickName());
        ImageHandlerUtils.loadIamge(ctx, user.getAvatar(), avatar);
        tag_tv.setText(shareMessage.getShTag());
        wanto_tv.setText(shareMessage.getShWantedNum().size());

        nickname_tv.setOnClickListener(this);
        avatar.setOnClickListener(this);
        wanto_tv.setOnClickListener(this);
    }

    @Override
    public int getlayoutid() {
        return R.layout.item_share_main;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.id_im_userH | R.id.id_userName://用户资料

//// TODO: 2015-11-15 查看用户资料
                break;

            case R.id.id_tx_wantogo://想去--数量

                break;

            case R.id.id_hadgo://去过--数量

                break;

            case R.id.id_tx_comment://评论数量

                break;

        }
    }

    private void updateShMs() {
//shareMessage

    }
}
