package com.sobot.appsdk;

import android.app.Application;

import com.sobot.appsdk.utils.ConstantUtils;
import com.sobot.online.SobotOnlineService;
//import com.squareup.leakcanary.AndroidExcludedRefs;
//import com.squareup.leakcanary.DisplayLeakService;
//import com.squareup.leakcanary.ExcludedRefs;
//import com.squareup.leakcanary.LeakCanary;

public class SobotAppSdkApplicgtion extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化方法由基础组件包提供，要使用IM SDK，需要在应用application.onCreate函数中调用基础组件包提供的初始化函数
        SobotOnlineService.initWithHost(this, ConstantUtils.HOST);
        //是否显示智齿日志
        SobotOnlineService.setShowDebug(true);
//        ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
//                .instanceField("android.mRootView.inputmethod.InputMethodManager", "sInstance")
//                .instanceField("android.mRootView.inputmethod.InputMethodManager", "mLastSrvView")
//                .instanceField("com.android.internal.policy.PhoneWindow$DecorView", "mContext")
//                .instanceField("android.support.v7.widget.SearchView$SearchAutoComplete", "mContext")
//                .build();
//        LeakCanary.refWatcher(this)
//                .listenerServiceClass(DisplayLeakService.class)
//                .excludedRefs(excludedRefs)
//                .buildAndInstall();
    }
}
