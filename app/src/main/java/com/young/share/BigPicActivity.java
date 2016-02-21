package com.young.share;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.young.share.adapter.BigpicturePagerAdapter;
import com.young.share.annotation.InjectView;
import com.young.share.base.BaseActivity;
import com.young.share.config.Contants;
import com.young.share.model.ImageInfo;
import com.young.share.utils.DisplayUtils;
import com.young.share.utils.EvaluateUtil;
import com.young.share.views.CustomViewPager;
import com.young.share.views.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图查看
 * <p/>
 * Created by Nearby Yang on 2016-02-20.
 */
public class BigPicActivity extends BaseActivity implements ViewTreeObserver.OnPreDrawListener {

    @InjectView(R.id.vp_big_picture)
    private CustomViewPager viewPager;
    @InjectView(R.id.rl_bigpic_root_layout)
    private RelativeLayout rootLayout;
    @InjectView(R.id.ll_bigpic_index)
    private LinearLayout indexLayout;

    private List<View> dotList = new ArrayList<>();
    private List<ImageInfo> imageInfoList;
    private int currentItem;
    private  BigpicturePagerAdapter viewpagerAdapter;
    private int height;
    private int width;

    @Override
    protected void initData() {
        imageInfoList = (List<ImageInfo>) getIntent().getSerializableExtra(Contants.INTENT_IMAGE_INFO_LIST);
        currentItem = getIntent().getIntExtra(Contants.INTENT_CURRENT_ITEM, 0);


    }

    @Override
    protected int getLayout() {
        return R.layout.activity_bigpic;
    }


    @Override
    protected void setupEvent() {

         viewpagerAdapter = new BigpicturePagerAdapter(this);
        viewpagerAdapter.setData(imageInfoList);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentItem);
        setPagerChangeListener(viewPager);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
    }

    /**
     * 绘制前开始动画
     *
     * @return
     */
    @Override
    public boolean onPreDraw() {
        viewPager.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = viewpagerAdapter.getPrimaryItem();
        final PhotoView imageView = (PhotoView) ((ViewGroup) view).getChildAt(0);

        computeImageWidthAndHeight(imageView);

        final ImageInfo imageInfo = imageInfoList.get(currentItem);
        final float vx = imageInfo.width * 1.0f / width;
        final float vy = imageInfo.height * 1.0f / height;

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animatedFraction = animation.getAnimatedFraction();

                view.setTranslationX(EvaluateUtil.evaluateInt(animatedFraction, imageInfo.x + imageInfo.width / 2 - imageView.getWidth() / 2, 0));
                view.setTranslationY(EvaluateUtil.evaluateInt(animatedFraction, imageInfo.y + imageInfo.height / 2 - imageView.getHeight() / 2, 0));
                view.setScaleX(EvaluateUtil.evaluateFloat(animatedFraction, vx, 1));
                view.setScaleY(EvaluateUtil.evaluateFloat(animatedFraction, vy, 1));

                rootLayout.setBackgroundColor((int) EvaluateUtil.evaluateArgb(animatedFraction, 0x0, 0xff000000));
            }
        });

        addIntoListener(valueAnimator);
        valueAnimator.setDuration(300);
        valueAnimator.start();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //返回键
        startActivityAnim();
    }

    /**
     * 页面改动监听
     *
     * @param viewPager
     */
    private void setPagerChangeListener(CustomViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                initDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 进场动画过程监听
     *
     * @param valueAnimator
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootLayout.setBackgroundColor(0x0);
                indexLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                indexLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    /**
     * 退场动画过程监听
     *
     * @param valueAnimator
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootLayout.setBackgroundColor(0x0);
                indexLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 开始activity的动画
     */
    public void startActivityAnim() {
//        得到当前页的View
        final View view = viewpagerAdapter.getPrimaryItem();
        final PhotoView imageView = (PhotoView) ((ViewGroup) view).getChildAt(0);
//      当图片被放大时，需要把其缩放回原来大小再做动画
        imageView.setZoomable(false);
        computeImageWidthAndHeight(imageView);

        final ImageInfo imageInfo = imageInfoList.get(currentItem);
        final float vx = imageInfo.width * 1.0f / width;
        final float vy = imageInfo.height * 1.0f / height;

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animatedFraction = animation.getAnimatedFraction();

                view.setTranslationX(EvaluateUtil.evaluateInt(animatedFraction, 0, imageInfo.x + imageInfo.width / 2 - imageView.getWidth() / 2));
                view.setTranslationY(EvaluateUtil.evaluateInt(animatedFraction, 0, imageInfo.y + imageInfo.height / 2 - imageView.getHeight() / 2));
                view.setScaleX(EvaluateUtil.evaluateFloat(animatedFraction, 1, vx));
                view.setScaleY(EvaluateUtil.evaluateFloat(animatedFraction, 1, vy));

                if (animatedFraction > 0.95) {
                    view.setAlpha(1 - animatedFraction);
                }
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    /**
     * 计算图片的宽高
     * @param imageView
     */
    private void computeImageWidthAndHeight(PhotoView imageView) {

//      获取真实大小
        Drawable drawable = imageView.getDrawable();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
//        计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
        float h = DisplayUtils.getScreenHeightPixels(this) * 1.0f / intrinsicHeight;
        float w = DisplayUtils.getScreenWidthPixels(this) * 1.0f / intrinsicWidth;
        if (h > w) {
            h = w;
        } else {
            w = h;
        }
//      得出当宽高至少有一个充满的时候图片对应的宽高
        height = (int) (intrinsicHeight * h);
        width = (int) (intrinsicWidth * w);
    }


    /**
     * 初始化轮播图点
     */
    private void initDot(int index) {
        // 清空点所在集合
        dotList.clear();
        indexLayout.removeAllViews();
        for (int i = 0; i < imageInfoList.size(); i++) {
            ImageView view = new ImageView(this);
            if (i == index || imageInfoList.size() == 1) {
                view.setBackgroundResource(R.drawable.icon_type_selected);
            } else {
                view.setBackgroundResource(R.drawable.icon_type_normal);
            }
            // 指定点的大小
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    DisplayUtils.dp2px(this, 5), DisplayUtils.dp2px(this, 5));
            // 指定点的间距
            layoutParams.setMargins(DisplayUtils.dp2px(this, 2), 0, DisplayUtils.dp2px(this, 2), 0);
            // 添加到线性布局中
            indexLayout.addView(view, layoutParams);
            // 添加到集合中去
            dotList.add(view);
        }
    }
}
