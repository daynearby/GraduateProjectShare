package com.young.share.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.BaiduMapActivity;
import com.young.share.BigPicActivity;
import com.young.share.MessageDetailActivity;
import com.young.share.R;
import com.young.share.RankListActivity;
import com.young.share.VideoplayerActivity;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.network.NetworkReuqest;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DateUtils;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.NetworkUtils;
import com.young.share.utils.StorageUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.MultiImageView.MultiImageView;
import com.young.share.views.PopupWinUserInfo;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 实例化
 * <p/>
 * 父类中setdata并且刷新
 * <p/>
 * <p/>
 * Created by yangfujing on 15/10/10.
 */
public class DiscoverAdapter extends CommAdapter<ShareMessage_HZ> {
    private String contentString = null;
    private String imageUrl = null;//图片签名后的地址
    private List<String> imageList;

    /**
     * 实例化对象
     *
     * @param context
     */
    public DiscoverAdapter(Context context) {
        super(context);
    }

    /**
     * 绑定ListView 实现监听item显示还是隐藏
     *
     * @param listView
     */
    public void bindListView(ListView listView) {
//
//        Toast.makeText(ctx, "  listView.getFirstVisiblePosition()  = " + listView.getFirstVisiblePosition()
//                + " listView.getLastVisiblePosition() = " + listView.getLastVisiblePosition(), Toast.LENGTH_LONG).show();
//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                Toast.makeText(ctx, " absListView  = " + absListView
//                        + " scrollState = " + scrollState, Toast.LENGTH_LONG).show();
//
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                Toast.makeText(ctx, " firstVisibleItem  = " + firstVisibleItem
//                        + " visibleItemCount = " + visibleItemCount
//                        + " totalItemCount = " + totalItemCount, Toast.LENGTH_LONG).show();
//
//            }
//        });
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
        LogUtils.e(" adapter = " + imageUrl);
        return imageUrl;
    }

    /**
     * 获取高清大图的list
     *
     * @return
     */
    public List<String> getImageList() {
        return imageList;
    }

    @Override
    public void convert(ViewHolder holder, final ShareMessage_HZ shareMessage, int position) {
//        this.shareMessage = shareMessage;
        MyUser myUser = shareMessage.getMyUserId();

        ImageView avatar = holder.getView(R.id.id_im_userH);//用户头像
        TextView nickname_tv = holder.getView(R.id.id_userName);//昵称
        TextView tag_tv = holder.getView(R.id.id_tx_tab);//标签
        TextView content_tv = holder.getView(R.id.id_tx_share_content);//分享的文本内容
        TextView location = holder.getView(R.id.tv_item_share_main_location);//分享信息的位置
        MultiImageView multiImageView = holder.getView(R.id.miv_share_iamges);//图片

        TextView wanto_tv = holder.getView(R.id.id_tx_wantogo);//想去数量
        TextView hadgo_tv = holder.getView(R.id.id_hadgo);//去过数量
        TextView comment_tv = holder.getView(R.id.id_tx_comment);//评论数量
        RelativeLayout tagLayout = holder.getView(R.id.rl_head_tag_layout);

        ((TextView) holder.getView(R.id.tv_item_share_main_created_at))
                .setText(DateUtils.convertDate2Str(shareMessage.getCreatedAt()));//创建时间

        ViewGroup.LayoutParams lp = multiImageView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels((Activity) ctx) / 3 * 2;//设置宽度
//
//************************************************初始化数据********************************************

//        StringBuilder sb = new StringBuilder(shareMessage.getShContent());
        // 特殊文字处理,将表情等转换一下
        if (!TextUtils.isEmpty(shareMessage.getShContent())) {
            content_tv.setText(StringUtils.getEmotionContent(
                    ctx, content_tv, shareMessage.getShContent()));
            content_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    contentString = shareMessage.getShContent();
                    return false;
                }
            });
            ((Activity) ctx).registerForContextMenu(content_tv);
            content_tv.setOnCreateContextMenuListener(new OnContextMenuCreat());

        } else {
            content_tv.setVisibility(View.GONE);
        }

        nickname_tv.setText(TextUtils.isEmpty(myUser.getNickName()) ? "" : myUser.getNickName());

        ImageLoader.getInstance().displayImage(
                TextUtils.isEmpty(myUser.getAvatar()) ? Contants.DEFAULT_AVATAR :
                        NetworkUtils.getRealUrl(ctx, myUser.getAvatar()), avatar);

        if (TextUtils.isEmpty(shareMessage.getShTag())) {
            tagLayout.setVisibility(View.INVISIBLE);
        } else {
            tagLayout.setVisibility(View.VISIBLE);
            tag_tv.setText(shareMessage.getShTag());
        }


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
            CommonFunctionUtils.leftDrawableWantoGO(wanto_tv, shareMessage.getShWantedNum(), cuser.getObjectId());//设置图标
            CommonFunctionUtils.leftDrawableVisited(hadgo_tv, shareMessage.getShVisitedNum(), cuser.getObjectId());//设置图标
        }

        comment_tv.setText(shareMessage.getShCommNum() > 0 ?
                String.valueOf(shareMessage.getShCommNum()) : ctx.getString(R.string.tx_comment));

        //地理信息的显示。显示了可以点击查看详细
        if (!TextUtils.isEmpty(shareMessage.getShLocation())) {
            location.setVisibility(View.VISIBLE);
            location.setText(shareMessage.getShLocation());
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Contants.INTENT_BMOB_GEOPONIT, shareMessage.getGeographic());
                    startActivity(BaiduMapActivity.class, bundle);
                }
            });
        }
//        location.setText();
        //图片显示
        setImages(multiImageView, shareMessage);

        //设置视频
        setVideo(holder, shareMessage);

//添加监听事件
        nickname_tv.setOnClickListener(new click(myUser));
        avatar.setOnClickListener(new click(myUser));
        wanto_tv.setOnClickListener(new click(shareMessage));
        hadgo_tv.setOnClickListener(new click(shareMessage));
        comment_tv.setOnClickListener(new click(shareMessage));
        tag_tv.setOnClickListener(new click(shareMessage.getShTag()));
    }

    /**
     * 设置照片
     *
     * @param multiImageView
     * @param shareMessage
     */
    private void setImages(final MultiImageView multiImageView, final ShareMessage_HZ shareMessage) {
        multiImageView.setRegisterForContextMenu(true);
        multiImageView.setList(DataFormateUtils.thumbnailList(ctx, shareMessage.getShImgs()));
        multiImageView.setBigImagesList(DataFormateUtils.bigImagesList(ctx, shareMessage.getShImgs()));
        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(ctx, shareMessage.getShImgs());

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
                imageList = multiImageView.getBigImagesList();
            }
        });


    }

    /**
     * 设置视频
     *
     * @param holder
     * @param shareMessage
     */
    private void setVideo(ViewHolder holder, final ShareMessage_HZ shareMessage) {
        RelativeLayout videoLayout = holder.getView(R.id.rl_share_video_layout);
        final VideoView videoView = holder.getView(R.id.vv_share_preview_video);
        final ImageView videoPrevideo = holder.getView(R.id.im_share_video_priview);
        final ImageView playvideo = holder.getView(R.id.im_share_start_btn);
        final ProgressBar videoDownloadPb = holder.getView(R.id.pb_share_loading);

        if (shareMessage.getVideo() != null
                && !TextUtils.isEmpty(shareMessage.getVideo().getFileUrl(ctx))) {

            final String videoUrl = shareMessage.getVideo().getFileUrl(ctx);

            videoLayout.setVisibility(View.VISIBLE);
            videoPrevideo.setVisibility(View.VISIBLE);
            playvideo.setVisibility(View.VISIBLE);
            videoDownloadPb.setVisibility(View.GONE);
            /*设置标识，为了不出现错乱*/
            videoView.setTag(videoUrl);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0.0f, 0.0f);
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
/*需要播放*/
                    mp.setLooping(true);
                    mp.start();
                }
            });

/*判断视频有没有*/
            if (shareMessage.getVideoPreview() != null) {
                ImageHandlerUtils.loadIamge(ctx, shareMessage.getVideoPreview().getFileUrl(ctx), videoPrevideo);
            } else {
                videoPrevideo.setBackgroundColor(ctx.getResources().getColor(R.color.gray_lighter));
            }

//            videoDownloadPb.setVisibility(View.VISIBLE);
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(ctx, VideoplayerActivity.class);
                        intent.putExtra(Contants.INTENT_KEY_VIDEO_PATH, shareMessage.getVideo().getFileUrl(ctx));
                        intent.putExtra(Contants.INTENT_KEY_VIDEO_PREVIEW_PATH, shareMessage.getVideoPreview().getFileUrl(ctx));
                        ctx.startActivity(intent);
                        ((Activity) ctx).overridePendingTransition(0, 0);

                    }

                    return true;
                }
            });

            playvideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //下载并且播放视频
                    downloadVideo(videoUrl, videoView, videoPrevideo, videoDownloadPb, playvideo);
                }
            });

        } else {
            videoLayout.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            videoPrevideo.setVisibility(View.GONE);
            playvideo.setVisibility(View.GONE);
            videoDownloadPb.setVisibility(View.GONE);
        }


    }

    /**
     * 下载视频并且播放
     *
     * @param url           getfileurl
     * @param videoView     播放器
     * @param videoPrevideo 视频预览图片
     * @param pb            进度条
     * @param playvideo
     */
    private void downloadVideo(final String url,
                               final VideoView videoView,
                               final ImageView videoPrevideo,
                               final ProgressBar pb,
                               ImageView playvideo) {

        pb.setVisibility(View.VISIBLE);
        playvideo.setVisibility(View.GONE);

//        String filePath = Environment.getExternalStorageDirectory().getPath()
//                + Contants.FILE_PAHT_DOWNLOAD
//                + StorageUtils.getFileName(url);
        String filePath=  StorageUtils.createVideoFile(ctx) + "/" + StorageUtils.getFileName(url);
        File file = new File(filePath);
//        videoPlayerList.add(view);
        LogUtils.e("down load filePath = " + filePath);

        if (file.exists()) {//视频已经下载了
            videoView.setVideoPath(filePath);
            pb.setVisibility(View.GONE);
            videoPrevideo.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();


        } else {//视频未下载，进行下载

            //下载完成之后进行播放
            NetworkReuqest.call(ctx, url, new NetworkReuqest.JsonRequstCallback<String>() {
                @Override
                public void onSuccess(String videoPath) {
//                    LogUtils.E("response Filepath = " + object);
                    if (videoView.getTag().equals(url)) {
                        videoView.setVideoPath(videoPath);
                        pb.setVisibility(View.GONE);
                        videoPrevideo.setVisibility(View.GONE);

                        videoView.setVisibility(View.VISIBLE);
                        videoView.start();
                    }
                }

                @Override
                public void onFaile(VolleyError error) {
                    Toast.makeText(ctx, R.string.toast_download_video_failure, Toast.LENGTH_SHORT).show();
                }
            });


        }


    }


    @Override
    public int getlayoutid(int position) {
        return R.layout.item_discover;
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
        private ShareMessage_HZ shareMessage;

        public click(Object o) {
            this.o = o;


//            LogUtils.logD("dian ji ");
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.id_im_userH://用户资料
                    MyUser u = (MyUser) o;
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

                        CommonFunctionUtils.wantToGo(ctx, cuser, UserUtils.isHadCurrentUser(shWantedNum, cuser.getObjectId()),
                                shareMessage, (TextView) v, new CommonFunctionUtils.Callback() {
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
                        shareMessage = (ShareMessage_HZ) o;
                        List<String> shVisitedNum = shareMessage.getShVisitedNum();
                        CommonFunctionUtils.visit(ctx, cuser, UserUtils.isHadCurrentUser(shVisitedNum, cuser.getObjectId()),
                                shareMessage, v, new CommonFunctionUtils.Callback() {
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
        bundle.putSerializable(Contants.INTENT_KEY_DISCOVER, shareMessage);

        startActivity(MessageDetailActivity.class, bundle);

    }


}
