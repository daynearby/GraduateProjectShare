package com.young.share.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.R;
import com.young.share.adapter.baseAdapter.BasePagerAdapter;
import com.young.share.model.PictureInfo;
import com.young.share.utils.ImageHandlerUtils;
import com.young.share.views.photoview.PhotoView;
import com.young.share.views.photoview.PhotoViewAttacher;

import java.util.List;

public class ImageBrowserPagerAdapter extends BasePagerAdapter<PictureInfo> {

    private boolean isLocal;
    private ActionBar mActionBar;

    public ImageBrowserPagerAdapter(Context context, List<PictureInfo> dataList, ActionBar mActionBar, boolean isLocal) {
        super(context);
        this.dataList = dataList;
        this.isLocal = isLocal;
        this.mActionBar = mActionBar;
    }

    @Override
    protected int getLayout() {
        return R.layout.item_photoview;
    }



    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    protected void instanceItem(View v, PictureInfo pictureInfo, int position) {
        PhotoView photo = (PhotoView) v.findViewById(R.id.pv_item_photoview);
        photo.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (mActionBar.isShowing()) {
                    mActionBar.hide();
                } else {
                    mActionBar.show();
                }
            }
        });

        if (dataList != null) {

            if (!isLocal) {//网络图片的加载
                photo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ((Activity) context).openContextMenu(v);
                        return false;
                    }
                });

                ImageLoader.getInstance().displayImage( pictureInfo.imageUrl, photo);

            } else {//本地图片
                ImageHandlerUtils.loadIamge(context, pictureInfo.imageUrl, photo, true);

            }
        }
    }


}
