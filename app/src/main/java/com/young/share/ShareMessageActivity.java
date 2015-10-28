package com.young.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.young.adapter.myGridViewAdapter;
import com.young.annotation.InjectView;
import com.young.base.BaseAppCompatActivity;
import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.XmlUtils;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by Nearby Yang on 2015-10-23.
 */
public class ShareMessageActivity extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.et_contnent_popupwin_content)
    private EditText content_et;
    @InjectView(R.id.tv_content_popupwin_share_lacation_i)
    private TextView shareLocation_tv;
    @InjectView(R.id.im_content_dialog_share_emotion)
    private ImageView emotion_im;
    @InjectView(R.id.sp_content_popupwin_share_seletag)
    private Spinner tagSpinner;
    @InjectView(R.id.gv_content_popupwin_share_addimg)
    private GridView gv_img;

    private myGridViewAdapter gridViewAdapter;


    private List<String> tagList;

    private String locationInfo;//定位信息
    private String tagInfo;//标签信息

    // TODO: 2015-10-23 文本输入框，清空的选项，表情，位置，标签。取消的时候询问是否保存草稿

    @Override
    public int getLayoutId() {
        return R.layout.activity_share_message;
    }

    @Override
    public void initData() {
        super.initData();
        setTvTitle(R.string.let_me_share);
        setTvRight(R.string.send);
    }

    @Override
    public void bindData() {

        tagList = XmlUtils.getSelectTag(this);
        tagList.remove(0);

        gridViewAdapter = new myGridViewAdapter(this, gv_img, true);
        gridViewAdapter.setDatas(null);

        gv_img.setAdapter(gridViewAdapter);
        gv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = gridViewAdapter.getItem(position);
                if (item != null) {
                    if (item.equals(Contants.LAST_ADD_IMG)) {

                        ImageHandlerUtils.starSelectImages(mActivity, gridViewAdapter.getData());

                    }
                }
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item);
        arrayAdapter.addAll(tagList);
        arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);

        tagSpinner.setPrompt(" 请选择标签 ： ");
        tagSpinner.setAdapter(arrayAdapter);

        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tagInfo = tagList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tagSpinner.setPrompt(" 请选择标签 ： ");
            }
        });

        //监听
        setItemListener(new itemClick());
    }

    @Override
    public void findviewbyid() {

        shareLocation_tv.setOnClickListener(this);
        emotion_im.setOnClickListener(this);

    }



    //注册广播接收者。地理信息
    public void registerBoradcastReceiver() {

        myIntentFilter.addAction(Contants.BORDCAST_LOCATIONINFO);
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_content_popupwin_share_lacation_i:

                intents = new Intent();
                intents.setAction(Contants.BORDCAST_REQUEST_LOCATIONINFO);
                sendBroadcast(intents);

                //注册广播接收者
                registerBoradcastReceiver();

// TODO: 2015-10-28 广播定位
                LogUtils.logI("dialog 定位");

                break;

            case R.id.im_content_dialog_share_emotion://添加表情

                break;
        }
    }

    /**
     * 消息的广播接收者
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
                shareLocation_tv.setTextColor(getResources().getColor(R.color.theme_puple));

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
        if (broadcastReceiver.isOrderedBroadcast()) {
            unregisterReceiver(broadcastReceiver);
        }

    }

    @Override
    public void handerMessage(Message msg) {

    }

    /**
     * 点击事件监听
     */
    private class itemClick implements BarItemOnClick {

        @Override
        public void leftClick(View v) {
            LogUtils.logE("左边点击");
            // TODO: 2015-10-25 保存草稿
            mActivity.finish();

        }

        @Override
        public void rightClivk(View v) {

            LogUtils.logE("右边点击");
        }
    }
}
