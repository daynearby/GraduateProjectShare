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
import com.young.adapter.myGridViewAdapter;
import com.young.annotation.InjectView;
import com.young.base.BaseAppCompatActivity;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.ShareMessage_HZ;
import com.young.myInterface.GoToUploadImages;
import com.young.network.BmobApi;
import com.young.utils.EmotionUtils;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.XmlUtils;
import com.young.utils.cache.ACache;
import com.young.utils.cache.DarftUtils;
import com.young.views.Dialog4Tips;
import com.young.views.PopupWinListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private myGridViewAdapter gridViewAdapter;
    private InputMethodManager imm;

    private PopupWinListView popupWinListView;
    private boolean isResgiter = false;
    private String locationInfo;//定位信息
    private String tagInfo = "未分类";//标签信息
    private ACache acache;//缓存
    private Dialog4Tips dialog;//保存草稿的提示框
    private DarftUtils darftUtils;//草稿
    private static final int FINISH_ACTIVITY = 0;

    // TODO: 2015-12-05 移除item而不需要刷新整个ListView
    @Override
    public int getLayoutId() {
        return R.layout.activity_share_message;
    }

    @Override
    public void initData() {
        super.initData();
        setTvTitle(R.string.let_me_share);
        setTvRight(R.string.send);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void bindData() {


        List<String> tagList = XmlUtils.getSelectTag(this);
        tagList.remove(0);

        popupWinListView = new PopupWinListView(this, tagList, false);
        gridViewAdapter = new myGridViewAdapter(this, gv_img, true);
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

    }

    /**
     * 恢复草稿
     */
    private void resetDraft() {
        final List<String> list = new ArrayList<>();
        final String darft_content = acache.getAsString(Contants.DRAFT_CONTENT);

        if (darft_content != null) {
            dialog.setContent(getString(R.string.had_draft_would_reset_tips));
            dialog.setBtnOkText(getString(R.string.reset));
            dialog.setBtnCancelText(getString(R.string.do_not_reset));
            dialog.setDialogListener(new Dialog4Tips.Listener() {
                @Override
                public void btnOkListenter() {
                    content_et.setText(darft_content);
                    tag_tv.setText(acache.getAsString(Contants.DRAFT_TAG));
                    shareLocation_tv.setText(acache.getAsString(Contants.DRAFT_LOCATION_INFO));
                    JSONArray imgJsArray = acache.getAsJSONArray(Contants.DRAFT_IMAGES_LIST);

                    if (imgJsArray != null) {
                        for (int i = 0; i < imgJsArray.length(); i++) {
                            try {
                                list.add(imgJsArray.getString(i));
                            } catch (JSONException e) {
                                LogUtils.logE("读取jsonArray数据出错" + e.toString());
                            }
                        }

                        gridViewAdapter.setDatas(list, true);
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

                ArrayList<String> l = gridViewAdapter.getData();

                ImageHandlerUtils.starSelectImages(mActivity, l);

                break;

            case R.id.im_content_popupwin_share_lacation_i://选择位置

                intents.setAction(Contants.BORDCAST_REQUEST_LOCATIONINFO);
                sendBroadcast(intents);

                //注册广播接收者
                registerBoradcastReceiver();
                mToast(R.string.locationing);

//                LogUtils.logI("dialog 定位");

                break;

            case R.id.im_content_popupwin_share_seletag://标签选择
// TODO: 2015-11-13 选择标签的popupwindow的位置问题 主要是4.1的适配
                popupWinListView.onShow(tag_im);
                break;
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

                String province = bundle.getString(Contants.PROVINCE);
                String city = bundle.getString(Contants.CITY);
                String district = bundle.getString(Contants.DISTRICT);
                String street = bundle.getString(Contants.STREET);
                String streetNumber = bundle.getString(Contants.STREETNUMBER);

                locationInfo = city + district + street + streetNumber;

                shareLocation_tv.setText(district + street + streetNumber);

            }
        }

    };

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
                    gridViewAdapter.setDatas(mSelectPath, true);
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
        mActivity.finish();
    }

    @Override
    public void mBack() {
        goback();
    }

    private void goback() {

        if (!TextUtils.isEmpty(content_et.getText().toString())) {


            dialog.setContent(getString(R.string.need_to_save_draft));
            dialog.setBtnOkText(getString(R.string.save));
            dialog.setBtnCancelText(getString(R.string.do_not_save));
            dialog.setDialogListener(new Dialog4Tips.Listener() {
                @Override
                public void btnOkListenter() {

                    darftUtils.saveDraft(content_et.getText().toString(),
                            shareLocation_tv.getText().toString(),
                            tag_tv.getText().toString(),
                            gridViewAdapter.getData()
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

        @Override
        public void rightClivk(View v) {//发送按钮

            String content = content_et.getText().toString();
            List<String> lists = gridViewAdapter.getData();

            if (!TextUtils.isEmpty(content) || lists != null) {

                mToast(R.string.sending);
                mBackStartActivity(MainActivity.class);


                final ShareMessage_HZ shareMessage_hz = new ShareMessage_HZ();

                shareMessage_hz.setShContent(content);
                shareMessage_hz.setShTag(tagInfo);
                shareMessage_hz.setShLocation(locationInfo);
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


                            } else {

//                                LogUtils.logE("回调第一次，isfinish = " + isFinish);
//                                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.some_images_maybe_miss));
                            }
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


            } else {

                SVProgressHUD.showErrorWithStatus(mActivity, getString(R.string.share_messages_empty));

            }
        }


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
                mHandler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Contants.ONE_SECOND);

            }

            @Override
            public void onFailure(int i, String s) {
                mToast(R.string.share_message_fail);
//                LogUtils.logI("share messages faile ");
                darftUtils.saveDraft(
                        content_et.getText().toString(),
                        shareLocation_tv.getText().toString(),
                        tag_tv.getText().toString(),
                        gridViewAdapter.getData()
                );

                mHandler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Contants.ONE_SECOND);
            }
        });
    }

    private void clean() {
        content_et.setText("");
        shareLocation_tv.setText("");
        tag_tv.setText("");
        gridViewAdapter.setDatas(null, true);

    }


}
