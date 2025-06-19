package com.sobot.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.api.MemoryCacheManager;
import com.sobot.online.util.SobotChatUtils;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.online.weight.recyclerview.adapter.HelperStateRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SwipeMenuLayout;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.ChatMessageObjectModel;
import com.sobot.onlinecommon.model.ChatMessageWhatsAppModel;
import com.sobot.onlinecommon.socket.MsgCacheManager;
import com.sobot.onlinecommon.socket.module.ChatMessageMsgModel;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.util.Collections;
import java.util.Comparator;

//会话中-用户列表 adapter
public class SobotReceptionAdapter extends HelperStateRecyclerViewAdapter<PushMessageModel> {
    private Context mContext;
    private OnReceptionSwipeListener mReceptionSwipeListener;
    private boolean canSummary;//是否打开服务评价开关
    private boolean mSwipeEnable;//是否能策划

    public SobotReceptionAdapter(Context context, boolean swipeEnable, OnReceptionSwipeListener onReceptionSwipeListener) {
        super(context, R.layout.adapter_reception_layout);
        mContext = context;
        mSwipeEnable = swipeEnable;
        mReceptionSwipeListener = onReceptionSwipeListener;
        this.canSummary = SobotSPUtils.getInstance().getBoolean(OnlineConstant
                .OPEN_SUMMARY_FLAG, false);
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, final int position, final PushMessageModel item) {
        if (item != null) {
            final SwipeMenuLayout superSwipeMenuLayout = (SwipeMenuLayout) viewHolder.itemView;
            superSwipeMenuLayout.setSwipeEnable(mSwipeEnable);   //设置是否可以侧滑
            if (canSummary) {
                TextView tv_reception_menu_summary = viewHolder.getView(R.id.tv_reception_menu_summary);
                tv_reception_menu_summary.setVisibility(View.VISIBLE);
                //若服务总结已总结，则显示 已总结，若服务总结未总结，则显示 服务总结
                if (MemoryCacheManager.getInstance().hasSummaryCid(item.getCid())) {
                    tv_reception_menu_summary.setText(mContext.getResources().getString(R.string.sobot_online_has_zongjie));
                } else {
                    tv_reception_menu_summary.setText(mContext.getResources().getString(R.string.sobot_online_fuwuzongjie));
                }
            } else {
                viewHolder.getView(R.id.tv_reception_menu_summary).setVisibility(View.GONE);

            }
            viewHolder.setOnClickListener(R.id.tv_reception_menu_close, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReceptionSwipeListener != null) {
                        mReceptionSwipeListener.onClose(item, position);
                    }
                }
            }).setOnClickListener(R.id.tv_reception_menu_star_target, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReceptionSwipeListener != null) {
                        mReceptionSwipeListener.onMark(item, position);
                    }
                }
            }).setOnClickListener(R.id.tv_reception_menu_summary, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReceptionSwipeListener != null) {
                        //未总结的会话
                        if (canSummary && !MemoryCacheManager.getInstance().hasSummaryCid(item.getCid())) {
                            mReceptionSwipeListener.onSummary(item, position);
                        }
                    }
                }
            });
            ImageView iv_reception_user_mark = viewHolder.getView(R.id.iv_reception_user_mark);
            if (item.getMarkStatus() == 1) {
                iv_reception_user_mark.setVisibility(View.VISIBLE);
                viewHolder.setText(R.id.tv_reception_menu_star_target, mContext.getResources().getString(R.string.online_mark_cancle));
            } else {
                viewHolder.setText(R.id.tv_reception_menu_star_target, mContext.getResources().getString(R.string.online_mark));
                iv_reception_user_mark.setVisibility(View.GONE);
            }
            viewHolder.setText(R.id.tv_reception_user_nike, item.getUname());
            String lastMsg = getChatHintStr(mContext, item);
            if (TextUtils.isEmpty(lastMsg)) {
                lastMsg = TextUtils.isEmpty(item.getLastMsg()) ? mContext.getResources().getString(R.string.online_click_look_msg) : item.getLastMsg();
            }
            TextView tv_reception_last_msg_content = viewHolder.getView(R.id.tv_reception_last_msg_content);
            tv_reception_last_msg_content.setText(lastMsg);
//            HtmlTools.getInstance(mContext).setRichText(tv_reception_last_msg_content, lastMsg, R.color.sobot_online_color);
            item.setLastMsg(TextUtils.isEmpty(lastMsg) ? item.getLastMsg() : lastMsg);
            if (item.getT() > 0) {
                viewHolder.getView(R.id.tv_reception_last_msg_time).setVisibility(View.VISIBLE);
                viewHolder.setText(R.id.tv_reception_last_msg_time, SobotTimeUtils.getDiffTime(item.getT(), mContext.getResources().getString(R.string.onnline_time_tianqian)));
            } else {
                viewHolder.getView(R.id.tv_reception_last_msg_time).setVisibility(View.GONE);
            }
            //头像
            ImageView head_avator = viewHolder.getView(R.id.srcv_reception_user_avatar);
            if (head_avator != null) {
                setUserHead(item, mContext, head_avator);
            }
            //未读消息数
            TextView unread_msg_number = viewHolder.getView(R.id.tv_reception_noread_msg_count);
            item.setUnReadCount(MsgCacheManager.getInstance().getUnReadMsgCount(item.getUid()));
            if (unread_msg_number != null) {
                if (item.getUnReadCount() > 0) {
                    if (item.getUnReadCount() <= 99) {
                        unread_msg_number.setText(item.getUnReadCount() + "");
                    } else {
                        unread_msg_number.setText("99+");
                    }
                    unread_msg_number.setVisibility(View.VISIBLE);
                } else {
                    unread_msg_number.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    public View getEmptyView(ViewGroup parent) {
        View view = mLInflater.inflate(R.layout.sobot_layout_empty, parent, false);
        TextView tv_empty_view = view.findViewById(R.id.tv_empty_view);
        tv_empty_view.setText(mContext.getResources().getString(R.string.online_no_user_coming));
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

    public void myNotifyDataSetChanged() {
        Collections.sort(getList(), new Comparator<PushMessageModel>() {
            @Override
            public int compare(PushMessageModel o1, PushMessageModel o2) {
                if (o1.isOnline() != o2.isOnline()) {
                    return (Integer.valueOf(o2.isOnline() ? 1 : 0)) - Integer.valueOf(o1.isOnline() ? 1 : 0);
                } else {
                    return Long.compare(o2.getT(), o1.getT());
                }
            }
        });
        notifyDataSetChanged();
    }

    /**
     * 根据消息的类型来获取消息的提示语
     *
     * @param model 传入的消息实体
     * @return
     */
    public static String getChatHintStr(Context context, PushMessageModel model) {

        if (model == null) {
            return "";
        }
        ChatMessageMsgModel messageMsgModel = model.getMessage();
        if (messageMsgModel != null && !TextUtils.isEmpty(messageMsgModel.getMsgType())) {
            String msgType = messageMsgModel.getMsgType();
            //msgType：文本,图片,音频,视频,文件,对象
            //msgType：0,1,2,3,4,5
            switch (msgType) {
                case "0":
                    return (String) messageMsgModel.getContent();
                case "1":
                    return "[" + context.getResources().getString(R.string.online_tupian_biaoshi) + "]";
                case "2":
                    return "[" + context.getResources().getString(R.string.online_yinpin_biaoshi) + "]";
                case "3":
                    return "[" + context.getResources().getString(R.string.online_shipin_biaoshi) + "]";
                case "4":
                    return "[" + context.getResources().getString(R.string.online_wenjian_biaoshi) + "]";
                case "5":
                    if (messageMsgModel.getContent() != null) {
                        ChatMessageObjectModel messageObjectModel = null;
                        String temp = SobotGsonUtil.gsonString(model.getMessage().getContent());
                        if (!TextUtils.isEmpty(temp) && temp.contains("msg")) {
                            messageObjectModel = SobotGsonUtil.gsonToBean(temp, ChatMessageObjectModel.class);
                            if (messageObjectModel != null) {
                                //0-富文本 1-多伦会话 2-位置 3-小卡片 4-订单卡片
                                switch (messageObjectModel.getType()) {
                                    case 0:
                                        return "[" + context.getResources().getString(R.string.online_fuwenben_biaoshi) + "]";
                                    case 1:
                                        return context.getResources().getString(R.string.online_a_new_message);
                                    case 2:
                                        return "[" + context.getResources().getString(R.string.online_weizhi_biaoshi) + "]";
                                    case 3:
                                        return "[" + context.getResources().getString(R.string.online_xiaokapian_biaoshi) + "]";
                                    case 4:
                                        return "[" + context.getResources().getString(R.string.online_ordercard_biaoshi) + "]";
                                    case 20:
                                        //whatsapp 模板消息
                                        ChatMessageWhatsAppModel messageWhatsAppModel = null;
                                        String msgtemp = SobotGsonUtil.gsonString(messageObjectModel.getMsg());
                                        if (!TextUtils.isEmpty(msgtemp)) {
                                            messageWhatsAppModel = SobotGsonUtil.gsonToBean(msgtemp, ChatMessageWhatsAppModel.class);
                                        }
                                        if (messageWhatsAppModel != null) {
                                            return messageWhatsAppModel.getBodyContent();
                                        } else {
                                            return "";
                                        }
                                }
                            }
                        }
                    }
                    break;

            }
        }
        return "";
    }

    private void setUserHead(PushMessageModel model, Context context, ImageView head_avator) {
        int source = model.getUsource();
        boolean isOnline = model.isOnline();
        if (isOnline && !TextUtils.isEmpty(model.getFace())) {
            SobotBitmapUtil.display(mContext, model.getFace(), head_avator);
            return;
        }
        SobotBitmapUtil.display(mContext, SobotChatUtils.getUserAvatorWithSource(source, isOnline), head_avator);
    }

    public interface OnReceptionSwipeListener {
        //关闭，结束用户会话
        void onClose(PushMessageModel item, int position);

        //星标，关注用户
        void onMark(PushMessageModel item, int position);

        //填写服务总结
        void onSummary(PushMessageModel item, int position);
    }

    public boolean isCanSummary() {
        return canSummary;
    }

    public void setCanSummary(boolean canSummary) {
        this.canSummary = canSummary;
    }
}
