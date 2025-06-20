package com.sobot.online.dialog.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sobot.online.weight.kpswitch.util.KeyboardUtil;
import com.sobot.onlinecommon.utils.SobotResourceUtils;


public abstract class SobotActionSheet extends Dialog {

    protected DialogOnClickListener listener;
    private final int screenHeight;
    protected View sobot_container;

    public SobotActionSheet(Activity context) {
        // 给Dialog的Window设置样式
        this(context, SobotResourceUtils.getIdByName(context, "style", "sobot_base_dialog_style"));

    }

    public SobotActionSheet(Activity context,  int themeResId) {
        // 给Dialog的Window设置样式

        super(context, themeResId);

        // 修改Dialog(Window)的弹出位置
        screenHeight = getScreenHeight(context);
        Window window = getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        if(getWindow() != null){
            View view = getWindow().getDecorView();
            view.getWindowVisibleDisplayFrame(rect);
            lay.width = dm.widthPixels;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - (sobot_container !=null ?sobot_container.getHeight():screenHeight*0.7) - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(SobotResourceUtils.getIdByName(getContext(), "layout",
                getLayoutStrName()));
        initView();
        sobot_container = getDialogContainer();
        sobot_container.measure(0,0);
        initData();
    }

    /**
     * 获取dialog布局
     * @return
     */
    protected abstract String getLayoutStrName();


    /**
     * 获取布局容器的高度
     * @return
     */
    protected abstract View getDialogContainer();

    protected abstract void initData();

    protected abstract void initView();

    public void setOnClickListener(DialogOnClickListener listener) {
        this.listener = listener;
    }

    public interface DialogOnClickListener {
        void onSure();
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public int getResId(String name) {
        return SobotResourceUtils.getIdByName(getContext(), "id", name);
    }

    public int getResDrawableId(String name) {
        return SobotResourceUtils.getIdByName(getContext(), "drawable", name);
    }

    public int getResLayoutId(String name) {
        return SobotResourceUtils.getIdByName(getContext(), "layout", name);
    }

    public int getResStringId(String name) {
        return SobotResourceUtils.getIdByName(getContext(), "string", name);
    }

    public String getResString(String name){
        return  SobotResourceUtils.getResString(getContext(),name);
//        return getContext().getResources().getString(getResStringId(name));
    }

    @Override
    public void dismiss() {
        KeyboardUtil.hideKeyboard(getCurrentFocus());
        super.dismiss();
    }

}