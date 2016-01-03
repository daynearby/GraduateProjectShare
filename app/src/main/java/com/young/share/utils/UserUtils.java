package com.young.share.utils;

import java.util.List;

/**
 * 用户信息的操作类
 * Created by Nearby Yang on 2015-12-03.
 */
public class UserUtils {

    /**
     * 判断用户是否存在于列表中
     *
     * @param usersID
     * @return
     */
    public static boolean isHadCurrentUser(List<String> usersID,String user) {
        boolean isHad = false;
        if (usersID == null) {
            return false;
        }
        for (String userId : usersID) {

            if (user.equals(userId)) {
                isHad = true;
                break;
            }
        }
        return isHad;
    }


}
