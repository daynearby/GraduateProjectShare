package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.bmob.BmobPro;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.ActivityResult;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.model.User;
import com.young.share.myInterface.GoToUploadImages;
import com.young.share.network.BmobApi;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.XmlUtils;
import com.young.share.views.Dialog4UploadAvatar;

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
    @InjectView(R.id.tv_user_signture)
    private TextView signture_tv;
    @InjectView(R.id.ls_include_content_scrolling_content_list)
    private ListView select_ls;
    @InjectView(R.id.btn_include_content_scrolling_logout)
    private Button logout_btn;
    @InjectView(R.id.rtl_include_content_csrcolling)
    private RelativeLayout relativeLayout;

    private List<String> data;
    private int displayHeightDp = 0;
    private Dialog4UploadAvatar dialog4UploadAvater;


    @Override
    public int getLayoutId() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void findviewbyid() {
        initToolbar();
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
        params.height = displayHeightDp / 2;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        relativeLayout.setLayoutParams(params);
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

        nickname_tv.setText(TextUtils.isEmpty(mUser.getNickName()) ? getString(R.string.user_name_defual) : mUser.getNickName());
        signture_tv.setText(TextUtils.isEmpty(mUser.getSignture()) ? getString(R.string.user_info_hint_enjoy_life) : mUser.getSignture());
        loadingAvatar();
    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public void mBack() {
        mBackStartActivity(MainActivity.class);
    }

    /**
     * toolbar 初始化
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        ViewGroup.LayoutParams lp = appBarLayout.getLayoutParams();
        lp.height = displayHeightDp / 3;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getString(R.string.personal_center));
        toolBarLayout.setExpandedTitleColor(Color.argb(0, 0, 0, 0));
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

        if (!TextUtils.isEmpty(mUser.getAvatar())) {
            url = mUser.getAvatar();
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
                    Bundle bundle = new Bundle();
                    bundle.putInt(Contants.RECORD_TYPE, Contants.RECORD_TYPE_COLLECT);
                    mStartActivity(RecordCommActivity.class, bundle);
                    break;
                case 1://<item>分享记录</item>
                    Bundle bundle_share = new Bundle();
                    bundle_share.putInt(Contants.RECORD_TYPE, Contants.RECORD_TYPE_SHARE);
                    mStartActivity(RecordCommActivity.class, bundle_share);

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
        BmobPro.getInstance(mActivity).clearCache();
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

            BmobApi.UploadFiles(mActivity, files, Contants.IMAGE_TYPE_AVATAR, new GoToUploadImages() {
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


}
