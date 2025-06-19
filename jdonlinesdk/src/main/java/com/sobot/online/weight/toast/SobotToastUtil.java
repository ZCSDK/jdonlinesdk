package com.sobot.online.weight.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SobotToastUtil {

    private static Toast toast;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof OnAfterShowListener) {
                OnAfterShowListener listener = (OnAfterShowListener) msg.obj;
                listener.doAfter();
            }
        }
    };

    /**
     * 自定义的toast
     *
     * @param context
     * @param str
     */
    public static void showCustomToast(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            SobotOnlineLogUtils.i("toast 内容 不能为空");
            return;
        }
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义停留长时间的toast
     *
     * @param context
     * @param str
     */
    public static void showCustomLongToast(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            SobotOnlineLogUtils.i("toast 内容 不能为空");
            return;
        }
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 自定义的带图片的toast
     *
     * @param context
     * @param str
     * @param resId
     */
    public static void showCustomToast(Context context, String str, int resId) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT, resId).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义停留长时间的带图片的toast
     *
     * @param context
     * @param str
     * @param resId
     */
    public static void showCustomLongToast(Context context, String str, int resId) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT, resId).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 自定义toast，显示固定时间,然后执行监听方法
     *
     * @param context
     * @param str
     * @param onAfterShowListener
     */
    public static void showCustomToastWithListenr(Context context, String str, final OnAfterShowListener onAfterShowListener) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (onAfterShowListener != null)
                        doListener(onAfterShowListener);
                }
            }, 1000);//延时执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义带图标的toast，显示固定时间,然后执行监听方法
     *
     * @param context
     * @param str
     * @param resId
     * @param onAfterShowListener
     */
    public static void showCustomToastWithListenr(Context context, String str, int resId, final OnAfterShowListener onAfterShowListener) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT, resId).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (onAfterShowListener != null)
                        doListener(onAfterShowListener);
                }
            }, 1500);//延时执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doListener(OnAfterShowListener onAfterShowListener) {
        Message message = mHandler.obtainMessage();
        message.obj = onAfterShowListener;
        mHandler.sendMessage(message);
    }


    public interface OnAfterShowListener {
        void doAfter();
    }
}