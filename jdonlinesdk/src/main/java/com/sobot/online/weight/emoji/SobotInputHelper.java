package com.sobot.online.weight.emoji;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sobot.online.R;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kymjs (http://www.kymjs.com)
 */
public class SobotInputHelper {
    public static void backspace(EditText editText) {
        if (editText == null) {
            return;
        }
        //模仿软键盘实现软键盘的删除功能
        int keyCode = KeyEvent.KEYCODE_DEL;
        KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        editText.onKeyDown(keyCode, keyEventDown);
        editText.onKeyUp(keyCode, keyEventUp);
    }

    /**
     * 获取name对应的资源
     *
     * @param context
     * @param name
     * @return
     */

    public static int getEmojiResId(Context context, String name) {
        Map<String, Integer> mapAll = DisplayRules.getMapAll(context);
        if (mapAll.size() > 0) {
            Integer res = mapAll.get(name);
            if (res != null) {
                return res.intValue();
            } else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Support OSChina Client，due to the need to support both 2 Format<br>
     * (I'm drunk, I go home)
     */
    public static Spannable displayEmoji(Context context, CharSequence s) {
        String str = s.toString();
        Spannable spannable = null;
        if (s instanceof Spannable) {
            spannable = (Spannable) s;
        } else {
            // 构建文字span
            spannable = new SpannableString(str);
        }
        Resources res = context.getResources();

        // a>，[大兵] </body> :smile:
        int bound = (int) res.getDimension(R.dimen.sobot_meau_text_font_large);
        String regex = "\\[[^\\]^\\[]+\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(spannable);
        while (m.find()) {
            String group = m.group();
//            LogUtils.i("aaa:"+group+"  "+m.start()+"  "+m.end());
            try { // [大兵]
                String emojiStr = m.group();
                int resId = getEmojiResId(context, emojiStr); // 通过字符串得到图片资源id
                if (resId > 0) {
                    // 构建图片span
                    Drawable drawable = res.getDrawable(resId);
                    drawable.setBounds(0, 0, bound, bound + 0);

                    // 将Drawable封装到ImageSpan中
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

                    // 替换指定位置内容为图片ImageSpan
                    spannable.setSpan(span, m.start(), m.end(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
            }
        }

        return spannable;
    }

    public static void input2OSC(EditText editText, EmojiconNew emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            // 没有多选时，直接在当前光标处添加
            editText.append(emojicon.getEmojiCode());
        } else {
            // 将已选中的部分替换为表情(当长按文字时会多选刷中很多文字)
            String str = emojicon.getEmojiCode();
            editText.getText().replace(Math.min(start, end),
                    Math.max(start, end), str, 0, str.length());
        }
    }

    /**
     * //此方法只是打开软键盘
     * @param act   activity
     */
    public static void openInputMode(Activity act) {
        InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && imm.isActive() && act.getCurrentFocus()!=null) {
            imm.showSoftInputFromInputMethod(act.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * //此方法只是关闭软键盘
     * @param act activity
     */
    public static void hideInputMode(Activity act) {
        InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && imm.isActive() && act.getCurrentFocus()!=null){
            if (act.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * //此方法，如果显示则隐藏，如果隐藏则显示
     * @param act   activity
     */
    public static void toggleInputMode(Activity act) {
        InputMethodManager imm = (InputMethodManager)act.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm != null && imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}