package com.young.share.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义，宽高相等的gridview
 * Created by Nearby Yang on 2015-06-28.
 */
public class WrapHightListview extends ListView {
    public WrapHightListview(Context context) {
        super(context);
    }

    public WrapHightListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapHightListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
