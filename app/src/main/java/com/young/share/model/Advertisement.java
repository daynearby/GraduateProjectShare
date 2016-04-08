package com.young.share.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 广告列表
 * Created by Nearby Yang on 2016-03-27.
 */
public class Advertisement extends BmobObject {
    private static final long serialVersionUID = 6140566514872585L;
    private BmobFile adImage;
    private String adLink;
    private String adPubUser;
    private BmobDate alive;

    public BmobFile getAdImage() {
        return adImage;
    }

    public void setAdImage(BmobFile adImage) {
        this.adImage = adImage;
    }

    public String getAdLink() {
        return adLink;
    }

    public void setAdLink(String adLink) {
        this.adLink = adLink;
    }

    public String getAdPubUser() {
        return adPubUser;
    }

    public void setAdPubUser(String adPubUser) {
        this.adPubUser = adPubUser;
    }

    public BmobDate getAlive() {
        return alive;
    }

    public void setAlive(BmobDate alive) {
        this.alive = alive;
    }
}
