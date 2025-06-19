package com.sobot.online.util;


import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class OrderUtils {

    /*public static void main(String[] args) {
        List<PushMessageModel> list = new ArrayList<PushMessageModel>();
        list.add(new PushMessageModel(true, "2009-01-01 20:20:20"));
        list.add(new PushMessageModel(false, "2001-01-01 21:10:20"));
        list.add(new PushMessageModel(true, "2005-05-01 02:20:20"));
        list.add(new PushMessageModel(false, "2003-01-01 10:22:20"));
        list.add(new PushMessageModel(false, "2003-02-01 22:25:20"));
        list.add(new PushMessageModel(true, "2004-01-01 23:22:20"));
        list.add(new PushMessageModel(false, "2003-05-01 01:22:20"));
        list.add(new PushMessageModel(true, "2007-01-01 02:25:20"));
        LogUtils.i("排序前:" + list);

        LogUtils.i("排序后:" + orderList(list));

    }*/


    public static class OrderComparator implements Comparator<PushMessageModel> {
        private int sortflag = 0;
        private int topflag = 0;

        public OrderComparator(int sortflag, int topflag) {
            this.sortflag = sortflag;
            this.topflag = topflag;
        }

        public int compare(PushMessageModel p1, PushMessageModel p2) {
            //是否在线  是否置顶   时间
            if (p1.isOnline() == p2.isOnline()) {
                if (topflag == 1) {
                    //星标 置顶
                    if (p1.getMarkStatus() == p2.getMarkStatus()) {
                        return compareTime(p1, p2, sortflag);
                    } else if (p1.getMarkStatus() == 1) {
                        //星标置顶
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return compareTime(p1, p2, sortflag);
            } else if (p1.isOnline()) {
                return -1;
            } else {
                return 1;
            }
        }

        private int compareTime(PushMessageModel p1, PushMessageModel p2, int sortflag) {
            if (sortflag == 1) {
                return compareNewMsgTime(p1, p2);
            } else {
                return compareAcceptTime(p1, p2);
            }

        }

        //根据新消息排序
        private int compareNewMsgTime(PushMessageModel p1, PushMessageModel p2) {
            Date d1 = getFormatTS(p1);
            Date d2 = getFormatTS(p2);
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null)
                return -1;
            if (d2 == null)
                return 1;
            return d2.compareTo(d1);
        }

        //根据接入时间排序
        private int compareAcceptTime(PushMessageModel p1, PushMessageModel p2) {
            Date d1 = getFormatTS2(p1);
            Date d2 = getFormatTS2(p2);
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null)
                return -1;
            if (d2 == null)
                return 1;
            return d2.compareTo(d1);
        }

    }

    private static Date getFormatTS(PushMessageModel pushMessageModel) {
        if (pushMessageModel == null || pushMessageModel.getTimeOrder() == null) {
            return null;
        }
        return SobotTimeUtils.string2Date(pushMessageModel.getTimeOrder());

    }

    private static Date getFormatTS2(PushMessageModel pushMessageModel) {
        if (pushMessageModel == null || pushMessageModel.getAcceptTimeOrder() == null) {
            return null;
        }
        return SobotTimeUtils.string2Date(pushMessageModel.getAcceptTimeOrder());

    }

    /**
     * 排序的方法
     *
     * @param listBefore
     * @param sortflag   会话排序 0 按接入顺序 1 按新消息时间
     * @param topflag    星标置顶 0不置顶 1置顶
     * @return
     */
    public static List<PushMessageModel> orderList(List<PushMessageModel> listBefore, int sortflag, int topflag) {
        /*规则：在线在上，离线在下，都按时间从近到远
        在线用户排列顺序按照设置接待顺序排列：

        按接入顺序：新接入的用户在列表上方，用户发来信息后列表排序不会改变；
        按新消息时间：用户发来新消息后自动移动到列表上方*/

//        LogUtils.i("sobot--排序:" + listBefore);
        SobotOnlineLogUtils.i("topflag:" + topflag + "  sortflag: " + sortflag);
        Collections.sort(listBefore, new OrderComparator(sortflag, topflag));

//        LogUtils.i("sobot--排序后:" + listAfter);

        return null;
    }
}