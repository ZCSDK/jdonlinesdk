package com.sobot.online.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.sobot.online.api.ZhiChiOnlineApiFactory;
import com.sobot.onlinecommon.api.ZhiChiOnlineApi;

public class SobotOnlineBaseFragment extends SobotBaseFragment {
    public ZhiChiOnlineApi zhiChiApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (zhiChiApi == null) {
            synchronized (SobotOnlineBaseActivity.class) {
                if (zhiChiApi == null) {
                    zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(getSobotActivity());
                }
            }
        }
    }
}
