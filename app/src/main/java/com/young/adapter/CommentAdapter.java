package com.young.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.young.adapter.CommonAdapter.CommAdapter;
import com.young.adapter.CommonAdapter.ViewHolder;
import com.young.config.Contants;
import com.young.model.CommRemoteModel;
import com.young.model.User;
import com.young.share.R;
import com.young.utils.StringUtils;
import com.young.views.PopupWinUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 详细信息的通用界面
 * <p>
 * Created by Nearby Yang on 2015-11-27.
 */
public class CommentAdapter extends CommAdapter<CommRemoteModel> {

    private TextView content_txt;
    private ToReply toReply;

    private static final int ITEM_TYPE_SIZE = 2;
    private static final int ITEM_TYPE_HEAD = 1;
    private static final int CLICK_TYPE_USER_INFO = 3;
    private static final int CLICK_TYPE_REPLY = 4;

    public CommentAdapter(Context context) {
        super(context);

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
        Link nickname = new Link(commRemoteModel.getUser().getNickName());

        nickname.setOnClickListener(new linkOnclick(commRemoteModel.getUser(), CLICK_TYPE_USER_INFO));

        links.add(nickname);


        return links;
    }

    /**
     * 信息主题内容
     *
     * @param holder
     * @param commRemoteModel
     */
    private void listviewHead(ViewHolder holder, CommRemoteModel commRemoteModel) {


    }

    @Override
    public int getlayoutid(int position) {

        return getItemViewType(position) != ITEM_TYPE_HEAD ?
                R.layout.item_message_detail_head : R.layout.item_message_detail;

    }


    /**
     * 对应控件的点击事件
     */
//    private class onclick implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//
//            switch (v.getId()) {
//                case
//
//
//            }
//
//
//        }
//    }


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
