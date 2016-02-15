package com.young.share.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


/**
 * 适应scrollview 的listview
 * 具有item载入特效
 * Created by Nearby Yang on 2015-12-03.
 */
public class WrapHeightListView extends ListView {

    public WrapHeightListView(Context context) {
        super(context);
    }

    public WrapHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapHeightListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
