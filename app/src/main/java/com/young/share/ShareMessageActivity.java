package com.young.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.share.adapter.GridviewAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.PictureInfo;
import com.young.share.model.ShareMessage_HZ;
import com.young.share.myInterface.GoToUploadImages;
import com.young.share.network.BmobApi;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.EmotionUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.XmlUtils;
import com.young.share.utils.cache.ACache;
import com.young.share.utils.cache.DarftUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.PopupWinListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 发送分享信息
 * <p/>
 * Created by Nearby Yang on 2015-10-23.
 */
public class ShareMessageActivity extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.et_contnent_popupwin_content)
    private EditText content_et;
    @InjectView(R.id.tv_content_popupwin_share_lacation_i)
    private TextView shareLocation_tv;
    @InjectView(R.id.im_content_dialog_share_emotion)
    private ImageView emotion_im;
    @InjectView(R.id.im_activity_share_message_addimg)
    private ImageView addimg_im;
    @InjectView(R.id.gv_content_popupwin_share_addimg)
    private GridView gv_img;
    @InjectView(R.id.vp_popupwindow_emotion_dashboard)
    private ViewPager vp_emotion_dashboard;
    @InjectView(R.id.ll_popip_window_emotion_panel)
    private LinearLayout emotionPanel_bg;
    @InjectView(R.id.im_content_popupwin_share_lacation_i)
    private ImageView shareLocation_im;
    @InjectView(R.id.im_content_popupwin_share_seletag)
    private ImageView tag_im;
    @InjectView(R.id.tv_content_popupwin_share_seletag)
    private TextView tag_tv;
    @InjectView(R.id.tv_share_message_tips)
    private TextView tips_tv;

    private GridviewAdapter gridViewAdapter;
    private InputMethodManager imm;

    private PopupWinListView popupWinListView;
    private boolean isResgiter = false;
    private String locationInfo;//定位信息
    private String tagInfo = "未分类";//标签信息
    private double latitude;//纬度
    private double longitude;//经度
    private boolean addLocation = false;//是否添加地理信息
    private boolean isGotLocationInfo = false;//是否已经获取地理信息
    private String locationInfoString;//位置信息
    private ACache acache;//缓存
    private Dialog4Tips dialog;//保存草稿的提示框
    private DarftUtils darftUtils;//草稿
    private static final int FINISH_ACTIVITY = 0;
    private boolean currentIsDiscount;//当前页面是否为商家优惠
    private String draftType;//草稿类型

    // TODO: 2015-12-05 移除item而不需要刷新整个ListView
    // TODO: 2016-02-27 删除图片的操作
    @Override
    public int getLayoutId() {
        return R.layout.activity_share_message;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        currentIsDiscount = bundle.getBoolean(Contants.BUNDLE_CURRENT_IS_DISCOUNT, false);


        if (currentIsDiscount) {
            tips_tv.setVisibility(View.VISIBLE);
            draftType = Contants.DRAFT_TYPE_DICOUNT;
        } else {
            tips_tv.setVisibility(View.INVISIBLE);
            draftType = Contants.DRAFT_TYPE_DICOVER;
        }

        setTvTitle(R.string.let_me_share);
        setTvRight(R.string.send);


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void bindData() {


        List<String> tagList = XmlUtils.getSelectTag(this);
        tagList.remove(0);

        popupWinListView = new PopupWinListView(this, tagList, false);
        gridViewAdapter = new GridviewAdapter(this, gv_img, true);
        gridViewAdapter.setDatas(null, true);

        gv_img.setAdapter(gridViewAdapter);

        popupWinListView.setItemClick(new PopupWinListView.onItemClick() {
            @Override
            public void onClick(View view, String str, int position, long id) {
                if (str != null) {
                    tagInfo = str;
                    tag_tv.setText(tagInfo);
                }


            }
        });

        //监听
        setItemListener(new itemClick());
        //表情
        new EmotionUtils(mActivity, vp_emotion_dashboard, content_et);
        acache = ACache.get(mActivity);
        darftUtils = DarftUtils.builder(mActivity);
        dialog = new Dialog4Tips(mActivity);
//恢复草稿
        resetDraft();

        startLocation();
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
                                LogUtils.logE("读取jsonArray数据出错" + e.toString());
                            }

                        }

                        gridViewAdapter.setDatas(DataFormateUtils.formateStringInfoList(mActivity,list), true);
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
            LogUtils.logI("有草稿 存在");
        }

    }

    @Override
    public void findviewbyid() {

        shareLocation_tv.setOnClickListener(this);
        emotion_im.setOnClickListener(this);
        addimg_im.setOnClickListener(this);
        shareLocation_im.setOnClickListener(this);
        tag_im.setOnClickListener(this);

        content_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionPanel_bg.setVisibility(View.GONE);
            }
        });


    }


    //注册广播接收者。地理信息
    public void registerBoradcastReceiver() {

        myIntentFilter.addAction(Contants.BORDCAST_LOCATIONINFO);
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
        isResgiter = true;

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.im_content_dialog_share_emotion://添加表情

                imm.hideSoftInputFromWindow(content_et.getWindowToken(), 0);

                emotionPanel_bg.setVisibility(emotionPanel_bg.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;

            case R.id.im_activity_share_message_addimg://添加照片

               List<PictureInfo> pictureInfoList = gridViewAdapter.getData();

                ArrayList<String> l = new ArrayList<>();
                for (PictureInfo pictureInfo : pictureInfoList){

                    l.add(pictureInfo.getImageUrl());
                }
                ImageHandlerUtils.starSelectImages(mActivity, l);

                break;

            case R.id.im_content_popupwin_share_lacation_i://选择位置
                addLocation = true;
                if (!isGotLocationInfo) {
                    startLocation();
                } else {
                    shareLocation_tv.setText(locationInfoString);
                }



                break;

            case R.id.im_content_popupwin_share_seletag://标签选择

                popupWinListView.onShow(tag_im);

                break;
        }
    }

    /**
     * 启动定位
     */
    private void startLocation() {
        intents.setAction(Contants.BORDCAST_REQUEST_LOCATIONINFO);
        sendBroadcast(intents);

        //注册广播接收者
        registerBoradcastReceiver();
        if (addLocation&&!isGotLocationInfo) {
            mToast(R.string.locationing);
        }
    }

    /**
     * 位置广播接收者
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Contants.BORDCAST_LOCATIONINFO)) {

                Bundle bundle = intent.getBundleExtra(BaseAppCompatActivity.BUNDLE_BROADCAST);
                latitude = bundle.getDouble(Contants.LATITUDE, 39.963175);//纬度
                longitude = bundle.getDouble(Contants.LONGITUDE, 116.400244);//经度
                String province = bundle.getString(Contants.PROVINCE);
                String city = bundle.getString(Contants.CITY);
                String district = bundle.getString(Contants.DISTRICT);
                String street = bundle.getString(Contants.STREET);
                String streetNumber = bundle.getString(Contants.STREETNUMBER);

                locationInfo = String.format("%s%s%s%s", city, district, street, streetNumber);
                locationInfoString = String.format("%s%s%s", district, street, streetNumber);

                if (addLocation) {
                    shareLocation_tv.setText(locationInfoString);
                }
                isGotLocationInfo = !TextUtils.isEmpty(locationInfo);
            }
        }

    };

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

                    ArrayList<String> mSelectPath = new ArrayList<>();

                    for (String path : pathList) {
                        path = Contants.FILE_HEAD + path;
                        mSelectPath.add(path);

                    }

                    //图片路径
                    gridViewAdapter.setDatas(DataFormateUtils.formateStringInfoList(mActivity,mSelectPath), true);
                }


            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isResgiter) {
            unregisterReceiver(broadcastReceiver);
        }

    }


    @Override
    public void handerMessage(Message msg) {
//        LogUtils.logI("handler = "+msg);

        if (msg.what != FINISH_ACTIVITY) {
            intents.setAction(Contants.BORDCAST_REQUEST_REFRESH);
            intents.putExtra(Contants.REFRESH_TYPE, msg.what);
            sendBroadcast(intents);
        }

        mActivity.finish();
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

        if (!TextUtils.isEmpty(content_et.getText().toString()) | gridViewAdapter.getData() != null) {

            dialog.setContent(getString(R.string.need_to_save_draft));
            dialog.setBtnOkText(getString(R.string.save));
            dialog.setBtnCancelText(getString(R.string.do_not_save));
            dialog.setDialogListener(new Dialog4Tips.Listener() {
                @Override
                public void btnOkListenter() {

                    List<PictureInfo> pictureInfoList = gridViewAdapter.getData();

                    ArrayList<String> l = new ArrayList<>();
                    for (PictureInfo pictureInfo : pictureInfoList){

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
        mActivity.finish();
    }

    /**
     * 点击事件监听
     */
    private class itemClick implements BarItemOnClick {

        @Override
        public void leftClick(View v) {
            /**
             * 返回上一级前，先询问是否保存草稿
             */
            goback();

        }

        /**
         * 分享信息
         *
         * @param v
         */
        @Override
        public void rightClivk(View v) {//发送按钮

            String content = content_et.getText().toString();

            List<PictureInfo> pictureInfoList = gridViewAdapter.getData();

            ArrayList<String> lists = new ArrayList<>();
            for (PictureInfo pictureInfo : pictureInfoList){

                lists.add(pictureInfo.getImageUrl());
            }


            if (!TextUtils.isEmpty(content) || lists != null) {//信息或者图片不为空

                mToast(R.string.sending);
                mBackStartActivity(MainActivity.class);

                //发送信息
                if (currentIsDiscount) {//商家优惠
                    shareDiscount(lists, content);
                } else {
                    shareDicover(lists, content);
                }

            } else {//信息或者图片为空

                SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.share_messages_empty));

            }


        }
    }

    /**
     * 分享信息
     */
    private void shareDicover(List<String> lists, String content) {


        final ShareMessage_HZ shareMessage_hz = new ShareMessage_HZ();

        shareMessage_hz.setShContent(content);
        shareMessage_hz.setShTag(tagInfo);
        if (!TextUtils.isEmpty(locationInfo)) {
            shareMessage_hz.setShLocation(locationInfo);
        }
        shareMessage_hz.setGeographic(new BmobGeoPoint(longitude, latitude));
        shareMessage_hz.setUserId(mUser);
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
                public void Result(boolean isFinish, String[] urls) {

                    if (isFinish) {
                        List<String> list = new ArrayList<>();

                        Collections.addAll(list, urls);

                        shareMessage_hz.setShImgs(list);

                        //保存分享信息到云端
                        shareMessage(shareMessage_hz);


                    }
//                            else {
//
//                                LogUtils.logE("回调第一次，isfinish = " + isFinish);
//                                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.some_images_maybe_miss));
//                            }
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.upload_images_fail));
                }
            });

        } else {//没有上传图片的
            mBackStartActivity(MainActivity.class);
            shareMessage(shareMessage_hz);

        }
        //在没有销毁该Activity的时候，需要清空该Activity中的数据

//shareMessage_hz.setShImgs();
//                LogUtils.logE("右边点击");


    }


    /**
     * 分享商家优惠信息
     */
    private void shareDiscount(List<String> lists, String content) {

        final DiscountMessage_HZ disMessages = new DiscountMessage_HZ();


        disMessages.setDtTag(tagInfo);
        disMessages.setDtContent(content);
        disMessages.setDtLocation(locationInfo);
        disMessages.setUserId(mUser);


        if (lists != null && !lists.isEmpty()) {//有上传图片的

            String[] files = new String[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                files[i] = lists.get(i);
            }
            BmobApi.UploadFiles(mActivity, files, Contants.IMAGE_TYPE_SHARE, new GoToUploadImages() {
                @Override
                public void Result(boolean isFinish, String[] urls) {

                    if (isFinish) {
                        List<String> list = new ArrayList<>();

                        Collections.addAll(list, urls);

                        disMessages.setDtImgs(list);
                        //保存分享信息到云端
                        shareDiscountMessage(disMessages);

                    }
//                            else {
//
//                                LogUtils.logE("回调第一次，isfinish = " + isFinish);
//                                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.some_images_maybe_miss));
//                            }
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.upload_images_fail));
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
        clean();
        //保存
        discountMessage.save(mActivity, new SaveListener() {
            @Override
            public void onSuccess() {
                mToast(R.string.share_messages_success);
                mHandler.sendEmptyMessageDelayed(Contants.REFRESH_TYPE_DISCOUNT, Contants.ONE_SECOND);
            }

            @Override
            public void onFailure(int i, String s) {
                mToast(R.string.share_message_fail);
                saveDarft();
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
        clean();
        shareMessage_hz.save(mActivity, new SaveListener() {
            @Override
            public void onSuccess() {
                mToast(R.string.share_messages_success);
//                LogUtils.logI("share messages success ");
                mHandler.sendEmptyMessageDelayed(Contants.REFRESH_TYPE_DISCOVER, Contants.ONE_SECOND);
            }

            @Override
            public void onFailure(int i, String s) {
                mToast(R.string.share_message_fail);
//                LogUtils.logI("share messages faile ");
                saveDarft();


                mHandler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Contants.ONE_SECOND);
            }
        });
    }

    /**
     * 保存草稿
     */
    private void saveDarft() {

        List<PictureInfo> pictureInfoList = gridViewAdapter.getData();

        ArrayList<String> l = new ArrayList<>();
        for (PictureInfo pictureInfo : pictureInfoList){

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
        gridViewAdapter.setDatas(null, true);

    }


}
