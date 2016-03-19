package com.young.share.views.CommentListView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.young.share.model.Comment_HZ;


/**
 * 使用自定义的linearLayout代替listview ，更为节省资源
 * 并且设置字体的颜色、点击监听
 *
 */
public class CommentListView extends LinearLayout {

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public CommentListView(Context context) {
        super(context);
    }

    public CommentListView(Context context, AttributeSet attrs){
        super(context, attrs);

    }

    public CommentListView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }


    public void setAdapter(CommentAdapter adapter){
        adapter.bindListView(this);
    }

    public void setOnItemClick(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClick(OnItemLongClickListener listener){
        mOnItemLongClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return mOnItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener(){
        return mOnItemLongClickListener;
    }

    /**
     * onclick de 功能是回复、评论
     */
    public  interface OnItemClickListener{
         void onItemClick(Comment_HZ comment, int position);
    }

    /**
     * long click 功能是复制
     *
     */
    public  interface OnItemLongClickListener{
         void onItemLongClick(String content, int position);
    }
}
