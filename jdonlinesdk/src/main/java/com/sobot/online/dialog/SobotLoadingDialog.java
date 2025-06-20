package com.sobot.online.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sobot.online.R;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;

public class SobotLoadingDialog extends Dialog {


    private static final String TAG = "SobotLoadingDialog";

    private String mMessage;
    private boolean mCancelable;
    private TextView tv_loading;

    public SobotLoadingDialog(@NonNull Context context, String message) {
        this(context, R.style.sobot_dialog_Progress, message, false);
    }

    public SobotLoadingDialog(@NonNull Context context, int themeResId, String message, boolean cancelable) {
        super(context, themeResId);
        mMessage = message;
        mCancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        initView();
    }

    private void initView() {
        setContentView(R.layout.sobot_progress_dialog);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        //  attributes.alpha = 0.3f;
        //attributes.width = (screenWidth / 5) * 2;
        // attributes.height = (screenWidth / 5) * 2;
        attributes.gravity = Gravity.CENTER;
        //横屏设置dialog全屏
        if (SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH) && SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        getWindow().setAttributes(attributes);
        setCancelable(mCancelable);

        tv_loading = findViewById(R.id.tv_loading);
        tv_loading.setText(mMessage);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 屏蔽返回键
            return mCancelable;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
        tv_loading.setText(mMessage);
    }
}
