package com.young.model.dbmodel;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * model的基类，有用户的基本信息 继承他
 * 要实现数据库操作，还有在Activity传递参数的时候使用
 * <p>
 * Created by Nearby Yang on 2015-10-16.
 */
public class DBModel extends DataSupport implements Serializable {

    private int id;
    @Column(unique = true)
    private String objectId;
    private String createdAt;
    private String updatedAt;

    public int getId() {
        return id;
    }
//
    public void setId(int id) {
        this.id = id;
    }
//
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }



    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
