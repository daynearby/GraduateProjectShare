package com.young.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.young.share.R;
import com.young.utils.DisplayUtils;


/**
 * 上传照片的对话框
 * <p/>
 * Created by feng on 2015/8/26.
 */
public class Dialog4UploadAvatar extends Dialog implements View.OnClickListener {

    private Activity activity;
    private boolean isBig;
    private TextView btnLocalPhoto, btnCamera;
    private View view;
    private View.OnClickListener listener;
    /**
     * 选择照片的对话框
     *
     * @param activity 上下文对象
     * @param StrId    标题文字 资源id
     * @param isBig    裁剪图片的尺寸,true == 大一点的尺寸 800*800,false == 默认尺寸 250*250
     */
    public Dialog4UploadAvatar(Activity activity, int StrId, boolean isBig) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = LayoutInflater.from(activity).inflate(R.layout.content_dialog4upload_avatar, null);
        setContentView(view);
        this.activity = activity;
        this.isBig = isBig;
        findViewById();
        initDialogSize();

        ((TextView) view.findViewById(R.id.tv_content_dialog4upload_title)).setText(StrId);

    }

    private void findViewById() {
        btnLocalPhoto = (TextView) view.findViewById(R.id.dialog_frag_real_name_btn_local_photo);
        btnCamera = (TextView) view.findViewById(R.id.dialog_frag_real_name_btn_camera);
    }
    /**
     * 初始化宽度
     */
    private void initDialogSize() {
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.bg_dialog4upload_linearLayout);
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.width = DisplayUtils.getScreenWidthPixels(activity);
        linearLayout.setLayoutParams(params);
    }


    public void setClickListener(View.OnClickListener listener) {
        this.listener = listener;

        btnLocalPhoto.setOnClickListener(listener);
        btnCamera.setOnClickListener(listener);

    }

    /**
     * 无参数 默认监听器
     */
    public void setClickListener() {
        btnLocalPhoto.setOnClickListener(this);

        btnCamera.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        //相册
        if (v.getId() == R.id.dialog_frag_real_name_btn_camera) {


            Crop. pickImage(activity, Crop.PIC_FROM_CAMERA, isBig);

            this.dismiss();
        } else {


            Crop.pickImage(activity, Crop.PIC_FROM_LOCAL, isBig);
//
            this.dismiss();

        }


    }
}