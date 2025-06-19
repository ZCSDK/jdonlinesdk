package com.sobot.online;


import com.sobot.online.listener.SobotPlusMenuListener;
import com.sobot.online.weight.kpswitch.view.SobotChattingPanelUploadView;

import java.util.List;

public class SobotOnlineUIConfig {

    /**
     * 更多面板中的菜单配置
     */
    public static final class pulsMenu {
        public static List<SobotChattingPanelUploadView.SobotPlusEntity> menus;
        public static SobotPlusMenuListener sSobotPlusMenuListener;
    }
}