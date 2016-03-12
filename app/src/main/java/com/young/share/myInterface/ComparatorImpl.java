package com.young.share.myInterface;

import com.young.share.model.dbmodel.CommRemoteModel;

import java.util.Comparator;

/**
 * 排序
 * list 数据进行排序，按照一定的数量进行排序
 *
 * <p>
 * Created by Nearby Yang on 2015-12-19.
 */
public class ComparatorImpl implements Comparator<Object> {
    private int key;

    public static final int COMPREHENSIVE = 100;//综合排序
    public static final int COMPREHENSIVE_OTHERS = 101;//其他的排序

    public ComparatorImpl(int key) {
        this.key = key;
    }

    /**
     * @param json1
     * @param json2
     * @return
     */
    @Override
    public int compare(Object json1, Object json2) {
        int result = 0;

        switch (key) {
            case COMPREHENSIVE://最多人想去,综合排序.想去占0.5，去过占0.4，评论占0.1

                CommRemoteModel data1 = (CommRemoteModel) json1;
                CommRemoteModel data2 = (CommRemoteModel) json2;
                int data1Compre = (int) ((data1.getWanted() == null ? 0 : data1.getWanted().size()) * 0.5
                        + (data1.getVisited() == null ? 0 : data1.getVisited().size()) * 0.4
                        + (data1.getComment() * 0.1) + 1);
                int data2Compre = (int) ((data2.getWanted() == null ? 0 : data2.getWanted().size()) * 0.5
                        + (data2.getVisited() == null ? 0 : data2.getVisited().size()) * 0.4
                        + (data2.getComment() * 0.1) + 1);

                result = data2Compre - data1Compre;
                break;

            case COMPREHENSIVE_OTHERS://排序.想去占0.3，去过占0.6，评论占0.1
                CommRemoteModel data3 = (CommRemoteModel) json1;
                CommRemoteModel data4 = (CommRemoteModel) json2;
                int data3Compre = (int) ((data3.getWanted() == null ? 0 : data3.getWanted().size()) * 0.3
                        + (data3.getVisited() == null ? 0 : data3.getVisited().size()) * 0.6
                        + (data3.getComment() * 0.1) + 1);
                int data4Compre = (int) ((data4.getWanted() == null ? 0 : data4.getWanted().size()) * 0.3
                        + (data4.getVisited() == null ? 0 : data4.getVisited().size()) * 0.6
                        + (data4.getComment() * 0.1) + 1);

                result = data4Compre - data3Compre;

                break;
        }

        return result;
    }


}
