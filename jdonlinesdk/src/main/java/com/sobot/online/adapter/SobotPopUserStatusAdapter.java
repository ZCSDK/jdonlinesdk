package com.sobot.online.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.OnlineServiceStatus;

//客服在线状态弹窗 adapter
public class SobotPopUserStatusAdapter extends HelperRecyclerViewAdapter<OnlineServiceStatus> {
    private Context mContext;

    public SobotPopUserStatusAdapter(Context context) {
        super(context, R.layout.adapter_string_item);
        mContext = context;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, OnlineServiceStatus item) {
        viewHolder.setText(R.id.tv_common_pop_item_text, item.getDictName());
        ImageView ivStatus = viewHolder.getView(R.id.iv_common_pop_item_img);

        if (OnlineConstant.STATUS_ONLINE.equals(item.getDictValue())) {
            ivStatus.setBackgroundResource(R.drawable.sobot_status_green);
        } else if (OnlineConstant.STATUS_BUSY.equals(item.getDictValue())) {
            ivStatus.setBackgroundResource(R.drawable.sobot_status_red);
        } else if (OnlineConstant.STATUS_OFFLINE.equals(item.getDictValue())) {
            ivStatus.setBackgroundResource(R.drawable.sobot_status_gray);
        } else {
            ivStatus.setBackgroundResource(R.drawable.sobot_status_custom);
        }
    }

}
