package com.sobot.online.api;


import com.sobot.onlinecommon.socket.channel.LimitQueue;
import com.sobot.onlinecommon.utils.SobotSPUtils;

/**
 * 缓存一些临时变量
 *
 * @author Created by jinxl on 2018/5/11.
 */
public class MemoryCacheManager {
    private MemoryCacheManager() {
    }

    private static MemoryCacheManager api;

    public static MemoryCacheManager getInstance() {
        if (api == null) {
            synchronized (MemoryCacheManager.class) {
                if (api == null) {
                    api = new MemoryCacheManager();
                }
            }
        }
        return api;
    }

    /**
     * 标记哪些cid已经做过会话总结
     */
    private LimitQueue<String> summaryList = null;

    public void putSummaryCid(String cid) {
        summaryList = (LimitQueue<String>) SobotSPUtils.getInstance().getObject("summaryList");
        if (summaryList == null) {
            summaryList = new LimitQueue<>(30);
        }
        if (summaryList.indexOf(cid) == -1) {
            //队列中没有 表示是新数据
            //新数据就添加进队列中
            summaryList.offer(cid);
        }
        SobotSPUtils.getInstance().put("summaryList", summaryList);
    }

    /**
     * 判断cid是否已经总结
     *
     * @param cid
     * @return
     */
    public boolean hasSummaryCid(String cid) {
        summaryList = (LimitQueue<String>) SobotSPUtils.getInstance().getObject("summaryList");
        if (summaryList != null) {
            return summaryList.indexOf(cid) != -1;
        }
        return false;
    }
}
