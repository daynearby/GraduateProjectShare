package com.young.share;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.share.adapter.ButtombarPageAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.config.Contants;
import com.young.share.fragment.LikeFragment;
import com.young.share.fragment.WantToGoFragment;
import com.young.share.model.CommRemoteModel;
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
    @InjectView(R.id.vs_share_main_vp)
    private ViewStub viewStub;
    private CustomViewPager viewPager;

    private ButtombarPageAdapter pageAdapter;
    DiscountMessage_HZ discountMessage;
    private CommRemoteModel commModel;
    private boolean isClick = false;//是否点击过

    private static final int GET_NEW_COUNT = 0x01;//刷新下面两个数量

    @Override
    public int getLayoutId() {
        return R.layout.item_discover;

    }

    @Override
    public void initData() {
        initialiToolbar();
        setTitle(R.string.title_body);

        commModel = (CommRemoteModel) getIntent().getExtras().getSerializable(Contants.CLAZZ_DATA_MODEL);
        discountMessage = new DiscountMessage_HZ();

        threadPool.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                discountMessage.setObjectId(commModel.getObjectId());
                discountMessage.setDtVisitedNum(commModel.getVisited());
                discountMessage.setDtWantedNum(commModel.getWanted());

                getData(commModel.getObjectId());
            }
        }));

    }


    /**
     * 初始化toolbar
     */
    private void initialiToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_share_main_toolbar);
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
                initBottomBar(commModel);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.d("get new message failure. code = " + i + " message = " + s);
            }
        });
    }

    @Override
    public void findviewbyid() {
        viewStub.inflate();
        viewPager = $(R.id.vp_viewpager);

        comment_tv.setVisibility(View.GONE);
        avatar.setOnClickListener(this);
        wanto_tv.setOnClickListener(this);
        hadgo_tv.setOnClickListener(this);
    }

    @Override
    public void bindData() {
        String url;
        boolean isLocation;
        MyUser myUser = commModel.getMyUser();

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

        tag_tv.setText(commModel.getTag());
        content_tv.setText(commModel.getContent());
        createdAt.setText(commModel.getMcreatedAt());
        initBottomBar(commModel);

/*设置图片*/
        setupImage();
        /*展示viewpager*/
        createdFragments();

    }

    /**
     * 设置图片
     */
    private void setupImage() {

        ViewGroup.LayoutParams lp = multiImageView.getLayoutParams();
        lp.width = DisplayUtils.getScreenWidthPixels(mActivity) / 3 * 2;//设置宽度
        multiImageView.setList(DataFormateUtils.thumbnailList(this, commModel.getImages()));
        multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<PictureInfo> pictureInfoList = DataFormateUtils.formate2PictureInfo(mActivity, commModel.getImages());

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
     *
     * @param comm
     */
    private void initBottomBar(CommRemoteModel comm) {
        wanto_tv.setText(comm.getWanted() == null ?
                getString(R.string.tx_wantogo) : String.valueOf(comm.getWanted().size()));
        hadgo_tv.setText(comm.getVisited() == null ?
                getString(R.string.hadgo) : String.valueOf(comm.getVisited().size()));

        LocationUtils.leftDrawableWantoGO(wanto_tv, comm.getWanted(), cuser.getObjectId());
        LocationUtils.leftDrawableVisited(hadgo_tv, comm.getVisited(), cuser.getObjectId());


    }


    /**
     * 获取fragment的实例
     *
     * @return
     */
    private void createdFragments() {

        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(new LikeFragment(this, commModel.getWanted()));
        fragmentList.add(new WantToGoFragment(this, commModel.getVisited()));
//        fragmentList.add(new CommentFragment(this, commModel.getObjectId()));

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

            case R.id.id_tx_wantogo://想去

                getUser();
                if (cuser != null) {
                    LocationUtils.discountWanto(mActivity, cuser, discountMessage,
                            UserUtils.isHadCurrentUser(commModel.getWanted(), cuser.getObjectId()),
                            (TextView) v);
                    isClick = true;
                } else {
                    Dialog4Tips.loginFunction(mActivity);

                }

                break;

            case R.id.id_hadgo://去过
                getUser();
                if (cuser != null) {

                    LocationUtils.discountVisit(mActivity, cuser, discountMessage,
                            UserUtils.isHadCurrentUser(commModel.getVisited(), cuser.getObjectId()),
                            (TextView) v);
                    isClick = true;

                } else {
                    Dialog4Tips.loginFunction(mActivity);

                }


                break;
        }
    }

    /**
     * 查看用户资料片
     *
     * @param v
     */
    private void userInfo(View v) {

        PopupWinUserInfo userWindow = new PopupWinUserInfo(mActivity, commModel.getMyUser());
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

        if (isClick) {
            LocationUtils.sendBordCast(mActivity, Contants.REFRESH_TYPE_DISCOUNT);
        }

        mBackStartActivity(MainActivity.class);
        this.finish();
    }

    /**
     * pager change listener
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == 0) {
                wanto_tv.setBackgroundColor(getResources().getColor(R.color.gray_lighter));
                hadgo_tv.setBackgroundColor(Color.WHITE);

            } else {
                wanto_tv.setBackgroundColor(Color.WHITE);
                hadgo_tv.setBackgroundColor(getResources().getColor(R.color.gray_lighter));
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
