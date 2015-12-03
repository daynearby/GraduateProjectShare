package com.young.views;

import android.content.Context;
import android.util.AttributeSet;

import com.twotoasters.jazzylistview.JazzyListView;

/**
 * 适应scrollview 的listview
 * 具有item载入特效
 * Created by Nearby Yang on 2015-12-03.
 */
public class EffectListView extends JazzyListView {

    public EffectListView(Context context) {
        super(context);
    }

    public EffectListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EffectListView(Context context, AttributeSet attrs, int defStyle) {
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
