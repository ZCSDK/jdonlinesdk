package com.sobot.online.weight.kpswitch.view;

import android.content.Context;

import com.sobot.online.R;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;


/**
 * 创建view 的工厂类
 * 根据按钮的id来创建对应的view
 */
public class CustomeViewFactory {
    public static BaseChattingPanelView getInstance(Context context, int btnId) {
        BaseChattingPanelView baseView = null;
        SobotOnlineLogUtils.i("BaseChattingPanelView");
        if (btnId != 0) {
            if (btnId == R.id.sobot_btn_upload_view) {
                baseView = new SobotChattingPanelUploadView(context);
            } else if (btnId == R.id.sobot_btn_emoticon_view) {
                baseView = new SobotChattingPanelEmoticonView(context);
            }
        }
        return baseView;
    }

    /**
     * 这里给的tag就是按钮对应的view的类名
     * @param context
     * @param btnId
     * @return
     */
    public static String getInstanceTag(Context context, int btnId) {
        String baseViewTag = null;
        if (btnId != 0) {
            if (btnId == R.id.sobot_btn_upload_view) {
//                baseViewTag = new SobotChattingPanelUploadView(context);
                baseViewTag = "SobotChattingPanelUploadView";
            } else if (btnId == R.id.sobot_btn_emoticon_view) {
//                baseViewTag = new SobotChattingPanelEmoticonView(context);
                baseViewTag = "SobotChattingPanelEmoticonView";
            }
        }
        return baseViewTag;
    }
}