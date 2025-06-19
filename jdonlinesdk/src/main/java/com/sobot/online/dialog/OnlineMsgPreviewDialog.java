package com.sobot.online.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.dialog.base.SobotActionSheet;

/**
 * whatsapp 消息预览弹窗
 */
public class OnlineMsgPreviewDialog extends SobotActionSheet {

    private Context context;
    public Button button;
    public Button button2;
    private TextView sobot_message;
    public OnItemClick mOnItemClick = null;

    private String whatsappMsg;
    private LinearLayout coustom_pop_layout;
    public OnlineMsgPreviewDialog(Activity context, String whatsappMsg) {
        super(context);
        this.whatsappMsg = whatsappMsg;
    }

    @Override
    protected String getLayoutStrName() {
        return "online_msg_preview_dialog";
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = (LinearLayout) findViewById(getResId("sobot_container"));
        }
        return coustom_pop_layout;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        sobot_message = (TextView) findViewById(R.id.sobot_message);
        sobot_message.setText(TextUtils.isEmpty(whatsappMsg) ? "" : whatsappMsg);
        sobot_message.setMovementMethod(ScrollingMovementMethod.getInstance());
        button = (Button) findViewById(R.id.sobot_cancel);
        button2 = (Button) findViewById(R.id.sobot_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClick.OnClick(0);
                dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClick.OnClick(1);
                dismiss();
            }
        });
    }

    public void setOnClickListener(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public interface OnItemClick {
        void OnClick(int type);
    }
}