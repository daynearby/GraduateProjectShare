package com.young.myCallback;

import android.content.Intent;

import java.util.List;

/**
 * 选择照片回调函数
 *
 * Created by Nearby Yang on 2015-10-24.
 */
public interface GoToSelectImages {

    void selectResult(List<String> dataList,int REQUEST_IMAGE);
}
