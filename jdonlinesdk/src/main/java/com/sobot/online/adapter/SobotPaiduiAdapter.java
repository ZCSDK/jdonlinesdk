package com.sobot.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.util.SobotChatUtils;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.online.weight.recyclerview.adapter.HelperStateRecyclerViewAdapter;
import com.sobot.onlinecommon.model.QueueUserModel;
import com.sobot.pictureframe.SobotBitmapUtil;

//排队用户列表 adapter
public class SobotPaiduiAdapter extends HelperStateRecyclerViewAdapter<QueueUserModel.QueueUser> {
    private Context mContext;
    private OnInviteListener mOnInviteListener;

    public SobotPaiduiAdapter(Context context, OnInviteListener onInviteListener) {
        super(context, R.layout.adapter_item_paidui);
        mContext = context;
        mOnInviteListener = onInviteListener;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, final QueueUserModel.QueueUser item) {
        TextView nikeTV = viewHolder.getView(R.id.tv_paidui_user_nike);
        nikeTV.setText(item.getUname());
        TextView onlyNikeTV = viewHolder.getView(R.id.tv_paidui_user_only_nike);
        onlyNikeTV.setText(item.getUname());
        TextView groupTV = viewHolder.getView(R.id.tv_paidui_user_group);
        if (TextUtils.isEmpty(item.getGroupName())) {
            groupTV.setVisibility(View.GONE);
            nikeTV.setVisibility(View.GONE);
            onlyNikeTV.setVisibility(View.VISIBLE);
        } else {
            groupTV.setVisibility(View.VISIBLE);
            nikeTV.setVisibility(View.VISIBLE);
            onlyNikeTV.setVisibility(View.GONE);
            groupTV.setText(item.getGroupName());
        }
        if (!TextUtils.isEmpty(item.getRemainTime())) {
            viewHolder.setText(R.id.tv_paidui_remain_time, item.getRemainTime());
        }
        //头像
        ImageView head_avator = viewHolder.getView(R.id.srcv_paidui_user_avatar);
        if (head_avator != null) {
            setUserHead(item, head_avator);
        }
        viewHolder.setOnClickListener(R.id.tv_paidui_invite, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnInviteListener != null) {
                    mOnInviteListener.onInvite(item);
                }
            }
        });
    }


    @Override
    public View getEmptyView(ViewGroup parent) {
        View view = mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
        TextView tv_empty_view = view.findViewById(R.id.tv_empty_view);
        tv_empty_view.setText(mContext.getResources().getString(R.string.sobot_online_no_paidui));
        return view;
    }

    @Override
    public View getErrorView(ViewGroup parent) {
        return mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
    }

    @Override
    public View getLoadingView(ViewGroup parent) {
        return mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
    }


    private void setUserHead(QueueUserModel.QueueUser model, ImageView head_avator) {
        int source = model.getSource();
        SobotBitmapUtil.display(mContext,  SobotChatUtils.getUserAvatorWithSource(source, true), head_avator);
    }

    //排队用户邀请监听
    public interface OnInviteListener {
        void onInvite(QueueUserModel.QueueUser queueUser);
    }
}
