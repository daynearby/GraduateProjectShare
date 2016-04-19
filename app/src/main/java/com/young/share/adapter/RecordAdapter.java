package com.young.share.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.young.share.R;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.config.Contants;
import com.young.share.model.RemoteModel;
import com.young.share.utils.DataFormateUtils;
import com.young.share.utils.DateUtils;
import com.young.share.utils.StringUtils;
import com.young.share.views.MultiImageView.MultiThumbnailImageView;

import java.util.List;

/**
 * 记录适配器
 * Created by Nearby Yang on 2015-12-04.
 */
public class RecordAdapter extends CommAdapter<RemoteModel> {

    private static final String IMAGES_NUMBER = "共%d张图片";
    private static final String DATE_MONTH = "%s月";
    
    public RecordAdapter(Context context) {
        super(context);

    }

    @Override
    public void convert(ViewHolder holder, RemoteModel remoteModel, int position) {

//        ((TextView) holder.getView(R.id.tv_record_comm_nickname)).setText(remoteModel.getMyUser().getNickName());
//        TextView content =  holder.getView(R.id.tv_record_comm_content);
//        content.setText(StringUtils.getEmotionContent(ctx,content,remoteModel.getContent()));
//        ((TextView) holder.getView(R.id.tv_record_comm_created))
//                .setText(DateUtils.convertDate2Str(remoteModel.getCreate
        TextView dayTxt = holder.getView(R.id.txt_post_message_day);
        TextView monthTxt = holder.getView(R.id.txt_post_message_month);
        TextView locationInfoTxt = holder.getView(R.id.txt_post_message_locationinfo);
        ImageView videoIm = holder.getView(R.id.im_post_message_video);
        MultiThumbnailImageView multiImageView = holder.getView(R.id.mv_post_message_images);
        TextView contentTxt = holder.getView(R.id.txt_post_message_content);
        TextView imagesNumber = holder.getView(R.id.txt_post_message_images_number);
        ImageView iconIm = holder.getView(R.id.im_message_type);

        if (position == 0) {
            dayTxt.setVisibility(View.VISIBLE);
            monthTxt.setVisibility(View.VISIBLE);
            dayTxt.setText(DateUtils.getDay(remoteModel.getCreatedAt()));
            monthTxt.setText(String.format(DATE_MONTH, DateUtils.getMonth(remoteModel.getCreatedAt())));

        } else {//第二条开始比较
            timeOrder(remoteModel, position, dayTxt, monthTxt);
        }
        iconIm.setImageResource(remoteModel.getType()== Contants.DATA_MODEL_SHARE_MESSAGES?
                R.drawable.ic_discover:R.drawable.ic_dicount);
        //地址
        if (!TextUtils.isEmpty(remoteModel.getLocationInfo())){
            locationInfoTxt.setVisibility(View.VISIBLE);
            locationInfoTxt.setText(remoteModel.getLocationInfo());
        }else {
            locationInfoTxt.setVisibility(View.GONE);
        }

//文字、图片处理显示
//显示图片
        setImages(multiImageView,imagesNumber, remoteModel);
/*显示视频*/
        setupVideo(remoteModel, videoIm,imagesNumber);
//
//        //只有文字的话，那么文字是有背景的，灰色背景
//        contentTxt.setBackgroundColor(remoteModel.getImages() == null
//                && remoteModel.getVideo() == null ? ctx.getResources().getColor(R.color.light_gray) : Color.WHITE);

//文字内容
        if (TextUtils.isEmpty(remoteModel.getContent())) {
            contentTxt.setVisibility(View.GONE);
        } else {
            contentTxt.setVisibility(View.VISIBLE);
            contentTxt.setText(StringUtils.getEmotionContent(ctx,  remoteModel.getContent()));
        }



    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_user_record;
    }

    /**
     * 设置照片
     * @param multiImageView 显示图片的控件
     * @param imagesNumber 图片数量
     * @param remoteModel  数据
     */
    private void setImages(final MultiThumbnailImageView multiImageView, TextView imagesNumber, final RemoteModel remoteModel) {
        if (remoteModel.getImages() != null && remoteModel.getImages().size() > 0) {
            List<String> list = remoteModel.getImages().size() > 4 ?
                    remoteModel.getImages().subList(0, 4) : remoteModel.getImages();
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.setList(DataFormateUtils.thumbnailList(ctx, list));
            if (remoteModel.getImages().size()>2){
                imagesNumber.setVisibility(View.VISIBLE);
                imagesNumber.setText(String.format(IMAGES_NUMBER,remoteModel.getImages().size()));
            }

        }else {
            multiImageView.setVisibility(View.GONE);
            imagesNumber.setVisibility(View.GONE);
        }


    }

    /**
     * 时间处理，比较时间
     * 前一条的时间是否与当前这一条的时间相同，如果相同，那么就不显示这一条的时间
     *
     * @param remoteModel
     * @param position
     * @param dayTxt
     * @param monthTxt
     */
    private void timeOrder(RemoteModel remoteModel, int position, TextView dayTxt, TextView monthTxt) {
        //前两条信息是空的，这里进行第四和第三条信息进行比较
        if (DateUtils.isTheSameDay(remoteModel.getCreatedAt(), dataList.get(position - 1).getCreatedAt())) {
            dayTxt.setVisibility(View.INVISIBLE);
            monthTxt.setVisibility(View.INVISIBLE);
//
        } else {//前一条信息与当前信息不是同一条或者是这是第一条不为空的信息，那么需要进行显示时间
            dayTxt.setVisibility(View.VISIBLE);
            monthTxt.setVisibility(View.VISIBLE);
            dayTxt.setText(DateUtils.getDay(remoteModel.getCreatedAt()));
            monthTxt.setText(String.format(DATE_MONTH, DateUtils.getMonth(remoteModel.getCreatedAt())));
        }
    }


    /**
     * 设置视频
     *  @param remoteModel
     * @param videoPreview
     * @param imagesNumber
     */
    private void setupVideo(RemoteModel remoteModel, final ImageView videoPreview, TextView imagesNumber) {
        //视频的处理,这里显示视频截图
        if (remoteModel.getVideo() != null && !TextUtils.isEmpty(remoteModel.getVideo().getFileUrl(ctx))) {
            /*一开始视频没有缩略图，这里加个判断*/
            if (remoteModel.getVideoPreview() != null && !TextUtils.isEmpty(remoteModel.getVideoPreview().getFileUrl(ctx))) {
                ImageLoader.getInstance().displayImage(remoteModel.getVideoPreview().getFileUrl(ctx), videoPreview);

            } else {/*没有图片默认设置成灰色*/
                videoPreview.setBackgroundColor(ctx.getResources().getColor(R.color.light_gray));
            }
            imagesNumber.setText(ctx.getString(R.string.txt_video));
            videoPreview.setVisibility(View.VISIBLE);

        } else {
            videoPreview.setVisibility(View.GONE);

        }
    }

}
