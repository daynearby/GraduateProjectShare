package com.young.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.young.adapter.myGridViewAdapter;
import com.young.base.BaseAppCompatActivity;
import com.young.config.Contants;
import com.young.share.MainActivity;
import com.young.share.R;
import com.young.utils.CommonUtils;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.XmlUtils;

import java.util.List;

/**
 * Created by Nearby Yang on 2015-10-23.
 */
public class DialogShareView extends Dialog implements View.OnKeyListener, View.OnClickListener {

    private View view;
    private Activity mainActivity;

    private LinearLayout linearLayout;
    private TextView title_tv;
    private EditText content_et;
    private TextView shareLocation_tv;
    private TextView send_tv;
    private TextView cancel_tv;

    private Spinner tagSpinner;
    private GridView gv_img;
    private myGridViewAdapter gridViewAdapter;

    private List<String> path;//图片路径
    private int imagesNumber;//图片的可选择数量，最多为6张
    private List<String> tagList;
    private boolean isRegister = false;//广播接收者是否被注册
    private IntentFilter myIntentFilter = new IntentFilter();

    private String locationInfo;//定位信息
    private String tagInfo;//标签信息

    // TODO: 2015-10-23 文本输入框，清空的选项，图片选择，位置，标签。取消的时候询问是否保存草稿

    public DialogShareView(Activity mainActivity) {
        super(mainActivity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mainActivity = mainActivity;

        view = LayoutInflater.from(mainActivity).inflate(R.layout.content_dialog_share, null);
        setContentView(view);


        findView();

        bingData();
        initDialogSize();

    }

    private void bingData() {

        tagList = XmlUtils.getSelectTag(mainActivity);
        tagList.remove(0);
//        title_tv.setFocusable(true);

        gridViewAdapter = new myGridViewAdapter(mainActivity, true);
        gridViewAdapter.setDatas(null);
        gv_img.setAdapter(gridViewAdapter);
        gv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = gridViewAdapter.getItem(position);
                if (item != null) {
                    if (item.equals(Contants.LAST_ADD_IMG)) {
// TODO: 2015-10-25 6张照片后隐藏添加照片的图标
                        registerBoradcastReceiverSelectorImages();

                        if (path != null) {
                            imagesNumber = Contants.IMAGENUMBER - path.size() + 1;
                        } else {
                            imagesNumber = Contants.IMAGENUMBER;
                        }

                        LogUtils.logE("number = " + imagesNumber);

                        ImageHandlerUtils.starSelectImages(mainActivity, imagesNumber);
                    }
                }
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity, R.layout.custom_spinner_item);
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

    }

    private void findView() {
        title_tv = (TextView) view.findViewById(R.id.tv_contnent_popupwin_title);
        content_et = (EditText) view.findViewById(R.id.et_contnent_popupwin_content);
        shareLocation_tv = (TextView) view.findViewById(R.id.tv_content_popupwin_share_lacation_i);
        send_tv = (TextView) view.findViewById(R.id.tv_contnent_popupwin_send);
        cancel_tv = (TextView) view.findViewById(R.id.tv_contnent_popupwin_cancel);
        tagSpinner = (Spinner) view.findViewById(R.id.sp_content_popupwin_share_seletag);
        gv_img = (GridView) view.findViewById(R.id.gv_content_popupwin_share_addimg);
        linearLayout = (LinearLayout) view.findViewById(R.id.layout_content_popupwin_share);

        send_tv.setOnClickListener(this);
        cancel_tv.setOnClickListener(this);
        shareLocation_tv.setOnClickListener(this);

    }

    /**
     * 初始化宽度
     */
    private void initDialogSize() {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.width = CommonUtils.getWidth(mainActivity) * 9 / 10;
        linearLayout.setLayoutParams(params);
    }


    /**
     * 设置标题
     *
     * @param strId 标题文字资源id
     */
    public void setTitle_tv(int strId) {
        title_tv.setText(strId);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return true;
//    }

    //注册广播接收者。地理信息
    public void registerBoradcastReceiver() {

        myIntentFilter.addAction(Contants.BORDCAST_LOCATIONINFO);
        //注册广播
        mainActivity.registerReceiver(broadcastReceiver, myIntentFilter);

    }

    //注册广播接收者。图片
    public void registerBoradcastReceiverSelectorImages() {

        myIntentFilter.addAction(Contants.BORDCAST_SELECTIMAGES);
        //注册广播
        mainActivity.registerReceiver(broadcastReceiver, myIntentFilter);

    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.dismiss();
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_content_popupwin_share_lacation_i:
                //注册广播接收者
                registerBoradcastReceiver();

                ((MainActivity) mainActivity).startLocation();

                isRegister = true;

                break;
            case R.id.tv_contnent_popupwin_send:
                LogUtils.ta("发送");


                break;
            case R.id.tv_contnent_popupwin_cancel:
                onDismiss();
                break;
        }
    }

    /**
     * 消息的广播接收者
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.logE("收到广播 Action = " + intent.getAction());
            if (intent.getAction().equals(Contants.BORDCAST_LOCATIONINFO)) {

                Bundle bundle = intent.getBundleExtra(BaseAppCompatActivity.BUNDLE_BROADCAST);

                String province = bundle.getString(Contants.PROVINCE);
                String city = bundle.getString(Contants.CITY);
                String district = bundle.getString(Contants.DISTRICT);
                String street = bundle.getString(Contants.STREET);
                String streetNumber = bundle.getString(Contants.STREETNUMBER);

                locationInfo = city + district + street + streetNumber;

                shareLocation_tv.setText(district + street + streetNumber);
                shareLocation_tv.setTextColor(mainActivity.getResources().getColor(R.color.theme_puple));

            } else if (intent.getAction().equals(Contants.BORDCAST_SELECTIMAGES)) {

                path = intent.getStringArrayListExtra(Contants.BORDCAST_IMAGEPATH_LIST);
                gridViewAdapter.setDatas(path);

                LogUtils.logI("size = " + path.size() + " toString = " + path.toString());
            }
        }

    };

    public void onDismiss() {

        if (isRegister) {
            mainActivity.unregisterReceiver(broadcastReceiver);
            isRegister = false;
        }
        // TODO: 2015-10-25 保存草稿

        dismiss();
    }
}
