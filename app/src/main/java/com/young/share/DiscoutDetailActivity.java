package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.young.share.adapter.ButtombarPageAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.fragment.WantToGoFragment;
import com.young.share.fragment.HadGoFragment;
import com.young.share.model.RemoteModel;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.MyUser;
import com.young.share.model.PictureInfo;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;
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

    private static final int GET_NEW_COUNT = 0x01;//刷新下面两个数量

    @Override
    public int getLayoutId() {
        return R.layout.activity_discount_detail;

    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.title_body);

        commModel = (RemoteModel) getIntent().getExtras().getSerializable(Contants.CLAZZ_DATA_MODEL);
        discountMessage =  DataFormateUtils.formateDataDiscount(commModel);

        threadPool.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                getData(discountMessage.getObjectId());
            }
        }));

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
                back2superclazz();
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
                initBottomBar();
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
        String url;
        boolean isLocation;
        MyUser myUser = discountMessage.getMyUserId();

        if (myUser.getAvatar() == null) {
            url = Contants.DEFAULT_AVATAR;
            isLocation = true;
        } else {
            url = myUser.getAvatar();
            isLocation = false;
        }

/*加载头像*/
        ImageHandlerUtils.loadIamge(mActivity, url, avatar, isLocation);

        nickname_tv.setText(myUser.getNickName() == null ?
                getString(R.string.user_name_defual) : myUser.getNickName());

        tag_tv.setText(discountMessage.getDtTag());
        content_tv.setText(discountMessage.getDtContent());
        createdAt.setText(discountMessage.getCreatedAt());
        initBottomBar();

/*设置图片*/
        setupImage();
        /*展示viewpager*/
        createdFragments();
        initAnima();
    }

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
     * 设置图片
     */
    private void setupImage() {

        ViewGroup.LayoutParams lp = multiImageView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels(mActivity) / 3 * 2;//设置宽度
        multiImageView.setList(DataFormateUtils.thumbnailList(this, discountMessage.getDtImgs()));
        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(mActivity,discountMessage.getDtImgs());

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

    }

    /**
     * 下方数据
     */
    private void initBottomBar() {
        wanto_tv.setText(discountMessage.getDtWantedNum() == null ?
                getString(R.string.tx_wantogo) : String.valueOf(discountMessage.getDtWantedNum().size()));
        hadgo_tv.setText(discountMessage.getDtVisitedNum() == null ?
                getString(R.string.hadgo) : String.valueOf(discountMessage.getDtVisitedNum().size()));

        LocationUtils.leftDrawableWantoGO(wanto_tv, discountMessage.getDtWantedNum(), cuser.getObjectId());
        LocationUtils.leftDrawableVisited(hadgo_tv, discountMessage.getDtVisitedNum(), cuser.getObjectId());


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
        bundle.putStringArrayList(WantToGoFragment.BUNDLE_USERID_LIST, (ArrayList<String>) discountMessage.getDtWantedNum());
        wantToGoFragment.setArguments(bundle);
/*去过*/
        hadGoFragment = new HadGoFragment();
//        hadGoFragment.initizliza(this);
        bundle.putStringArrayList(WantToGoFragment.BUNDLE_USERID_LIST, (ArrayList<String>) discountMessage.getDtWantedNum());
        hadGoFragment.setArguments(bundle);

        fragmentList.add(wantToGoFragment);
        fragmentList.add(hadGoFragment);

        pageAdapter = new ButtombarPageAdapter(fragmentList, getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);

    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public void mBack() {

        back2superclazz();
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
            LocationUtils.discountVisit(mActivity, cuser, discountMessage,
                    UserUtils.isHadCurrentUser(discountMessage.getDtVisitedNum(), cuser.getObjectId()),
                    t, new LocationUtils.Callback() {

                        @Override
                        public void onSuccesss() {
                            updateHadGo();
                        }

                        @Override
                        public void onFailure() {
                            toast(getString(R.string.txt_erro_message));
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
        hadGoFragment.setWantUserId(discountMessage.getDtVisitedNum());
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
            LocationUtils.discountWanto(mActivity, cuser, discountMessage,
                    UserUtils.isHadCurrentUser(discountMessage.getDtWantedNum(), cuser.getObjectId()),
                    t, new LocationUtils.Callback() {

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
        wantToGoFragment.setUserIdList(discountMessage.getDtWantedNum());
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
     * 返回上一级
     */
    private void back2superclazz() {

//        if (isClick) {
//            LocationUtils.sendBordCast(mActivity, Contants.REFRESH_TYPE_DISCOUNT);
//        }
//
//        mBackStartActivity(MainActivity.class);
        this.finish();
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
