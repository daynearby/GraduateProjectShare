package com.young.share.views;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
    private static int selectNumber = 0;
    private static StringBuilder cityString = null;

    /**
     * 城市选择的popupmenu
     *
     * @param context 对象
     * @return
     */
    public static PopupMenu citySelectMenu(Context context, View v, final SelectResult selectResult) {
        PopupMenu cityMenu = new PopupMenu(context, v);
        cityMenu.inflate(R.menu.menu_context_empty);
        List<String> cityList = XmlUtils.getSelectCities(context);


        Menu menu = cityMenu.getMenu();

        for (int i = 0; i < cityList.size(); i++) {
            int areaId = 2;//Menu.FIRST
            // 0,id,顺序，内容
            SubMenu subMenu = menu.addSubMenu(0, cityId + i, Menu.NONE, cityList.get(i));
            for (String area : XmlUtils.getSelectArea(context, i)) {
                // 0,id,顺序，内容，子菜单 从第二条开始，
                subMenu.add(i, areaId, areaId, area);
                areaId++;
            }

        }
        /**
         * popupMenu 监听
         *
         */
        cityMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (selectNumber == 0) {
                    cityString = new StringBuilder();
                    cityString.append(menuItem.getTitle());
                    selectNumber++;
                } else if (selectNumber == 1) {
                    cityString.append(menuItem.getTitle());
                    selectNumber = 0;
                    if (selectResult != null) {
                        selectResult.reslut(cityString);
                    }

                }
                ;
                return false;
            }
        });

        return cityMenu;
    }

    public interface SelectResult {
        void reslut(StringBuilder stringBuilder);
    }
}
