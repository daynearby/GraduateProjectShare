package com.young.share.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.young.share.config.ApplicationConfig;
import com.young.share.utils.LogUtils;

/**
 * fragment基类
 * 必须先初始化 initBaseFragment
 * Created by Nearby Yang on 2015-10-09.
 */
public abstract class BaseFragment extends Fragment {

    public View view;
//    public ThreadPool threadPool;
    public Context context;
    public ApplicationConfig app;
    private Bundle savedState;
    //保存状态的key
    private static final String internalSavedViewState = "internalSavedViewState522564745";
    public static final String BUNDLE_KEY = "bundle_key";

    /**
     * 初始化参数
     *
     * @param context
     */
    public void initizliza(Context context) {
        this.context = context;
        app = ApplicationConfig.getInstance();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initizliza(context);

    }

    public Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler(msg);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(getLayoutId(), container,false);
        ViewGroup vg = (ViewGroup) view.getParent();
//        view.findViewById(R.id.pb_item_photoview);
        if (vg != null) {
            vg.removeAllViewsInLayout();
        }
//        //context maybe null
//        try {
//            threadPool = app.getThreadInstance();
//        } catch (NullPointerException e) {
//            LogUtils.e(" must initBaseFragment() first app = null");
//        }
        getDataFromBunlde(getArguments());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore State Here

        if (!restoreStateFromArguments()) {
            // First Time, Initialize something here
            //context maybe null
            try {
                onFirstTimeLaunched();
            } catch (NullPointerException e) {
                LogUtils.e("must initBaseFragment() first context = null");
            }
        }


    }

    /**
     * 判断是否有参数
     *
     * @return
     */
    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if (b == null) {//没有保存数据
            return false;
        }
        savedState = b.getBundle(internalSavedViewState);
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }


    /////////////////////////////////
    // Restore Instance State Here
    /////////////////////////////////

    private void restoreState() {
        if (savedState != null) {
            // For Example
            //tv1.setText(savedState.getString(text));
            onRestoreState(savedState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State Here
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Save State Here
        saveStateToArguments();
    }


    ////////////////////
    //  保存状态
    ////////////////////
    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            if (b != null) {//有保存数据。那么就保存状态。没有保存数据，不进行操作
                b.putBundle(internalSavedViewState, savedState);
            }/*else {//没有保存数据。创建一个新的状态信息
                b =new Bundle();
                b.putBundle(internalSavedViewState, savedState);
            }*/
        }
    }

    //////////////////////////////
    // Save Instance State Here
    //////////////////////////////

    private Bundle saveState() {
        Bundle state = new Bundle();
        // For Example
        //state.putString(text, tv1.getText().toString());
        onSaveState(state);
        return state;
    }

    /**
     * 只有第一次才初始化控件
     */
    protected void onFirstTimeLaunched() {
        initData();
        initView();
        bindData();
    }

    /**
     * 要保持状态的控件的数据、信息
     *
     * @param outState
     */
    protected abstract void onSaveState(Bundle outState);

    /**
     * 回复控件的数据
     *
     * @param savedInstanceState
     */
    protected abstract void onRestoreState(Bundle savedInstanceState);

    /**
     * 获取传递过来的数据
     * 需要进行获取参数，那么就需要重写
     */
    protected void getDataFromBunlde(Bundle bundle) {
    }

    ;

    public abstract int getLayoutId();

    //初始化数据
    public abstract void initData();

    //实例化控件
    public abstract void initView();

    //绑定数据
    public abstract void bindData();

    public abstract void handler(Message msg);

    /**
     * 简化findviewbyid
     *
     * @param viewID
     * @param <T>
     * @return
     */
    public <T> T $(int viewID) {
        return (T) view.findViewById(viewID);
    }
}
