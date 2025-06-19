package com.sobot.online.weight.recyclerview.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.onlinecommon.utils.SobotSizeUtils;

/**
 * <p>描述：库中默认的加载更多实现</p>
 */
public class LoadingMoreFooter extends BaseMoreFooter {
    private SimpleViewSwitcher progressCon;
    private TextView mText;

    public LoadingMoreFooter(Context context) {
        super(context);
    }

    public LoadingMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView() {
        super.initView();
        progressCon = new SimpleViewSwitcher(getContext());
        progressCon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        try {
            ProgressBar pb = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
            pb.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar_circle_loading));
            progressCon.setView(pb);
            addView(progressCon);
        } catch (Exception e) {
        }
        mText = new TextView(getContext());
        mText.setText(R.string.Sobot_app_listview_loading);
        mText.setTextColor(getContext().getResources().getColor(R.color.sobot_common_red));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(SobotSizeUtils.dp2px(10), 0, 0, 0);

        mText.setLayoutParams(layoutParams);
        addView(mText);
    }

    @Override
    public void setProgressStyle(int style) {
        try {
            ProgressBar pb = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
            pb.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar_circle_loading));
            progressCon.setView(pb);
        } catch (Exception e) {
        }
    }


    @Override
    public void setState(int state) {
        super.setState(state);
        switch (state) {
            case STATE_LOADING:
                progressCon.setVisibility(View.VISIBLE);
                mText.setText(loadingHint);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                mText.setText(loadingDoneHint);
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                mText.setText(noMoreHint);
                progressCon.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }
}
