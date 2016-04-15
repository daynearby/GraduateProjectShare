package com.young.share.views.actionProvider;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.young.share.R;
import com.young.share.utils.LogUtils;
import com.young.share.views.PopupMenuHub;

/**
 * 百度地图搜索的toolbar中的Actionprovider
 * <p/>
 * Created by Nearby Yang on 2016-03-13.
 */
public class MainActyProvider extends ActionProvider {
    private Context context;
    private View view;
    private TextView cityTxt;
    private OnPopupMenuitemListener onPopupMenuitemListener;


    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public MainActyProvider(Context context) {
        super(context);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.menu_main_acty_provider, null);
        cityTxt = (TextView) view.findViewById(R.id.txt_select_city);
    }

    @Override
    public View onCreateActionView() {

        final PopupMenu citySelectMenu = PopupMenuHub.citySelectMenu(context, cityTxt, new PopupMenuHub.SelectResult() {

            @Override
            public void reslut(String selectResult) {
                LogUtils.d(" city = " + selectResult);
                cityTxt.setText(context.getString(R.string.txt_current_city) + selectResult);

                if (onPopupMenuitemListener != null) {
                    onPopupMenuitemListener.clickItem(selectResult);
                }
            }
        });

        cityTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                citySelectMenu.show();
            }
        });

        return view;

    }

    /**
     * 获取引用
     *
     * @return
     */
    public TextView getCityTx() {
        return cityTxt;
    }

    /**
     * popmenu item 点击监听
     *
     * @param onPopupMenuitemListener 回调
     */
    public void setOnPopupMenuitemListener(OnPopupMenuitemListener onPopupMenuitemListener) {
        this.onPopupMenuitemListener = onPopupMenuitemListener;
    }

    public interface OnPopupMenuitemListener {
        void clickItem(String city);
    }

}
