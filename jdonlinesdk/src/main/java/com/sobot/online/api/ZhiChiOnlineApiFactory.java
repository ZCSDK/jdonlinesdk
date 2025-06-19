package com.sobot.online.api;

import android.content.Context;

import com.sobot.onlinecommon.api.ZhiChiOnlineApi;
import com.sobot.onlinecommon.api.ZhiChiOnlineApiImpl;

/**
 * 对外输出接口类工厂
 */
public class ZhiChiOnlineApiFactory {

    public static ZhiChiOnlineApi createZhiChiApi(Context context) {
        if (context != null) {
            return new ZhiChiOnlineApiImpl(context.getApplicationContext());
        } else {
            throw new IllegalArgumentException("The context can not be null");
        }
    }
}