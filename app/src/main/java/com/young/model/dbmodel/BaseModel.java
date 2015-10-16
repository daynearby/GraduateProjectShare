package com.young.model.dbmodel;

import android.view.View;

import com.young.base.BaseActivity;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * model的基类， 继承他
 * 要实现数据库操作，还有在Activity传递参数的时候使用
 *
 * Created by Nearby Yang on 2015-10-16.
 */
public  class BaseModel extends DataSupport implements Serializable {

}
