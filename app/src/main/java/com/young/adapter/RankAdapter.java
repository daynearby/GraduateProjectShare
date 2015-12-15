package com.young.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.young.model.RankBean;
import com.young.share.R;

import java.util.List;

/**
 * recyclerview adapter瀑布流
 * Created by Nearby Yang on 2015-12-15.
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.myViewHolder> {

    private Context ctx;
    private List<RankBean> dataList;
    private OnItemClickListener onItemClickListener;

    public RankAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 刷新数据
     *
     * @param dataList
     */
    public void setDataList(List<RankBean> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_rank_main, parent, false));
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, final int position) {

        holder.cardView.setCardBackgroundColor(dataList.get(position).getBackgroundColor());
//        holder.cardView.setBackground(ctx.getResources().getDrawable(dataList.get(position).getBackgroundColor()));
        holder.icon_im.setImageResource(dataList.get(position).getIconId());

        holder.describe_txt.setText(dataList.get(position).getImageText());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(v, position);
                }
            }
        });
    }


    class myViewHolder extends RecyclerView.ViewHolder {

        TextView describe_txt;
        ImageView icon_im;
        CardView cardView;

        public myViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.layout_rank_main_bg);
            describe_txt = (TextView) itemView.findViewById(R.id.txt_rank_main_describe);
            icon_im = (ImageView) itemView.findViewById(R.id.im_rank_main_icon);

        }
    }


    public interface OnItemClickListener {

        void onClick(View v, int position);
    }
}
