package com.sobot.online.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.online.weight.recyclerview.adapter.HelperStateRecyclerViewAdapter;
import com.sobot.onlinecommon.model.OnlineCustomPopModel;

//带搜索选择弹窗  通用 adapter
public class SobotPopCommonAdapter extends HelperStateRecyclerViewAdapter<OnlineCustomPopModel> {
    private Context mContext;
    private int selectPosition = -1;//选中的索引，默认-1 没有选中的

    public SobotPopCommonAdapter(Context context) {
        super(context, R.layout.adapter_common_string_item);
        mContext = context;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, OnlineCustomPopModel item) {
        viewHolder.setText(R.id.tv_common_pop_item_text, item.getsValue());
        ImageView iv_common_pop_item_select = viewHolder.getView(R.id.iv_common_pop_item_select);
        TextView textView= viewHolder.getView(R.id.tv_common_pop_item_text);
        if (selectPosition == position) {
            textView.setTextColor(ContextCompat.getColor(mContext,R.color.sobot_online_common_gray1));
            iv_common_pop_item_select.setVisibility(View.VISIBLE);
        } else {
            textView.setTextColor(ContextCompat.getColor(mContext,R.color.sobot_online_common_gray2));
            iv_common_pop_item_select.setVisibility(View.GONE);
        }
    }


    @Override
    public View getEmptyView(ViewGroup parent) {
        return mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
    }

    @Override
    public View getErrorView(ViewGroup parent) {
        return mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
    }

    @Override
    public View getLoadingView(ViewGroup parent) {
        return mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
