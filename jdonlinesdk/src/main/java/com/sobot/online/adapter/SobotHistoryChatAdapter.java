package com.sobot.online.adapter;

import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.SOBOT_CUSTOM_USER;

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
import com.sobot.online.weight.recyclerview.swipemenu.SwipeMenuLayout;
import com.sobot.onlinecommon.control.CustomerServiceInfoModel;
import com.sobot.onlinecommon.model.HistoryChatModel;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotStringUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;
import com.sobot.pictureframe.SobotBitmapUtil;

//历史会话列表 adapter
public class SobotHistoryChatAdapter extends HelperStateRecyclerViewAdapter<HistoryChatModel> {
    private Context mContext;

    public SobotHistoryChatAdapter(Context context) {
        super(context, R.layout.adapter_reception_layout);
        mContext = context;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, HistoryChatModel item) {
        final SwipeMenuLayout superSwipeMenuLayout = (SwipeMenuLayout) viewHolder.itemView;
        superSwipeMenuLayout.setSwipeEnable(false);   //设置是否可以侧滑
        viewHolder.setText(R.id.tv_reception_user_nike, item.getUname());
        TextView tv_reception_last_msg_content = viewHolder.getView(R.id.tv_reception_last_msg_content);
        CustomerServiceInfoModel admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
        if (admin != null && !SobotStringUtils.isEmpty(admin.getAid())) {
            if (admin.getAid().equals(item.getLastStaffId())) {
                tv_reception_last_msg_content.setText(TextUtils.isEmpty(item.getLastMsg()) ? mContext.getResources().getString(R.string.online_click_look_msg) : item.getLastMsg());
            }
        }

//        HtmlTools.getInstance(mContext).setRichText(tv_reception_last_msg_content, TextUtils.isEmpty(item.getLastMsg()) ? mContext.getResources().getString(R.string.online_click_look_msg) : item.getLastMsg(), SobotResourceUtils.getResColorId(mContext, "sobot_online_color"));
        viewHolder.setText(R.id.tv_reception_last_msg_time, SobotTimeUtils.getDiffTime(item.getConvEndDateTime(), mContext.getResources().getString(R.string.onnline_time_tianqian)));
        ImageView iv_reception_user_mark = viewHolder.getView(R.id.iv_reception_user_mark);
        if (item.getMarkStatus() == 1) {
            iv_reception_user_mark.setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.tv_reception_menu_star_target, mContext.getResources().getString(R.string.online_mark_cancle));
        } else {
            viewHolder.setText(R.id.tv_reception_menu_star_target, mContext.getResources().getString(R.string.online_mark));
            iv_reception_user_mark.setVisibility(View.GONE);
        }
        //头像
        ImageView head_avator = viewHolder.getView(R.id.srcv_reception_user_avatar);
        if (head_avator != null) {
            SobotBitmapUtil.display(mContext, SobotChatUtils.getUserAvatorWithSource(item.getSource(), true), head_avator);
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
}
