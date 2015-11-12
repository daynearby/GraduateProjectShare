package com.young.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.young.config.Contants;
import com.young.utils.SharePreferenceUtils;

/**
 * Created by Nearby Yang on 2015-08-17.
 */
public class WelcomeActivity extends Activity {

    private Intent intents = new Intent();
    private boolean isFirstIn = true;

    private final static String isFirstInTAG = "isFirstIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        isFirstIn();
    }

    /**
     * 是否第一次启动
     */

    private void isFirstIn() {

        SharePreferenceUtils preferences = new SharePreferenceUtils(this);

        isFirstIn = preferences.getBoolean(Contants.sharePreferenceStr.hashCode() + "", isFirstInTAG);

        if (isFirstIn) {

            preferences.setBoolean(Contants.sharePreferenceStr.hashCode() + "", isFirstInTAG, false);
            handler.sendEmptyMessageDelayed(Contants.GO_GUIDE, Contants.TIME);


        } else {
//"welcome", MODE_PRIVATE);
            handler.sendEmptyMessageDelayed(Contants.GO_HOME, Contants.TIME);

        }


    }

    /**
     * 启动界面的
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Contants.GO_HOME:
                    goHome();

                    break;

                case Contants.GO_GUIDE:
                    goGuide();

                    break;

            }
        }

    };


    /**
     * 进入主界面
     */
    private void goHome() {

        intents.setClass(WelcomeActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.animator.activity_slid_bottom_in, R.animator.activity_slid_bottom_out);
        this.finish();

    }


    private void goGuide() {

        intents.setClass(WelcomeActivity.this, GuideActivity.class);

        startActivity(intents);
        overridePendingTransition(R.animator.activity_slid_bottom_in, R.animator.activity_slid_bottom_out);
        this.finish();

    }


}
