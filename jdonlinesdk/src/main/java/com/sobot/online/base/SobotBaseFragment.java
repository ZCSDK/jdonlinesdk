package com.sobot.online.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sobot.network.http.SobotOkHttpUtils;
import com.sobot.onlinecommon.ui.notchlib.SobotINotchScreen;
import com.sobot.onlinecommon.ui.notchlib.SobotNotchScreenManager;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;
import com.sobot.widget.ui.permission.SobotPermissionListener;

import java.io.File;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public abstract class SobotBaseFragment extends Fragment {

    protected File cameraFile;

    private Activity activity;
    //权限回调
    public SobotPermissionListener permissionListener;

    public SobotBaseFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (Activity) context;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH) && SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {
            // 支持显示到刘海区域
            SobotNotchScreenManager.getInstance().setDisplayInNotch(getActivity());
            // 设置Activity全屏
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void displayInNotch(final View view) {
        if (SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN) && SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH) && view != null) {
            // 获取刘海屏信息
            SobotNotchScreenManager.getInstance().getNotchInfo(getActivity(), new SobotINotchScreen.NotchScreenCallback() {
                @Override
                public void onResult(SobotINotchScreen.NotchScreenInfo notchScreenInfo) {
                    if (notchScreenInfo.hasNotch) {
                        for (Rect rect : notchScreenInfo.notchRects) {
                            if (view instanceof WebView && view.getParent() instanceof LinearLayout) {
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = rect.right + 14;
                                view.setLayoutParams(layoutParams);
                            } else if (view instanceof WebView && view.getParent() instanceof RelativeLayout) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = rect.right + 14;
                                view.setLayoutParams(layoutParams);
                            } else {
                                view.setPadding(rect.right, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SobotOkHttpUtils.getInstance().cancelTag(SobotBaseFragment.this);
    }

    /**
     * 返回activity
     *
     * @return
     */
    public Activity getSobotActivity() {
        Activity activity = getActivity();
        if (activity == null) {
            return this.activity;
        }
        return activity;

    }

    //隐藏键盘
    public void hideSoftInput(){
        if (getSobotActivity() != null) {
            SobotKeyboardUtils.hideSoftInput(getSobotActivity());
        }
    }

}