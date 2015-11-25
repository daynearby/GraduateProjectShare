package com.young.myInterface;

/**
 * 选择照片回调函数
 *
 * Created by Nearby Yang on 2015-10-24.
 */
public interface GoToUploadImages {

    void Result(boolean isFinish, String[] urls);
    void onError(int statuscode, String errormsg) ;
}
