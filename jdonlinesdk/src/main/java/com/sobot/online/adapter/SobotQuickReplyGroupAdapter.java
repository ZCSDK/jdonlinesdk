package com.sobot.online.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.online.weight.recyclerview.adapter.HelperStateRecyclerViewAdapter;
import com.sobot.onlinecommon.model.ChatReplyGroupInfoModel;

//快捷回复 - 分组 adapter
public class SobotQuickReplyGroupAdapter extends HelperStateRecyclerViewAdapter<ChatReplyGroupInfoModel> {
    private Context mContext;

    public SobotQuickReplyGroupAdapter(Context context) {
        super(context, R.layout.adapter_quick_reply_group_layout);
        mContext = context;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, ChatReplyGroupInfoModel item) {
        viewHolder.setText(R.id.tv_online_content, item.getGroupName() + " (" + (item.getChildren().size() == 0 ? item.getReplyCount() : item.getChildren().size()) + ")");
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

}
