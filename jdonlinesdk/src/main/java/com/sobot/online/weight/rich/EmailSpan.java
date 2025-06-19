package com.sobot.online.weight.rich;

import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.core.app.ShareCompat;

import com.sobot.online.OnlineOption;


public class EmailSpan extends ClickableSpan {

    private String email;
    private int color;

    public EmailSpan(Context context, String email, int color) {
        this.email = email;
        this.color = context.getResources().getColor(color);
    }

    @Override
    public void onClick(View widget) {
        if (OnlineOption.newHyperlinkListener != null) {
            //如果返回true,拦截;false 不拦截
            boolean isIntercept = OnlineOption.newHyperlinkListener.onEmailClick(email);
            if (isIntercept) {
                return;
            }
        }
        try {
            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
                    .from((Activity) widget.getContext());
            builder.setType("message/rfc822");
            builder.addEmailTo(email);
            builder.setSubject("");
            builder.setChooserTitle("");
            builder.startChooser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false); // 去掉下划线
    }
}