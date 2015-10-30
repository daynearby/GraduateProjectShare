package com.young.utils.cache;

import android.app.Activity;
import android.widget.Toast;

import com.young.config.Contants;
import com.young.share.R;
import com.young.utils.LogUtils;

import org.json.JSONArray;

import java.util.List;

/**
 * 保存草稿与删除
 * <p/>
 * Created by Nearby Yang on 2015-10-30.
 */
public class DarftUtils {
    private static DarftUtils saveDarftUtils;
    private ACache acache;
    private Activity activity;

    private DarftUtils(Activity mactivity) {
        activity = mactivity;
        acache = ACache.get(mactivity);

    }

    public static DarftUtils builder(Activity mactivity) {

        if (saveDarftUtils == null) {
            synchronized (ACache.class) {
                if (saveDarftUtils == null) {
                    saveDarftUtils = new DarftUtils(mactivity);
                }
            }
        }
        return saveDarftUtils;
    }


    /**
     * 保存草稿
     */

    public  void saveDraft(String content, String locationInfo, String tagLable, List<String> list) {
        //文字，位置，图片，标签

        JSONArray imagesJsonArray = new JSONArray(list);

        acache.put(Contants.DRAFT_CONTENT, content, Contants.DARFT_LIVE_TIME);
        acache.put(Contants.DRAFT_LOCATION_INFO, locationInfo, Contants.DARFT_LIVE_TIME);
        acache.put(Contants.DRAFT_TAG, tagLable, Contants.DARFT_LIVE_TIME);
        acache.put(Contants.DRAFT_IMAGES_LIST, imagesJsonArray, Contants.DARFT_LIVE_TIME);

        Toast.makeText(activity, R.string.save_draft_success, Toast.LENGTH_SHORT).show();

        LogUtils.logI("保存草稿");
    }

    /**
     * 删除草稿
     */
    public void deleteDraft() {

        acache.remove(Contants.DRAFT_CONTENT);
        acache.remove(Contants.DRAFT_LOCATION_INFO);
        acache.remove(Contants.DRAFT_TAG);
        acache.remove(Contants.DRAFT_IMAGES_LIST);

    }
}
