package com.young.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.young.config.Contants;
import com.young.utils.SharePreferenceUtils;

import cn.bmob.v3.BmobUser;

/**
 * Created by Nearby Yang on 2015-08-17.
 */
public class WelcomeActivity extends Activity{

    private Intent intents;
    private boolean isFirstIn=true;

    private final static String isFirstInTAG="isFirstIn";

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

    private void isFirstIn(){

        SharePreferenceUtils preferences=new SharePreferenceUtils(this);

        isFirstIn=preferences.getBoolean(Contants.sharePreferenceStr.hashCode()+"",isFirstInTAG);

        if(isFirstIn){

            preferences.setBoolean(Contants.sharePreferenceStr.hashCode()+"",isFirstInTAG,false);
            handler.sendEmptyMessageDelayed(Contants.GO_GUIDE, Contants.TIME);



        }else{
//"welcome", MODE_PRIVATE);
            handler.sendEmptyMessageDelayed(Contants.GO_HOME,Contants.TIME);

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
     *
     */
    private void goHome(){

        intents=new Intent(WelcomeActivity.this,MainActivity.class);

        loginFunction();

        startActivity(intents);
        this.finish();

    }




    private void goGuide(){

        intents=new Intent(WelcomeActivity.this,GuideActivity.class);

        startActivity(intents);

        this.finish();

    }

    /**
     * 登录过，才能进行登录
     * 没有登录过，则不进行其他操作
     */
    private void loginFunction() {

        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        if(bmobUser != null){
            // 允许用户使用应用

        }else{
            //缓存用户对象为空时， 可打开用户注册界面…
        }
    }

}
