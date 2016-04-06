package com.young.share.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.R;
import com.young.share.adapter.baseAdapter.BasePagerAdapter;
import com.young.share.model.PictureInfo;
import com.young.share.views.photoview.PhotoView;

import java.util.List;

public class ImageEditorPagerAdapter extends BasePagerAdapter<PictureInfo> {

    private ActionBar mActionBar;

    public ImageEditorPagerAdapter(Context context, List<PictureInfo> dataList, ActionBar mActionBar) {
        super(context);
        this.dataList = dataList;
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
//        photo.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap(View view, float x, float y) {
//                if (mActionBar.isShowing()) {
//                    mActionBar.hide();
//                } else {
//                    mActionBar.show();
//                }
//            }
//        });


        ImageLoader.getInstance().displayImage(pictureInfo.imageUrl, photo);

    }


}
