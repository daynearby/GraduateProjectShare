package com.young.share.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.young.share.adapter.EmotionGvAdapter;
import com.young.share.adapter.EmotionPagerAdapter;
import com.young.share.R;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.EmotionUtils;
import com.young.share.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情面板
 * 从下方弹出，与消失
 * Created by Nearby Yang on 2015-10-28.
 */
public class PopupWinEmotionPanel extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context ctx;
    private InputMethodManager imm;
    private View view;
    private EditText et_emotion;

    private EmotionPagerAdapter emotionPagerGvAdapter;
    private ViewPager vp_emotion_dashboard;


    /**
     *
     * @param ctx
     * @param et_emotion 输入框
     */
    public PopupWinEmotionPanel(Context ctx, EditText et_emotion) {
        this.ctx = ctx;
        this.et_emotion = et_emotion;

        init();
        findView();
        bingData();
    }

    private void bingData() {
        initEmotion();
    }

    private void findView() {
        vp_emotion_dashboard = (ViewPager) view.findViewById(R.id.vp_popupwindow_emotion_dashboard);
    }

    //初始化popupwindow
    private void init() {
        view = LayoutInflater.from(ctx).inflate(R.layout.content_popup_window_emotion_panel, null);
        setContentView(view);
        setFocusable(true);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setOutsideTouchable(true);
        imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        setBackgroundDrawable(new BitmapDrawable());
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(DisplayUtils.getScreenWidthPixels((Activity) ctx));

    }

    /**
     * 显示表情选择面板
     *
     * @param parentView
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onShow(View parentView) {
// TODO: 2015-10-29 键盘的隐藏

//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//键盘隐藏
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    /**
     * 初始化表情面板内容
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int gvWidth = DisplayUtils.getScreenWidthPixels((Activity) ctx);
        // 表情边距
        int spacing = DisplayUtils.dp2px(ctx, 8);
        // GridView中item的宽度
        int itemWidth = (gvWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        // 遍历所有的表情名字
        for (String emojiName : EmotionUtils.emojiMap.keySet()) {
            emotionNames.add(emojiName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<String>();
            }
        }

        // 检查最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        // 将多个GridView添加显示到ViewPager中
        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
        vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(ctx);
        gv.setBackgroundColor(Color.rgb(233, 233, 233));
        gv.setSelector(android.R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);
        LayoutParams params = new LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGvAdapter adapter = new EmotionGvAdapter(ctx, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        return gv;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();

        if (itemAdapter instanceof EmotionGvAdapter) {
            // 点击的是表情
            EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

            if (position == emotionGvAdapter.getCount() - 1) {
                // 如果点击了最后一个回退按钮,则调用删除键事件
                et_emotion.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                // 如果点击了表情,则添加到输入框中
                String emotionName = emotionGvAdapter.getItem(position);

                // 获取当前光标位置,在指定位置上添加表情图片文本
                int curPosition = et_emotion.getSelectionStart();
                StringBuilder sb = new StringBuilder(et_emotion.getText().toString());
                sb.insert(curPosition, emotionName);

                // 特殊文字处理,将表情等转换一下
                et_emotion.setText(StringUtils.getEmotionContent(
                        ctx, et_emotion, sb.toString()));

                // 将光标设置到新增完表情的右侧
                et_emotion.setSelection(curPosition + emotionName.length());
            }

        }
    }

}
