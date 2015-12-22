package com.young.myInterface;

import com.young.model.CommRemoteModel;

import java.util.Comparator;

/**
 * 排序
 * <p/>
 * Created by Nearby Yang on 2015-12-19.
 */
public class ComparatorImpl implements Comparator<Object> {
    private int key;

    public static final int COMPREHENSIVE = 100;//综合排序

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
            case COMPREHENSIVE://最多人想去,综合排序.想去占0.4，去过占0.4，评论占0.2

                CommRemoteModel data1 = (CommRemoteModel) json1;
                CommRemoteModel data2 = (CommRemoteModel) json2;
                int data1Compre = (int) ((data1.getWanted() == null ? 0 : data1.getWanted().size()) * 0.4
                        + (data1.getVisited() == null ? 0 : data1.getVisited().size()) * 0.4
                        + (data1.getComment() * 0.2));
                int data2Compre = (int) ((data2.getWanted() == null ? 0 : data2.getWanted().size()) * 0.4
                        + (data2.getVisited() == null ? 0 : data2.getVisited().size()) * 0.4
                        + (data2.getComment() * 0.2) + 1);

                result = data1Compre - data2Compre;
                break;

        }
        return result;
    }


}
