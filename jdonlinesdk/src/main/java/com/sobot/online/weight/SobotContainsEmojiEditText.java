package com.sobot.online.weight;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sobot.online.weight.emoji.SobotInputHelper;
import com.sobot.online.weight.kpswitch.util.KeyboardUtil;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;

/**
 * 自动补全的editText
 */
public class SobotContainsEmojiEditText extends EditText {

    View mContentView;
    MyWatcher myWatcher;
    MyEmojiWatcher myEmojiWatcher;

    public SobotContainsEmojiEditText(Context context) {
        super(context);
        initEditText();
    }

    public SobotContainsEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditText();
    }

    public SobotContainsEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditText();
    }

    // 初始化edittext 控件
    private void initEditText() {
        myEmojiWatcher = new MyEmojiWatcher();
        addTextChangedListener(myEmojiWatcher);
        myWatcher = new MyWatcher();
        addTextChangedListener(myWatcher);
        if (SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {//横屏
            setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {//完成
                        KeyboardUtil.hideKeyboard(SobotContainsEmojiEditText.this);
                        doAfterTextChanged(v.getText().toString());
                        return true;
                    }
                    if (actionId == KeyEvent.ACTION_DOWN) {
                        KeyboardUtil.hideKeyboard(SobotContainsEmojiEditText.this);
                        doAfterTextChanged(v.getText().toString());
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void doAfterTextChanged(String s) {

    }

    private class MyWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
          //  SobotLogUtils.e("beforeTextChanged: " + s.toString());
            if (!SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {
                doAfterTextChanged(s.toString());
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            doBeforeTextChanged();
         //   SobotLogUtils.e("beforeTextChanged: " + s.toString());
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
         //   SobotLogUtils.e("onTextChanged: " + s.toString());
        }
    }

    /**
     * 表情监听
     */
    private class MyEmojiWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            s = SobotInputHelper.displayEmoji(getContext(), s);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        removeTextChangedListener(myWatcher);
        mContentView = null;
        super.onDetachedFromWindow();
    }
}