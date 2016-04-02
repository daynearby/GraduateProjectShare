package com.young.share.adapter;

import android.content.Context;
import android.widget.TextView;

import com.young.share.R;
import com.young.share.adapter.baseAdapter.CommAdapter;
import com.young.share.adapter.baseAdapter.ViewHolder;
import com.young.share.model.RemoteModel;
import com.young.share.utils.DateUtils;
import com.young.share.utils.StringUtils;

/**
 * 记录适配器
 * Created by Nearby Yang on 2015-12-04.
 */
public class WantToGoAdapter extends CommAdapter<RemoteModel> {


    public WantToGoAdapter(Context context) {
        super(context);

    }

    @Override
    public void convert(ViewHolder holder, RemoteModel remoteModel, int position) {

        ((TextView) holder.getView(R.id.tv_record_comm_nickname)).setText(remoteModel.getMyUser().getNickName());
        TextView content =  holder.getView(R.id.tv_record_comm_content);
        content.setText(StringUtils.getEmotionContent(ctx,content,remoteModel.getContent()));
        ((TextView) holder.getView(R.id.tv_record_comm_created))
                .setText(DateUtils.convertDate2Str(remoteModel.getCreatedAt()));

    }

    @Override
    public int getlayoutid(int position) {
        return R.layout.item_record_comm;
    }
}
