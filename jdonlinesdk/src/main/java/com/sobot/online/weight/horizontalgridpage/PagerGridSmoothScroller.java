package com.sobot.online.weight.horizontalgridpage;

import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;


public class PagerGridSmoothScroller extends LinearSmoothScroller {
    private String TAG="PagerGridSmoothScroller";
    private RecyclerView mRecyclerView;

    public PagerGridSmoothScroller(@NonNull RecyclerView recyclerView) {
        super(recyclerView.getContext());
        mRecyclerView = recyclerView;
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        RecyclerView.LayoutManager manager = mRecyclerView.getLayoutManager();
        if (null == manager) return;
        if (manager instanceof PagerGridLayoutManager) {
            PagerGridLayoutManager layoutManager = (PagerGridLayoutManager) manager;
            int pos = mRecyclerView.getChildAdapterPosition(targetView);
            int[] snapDistances = layoutManager.getSnapOffset(pos);
            final int dx = snapDistances[0];
            final int dy = snapDistances[1];
            SobotOnlineLogUtils.i("dx = " + dx);
            SobotOnlineLogUtils.i("dy = " + dy);
            final int time = calculateTimeForScrolling(Math.max(Math.abs(dx), Math.abs(dy)));
            if (time > 0) {
                action.update(dx, dy, time, mDecelerateInterpolator);
            }
        }
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return PagerConfig.getMillisecondsPreInch() / displayMetrics.densityDpi;
    }
}
