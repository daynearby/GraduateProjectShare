package com.young.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.young.adapter.EmotionGvAdapter;
import com.young.adapter.EmotionPagerAdapter;
import com.young.adapter.myGridViewAdapter;
import com.young.annotation.InjectView;
import com.young.base.BaseAppCompatActivity;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.ShareMessage_HZ;
import com.young.myCallback.GoToUploadImages;
import com.young.network.ResetApi;
import com.young.utils.DisplayUtils;
import com.young.utils.EmotionUtils;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.StringUtils;
import com.young.utils.XmlUtils;
import com.young.utils.cache.ACache;
import com.young.utils.cache.DarftUtils;
import com.young.views.Dialog4Tips;
import com.young.views.PopupWinListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 发送分享信息
 * <p/>
 * Created by Nearby Yang on 2015-10-23.
 */
public class ShareMessageActivity extends ItemActBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

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
    private EmotionPagerAdapter emotionPagerGvAdapter;
    private InputMethodManager imm;

    private List<String> tagList;//标签
    private PopupWinListView popupWinListView;
    private boolean isResgiter = false;
    private String locationInfo;//定位信息
    private String tagInfo = "未分类";//标签信息
    private ACache acache;//缓存
    private Dialog4Tips dialog;//保存草稿的提示框
    private DarftUtils darftUtils;//草稿
    private static final int FINISH_ACTIVITY = 0;

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


        tagList = XmlUtils.getSelectTag(this);
        tagList.remove(0);

        popupWinListView = new PopupWinListView(this, tagList);
        gridViewAdapter = new myGridViewAdapter(this, gv_img, true);
        gridViewAdapter.setDatas(null);

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
        initEmotion();

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

                        gridViewAdapter.setDatas(list);
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

                popupWinListView.onShow(v);
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
                    gridViewAdapter.setDatas(mSelectPath);
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

    /**
     * 点击事件监听
     */
    private class itemClick implements BarItemOnClick {

        @Override
        public void leftClick(View v) {

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
            dialog.dismiss();
            mBackStartActivity(MainActivity.class);
            mActivity.finish();
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
                shareMessage_hz.setShVisitedNum(0);
                shareMessage_hz.setShWantedNum(0);

                if (lists != null && !lists.isEmpty()) {//有上传图片的

                    String[] files = new String[lists.size()];
                    for (int i = 0; i < lists.size(); i++) {
                        files[i] = lists.get(i);
                    }
                    ResetApi.UploadFiles(mActivity, files, Contants.IMAGE_TYPE_SHARE, new GoToUploadImages() {
                        @Override
                        public void Result(boolean isFinish, String[] urls) {

                            if (isFinish) {
                                List<String> list = new ArrayList<>();

                                for (int i = 0; i < urls.length; i++) {
                                    list.add(urls[i]);
                                }

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

        clean();
        shareMessage_hz.save(mActivity, new SaveListener() {
            @Override
            public void onSuccess() {
//                SVProgressHUD.showSuccessWithStatus(mActivity, getString(R.string.share_messages_success));
                mToast(R.string.share_messages_success);
//                LogUtils.logI("share messages success ");
                mHandler.sendEmptyMessageDelayed(FINISH_ACTIVITY, Contants.ONE_SECOND);

            }

            @Override
            public void onFailure(int i, String s) {
                mToast(R.string.share_message_fail);
//                SVProgressHUD.showInfoWithStatus(mActivity, getString(R.string.share_message_fail));
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
        gridViewAdapter.setDatas(null);

    }

    /**
     * 初始化表情面板内容
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int gvWidth = DisplayUtils.getScreenWidthPixels(mActivity);
        // 表情边距
        int spacing = DisplayUtils.dp2px(mActivity, 8);
        // GridView中item的宽度
        int itemWidth = (gvWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        // 遍历所有的表情名字
        for (String emojiName : EmotionUtils.emojiMap.keySet()) {
            emotionNames.add(emojiName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<String>();
            }
        }

        // 检查最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

//        ViewGroup.LayoutParams layoutParams = vp_emotion_dashboard.getLayoutParams();
        // 将多个GridView添加显示到ViewPager中
        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
        vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(mActivity);
        gv.setBackgroundColor(Color.rgb(233, 233, 233));
        gv.setSelector(android.R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return gv;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();

        if (itemAdapter instanceof EmotionGvAdapter) {
            // 点击的是表情
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

            if (position == emotionGvAdapter.getCount() - 1) {
                // 如果点击了最后一个回退按钮,则调用删除键事件
                content_et.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                String emotionName = emotionGvAdapter.getItem(position);

                // 获取当前光标位置,在指定位置上添加表情图片文本
                int curPosition = content_et.getSelectionStart();
                StringBuilder sb = new StringBuilder(content_et.getText().toString());
                sb.insert(curPosition, emotionName);

                // 特殊文字处理,将表情等转换一下
                content_et.setText(StringUtils.getEmotionContent(
                        mActivity, content_et, sb.toString()));

                // 将光标设置到新增完表情的右侧
                content_et.setSelection(curPosition + emotionName.length());
            }

        }
    }


}
