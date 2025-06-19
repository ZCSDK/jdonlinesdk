package com.sobot.online.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.utils.SobotScreenUtils;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;


/**
 * 客服被挤下线弹窗
 */

public class SobotOnlineExitDialog extends Dialog {

    private Button sobot_btn_cancle_conversation, sobot_btn_temporary_leave;
    private TextView sobot_tv_will_end_conversation;
    private LinearLayout coustom_pop_layout;
    private View.OnClickListener itemsOnClick;
    private final int screenHeight;
    private String customKickStatus;

    public SobotOnlineExitDialog(Activity context, String custom_kick_status, View.OnClickListener itemsOnClick) {
        super(context, R.style.sobot_noAnimDialogStyle);
        this.itemsOnClick = itemsOnClick;
        this.customKickStatus = custom_kick_status;
        screenHeight = SobotScreenUtils.getScreenHeight();

        // 修改Dialog(Window)的弹出位置
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            //横屏设置dialog全屏
            if (SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH) && SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_exit_popup);
        initView();
    }



    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - coustom_pop_layout.getHeight() - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
//                dismiss();
            }
        }
        return true;
    }

    private void initView() {
        sobot_btn_cancle_conversation = (Button) findViewById(R.id.sobot_btn_cancle_conversation);
        sobot_btn_cancle_conversation.setText(getContext().getString(R.string.onnline_exit));
        sobot_btn_temporary_leave = (Button) findViewById(R.id.sobot_btn_temporary_leave);
        sobot_btn_temporary_leave.setText(getContext().getString(R.string.onnline_relogin));
        coustom_pop_layout = (LinearLayout) findViewById(R.id.pop_layout);
        sobot_tv_will_end_conversation = (TextView) findViewById(R.id.sobot_tv_will_end_conversation);
        sobot_btn_cancle_conversation.setOnClickListener(itemsOnClick);
        sobot_btn_temporary_leave.setOnClickListener(itemsOnClick);

        if (SobotSocketConstant.push_custom_outline_kick.equals(customKickStatus)) {
            sobot_tv_will_end_conversation.setText(getContext().getString(R.string.onnline_kicked));
        } else if (SobotSocketConstant.push_custom_outline_kick_by_admin.equals(customKickStatus)) {
            sobot_tv_will_end_conversation.setText(getContext().getString(R.string.onnline_kicked_by_admin));
        } else {
            sobot_tv_will_end_conversation.setText(getContext().getString(R.string.onnline_kicked));
        }
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.width = dm.widthPixels;
    }
}
