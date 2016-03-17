package com.young.share.views;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;

import com.young.share.utils.DisplayUtils;
import com.young.share.utils.XmlUtils;

import java.util.List;

/**
 * 自定义选择城市
 * Created by Nearby Yang on 2015-11-14.
 */
public class CitySelectPopupWin extends PopupWinListView {

    private ResultListener resultListener;
    private boolean isCity = true;
    private String city;
    private String areas;
    private Context ctx;

    public CitySelectPopupWin(final Context ctx, List<String> datas) {
        super(ctx, datas, true);
        this.ctx = ctx;

        setItemClick(new onItemClick() {
            @Override
            public void onClick(View view, String str, int position, long id) {
                if (isCity) {
                    isCity = false;
                    setDatas(XmlUtils.getSelectArea(ctx, position));
                    city = str;

                } else {
                    isCity = true;
                    areas = str;

                    if (resultListener != null) {
                        resultListener.result(city + " " + areas);
                    }

                    dismiss();
                }

            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                isCity = true;
                city = "";
                areas = "";

            }
        });


    }

    @Override
    public void onShow(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int offsetPx = (DisplayUtils.getScreenWidthPixels((Activity) ctx) - v.getWidth()) / 2;
            int offsetPx2dp = DisplayUtils.px2dip(ctx, offsetPx);
            showAsDropDown(v, -offsetPx2dp, 0, Gravity.TOP);
        } else {

            int widthDp = DisplayUtils.px2dip(ctx, v.getWidth()) / 2;
            showAtLocation(v, Gravity.TOP, -widthDp, 0);
        }

    }


    /**
     * 最终的详细地区信息
     *
     * @param resultListener
     */
    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }


    public interface ResultListener {
        void result(String str);
    }
}
