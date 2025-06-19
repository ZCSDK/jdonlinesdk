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
import com.sobot.onlinecommon.utils.SobotScreenUtils;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;


/**
 * 通用弹窗
 */

public class SobotOnlineCommonDialog extends Dialog {

    private Button sobot_btn_cancle, sobot_btn_ok;
    private TextView sobot_tv_title;
    private LinearLayout coustom_pop_layout;
    private View.OnClickListener itemsOnClick;
    private final int screenHeight;
    private String mTitleStr;//弹窗标题文案
    private String mOkStr;//弹窗确认文案
    private String mCanncleStr;//弹窗取消文案


    public SobotOnlineCommonDialog(Activity context, String titleStr, String okStr, String canncleStr, View.OnClickListener itemsOnClick) {
        super(context, R.style.sobot_noAnimDialogStyle);
        this.itemsOnClick = itemsOnClick;
        this.mCanncleStr = canncleStr;
        this.mOkStr = okStr;
        this.mTitleStr = titleStr;
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
        setContentView(R.layout.sobot_common_popup);
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
        sobot_btn_cancle = (Button) findViewById(R.id.sobot_btn_cancle);
        sobot_btn_ok = (Button) findViewById(R.id.sobot_btn_ok);
        coustom_pop_layout = (LinearLayout) findViewById(R.id.pop_layout);
        sobot_tv_title = (TextView) findViewById(R.id.sobot_tv_title);
        sobot_btn_cancle.setOnClickListener(itemsOnClick);
        sobot_btn_ok.setOnClickListener(itemsOnClick);

        sobot_tv_title.setText(mTitleStr != null ? mTitleStr : "");
        sobot_btn_ok.setText(mOkStr != null ? mOkStr : "");
        sobot_btn_cancle.setText(mCanncleStr != null ? mCanncleStr : "");

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
