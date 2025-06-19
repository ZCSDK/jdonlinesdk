package com.sobot.online.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.weight.SobotCustomPopWindow;
import com.sobot.onlinecommon.utils.SobotSizeUtils;


/**
 * 消息撤回  只有    撤回  按钮
 * Created by Administrator on 2018/6/6.
 */

public class SobotOnlyRetractDialog implements View.OnClickListener {

    private View mContentView;
    private SobotCustomPopWindow popWindow;
    private RetractListener mRetractListener;
    private Context mContext;
    private int mShowMode;
    private boolean mIsRight = false;//右侧需要向上偏移
    private TextView tv_revoke_txt_only;
    private TextView tv_copy_txt;
    private TextView tv_smart_reply;
    private TextView tv_retract_txt;

    public void setRetractListener(RetractListener mRetractListener) {
        this.mRetractListener = mRetractListener;
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    @Override
    public void onClick(View v) {
        int btnType = -1;
        if (v == tv_revoke_txt_only) {
            btnType = 0;
        }
        if (v == tv_copy_txt) {
            btnType = 1;
        }
        if (v == tv_smart_reply) {
            btnType = 2;
        }
        if (v == tv_retract_txt) {
            btnType = 3;
        }
        if (mRetractListener != null) {
            mRetractListener.onRetractClickL(v, btnType);
        }
        dissmissPopWindow();
    }

    public interface RetractListener {
        void onRetractClickL(View v, int whichBtn);
    }

    public static SobotOnlyRetractDialog newInstance(Context context, int showMode, boolean isRight) {
        return new SobotOnlyRetractDialog(context, showMode, isRight);
    }

    private SobotOnlyRetractDialog(Context context, int showMode, boolean isRight) {
        mShowMode = showMode;
        mContext = context;
        mIsRight = isRight;
        if (1 == showMode) {
            mContentView = View.inflate(context, R.layout.pop_chat_room_long_press_only_retract_btn, null);
        } else if (2 == showMode) {
            mContentView = View.inflate(context, R.layout.pop_chat_room_long_press, null);
        } else {
            mContentView = View.inflate(context, R.layout.pop_chat_room_long_press_only_copy_btn, null);
        }


        popWindow = new SobotCustomPopWindow.PopupWindowBuilder(context)
                .setView(mContentView)
                .enableBackgroundDark(false)
//                .size(mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight())
                .setBgDarkAlpha(0.5f)
                .create();
        mContentView.measure(makeDropDownMeasureSpec(popWindow.getWidth()),
                makeDropDownMeasureSpec(popWindow.getHeight()));
        initView(context);
    }

    private void initView(final Context context) {
        tv_revoke_txt_only = mContentView.findViewById(R.id.tv_revoke_txt_only);
        if (tv_revoke_txt_only != null) {
            tv_revoke_txt_only.setOnClickListener(this);
        }

        tv_copy_txt = mContentView.findViewById(R.id.tv_copy_txt);
        if (tv_copy_txt != null) {
            tv_copy_txt.setOnClickListener(this);
        }

        tv_smart_reply = mContentView.findViewById(R.id.tv_smart_reply);
        if (tv_smart_reply != null) {
            tv_smart_reply.setOnClickListener(this);
        }

        tv_retract_txt = mContentView.findViewById(R.id.tv_retract_txt);
        if (tv_retract_txt != null) {
            if (mShowMode == 2) {
                tv_retract_txt.setVisibility(View.VISIBLE);
            } else {
                tv_retract_txt.setVisibility(View.GONE);
            }
            tv_retract_txt.setOnClickListener(this);
        }
    }

    public void showPopWindow(final View view) {
        int tempHeight = 0;
        if (mIsRight) {
            tempHeight = SobotSizeUtils.dp2px(10);
        }
        if (popWindow != null) {
            int popupWidth = popWindow.getPopupWindow().getContentView().getMeasuredWidth();
            int popupHeight = popWindow.getPopupWindow().getContentView().getMeasuredHeight();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            popWindow.showAtLocation(view, Gravity.NO_GRAVITY, (location[0] + view.getWidth() / 2) - popupWidth / 2,
                    location[1] - popupHeight - tempHeight);

        }
    }

    private void dissmissPopWindow() {
        if (popWindow != null) {
            popWindow.dissmiss();
        }
    }
}