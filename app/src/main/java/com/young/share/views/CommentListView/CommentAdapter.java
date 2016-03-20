package com.young.share.views.CommentListView;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.young.share.R;
import com.young.share.config.TextMovementMethod;
import com.young.share.model.Comment_HZ;
import com.young.share.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表的适配器
 * <p/>
 * Created by yiwei on 16/3/2.
 */
public class CommentAdapter {

    private Context context;
    private CommentListView mListview;
    private List<Comment_HZ> mDatas;

    public CommentAdapter(Context context) {
        this.context = context;
        mDatas = new ArrayList<Comment_HZ>();
    }

    public CommentAdapter(Context context, List<Comment_HZ> datas) {
        this.context = context;
        setDatas(datas);
    }

    public CommentAdapter(Context context, CommentListView mListview, List<Comment_HZ> datas) {
        this.context = context;
        this.mListview = mListview;

        setDatas(datas);
    }

    public void bindListView(CommentListView listView) {
        if (listView == null) {
            throw new IllegalArgumentException("CommentListView is null....");
        }
        mListview = listView;
    }

    public void setDatas(List<Comment_HZ> datas) {
        if (datas == null) {
            datas = new ArrayList<Comment_HZ>();
        }
        mDatas = datas;
    }

    public List<Comment_HZ> getDatas() {
        return mDatas;
    }

    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    public Comment_HZ getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        if (position < mDatas.size()) {
            return mDatas.get(position);
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * 刷新整个Linearlayout
     * <p/>
     * 添加控件到linearLayout中
     */
    public void notifyDataSetChanged() {
        if (mListview == null) {
            throw new NullPointerException("listview is null, please bindListView first...");
        }
        mListview.removeAllViews();
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mDatas.size(); i++) {
            //
            View view = getView(i);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            mListview.addView(view, i, layoutParams);
        }

    }

    /**
     * 创建每一个view，只包含一个textview，进行文字显示
     * 需要在这里设置文字的颜色、点击
     *
     * @param position
     * @return
     */
    private View getView(final int position) {
//       LogUtils.d("CommentAdapter getView-----------------------" + position);
        View convertView = View.inflate(context,
                R.layout.item_signal_text, null);
        final TextView commentTv = (TextView) convertView.findViewById(R.id.txt_signal_text);
        /*名字点击，设置颜色*/

        final Comment_HZ comment = mDatas.get(position);

        //布局复用，设置不同的样式
        commentTv.setTextSize(14);
        commentTv.setPadding(4, 0, 4, 0);
        commentTv.setClickable(true);

        final TextMovementMethod textMovementMethod = new TextMovementMethod();
        commentTv.setMovementMethod(textMovementMethod);

        String senderName = comment.getSenderId().getUsername();
        String receiver = comment.getReveicerId().getUsername();

        /*发送者用户名点击*/
        commentTv.setText(StringUtils.clickUsername(context, senderName, new StringUtils.TextLink() {
            @Override
            public void onclick(String str) {
                Toast.makeText(context, " sender = " + str
                        , Toast.LENGTH_SHORT).show();
            }
        }));
/*接收者用户名点击*/
        if (!TextUtils.isEmpty(receiver)) {
            commentTv.append(context.getString(R.string.txt_replay));

            commentTv.append(StringUtils.clickUsername(context, receiver, new StringUtils.TextLink() {
                @Override
                public void onclick(String str) {
                    Toast.makeText(context, " receiver = " + str
                            , Toast.LENGTH_SHORT).show();

                }
            }));
        }

        //表情
        commentTv.append(":");
        commentTv.append(StringUtils.getEmotionContent(context, commentTv,
                comment.getMessageId().getCommContent()));

        /*item click */
        commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListview.getOnItemClickListener() != null && textMovementMethod.isPassToTv()) {
//                    Toast.makeText(context, " click ", Toast.LENGTH_SHORT).show();
                    mListview.getOnItemClickListener().onItemClick(comment, position);
                }

            }
        });

/*item longclick*/
        commentTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(context, " long click ", Toast.LENGTH_SHORT).show();
                if (mListview.getOnItemLongClickListener() != null) {
                    mListview.getOnItemLongClickListener()
                            .onItemLongClick(comment.getMessageId().getCommContent(), position);

                }
                return false;
            }
        });


        return convertView;
    }

}
