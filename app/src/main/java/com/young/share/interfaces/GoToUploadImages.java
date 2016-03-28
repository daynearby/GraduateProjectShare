package com.young.share.interfaces;

import cn.bmob.v3.datatype.BmobFile;

/**
 * 选择照片回调函数
 *
 * Created by Nearby Yang on 2015-10-24.
 */
public interface GoToUploadImages {

    void Result( String[] urls,BmobFile[] files);
    void onError(int statuscode, String errormsg) ;
}
