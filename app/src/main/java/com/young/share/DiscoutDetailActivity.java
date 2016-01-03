package com.young.share;

import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.share.adapter.myGridViewAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.ItemActBarActivity;
import com.young.share.config.Contants;
import com.young.share.model.CommRemoteModel;
import com.young.share.model.DiscountMessage_HZ;
import com.young.share.model.User;
import com.young.share.thread.MyRunnable;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.utils.LocationUtils;
import com.young.share.utils.LogUtils;
import com.young.share.utils.UserUtils;
import com.young.share.views.Dialog4Tips;
import com.young.share.views.PopupWinUserInfo;
import com.young.share.views.WrapHightGridview;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;

/**
 * 优惠信息
 * 详细信息
 * <p/>
 * Created by Nearby Yang on 2015-12-07.
 */
public class DiscoutDetailActivity extends ItemActBarActivity implements View.OnClickListener {

    @InjectView(R.id.id_im_userH)
    private ImageView avatar;//用户头像
    @InjectView(R.id.id_userName)
    private TextView nickname_tv;//昵称
    @InjectView(R.id.id_tx_tab)
    private TextView tag_tv;//标签
    @InjectView(R.id.id_tx_share_content)
    private TextView content_tv;//分享的文本内容
    @InjectView(R.id.id_gv_shareimg)
    private WrapHightGridview myGridview;
    @InjectView(R.id.id_tx_wantogo)
    private TextView wanto_tv;//想去数量
    @InjectView(R.id.id_hadgo)
    private TextView hadgo_tv;//去过数量
    @InjectView(R.id.tv_item_share_main_created_at)
    private TextView createdAt;//创建时间
    @InjectView(R.id.id_tx_comment)
    private TextView comment_tv;


    DiscountMessage_HZ discountMessage ;
    private CommRemoteModel commModel;
    private boolean isClick = false;//是否点击过

    private static final int GET_NEW_COUNT = 11;//刷新下面两个数量

    @Override
    public int getLayoutId() {
        return R.layout.item_share_main;

    }

    @Override
    public void initData() {
        super.initData();
        commModel = (CommRemoteModel) getIntent().getExtras().getSerializable(Contants.CLAZZ_DATA_MODEL);
        discountMessage=new DiscountMessage_HZ();

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                discountMessage.setObjectId(commModel.getObjectId());
                discountMessage.setDtVisitedNum(commModel.getVisited());
                discountMessage.setDtWantedNum(commModel.getWanted());

                getData(commModel.getObjectId());
            }
        }));

        setBarItemVisible(true, false);
        setTvTitle(R.string.title_body);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                back2superclazz();
            }

            @Override
            public void rightClivk(View v) {

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
                LogUtils.logD("get new message failure. code = " + i + " message = " + s);
            }
        });
    }

    @Override
    public void findviewbyid() {

        comment_tv.setVisibility(View.GONE);
        avatar.setOnClickListener(this);
        wanto_tv.setOnClickListener(this);
        hadgo_tv.setOnClickListener(this);
    }

    @Override
    public void bindData() {
        String url;
        boolean isLocation;
        User user = commModel.getUser();

        if (user.getAvatar() == null) {
            url = Contants.DEFAULT_AVATAR;
            isLocation = true;
        } else {
            url = user.getAvatar();
            isLocation = false;
        }

        ImageHandlerUtils.loadIamge(mActivity, url, avatar, isLocation);

        nickname_tv.setText(user.getNickName() == null ?
                getString(R.string.user_name_defual) : user.getNickName());

        tag_tv.setText(commModel.getTag());
        content_tv.setText(commModel.getContent());
        createdAt.setText(commModel.getMcreatedAt());
        initBottomBar(commModel);

        myGridViewAdapter gridViewAdapter = new myGridViewAdapter(mActivity, myGridview, false);
        gridViewAdapter.setDatas(commModel.getImages(), false);

        myGridview.setAdapter(gridViewAdapter);
        myGridview.setOnItemClickListener(new LocationUtils.itemClick(mActivity, commModel.getImages()));

    }

    /**
     * 下方数据
     *
     * @param comm
     */
    private void initBottomBar(CommRemoteModel comm ) {
        wanto_tv.setText(comm.getWanted() == null ?
                getString(R.string.tx_wantogo) : String.valueOf(comm.getWanted().size()));
        hadgo_tv.setText(comm.getVisited() == null ?
                getString(R.string.hadgo) : String.valueOf(comm.getVisited().size()));

        LocationUtils.leftDrawableWantoGO(wanto_tv, comm.getWanted(), mUser.getObjectId());
        LocationUtils.leftDrawableVisited(hadgo_tv, comm.getVisited(), mUser.getObjectId());

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
                if (mUser != null) {
                    LocationUtils.discountWanto(mActivity, mUser, discountMessage,
                            UserUtils.isHadCurrentUser(commModel.getWanted(), mUser.getObjectId()),
                            (TextView) v);
                    isClick = true;
                } else {
                    Dialog4Tips.loginFunction(mActivity);

                }

                break;

            case R.id.id_hadgo://去过
                getUser();
                if (mUser != null) {

                    LocationUtils.discountVisit(mActivity, mUser, discountMessage,
                            UserUtils.isHadCurrentUser(commModel.getVisited(), mUser.getObjectId()),
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

        PopupWinUserInfo userWindow = new PopupWinUserInfo(mActivity, commModel.getUser());
        userWindow.onShow(v);

    }

    /**
     * 获取当前用户
     */
    public void getUser() {
        if (mUser == null) {
            mUser = BmobUser.getCurrentUser(mActivity, User.class);
        }
    }

    /**
     * 返回上一级
     */
    private void back2superclazz() {

        if (isClick){
            LocationUtils.sendBordCast(mActivity,Contants.REFRESH_TYPE_DISCOUNT);
        }

        mBackStartActivity(MainActivity.class);
        this.finish();
    }
}
