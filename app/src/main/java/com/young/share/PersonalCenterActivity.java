package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.ActivityResult;
import com.young.annotation.InjectView;
import com.young.base.BaseAppCompatActivity;
import com.young.config.Contants;
import com.young.model.User;
import com.young.myCallback.GoToUploadImages;
import com.young.network.ResetApi;
import com.young.utils.DisplayUtils;
import com.young.utils.ImageHandlerUtils;
import com.young.utils.LogUtils;
import com.young.utils.XmlUtils;
import com.young.views.Dialog4UploadAvatar;

import java.util.List;

import cn.bmob.v3.BmobUser;
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
    @InjectView(R.id.ls_include_content_scrolling_content_list)
    private ListView select_ls;
    @InjectView(R.id.btn_include_content_scrolling_logout)
    private Button logout_btn;
    @InjectView(R.id.rtl_include_content_csrcolling)
    private RelativeLayout relativeLayout;

    private List<String> data;
    private int displayHeightDp = 0;
    private Dialog4UploadAvatar dialog4UploadAvater;


    // TODO: 2015-10-31  界面效果
    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void findviewbyid() {
        initToolbar();
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.height = displayHeightDp / 2 + 44;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        relativeLayout.setLayoutParams(params);
//        displayHeightDp - DisplayUtils.px2dp(mActivity, 180);
        ViewGroup.LayoutParams layoutParams = select_ls.getLayoutParams();
        layoutParams.height = displayHeightDp / 2;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        select_ls.setLayoutParams(layoutParams);

        avatar_im.setOnClickListener(this);
        logout_btn.setOnClickListener(this);

    }

    @Override
    public void initData() {
        data = XmlUtils.getSelectOption(mActivity);
        displayHeightDp = DisplayUtils.getScreenHeightPixels(mActivity);
    }

    @Override
    public void bindData() {
        initDialog();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_single_textview);
        arrayAdapter.addAll(data);

        select_ls.setAdapter(arrayAdapter);
        select_ls.setOnItemClickListener(new onitemClick());

        loadingAvatar();
    }

    @Override
    public void handerMessage(Message msg) {

    }

    /**
     * toolbar 初始化
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ViewGroup.LayoutParams layoutParams = appBarLayout.getLayoutParams();
        layoutParams.height = displayHeightDp / 3;
        toolBarLayout.setTitle(getString(R.string.personal_center));
        toolBarLayout.setExpandedTitleColor(Color.argb(0, 0, 0, 0));

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
        ImageHandlerUtils.loadIamge(this, mUser.getAvatar(), avatar_im);
        ImageHandlerUtils.loadIamge(this, mUser.getAvatar(), avatar_bg_im);


    }

    private class onitemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            switch (position) {

                case 0://<item>我想去</item>


                    break;
                case 1://<item>分享记录</item>


                    break;
                case 2:// <item>修改资料</item>

                    mStartActivity(EditPersonalInfoActivity.class);
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

        ImageLoader.getInstance().clearDiskCache();
        SVProgressHUD.dismiss(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String croppath = "";
        boolean finish = ActivityResult.corpResult(this, requestCode, resultCode, data, avatar_im);

        if (finish) {
            //开始上传照片
            mToast(R.string.star_upload_avatar);

            avatar_bg_im.setImageURI(ActivityResult.getImguri());

            if (mActivity.getExternalCacheDir() != null) {
                croppath = mActivity.getExternalCacheDir().getAbsolutePath() + Contants.IMAGE_PATH_AND_NAME;
            }
            LogUtils.logI(croppath);

            String[] files = {croppath};

            ResetApi.UploadFiles(mActivity, files, Contants.IMAGE_TYPE_AVATAR, new GoToUploadImages() {
                @Override
                public void Result(boolean isFinish, String[] urls) {
                    //上传文件成功
                    if (isFinish) {

                        mUser = BmobUser.getCurrentUser(mActivity, User.class);
                        String avatar_url = urls[0];

                        if (!TextUtils.isEmpty(avatar_url)) {
                            mUser.setAvatar(avatar_url);

                            mUser.update(mActivity, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    mToast(R.string.reset_avatar_success);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    mToast(R.string.reset_avatar_failure);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    LogUtils.logE("上传头像文件失败 状态信息 = " + statuscode + " 错误信息 = " + errormsg);
                }
            });
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            mBackStartActivity(MainActivity.class);
        }


        return super.dispatchKeyEvent(event);
    }
}
