package com.sobot.online.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.sobot.online.R;


/**
 * 加载中 弹窗
 */

public class SobotDialogUtils {

    public static SobotLoadingDialog progressDialog;

    public static void startProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new SobotLoadingDialog(context, context.getResources().getString(R.string.online_loading));
        } else {
            progressDialog.setmMessage(context.getResources().getString(R.string.online_loading));
        }

        try {
            progressDialog.show();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void startProgressDialog(Context context, String str) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new SobotLoadingDialog(context, str);
        } else {
            progressDialog.setmMessage(str);
        }
        try {
            progressDialog.show();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void stopProgressDialog(Context context) {
        if (progressDialog != null && context != null && progressDialog.isShowing()) {
            Activity act = (Activity) context;
            try {
                if (!act.isFinishing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
//            e.printStackTrace();
            }
        }
        progressDialog = null;
    }

    public static void resetDialogStyle(AlertDialog alertDialog) {
        TextView tvMsg = (TextView) alertDialog.findViewById(android.R.id.message);
        if (tvMsg != null) {
            tvMsg.setTextSize(14);
            tvMsg.setGravity(Gravity.CENTER);
            tvMsg.setTextColor(Color.BLACK);
        }
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(14);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(14);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }
}
