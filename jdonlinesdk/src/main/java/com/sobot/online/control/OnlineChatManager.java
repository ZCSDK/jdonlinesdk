package com.sobot.online.control;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sobot.onlinecommon.socket.channel.Const;

import java.util.List;

/**
 * 消息管理类
 */
public class OnlineChatManager {

    /*************通道相关*********************/
    public static void disconnChannel(Context context) {
        if (context != null) {
            context.sendBroadcast(new Intent(Const.SOBOT_CUSTOME_DISCONNCHANNEL));
        }
    }


    public static void connChannel(Context context, List<String> wslinkBak, String wslinkDefault, String aid, String companyId, String puid) {

        if (context != null && !TextUtils.isEmpty(wslinkDefault) && !TextUtils.isEmpty(aid) && !TextUtils.isEmpty(companyId) && !TextUtils.isEmpty(puid)) {
            Intent intent = new Intent(Const.SOBOT_CUSTOME_CONNCHANNEL);
            if (wslinkBak != null && wslinkBak.size() > 0) {
                intent.putExtra("wslinkBak", wslinkBak.get(0));
            }
            intent.putExtra("wslinkDefault", wslinkDefault);
            intent.putExtra("companyId", companyId);
            intent.putExtra("aid", aid);
            intent.putExtra("puid", puid);
            intent.putExtra("userType", Const.user_type_customer);
            context.sendBroadcast(intent);
        }
    }
}
