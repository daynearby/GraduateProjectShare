package com.young.share.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * comment_hz 评论数据
 *
 * JSON解析类
 * Created by Nearby Yang on 2015-12-03.
 */
public class CommentList {

    @SerializedName("results")
    private List<Comment_HZ> commentList;

    public List<Comment_HZ> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment_HZ> commentList) {
        this.commentList = commentList;
    }
}
