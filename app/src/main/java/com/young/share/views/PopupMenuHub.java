package com.young.share.views;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.young.share.R;
import com.young.share.utils.XmlUtils;

import java.util.List;

/**
 * popupMenu 工具类
 * Created by Nearby Yang on 2016-04-04.
 */
public class PopupMenuHub {

    private static final int cityId = 0x100;

    /**
     * 城市选择的popupmenu
     *
     * @param context 对象
     * @return
     */
    public static PopupMenu citySelectMenu(Context context, View v) {
        PopupMenu cityMenu = new PopupMenu(context, v);
        cityMenu.inflate(R.menu.menu_context_empty);
        List<String> cityList = XmlUtils.getSelectCities(context);

        Menu menu = cityMenu.getMenu();

        for (int i = 0; i < cityList.size(); i++) {
            menu.add(0, cityId + i, Menu.NONE, cityList.get(i));
            for (String area : XmlUtils.getSelectArea(context, i)) {
                menu.addSubMenu(area);
            }

        }
        /**
         * popupMenu 监听
         *
         */
        cityMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

menuItem.getTitle();
                return false;
            }
        });

        return cityMenu;
    }
}
