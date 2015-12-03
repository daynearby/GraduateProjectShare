package com.young.share;

import android.os.Message;
import android.view.View;

import com.young.base.ItemActBarActivity;
import com.young.thread.MyRunnable;

/**
 * 通用记录查看
 * <p/>
 * Created by Nearby Yang on 2015-12-03.
 */
public class RecordCommActivity extends ItemActBarActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_record_comm;
    }

    @Override
    public void initData() {
        super.initData();

        threadUtils.startTask(new MyRunnable(new MyRunnable.GotoRunnable() {
            @Override
            public void running() {

                //直接读取本地缓存，没有数据再进行下载数据
                //下载数据
            }
        }));

    }

    @Override
    public void findviewbyid() {

        setBarItemVisible(true, false);
        setItemListener(new BarItemOnClick() {
            @Override
            public void leftClick(View v) {
                mBackStartActivity(PersonalCenterActivity.class);
                mActivity.finish();
            }

            @Override
            public void rightClivk(View v) {

            }
        });


    }

    @Override
    public void bindData() {

    }

    @Override
    public void handerMessage(Message msg) {

    }
}
