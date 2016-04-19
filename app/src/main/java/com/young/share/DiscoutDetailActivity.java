package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.adapter.ButtombarPageAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.fragment.HadGoFragment;
import com.young.share.fragment.WantToGoFragment;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.model.RemoteModel;
import com.young.share.network.NetworkReuqest;
import com.young.share.shareSocial.SocialShareByIntent;
import com.young.share.shareSocial.SocialShareManager;
import com.young.share.utils.CommonFunctionUtils;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.LogUtils;
import com.young.share.utils.NetworkUtils;
import com.young.share.utils.StringUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.CustomViewPager;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.MultiImageView.MultiImageView;
import com.young.share.views.PopupWinUserInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

/**
 * 优惠信息
 * 详细信息
 * <p/>
 * Created by Nearby Yang on 2015-12-07.
 */
public class DiscoutDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.id_im_userH)
    private ImageView avatar;//用户头像
    @InjectView(R.id.id_userName)
    private TextView nickname_tv;//昵称
    @InjectView(R.id.id_tx_tab)
    private TextView tag_tv;//标签
    @InjectView(R.id.im_tx_tab_icon)
    private ImageView tagIconIm;//标签的图标
    @InjectView(R.id.id_tx_share_content)
    private TextView content_tv;//分享的文本内容
    @InjectView(R.id.miv_share_iamges)
    private MultiImageView multiImageView;
    @InjectView(R.id.id_tx_wantogo)
    private TextView wanto_tv;//想去数量
    @InjectView(R.id.id_hadgo)
    private TextView hadgo_tv;//去过数量
    @InjectView(R.id.tv_item_share_main_created_at)
    private TextView createdAt;//创建时间
    @InjectView(R.id.id_tx_comment)
    private TextView comment_tv;
    @InjectView(R.id.tv_item_share_main_location)
    private TextView locationInfo;
    //    @InjectView(R.id.vp_discount_detail)
    private CustomViewPager viewPager;
    /*三个radioButton 显示标题*/
    @InjectView(R.id.rb_message_detail_comment)
    private RadioButton commentRbtn;
    @InjectView(R.id.rg_msg_detail_gd)
    private RadioGroup indexRadiog;
    @InjectView(R.id.rb_message_detail_had_go)
    private RadioButton hadGoRbtn;
    @InjectView(R.id.rb_message_detail_want_to)
    private RadioButton wantToRbtn;
    @InjectView(R.id.im_msg_detail_index)
    private ImageView indexIm;// 页面指示器

    private ButtombarPageAdapter pageAdapter;
    private DiscountMessage_HZ discountMessage;
    private RemoteModel commModel;
    private WantToGoFragment wantToGoFragment;
    private HadGoFragment hadGoFragment;
    private boolean isClick = false;//是否点击过

    private int tabWidth = 0;
    private OvershootInterpolator overshootInterpolator;
    private int DURATION = 500;
    private int totalOffset = 0;//动画总的偏移量
    private int lastIndex = 0;//上一个页面的position
    private String copyContent ;//要复制的文字
    private String imageUrl;//要分享或者保存的图片

    private static final int GET_NEW_COUNT = 0x01;//刷新下面两个数量
    private static final int MESSAGE_BOTTOM_BAR_DATA = 0x02;//刷新下面两个数量

    @Override
    public int getLayoutId() {
        return R.layout.activity_discount_detail;

    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.title_body);

        commModel = (RemoteModel) getIntent().getExtras().getSerializable(Contants.CLAZZ_DATA_MODEL);
        discountMessage = DataFormateUtils.formateDataDiscount(commModel);

    /*获取数据*/
        initDataByThread();
    }

    /**
     * 获取数据，通过线程
     */
    private void initDataByThread() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getData(discountMessage.getObjectId());
//            }
//        }).start();
        getData(discountMessage.getObjectId());
    }


    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_deatil_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.icon_menu_back);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });

    }

    /**
     * 获取最新的想去、去过的数量
     *
     * @param objectId
     */
    private void getData(String objectId) {

        BmobQuery<DiscountMessage_HZ> disQuery = new BmobQuery<>();
        disQuery.getObject(mActivity, objectId, new GetListener<DiscountMessage_HZ>() {
            @Override
            public void onSuccess(DiscountMessage_HZ discountMessage_hz) {
                mHandler.sendEmptyMessage(MESSAGE_BOTTOM_BAR_DATA);


            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.d("get new message failure. code = " + i + " message = " + s);
            }
        });
    }

    @Override
    public void findviewbyid() {
        viewPager = $(R.id.vp_discount_detail);
        comment_tv.setVisibility(View.GONE);
        avatar.setOnClickListener(this);
        wanto_tv.setOnClickListener(this);
        hadgo_tv.setOnClickListener(this);
    }

    @Override
    public void bindData() {
        commentRbtn.setVisibility(View.GONE);

        MyUser myUser = discountMessage.getMyUserId();

/*用户头像*/
        ImageLoader.getInstance().displayImage(
                TextUtils.isEmpty(myUser.getAvatar()) ?
                        Contants.DEFAULT_AVATAR :
                        NetworkUtils.getRealUrl(this, myUser.getAvatar()),
                avatar);

        nickname_tv.setText(myUser.getNickName() == null ?
                getString(R.string.user_name_defual) : myUser.getNickName());

        tag_tv.setText(discountMessage.getDtTag());


        if (TextUtils.isEmpty(discountMessage.getDtTag())) {
            tag_tv.setVisibility(View.GONE);
            tagIconIm.setVisibility(View.GONE);
        } else {
            tag_tv.setText(discountMessage.getDtTag());
        }
        //内容
        if (TextUtils.isEmpty(discountMessage.getDtContent())) {
            content_tv.setVisibility(View.GONE);
        } else {
            content_tv.setVisibility(View.VISIBLE);
            content_tv.setText(StringUtils.getEmotionContent(
                    this, content_tv, discountMessage.getDtContent()));
            copyContent =content_tv.getText().toString();
            registerForContextMenu(content_tv);
            content_tv.setOnCreateContextMenuListener(new OnContextMenuCreat());
        }

        createdAt.setText(discountMessage.getCreatedAt());

        initBottomBar();

/*设置图片*/
        setupImage();
        /*展示viewpager*/
        createdFragments();
        initAnima();
    }

    /**
     * 指示器
     */
    private void initAnima() {
        indexRadiog.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tabWidth = indexRadiog.getMeasuredWidth() / 2;
        ViewGroup.LayoutParams params = indexIm.getLayoutParams();
        params.width = tabWidth;

        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        indexIm.setImageMatrix(matrix);// 设置动画初始位置
        overshootInterpolator = new OvershootInterpolator();
        wantToRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
        wantToRbtn.setOnClickListener(this);
        hadGoRbtn.setOnClickListener(this);
    }

    /**
     * context menu 创建
     */
    private class OnContextMenuCreat implements View.OnCreateContextMenuListener {

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            getMenuInflater().inflate(R.menu.menu_context_content, contextMenu);
        }
    }

    /**
     * 设置图片
     */
    private void setupImage() {

        ViewGroup.LayoutParams lp = multiImageView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels(mActivity) / 3 * 2;//设置宽度
        multiImageView.setRegisterForContextMenu(true);
        multiImageView.setList(DataFormateUtils.thumbnailList(this, discountMessage.getDtImgs()));
        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(mActivity, discountMessage.getDtImgs());

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
        //长按，为了获取文件地址
        multiImageView.setOnItemLongClickListener(new MultiImageView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                imageUrl = multiImageView.getImagesList().get(position);
            }
        });
    }

    /**
     * 下方数据
     */
    private void initBottomBar() {
        wanto_tv.setText(discountMessage.getDtWantedNum() > 0 ?
                String.valueOf(discountMessage.getDtWantedNum()) : getString(R.string.tx_wantogo));
        hadgo_tv.setText(discountMessage.getDtVisitedNum() > 0 ?
                String.valueOf(discountMessage.getDtVisitedNum()) : getString(R.string.hadgo));

        CommonFunctionUtils.leftDrawableWantoGO(wanto_tv, discountMessage.getDtWanted(), cuser.getObjectId());
        CommonFunctionUtils.leftDrawableVisited(hadgo_tv, discountMessage.getDtVisited(), cuser.getObjectId());


    }


    /**
     * 获取fragment的实例
     *
     * @return
     */
    private void createdFragments() {

        List<Fragment> fragmentList = new ArrayList<>();
        Bundle bundle = new Bundle();
        /*想去*/
        wantToGoFragment = new WantToGoFragment();
//        wantToGoFragment.initizliza(this);
        bundle.putStringArrayList(WantToGoFragment.BUNDLE_USERID_LIST, (ArrayList<String>) discountMessage.getDtWanted());
        wantToGoFragment.setArguments(bundle);
/*去过*/
        hadGoFragment = new HadGoFragment();
//        hadGoFragment.initizliza(this);
        bundle.putStringArrayList(WantToGoFragment.BUNDLE_USERID_LIST, (ArrayList<String>) discountMessage.getDtWanted());
        hadGoFragment.setArguments(bundle);

        fragmentList.add(wantToGoFragment);
        fragmentList.add(hadGoFragment);

        pageAdapter = new ButtombarPageAdapter(fragmentList, getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

    }

    @Override
    public void handerMessage(Message msg) {

        switch (msg.what) {
            case MESSAGE_BOTTOM_BAR_DATA://初始化下方的数据
                initBottomBar();
                break;

        }

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_content_copy://复制文本
                StringUtils.CopyText(this, copyContent);
                break;

            case R.id.menu_content_share://分享文本
                SocialShareManager.shareText(this,copyContent);
                break;
            case R.id.menu_image_save://保存图片
                NetworkReuqest.call2(this, imageUrl);
                break;

            case R.id.menu_image_share_all://分享全部图片
//下载图片
                SocialShareByIntent.downloadImagesAndShare(mActivity, discountMessage.getDtImgs());
//                SocialShareManager.shareImage(context, discAdapter.getImageUrl());

                break;
            case R.id.menu_image_share_singal://分享打仗图片
                SocialShareByIntent.downloadImageAndShare(mActivity, imageUrl);
                break;

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.id_im_userH://用户头像

                userInfo(v);
                break;

            case R.id.id_userName:

                userInfo(v);

                break;
            case R.id.rb_message_detail_want_to:/*想去*/
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.rb_message_detail_had_go:/*去过了*/
                viewPager.setCurrentItem(1, true);
                break;

            case R.id.id_tx_wantogo://想去
                viewPager.setCurrentItem(0, true);
                wantTo((TextView) v);
                break;

            case R.id.id_hadgo://去过
                viewPager.setCurrentItem(1, true);
                hadGo((TextView) v);


                break;
        }
    }

    /**
     * 去过操作逻辑
     */
    private void hadGo(TextView t) {
        getUser();
        if (cuser != null) {
            CommonFunctionUtils.discountVisit(mActivity, cuser, discountMessage,
                    UserUtils.isHadCurrentUser(discountMessage.getDtVisited(), cuser.getObjectId()),
                    t, new CommonFunctionUtils.Callback() {

                        @Override
                        public void onSuccesss() {
                            updateHadGo();
                        }

                        @Override
                        public void onFailure() {
                            toast(getString(R.string.txt_serve_erro));
                        }
                    });
            isClick = true;

        } else {
            Dialog4Tips.loginFunction(mActivity);

        }

    }

    /**
     * 更新想去的用户列表
     */
    private void updateHadGo() {


        hadgo_tv.setText(discountMessage.getDtVisitedNum() > 0 ?
                String.valueOf(discountMessage.getDtVisitedNum()) : getString(R.string.hadgo));
        hadGoFragment.setWantUserId(discountMessage.getDtVisited());
        hadGoFragment.initData();
    }

    /**
     * 想去
     *
     * @param t
     */
    private void wantTo(TextView t) {
        getUser();
        if (cuser != null) {
            CommonFunctionUtils.discountWanto(mActivity, cuser, discountMessage,
                    UserUtils.isHadCurrentUser(discountMessage.getDtWanted(), cuser.getObjectId()),
                    t, new CommonFunctionUtils.Callback() {

                        @Override
                        public void onSuccesss() {
                            //更新到显示列表中
                            updateWantTo();
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
            isClick = true;
        } else {
            Dialog4Tips.loginFunction(mActivity);

        }

    }

    /**
     * 更新喜欢的用户
     */
    private void updateWantTo() {
        wanto_tv.setText(discountMessage.getDtWantedNum()  > 0 ?
                String.valueOf(discountMessage.getDtWantedNum()) : getString(R.string.tx_wantogo));

        wantToGoFragment.setUserIdList(discountMessage.getDtWanted());
        wantToGoFragment.initData();

    }


    /**
     * 查看用户资料片
     *
     * @param v
     */
    private void userInfo(View v) {

        PopupWinUserInfo userWindow = new PopupWinUserInfo(mActivity, discountMessage.getMyUserId());
        userWindow.onShow(v);

    }

    /**
     * 获取当前用户
     */
    public void getUser() {
        if (cuser == null) {
            cuser = BmobUser.getCurrentUser(mActivity, MyUser.class);
        }
    }


    /**
     * pager change listener
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == 0) {
                wantToRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
                hadGoRbtn.setTextColor(Color.BLACK);

            } else {
                wantToRbtn.setTextColor(Color.BLACK);
                hadGoRbtn.setTextColor(getResources().getColor(R.color.theme_puple));
            }
        }

        @Override
        public void onPageSelected(int position) {
            changeTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 动画效果
     *
     * @param index
     */
    private void changeTab(int index) {
//        int position = tabWidth*index - tabWidth / 3 - bmpW / 3 - offset;
        totalOffset = totalOffset + (index - lastIndex) * tabWidth;
        lastIndex = index;
//        LogUtils.e(" translationX = "+totalOffset);
        ViewPropertyAnimator.animate(indexIm).translationX(totalOffset)
                .setInterpolator(overshootInterpolator).setDuration(DURATION);
    }
}
