package com.sobot.online.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sobot.online.R;

/**
 * 提醒消息弹窗，带有一个确定按钮
 */
public class OnlineTipDialog extends Dialog {

	private Context content;
	public Button button;
	private TextView sobot_title;
	public OnItemClick mOnItemClick = null;
	private String mTitle;
	private String mBtnText;

	public OnlineTipDialog(Context context,String title, String oneBtnText,
						   OnItemClick onItemClick) {
		super(context, R.style.sobot_noAnimDialogStyle);
		this.content = context;
		mTitle = title;
		mBtnText = oneBtnText;
		mOnItemClick = onItemClick;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.online_tip_ok_dialog);
		sobot_title= (TextView) findViewById(R.id.sobot_title);
		sobot_title.setText(mTitle);
		button = (Button) findViewById(R.id.sobot_ok);
		button.setText(mBtnText);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mOnItemClick.OnClick(0);
			}
		});
	}

	public void setOnClickListener(OnItemClick onItemClick) {
		mOnItemClick = onItemClick;
	}

	public interface OnItemClick{
		void OnClick(int type);
	}
}