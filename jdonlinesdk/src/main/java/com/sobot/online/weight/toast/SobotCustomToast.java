package com.sobot.online.weight.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.online.R;

/**
 * 自定义时长的Toast
 */
public class SobotCustomToast {

    //不带图片的 toast
    public static Toast makeText(Context context, CharSequence text,
                                 int duration) {

        Toast toast = new Toast(context.getApplicationContext());
        View view = View.inflate(context, R.layout.sobot_custom_toast_text_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.sobot_tv_content);
        tv.setText(text);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        return toast;
    }

    //带图片的 toast
    public static Toast makeText(Context context, CharSequence text,
                                 int duration, int resId) {
        Toast toast = new Toast(context.getApplicationContext());
        View view = View.inflate(context, R.layout.sobot_custom_toast_text_img_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.sobot_tv_content);
        tv.setText(text);
        ImageView tv_content = (ImageView) view.findViewById(R.id.sobot_iv_content);
        tv_content.setImageResource(resId);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        return toast;
    }
}