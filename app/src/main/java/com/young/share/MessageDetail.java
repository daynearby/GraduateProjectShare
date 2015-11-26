package com.young.share;

import android.os.Message;
import android.view.View;

import com.young.base.ItemActBarActivity;
import com.young.config.Contants;
import com.young.model.CommRemoteModel;
import com.young.model.ShareMessage_HZ;
import com.young.thread.MyRunnable;
import com.young.utils.ThreadUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 查看详细信息
 * Created by Nearby Yang on 2015-11-26.
 */
public class MessageDetail extends ItemActBarActivity {

    private static String backTagClazz;
    private static CommRemoteModel commModel = new CommRemoteModel();
    private List<String> commentList;


    @Override
    public int getLayoutId() {
        // TODO: 2015-11-27 用户头像使用缩略图，这里是用大图。界面的设计-->控件选择
        return R.layout.activity_message_detail;
    }

    @Override
    public void initData() {
        super.initData();
        setBarItemVisible(true, false);
        setTvTitle(R.string.title_body);

        backTagClazz = getIntent().getStringExtra(Contants.CLAZZ_NAME);

        ThreadUtils threadUtils = new ThreadUtils();

        threadUtils.addTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                switch (backTagClazz) {

                    case Contants.CLAZZ_DISCOVER_ACTIVITY://shareMessage

                        formateDataDiscover(getIntent().getSerializableExtra(Contants.CLAZZ_DATA_MODEL));

                        break;

                }


            }
        }));

        threadUtils.start();

    }

    @Override
    public void findviewbyid() {


    }

    @Override
    public void bindData() {

//标题栏的监听
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                mBackStartActivity(backTagClazz);
            }

            @Override
            public void rightClivk(View v) {

            }
        });


    }

    @Override
    public void handerMessage(Message msg) {

    }


    /**
     * sharemessage
     * <p/>
     * 处理数据
     *
     * @param serializableExtra
     */
    private void formateDataDiscover(Serializable serializableExtra) {

        ShareMessage_HZ shareMessage = (ShareMessage_HZ) serializableExtra;

        commModel.setContent(shareMessage.getShContent());
        commModel.setImages(shareMessage.getShImgs());
        commModel.setLocationInfo(shareMessage.getShLocation());
        commModel.setTag(shareMessage.getShTag());
        commModel.setUser(shareMessage.getUserId());
        commModel.setVisited(shareMessage.getShVisitedNum());
        commModel.setWanted(shareMessage.getShWantedNum());
        commModel.setObjectId(shareMessage.getObjectId());
        commModel.setComment(shareMessage.getShCommNum());
        commModel.setType(Contants.DATA_MODEL_SHARE_MESSAGES);//属于分享信息

    }


}
