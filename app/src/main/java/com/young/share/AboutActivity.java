package com.young.share;

import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.share.annotation.InjectView;
import com.young.share.base.BaseAppCompatActivity;
import com.young.share.utils.DisplayUtils;

/**
 * 关于
 * Created by Nearby Yang on 2015-11-13.
 */
public class AboutActivity extends BaseAppCompatActivity {
    @InjectView(R.id.im_about_head)
    private ImageView headerLogoIm;
    @InjectView(R.id.txt_about_version)
    private TextView versionTxt;
    @InjectView(R.id.txt_instruction)
    private TextView instrunctionTxt;


    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }


    @Override
    public void initData() {
        initializeToolbar();
        setTitle(R.string.about);

    }

    @Override
    public void findviewbyid() {

        ViewGroup.LayoutParams lp = headerLogoIm.getLayoutParams();
        lp.height = DisplayUtils.getScreenWidthPixels(mActivity) / 2;

    }

    @Override
    public void bindData() {

        instrunctionTxt.append(getString(R.string.txt_about_contect));
    }

    @Override
    public void handerMessage(Message msg) {

    }

    @Override
    public void mBack() {
        mActivity.finish();
    }


    /**
     * 初始化toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_about);
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


}
