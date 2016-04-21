package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bmob.BmobPro;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.ActivityResult;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.interfaces.GoToUploadImages;
import com.young.share.model.MyUser;
import com.young.share.network.BmobApi;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.XmlUtils;
import com.young.share.views.Dialog4UploadAvatar;
import com.young.share.views.WrapHightListview;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 个人中心
 * <p/>
 * Created by Nearby Yang on 2015-10-22.
 */
public class PersonalCenterActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.id_im_personal_avatar)
    private ImageView avatar_im;
    @InjectView(R.id.im_activity_personal_head_bg)
    private ImageView avatar_bg_im;
    @InjectView(R.id.tv_user_nickname)
    private TextView nickname_tv;
    @InjectView(R.id.tv_user_signture)
    private TextView signture_tv;
    @InjectView(R.id.ls_include_content_scrolling_content_list)
    private WrapHightListview select_ls;
    @InjectView(R.id.btn_include_content_scrolling_logout)
    private Button logout_btn;
    @InjectView(R.id.rl_head_avatar_layout)
    private RelativeLayout headerLayout;

    private List<String> data;
    private int displayHeight = 0;
    private Dialog4UploadAvatar dialog4UploadAvater;


    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void initData() {
        initToolbar();
        setTitle(R.string.personal_center);
        data = XmlUtils.getSelectOption(mActivity);
        displayHeight = DisplayUtils.getScreenHeightPixels(mActivity);
    }

    @Override
    public void findviewbyid() {

        avatar_im.setOnClickListener(this);
        logout_btn.setOnClickListener(this);

    }



    @Override
    public void bindData() {

        initDialog();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_single_textview);
        arrayAdapter.addAll(data);
        ViewGroup.LayoutParams params = headerLayout.getLayoutParams();
        params.height = displayHeight / 3 + DisplayUtils.dip2px(this, 44);
        select_ls.setAdapter(arrayAdapter);
        select_ls.setOnItemClickListener(new onitemClick());
//显示用户信息
        initUserInfo();
        loadingAvatar();
    }

    /**
     * 初始化用户资料
     */
    private void initUserInfo() {
        nickname_tv.setText(TextUtils.isEmpty(cuser.getNickName()) ? getString(R.string.user_name_defual) : cuser.getNickName());
        signture_tv.setText(TextUtils.isEmpty(cuser.getSignture()) ? getString(R.string.user_info_hint_enjoy_life) : cuser.getSignture());

    }

    @Override
    public void handerMessage(Message msg) {

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {

            mActivity.finish();
            return true;
        }


        return super.dispatchKeyEvent(event);
    }
    /**
     * toolbar 初始化
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.id_im_personal_avatar://头像
//                修改当前头像，上传文件
                dialog4UploadAvater.show();

                break;
            case R.id.btn_include_content_scrolling_logout://注销登录
//                退出当前账号，清除保存的个人信息
                BmobUser.logOut(this);   //清除缓存用户对象
                mStartActivity(WelcomeActivity.class);
                finish();
                break;

        }

    }


    /**
     * 上传头像对话框
     * <p/>
     * 初始化对话框
     */
    private void initDialog() {
        dialog4UploadAvater = new Dialog4UploadAvatar(this, R.string.upload_avater, false);
        dialog4UploadAvater.setClickListener();

    }

    /**
     * 加载头像
     */
    private void loadingAvatar() {
        //默认头像
        String url;
        boolean isLocation;

        if (!TextUtils.isEmpty(cuser.getAvatar())) {
            url = cuser.getAvatar();
            isLocation = false;
        } else {
            url = Contants.DEFAULT_AVATAR;
            isLocation = true;
        }

        ImageHandlerUtils.loadIamge(this, url, avatar_im, isLocation);
        ImageHandlerUtils.loadIamge(this, url, avatar_bg_im, isLocation);


    }

    private class onitemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            switch (position) {
//// TODO: 2015-12-07 滑动删除收藏记录

                case 0://<item>我想去</item>

                    mStartActivity(WantToGoActivity.class);
                    break;
                case 1://<item>分享记录</item>

                    mStartActivity(UserRecordActivity.class);

                    break;
                case 2:// <item>修改资料</item>
                    intents = new Intent(mActivity, EditPersonalInfoActivity.class);
                    startActivityForResult(intents, Contants.REQUSET_EDIT_PERSONAL_INFO);
                    overridePendingTransition(R.anim.activity_slid_right_in, R.anim.activity_slid_left_out);

                    break;
                case 3:// <item>修改密码</item>
                    mStartActivity(ResetPwdActivity.class);

                    break;
                case 4://<item>清除缓存</item>

                    clearCache();
                    break;
                case 5://<item>关于</item>
                    mStartActivity(AboutActivity.class);
                    break;
            }
        }
    }


    /**
     * 清除缓存
     */
    private void clearCache() {
        SVProgressHUD.showWithStatus(this, getString(R.string.cleaning_cache));
        BmobPro.getInstance(mActivity).clearCache();
        ImageLoader.getInstance().clearDiskCache();
        app.getCacheInstance().clear();

        SVProgressHUD.dismiss(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

        String croppath = "";
        boolean finish = ActivityResult.corpResult(this, requestCode, resultCode, data, avatar_im);

        if (finish) {
            //开始上传照片
            toast(R.string.star_upload_avatar);

            avatar_bg_im.setImageURI(ActivityResult.getImguri());

            if (mActivity.getExternalCacheDir() != null) {
                croppath = mActivity.getExternalCacheDir().getAbsolutePath() + Contants.IMAGE_PATH_AND_NAME;
            }
            LogUtils.i(croppath);

            String[] files = {croppath};

            BmobApi.UploadFiles(mActivity, files, Contants.FILE_TYPE_MULTI, new GoToUploadImages() {
                @Override
                public void Result( String[] urls,BmobFile[] bmobfiles) {
                    //上传文件成功
                    MyUser newuser = new MyUser();
                    cuser = app.getCUser();
                    final String avatar_url = urls[0];

                        if (!TextUtils.isEmpty(avatar_url)) {
                            newuser.setAvatar(avatar_url);

                            newuser.update(mActivity, cuser.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {

                                    cuser.setAvatar(avatar_url);
                                    toast(R.string.reset_avatar_success);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    LogUtils.e("更新用户信息失败 code = " + i + " 错误信息 = " + s);
                                    toast(R.string.reset_avatar_failure);
                                }
                            });
                        }

                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    LogUtils.e("上传头像文件失败 状态信息 = " + statuscode + " 错误信息 = " + errormsg);
                }
            });
        }
        } else if (resultCode == Contants.RESULT_EDIT_PERSONAL_INFO) {
            if (data.getBooleanExtra(Contants.INTENT_KEY_REFRESH, false)) {
                //更新当前用户信息，更新用户昵称、签名
                cuser = app.getCUser();
                initUserInfo();
            }

        }
    }


}
