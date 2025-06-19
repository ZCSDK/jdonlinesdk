package com.sobot.online.util;

import android.app.ActivityManager;
import android.content.Context;

import com.sobot.online.R;

import java.util.List;

public class SobotChatUtils {
    /**
     * 聊天列表获取默认头像id
     *
     * @param source
     * @param isOnline
     * @return
     */
    public static int getUserAvatorWithSource(int source, boolean isOnline) {
        int avatorDrawable;
        switch (source) {
            case 0:
                avatorDrawable = isOnline ? R.drawable.avatar_computer_online : R.drawable.avatar_computer_offline;
                break;
            case 1:
            case 9:
            case 10:
                avatorDrawable = isOnline ? R.drawable.avatar_wechat_online : R.drawable.avatar_wechat_offline;
                break;
            case 2:
                avatorDrawable = isOnline ? R.drawable.avatar_app_online : R.drawable.avatar_app_offline;
                break;
            case 3:
                avatorDrawable = isOnline ? R.drawable.avatar_weibo_online : R.drawable.avatar_weibo_offline;
                break;
            case 4:
                avatorDrawable = isOnline ? R.drawable.avatar_phone_online : R.drawable.avatar_phone_offline;
                break;
            case 22:
                avatorDrawable = isOnline ? R.drawable.avatar_facebook_online : R.drawable.avatar_facebook_offline;
                break;
            case 23:
                avatorDrawable = isOnline ? R.drawable.avatar_whatsapp_online : R.drawable.avatar_whatsapp_offline;
                break;
            case 24:
                avatorDrawable = isOnline ? R.drawable.avatar_instagram_online : R.drawable.avatar_instagram_offline;
                break;
            default:
                avatorDrawable = isOnline ? R.drawable.avatar_whatsapp_online : R.drawable.avatar_whatsapp_offline;
                break;
        }
        return avatorDrawable;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：com.sobot.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}
