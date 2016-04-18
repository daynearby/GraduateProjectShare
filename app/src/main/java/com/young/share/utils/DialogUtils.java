package com.young.share.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Process;
import android.support.v7.app.AlertDialog;

import com.young.share.R;

/**
 * 弹窗的工具类
 * Created by Nearby Yang on 2016-04-18.
 */
public class DialogUtils {

    /**
     * 退出当前按程序的dialog
     *
     * @param context
     * @return v7 AlertDialog
     */
    public static AlertDialog.Builder exitDialog(Context context){
        AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(context);
        alertbBuilder
                .setTitle(R.string.txt_exit_title)
                .setMessage(R.string.txt_exit_message)
                .setPositiveButton(R.string.config,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                int nPid = Process.myPid();
                                Process.killProcess(nPid);
                                System.exit(0);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.cancel();

                            }
                        }).create();

//        alertbBuilder.show();
        return alertbBuilder;
    }
}
