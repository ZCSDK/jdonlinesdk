package com.sobot.online.weight.recyclerview.manager;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.online.weight.recyclerview.adapter.HelperStateRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.group.GroupedStateRecyclerViewAdapter;

/**
 * <p>描述：自定义GridLayoutManager</p>
 * 主要用于，如果你是GridLayoutManager布局有3列，但是要显示空页面的时候需要1列，也就是整个一个地方都是空白<br/>
 */
public class StateGridLayoutManager extends GridLayoutManager {
    private static final int DEFAULT_SPAN_SIZE = 1;
    private int mSpanCount;

    public StateGridLayoutManager(Context context, int spanCount) {
        super(context, DEFAULT_SPAN_SIZE);
        mSpanCount = spanCount;
    }

    public StateGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, DEFAULT_SPAN_SIZE, orientation, reverseLayout);
        mSpanCount = spanCount;
    }

    @Override
    public void onItemsChanged(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        boolean adapter_state = false;
        if (adapter != null && (adapter instanceof HelperStateRecyclerViewAdapter || adapter instanceof GroupedStateRecyclerViewAdapter)) {
            HelperStateRecyclerViewAdapter stateAdapter = (HelperStateRecyclerViewAdapter) adapter;
            int state = stateAdapter.getState();
            if (state == HelperStateRecyclerViewAdapter.STATE_NORMAL) {
                adapter_state = true;
            }
        }
        int span = getItemCount() == DEFAULT_SPAN_SIZE ? (adapter_state ? mSpanCount : DEFAULT_SPAN_SIZE) : mSpanCount;
        setSpanCount(span);
        super.onItemsChanged(recyclerView);
    }
}
