package com.sobot.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.online.weight.recyclerview.adapter.HelperStateRecyclerViewAdapter;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.OnLineServiceModel;
import com.sobot.pictureframe.SobotBitmapUtil;

//转接客服列表 adapter
public class SobotTransferKefuAdapter extends HelperStateRecyclerViewAdapter<OnLineServiceModel> {
    private Context mContext;
    private OnTransferKefuListener mTransferKefuListener;

    public SobotTransferKefuAdapter(Context context, OnTransferKefuListener transferKefuListener) {
        super(context, R.layout.adapter_item_transfer);
        mContext = context;
        mTransferKefuListener = transferKefuListener;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, final int position, final OnLineServiceModel item) {
        viewHolder.setText(R.id.tv_transfer_name, item.getUname());
        viewHolder.setText(R.id.tv_transfer_kefu_info, item.getCount() + "/" + item.getMaxcount());
        //头像
        ImageView head_avator = viewHolder.getView(R.id.srcv_transfer_avatar);
        if (head_avator != null) {
            SobotBitmapUtil.display(mContext, item.getFace(), head_avator);
        }
        viewHolder.setOnClickListener(R.id.tv_transfer_invite, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTransferKefuListener != null) {
                    mTransferKefuListener.onTransferKefu(item, position);
                }
            }
        });
        //客服在线状态
        ImageView sobot_online_kefu_status_iv = viewHolder.getView(R.id.sobot_online_kefu_status_iv);

        if (1 == item.getStatus() || -1 == item.getStatus()) {
            if (OnlineConstant.ADMIN_STATUS_ONLINE == item.getStatus()) {
                sobot_online_kefu_status_iv.setBackgroundResource(R.drawable.sobot_status_green);
            }
        } else if (0 == item.getStatus()) {
            if (OnlineConstant.ADMIN_STATUS_ONLINE == item.getStatus()) {
                sobot_online_kefu_status_iv.setBackgroundResource(R.drawable.sobot_status_gray);
            }
        } else if (2 == item.getStatus()) {
            int currentStatus = TextUtils.isEmpty(item.getStatusCode()) ? -10 : Integer.parseInt(item.getStatusCode());
            if (currentStatus == -10) {
                sobot_online_kefu_status_iv.setVisibility(View.GONE);
            } else {
                if (currentStatus > OnlineConstant.ADMIN_STATUS_BUSY) {
                    sobot_online_kefu_status_iv.setBackgroundResource(R.drawable.sobot_status_custom);
                } else {
                    sobot_online_kefu_status_iv.setBackgroundResource(R.drawable.sobot_status_red);
                }
            }
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

    //转接客服点击监听
    public interface OnTransferKefuListener {
        void onTransferKefu(OnLineServiceModel onLineServiceModel, int position);
    }
}
