package com.young.share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.duanqu.qupai.android.app.QupaiServiceImpl;
import com.duanqu.qupai.editor.EditorResult;
import com.duanqu.qupai.recorder.EditorCreateInfo;
import com.duanqu.qupai.upload.AuthService;
import com.duanqu.qupai.upload.QupaiAuthListener;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.config.VideoRecorderConfig;
import com.young.share.interfaces.GoToUploadImages;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.PictureInfo;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.model.gson.PlaceSearch;
import com.young.share.network.BmobApi;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.EmotionUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.StorageUtils;
import com.young.share.utils.XmlUtils;
import com.young.share.utils.cache.ACache;
import com.young.share.utils.cache.DarftUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.MultiImageView.MultiImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 发送分享信息
 * <p/>
 * Created by Nearby Yang on 2015-10-23.
 */
public class ShareMessageActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.et_contnent_popupwin_content)
    private EditText content_et;
    @InjectView(R.id.tv_content_popupwin_share_lacation_i)
    private TextView shareLocation_tv;
    @InjectView(R.id.im_content_dialog_share_emotion)
    private ImageView emotion_im;
    @InjectView(R.id.im_activity_share_message_addimg)
    private ImageView addimg_im;
    @InjectView(R.id.miv_share_message_image)
    private MultiImageView multiImageView;
    @InjectView(R.id.vp_popupwindow_emotion_dashboard)
    private ViewPager vp_emotion_dashboard;
    @InjectView(R.id.ll_popip_window_emotion_panel)
    private LinearLayout emotionPanel_bg;
    @InjectView(R.id.ll_share_select_tag)
    private LinearLayout tagLl;//标签
    @InjectView(R.id.ll_share_loaction_info)
    private LinearLayout lactionInfoLl;//分享位置
    @InjectView(R.id.tv_content_popupwin_share_seletag)
    private TextView tag_tv;
    @InjectView(R.id.tv_share_message_tips)
    private TextView tips_tv;
    @InjectView(R.id.im_activity_share_message_add_video)
    private ImageView addVideo;
    @InjectView(R.id.vdv_share_video)
    private VideoView videoPreview;//视频

    private InputMethodManager imm;
    private final EditorCreateInfo createInfo = new EditorCreateInfo();//录制的时候，参数设置
    private String locationInfo;//定位信息
    private String tagInfo = null;//标签信息
    private double latitude;//纬度
    private double longitude;//经度
    private ACache acache;//缓存
    private Dialog4Tips dialog;//保存草稿的提示框
    private DarftUtils darftUtils;//草稿

    private boolean currentIsDiscount;//当前页面是否为商家优惠
    private String draftType;//草稿类型
    private String imageFilePath;//图片的地址
    private String videoPath;
    private boolean isVideo = false;//上传的内容是否是视频

    private int placeSelect = 0;//选择的地点的position
    private PlaceSearch.ResultsEntity placeResult;
    private static final int REQUEST_CODE_RECORD_VIDEO = 0x01;//录制视频requestCode
    private static final int FINISH_ACTIVITY = 0;//结束程序
    private static final int MESSAGE_TOAST=0x02;//toast
    private static final int MESSAGE_UPLOAD_IMAGE_FAILURE=0x03;//上传图片失败
    private static final int MESSAGE_UPLOAD_VIDEO_FAILURE=0x04;//上传视频失败
    private static final int MESSAGE_CLEAR=0x05;//清理工作
    private static final int MESSAGE_SHARE_SUCCESS=0x06;//分享失败
    private static final int MESSAGE_SHARE_FAILURE=0x07;//分享成功

    // TODO: 2016-02-27 删除图片的操作
    @Override
    public int getLayoutId() {
        return R.layout.activity_share_message;
    }

    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.let_me_share);

        Bundle bundle = getIntent().getExtras();
        currentIsDiscount = bundle.getBoolean(Contants.BUNDLE_CURRENT_IS_DISCOUNT, false);

//趣拍初始化
        initAuth(Contants.QUPAI_APP_KEY, Contants.QUPAI_APP_SECRET, Contants.QUPAI_APP_SPACE);

        String LONGITUDE = app.getCacheInstance().getAsString(Contants.ACAHE_KEY_LONGITUDE);
        String[] strs = LONGITUDE.split(",");
        longitude = Double.valueOf(strs[0]);
        latitude = Double.valueOf(strs[1]);


        if (currentIsDiscount) {
            tips_tv.setVisibility(View.VISIBLE);
            draftType = Contants.DRAFT_TYPE_DICOUNT;
        } else {
            tips_tv.setVisibility(View.GONE);
            draftType = Contants.DRAFT_TYPE_DICOVER;
        }

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void findviewbyid() {


        /**
         * 创建文件地址
         */
        String videoFilePath = StorageUtils.createVideoFile(this).getAbsolutePath()
                + "/" + Contants.FILE_START_NAME_VIDEO + System.currentTimeMillis()
                + Contants.VIDEO_EXTENSION;

        imageFilePath = StorageUtils.createImageFile(this).getAbsolutePath() + "/"
                + Contants.FILE_START_NAME_IMAGE + System.currentTimeMillis()
                + Contants.IMAGE_EXTENSION_PNG;
        /*配置摄像头参数*/
        createInfo.setSessionCreateInfo(VideoRecorderConfig.CreateInfo(this));
        createInfo.setNextIntent(null);
        createInfo.setOutputThumbnailSize(Contants.OUTPUT_VIDEO_WIDTH, Contants.OUTPUT_VIDEO_HEIGHT);//输出图片宽高,与视频的宽高一样
        createInfo.setOutputVideoPath(videoFilePath);//输出视频路径
        createInfo.setOutputThumbnailPath(imageFilePath);//输出图片路径

    }

    @Override
    public void bindData() {


//        popupWinListView = new PopupWinListView(this, tagList, false);

        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList =
                        DataFormateUtils.formate2PictureInfo4Local(multiImageView.getImagesList());

                EvaluateUtil.setupCoords(mActivity, (ImageView) view, pictureInfoList, position);
                Intent intent = new Intent(mActivity, BigPicActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable(Contants.INTENT_IMAGE_INFO_LIST, (Serializable) pictureInfoList);
                intent.putExtras(bundle);
                intent.putExtra(Contants.INTENT_CURRENT_ITEM, position);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        tagLl.setOnCreateContextMenuListener(contextMenuListener);
        //表情
        new EmotionUtils(mActivity, vp_emotion_dashboard, content_et);
        acache = ACache.get(mActivity);
        darftUtils = DarftUtils.builder(mActivity);
        dialog = new Dialog4Tips(mActivity);
//恢复草稿
        resetDraft();

        shareLocation_tv.setOnClickListener(this);
        emotion_im.setOnClickListener(this);
        addimg_im.setOnClickListener(this);
        addVideo.setOnClickListener(this);
        tagLl.setOnClickListener(this);
        lactionInfoLl.setOnClickListener(this);


        content_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionPanel_bg.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 鉴权 趣拍
     *
     * @param appKey    appkey
     * @param appsecret appsecret
     * @param space     space
     */
    private void initAuth(String appKey, String appsecret, String space) {
        AuthService service = AuthService.getInstance();
        service.setQupaiAuthListener(new QupaiAuthListener() {
            @Override
            public void onAuthError(int errorCode, String message) {
                LogUtils.e("AUTHTAG ErrorCode" + errorCode + " message = " + message);
            }

            @Override
            public void onAuthComplte(int responseCode, String responseMessage) {
//                Contant.accessToken = responseMessage;
                LogUtils.d("accessToken = " + responseMessage);
            }
        });
        service.startAuth(getApplicationContext(), appKey, appsecret, space);
    }

    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_share_message);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 返回上一级前，先询问是否保存草稿
                 */
                goback();
            }
        });

    }

    /**
     * 恢复草稿
     */
    private void resetDraft() {

        String draft_type = acache.getAsString(Contants.DRAFT_TYPE);

        if (!TextUtils.isEmpty(draft_type)) {

            if (currentIsDiscount && draft_type.equals(Contants.DRAFT_TYPE_DICOUNT)) {//商家优惠

                setDraft();
            } else if (!currentIsDiscount && draft_type.equals(Contants.DRAFT_TYPE_DICOVER)) {//发现
                setDraft();

            }
        }


    }

    /**
     * 回复草稿
     */
    private void setDraft() {

        final List<String> list = new ArrayList<>();
        final String draft_content = acache.getAsString(Contants.DRAFT_CONTENT);
        final JSONArray imgJsArray = acache.getAsJSONArray(Contants.DRAFT_IMAGES_LIST);

        if (draft_content != null || imgJsArray != null) {

            dialog.setContent(getString(R.string.had_draft_would_reset_tips));
            dialog.setBtnOkText(getString(R.string.reset));
            dialog.setBtnCancelText(getString(R.string.do_not_reset));

            dialog.setDialogListener(new Dialog4Tips.Listener() {
                @Override
                public void btnOkListenter() {

                    content_et.setText(draft_content);
                    tag_tv.setText(acache.getAsString(Contants.DRAFT_TAG));
                    shareLocation_tv.setText(acache.getAsString(Contants.DRAFT_LOCATION_INFO));

                    if (imgJsArray != null) {
                        for (int i = 0; i < imgJsArray.length(); i++) {

                            try {
                                list.add(imgJsArray.getString(i));
                            } catch (JSONException e) {
                                LogUtils.e("读取jsonArray数据出错" + e.toString());
                            }

                        }
                        multiImageView.setList(list);
//                        gridViewAdapter.setDatas(DataFormateUtils.formateLocalImage(list));
                    }

                    //删除草稿
                    darftUtils.deleteDraft();
                    dialog.dismiss();
                }

                @Override
                public void btnCancelListener() {

                    //删除草稿
                    darftUtils.deleteDraft();

                    dialog.dismiss();
                }
            });
            dialog.show();
            LogUtils.i("有草稿 存在");
        }

    }


    /**
     * contextMenu
     */
    private View.OnCreateContextMenuListener contextMenuListener = new View.OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            int menuViewId = 0;
            switch (view.getId()) {

                case R.id.ll_share_select_tag://选择标签
                    menuViewId = R.menu.menu_context_empty;
                    addTagMenu(contextMenu);
                    break;

                case R.id.popupwin_edit_personnal_info_age:

                    break;

            }


            getMenuInflater().inflate(menuViewId, contextMenu);
        }
    };

    /**
     * 添加标签选择的选择contextMenu
     *
     * @param contextMenu
     */
    private void addTagMenu(ContextMenu contextMenu) {
        List<String> tagList = XmlUtils.getSelectTag(this);
        tagList.remove(0);
        tagList.add(0, getString(R.string.txt_tag_invisible));

        for (int i = 0; i < tagList.size(); i++) {
            contextMenu.add(0, i, Menu.NONE, tagList.get(i));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_send) {
            final List<String> lists = multiImageView.getImagesList();
            final String content = content_et.getText().toString();

            if (!TextUtils.isEmpty(content) || lists != null && lists.size() > 0 || !TextUtils.isEmpty(videoPath)) {//信息或者图片不为空
            /*发送*/
                // TODO: 2016-03-28 使用线程进行分享
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(content, lists);
                    }
                }).start();

            }
        } else {//信息或者图片为空

            SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.share_messages_empty));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        tag_tv.setText(item.getTitle());
        tagInfo = !TextUtils.isEmpty(item.getTitle()) && !getString(R.string.txt_tag_invisible).equals(item.getTitle()) ?
                String.valueOf(item.getTitle()) : "";

        return super.onContextItemSelected(item);
    }
    // TODO: 2016-04-06 完善修改图片的功能

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.im_content_dialog_share_emotion://添加表情

                imm.hideSoftInputFromWindow(content_et.getWindowToken(), 0);

                emotionPanel_bg.setVisibility(emotionPanel_bg.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;

            case R.id.im_activity_share_message_addimg://添加照片
                emotionPanel_bg.setVisibility(emotionPanel_bg.getVisibility() == View.VISIBLE ? View.GONE : View.GONE);
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(this, multiImageView.getImagesList());

                ArrayList<String> l = new ArrayList<>();
                if (pictureInfoList != null && pictureInfoList.size() > 0) {
                    for (PictureInfo pictureInfo : pictureInfoList) {

                        l.add(pictureInfo.getImageUrl());
                    }
                }
                //选择图片
                ImageHandlerUtils.starSelectImages(mActivity, l);

                break;

            case R.id.ll_share_loaction_info://选择位置

                intents = new Intent(mActivity, BaiduMapActivity.class);
                intents.putExtra(Contants.INTENT_BMOB_IS_POSITION, true);
                intents.putExtra(Contants.INTENT_SELECTOR_POSITION, placeSelect);

                startActivityForResult(intents, Contants.REQUSET_CODE_PLACE);

                break;

            case R.id.ll_share_select_tag://标签选择

                openContextMenu(v);
                break;

            case R.id.im_activity_share_message_add_video:/*录制视频*/

                QupaiServiceImpl qupaiService = new QupaiServiceImpl.Builder()
                        .setEditorCreateInfo(createInfo).build();
                qupaiService.showRecordPage(mActivity, REQUEST_CODE_RECORD_VIDEO);


                break;
        }
    }


    /**
     * 获取图片返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Contants.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT).size() > 0) {

                    List<String> pathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

                    //图片路径
                    multiImageView.setList(urlAddHead(pathList));
                    if (multiImageView.getImagesList() != null
                            && multiImageView.getImagesList().size() > 0) {
                        /*添加视频的按钮*/
                        addVideo.setVisibility(View.GONE);
                    }
                }

            }
        }

        if (resultCode == Contants.RESULT_CODE_PLACE) {//地点选择了
            //判断是否是第一个，第一个意思是不显示地点
            placeResult = (PlaceSearch.ResultsEntity) data.getExtras().getSerializable(Contants.INTENT_PLACE);
            placeSelect = data.getIntExtra(Contants.INTENT_SELECTOR_POSITION, 0);
            if (placeSelect != 0) {
                shareLocation_tv.setText(placeResult.getName());
                locationInfo = placeResult.getName();
                // 在现实市级的时候，没有返回经纬度
                longitude = placeResult.getLocation() != null ? placeResult.getLocation().getLng() : 114.424984;
                latitude = placeResult.getLocation() != null ? placeResult.getLocation().getLat() : 23.043472;


            }


        } else if (requestCode == REQUEST_CODE_RECORD_VIDEO) {
            if (resultCode == RESULT_OK) {
                EditorResult result = new EditorResult(data);

                videoPath = result.getPath();

                if (!TextUtils.isEmpty(videoPath)) {
                    addimg_im.setVisibility(View.GONE);
                }

                /*输出的缩略图的地址没有返回，取输出的地址*/
                videoPlayback();
//                LogUtils.E("file path  video = " + filePath + " iamge = " + thumbnailPath);


            }
        }


    }

    /**
     * 播放视频
     */
    private void videoPlayback() {
        videoPreview.setVisibility(View.VISIBLE);
        videoPreview.setTag(videoPath);//播放器
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0.0f, 0.0f);
            }
        });
        videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
/*需要播放*/
                mp.setLooping(true);
                mp.start();
            }
        });

/*不能直接使用onclick，需要通过ontouch进行监听,只触发down*/
        videoPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(mActivity, VideoplayerActivity.class);
                    intent.putExtra(Contants.INTENT_KEY_VIDEO_PATH, videoPath);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

                return false;
            }
        });

        /*本地播放视频*/
        File file = new File(videoPath);
        if (file.exists()) {//视频找到了
            videoPreview.setVideoPath(videoPath);
            videoPreview.start();
        } else {//视频找不到，可能被删除

            Toast.makeText(this, R.string.toast_video_can_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将图片路径添加file://，为了实现universal image loader 加载本地图片
     *
     * @param pathList
     * @return
     */
    private List<String> urlAddHead(List<String> pathList) {
        ArrayList<String> mSelectPath = new ArrayList<>();
        for (String path : pathList) {
            path = Contants.FILE_HEAD + path;
            mSelectPath.add(path);

        }

        return mSelectPath;
    }


    @Override
    public void handerMessage(Message msg) {
//        LogUtils.logI("handler = "+msg);
        switch (msg.what) {
            case FINISH_ACTIVITY:
                intents.setAction(Contants.BORDCAST_REQUEST_REFRESH);
                intents.putExtra(Contants.REFRESH_TYPE, msg.what);
                sendBroadcast(intents);
                mActivity.finish();
                break;

            case MESSAGE_TOAST://发送分享的吐司
                toast(R.string.sending);
                break;

            case MESSAGE_UPLOAD_IMAGE_FAILURE://上传照片失败
                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.upload_images_fail));
                break;

            case MESSAGE_UPLOAD_VIDEO_FAILURE://上传视频失败

                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.upload_video_fail));
                break;

            case MESSAGE_CLEAR://发送完成之后进行clear工作

                clean();
                break;
            case MESSAGE_SHARE_SUCCESS://分享成功
                toast(R.string.share_messages_success);
                break;
            case MESSAGE_SHARE_FAILURE://分享失败,并且保存草稿
                toast(R.string.share_message_fail);
                saveDarft();
                break;
        }


    }


    @Override
    public void mBack() {
        goback();
    }

    /**
     * 返回上一级
     * 退出当前activity
     */
    private void goback() {

        if (!TextUtils.isEmpty(content_et.getText().toString()) | multiImageView.getImagesList() != null) {

            dialog.setContent(getString(R.string.need_to_save_draft));
            dialog.setBtnOkText(getString(R.string.save));
            dialog.setBtnCancelText(getString(R.string.do_not_save));
            dialog.setDialogListener(new Dialog4Tips.Listener() {
                @Override
                public void btnOkListenter() {

                    List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(mActivity, multiImageView.getImagesList());

                    ArrayList<String> l = new ArrayList<>();
                    for (PictureInfo pictureInfo : pictureInfoList) {

                        l.add(pictureInfo.getImageUrl());
                    }

                    darftUtils.saveDraft(draftType, content_et.getText().toString(),
                            shareLocation_tv.getText().toString(),
                            tag_tv.getText().toString(),
                            l
                    );
                    back2MainActivity();

                }

                @Override
                public void btnCancelListener() {
                    back2MainActivity();

                }
            });
            dialog.show();

        } else {
            back2MainActivity();
        }
    }

    /**
     * 回到MainActivity
     */
    private void back2MainActivity() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        mBackStartActivity(MainActivity.class);
        //删除搜索的记录 Contants.ACACHE_PLACE_SERVE
        boolean removed = acache.remove(Contants.ACACHE_PLACE_SERVE);
        LogUtils.e("removed = " + removed);
        mActivity.finish();
    }

    /**
     * 发送
     * 分享信息或者是商家优惠
     *
     * @param content
     * @param lists
     */
    private void send(String content, List<String> lists) {


        mBackStartActivity(MainActivity.class);

        //发送信息
        if (currentIsDiscount) {//商家优惠
            shareDiscount(lists, content);
        } else {
            shareDicover(lists, content);
        }
    }

    /**
     * 分享信息
     * 包含视频 或者 图片
     */
    private void shareDicover(List<String> lists, String content) {


        final ShareMessage_HZ shareMessage_hz = new ShareMessage_HZ();

        shareMessage_hz.setShContent(content);
        shareMessage_hz.setShTag(tagInfo);
        shareMessage_hz.setShLocation(!TextUtils.isEmpty(locationInfo) ? locationInfo : null);
        shareMessage_hz.setGeographic(new BmobGeoPoint(longitude, latitude));
        shareMessage_hz.setMyUserId(cuser);
        shareMessage_hz.setShCommNum(0);
        shareMessage_hz.setShVisitedNum(new ArrayList<String>());
        shareMessage_hz.setShWantedNum(new ArrayList<String>());

        if (lists != null && !lists.isEmpty()) {//有上传图片的

            String[] files = new String[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                files[i] = lists.get(i);
            }
            BmobApi.UploadFiles(mActivity, files, Contants.IMAGE_TYPE_SHARE, new GoToUploadImages() {
                @Override
                public void Result(String[] urls, BmobFile[] bmobFile) {
/*数组转换成list*/
                    List<String> list = new ArrayList<>();
                    Collections.addAll(list, urls);
                    shareMessage_hz.setShImgs(list);
                    //保存分享信息到云端
                    shareMessage(shareMessage_hz);

                }

                @Override
                public void onError(int statuscode, String errormsg) {

                    mHandler.sendEmptyMessage(MESSAGE_UPLOAD_IMAGE_FAILURE);
                }
            });

        } else if (!TextUtils.isEmpty(videoPath)) {//视频

            String[] file = new String[]{videoPath, imageFilePath};
            BmobApi.UploadFiles(mActivity, file, Contants.FILE_TYPE_MULTI, new GoToUploadImages() {
                /**
                 * @param urls
                 * @param bmobFile
                 */
                @Override
                public void Result(String[] urls, BmobFile bmobFile[]) {
                    LogUtils.d("video = " + urls[0]);
                  /*第一个文件*/
                    shareMessage_hz.setVideo(bmobFile[0]);
                    /*第二个文件，应该是图片*/
                    shareMessage_hz.setVideoPreview(bmobFile[1]);
                    LogUtils.d("video prevideo  = " + urls[1]);
                    //保存分享信息到云端
                    shareMessage(shareMessage_hz);

                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    mHandler.sendEmptyMessage(MESSAGE_UPLOAD_VIDEO_FAILURE);
                }
            });
        } else {//没有上传图片的
            mBackStartActivity(MainActivity.class);
            shareMessage(shareMessage_hz);

        }
        //在没有销毁该Activity的时候，需要清空该Activity中的数据

//shareMessage_hz.setShImgs();
//                LogUtils.e("右边点击");


    }


    /**
     * 分享商家优惠信息
     */
    private void shareDiscount(List<String> lists, String content) {

        final DiscountMessage_HZ disMessages = new DiscountMessage_HZ();


        disMessages.setDtTag(tagInfo);
        disMessages.setDtContent(content);

        disMessages.setDtLocation(!TextUtils.isEmpty(locationInfo) ? locationInfo : null);
        disMessages.setMyUserId(cuser);


        if (lists != null && !lists.isEmpty()) {//有上传图片的

            String[] files = new String[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                files[i] = lists.get(i);
            }
            BmobApi.UploadFiles(mActivity, files, Contants.IMAGE_TYPE_SHARE, new GoToUploadImages() {
                @Override
                public void Result(String[] urls, BmobFile[] bmobFiles) {


                    List<String> list = new ArrayList<>();

                    Collections.addAll(list, urls);

                    disMessages.setDtImgs(list);
                    //保存分享信息到云端
                    shareDiscountMessage(disMessages);

                }

                @Override
                public void onError(int statuscode, String errormsg) {
//                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.upload_images_fail));
                    mHandler.sendEmptyMessage(MESSAGE_UPLOAD_IMAGE_FAILURE);
                }
            });

        } else {//没有上传图片的

            mBackStartActivity(MainActivity.class);
            shareDiscountMessage(disMessages);

        }


    }

    /**
     * 分享优惠信息
     *
     * @param discountMessage
     */
    private void shareDiscountMessage(DiscountMessage_HZ discountMessage) {
        //清理工作
//        clean();
        mHandler.sendEmptyMessage(MESSAGE_CLEAR);
        //保存
        discountMessage.save(mActivity, new SaveListener() {
            @Override
            public void onSuccess() {
//                toast(R.string.share_messages_success);
                mHandler.sendEmptyMessage(MESSAGE_SHARE_SUCCESS);
                mHandler.sendEmptyMessageDelayed(Contants.REFRESH_TYPE_DISCOUNT, Contants.ONE_SECOND);
            }

            @Override
            public void onFailure(int i, String s) {
//                toast(R.string.share_message_fail);
//                saveDarft();
                mHandler.sendEmptyMessage(MESSAGE_SHARE_FAILURE);
                mHandler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Contants.ONE_SECOND);
            }
        });

    }


    /**
     * 分享信息
     *
     * @param shareMessage_hz
     */
    private void shareMessage(ShareMessage_HZ shareMessage_hz) {
// TODO: 2015-11-13  分享信息后台一直gcc
     mHandler.sendEmptyMessage(MESSAGE_CLEAR);

        shareMessage_hz.save(mActivity, new SaveListener() {
            @Override
            public void onSuccess() {

                mHandler.sendEmptyMessage(MESSAGE_SHARE_SUCCESS);
//                LogUtils.logI("share messages success ");
                mHandler.sendEmptyMessageDelayed(Contants.REFRESH_TYPE_DISCOVER, Contants.ONE_SECOND);
            }

            @Override
            public void onFailure(int i, String s) {
               mHandler.sendEmptyMessage(MESSAGE_SHARE_FAILURE);
//                LogUtils.logI("share messages faile ");



                mHandler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Contants.ONE_SECOND);
            }
        });
    }

    /**
     * 保存草稿
     */
    private void saveDarft() {

//        List<PictureInfo> pictureInfoList = gridViewAdapter.getData();
        List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(this, multiImageView.getImagesList());

        ArrayList<String> l = new ArrayList<>();
        for (PictureInfo pictureInfo : pictureInfoList) {

            l.add(pictureInfo.getImageUrl());
        }
        darftUtils.saveDraft(draftType,
                content_et.getText().toString(),
                shareLocation_tv.getText().toString(),
                tag_tv.getText().toString(),
                l
        );

    }

    /**
     * 清理工作
     */
    private void clean() {
        content_et.getText().clear();
        shareLocation_tv.setText("");
        tag_tv.setText("");
//        gridViewAdapter.setDatas(null);
        multiImageView.setList(new ArrayList<String>());
    }


}
