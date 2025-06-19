package com.sobot.online.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.api.ZhiChiOnlineApiFactory;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.ZhiChiOnlineApi;
import com.sobot.onlinecommon.model.OfflineMsgModel;
import com.sobot.onlinecommon.model.OnlineCommonModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;

/**
 * 聊天界面底部  显示用户状态的view
 */
public class OfflineStateView extends LinearLayout implements View.OnClickListener {
    public static final int showinputbox = 1;//显示输入框
    public static final int showNetError = 2;//显示输入框

    private View view = null;
    private TextView tv_main_content;
    private TextView tv_invite;
    private ProgressBar pb_progress;
    private OfflineStateViewListener listener;
    private String mUid;
    private String mAname;
    private String mUstatus;//当前用户的状态

    public ZhiChiOnlineApi zhiChiApi;

    public OfflineStateView(Context context) {
        this(context, null);
    }

    public OfflineStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfflineStateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (zhiChiApi == null) {
            synchronized (SobotOnlineBaseActivity.class) {
                if (zhiChiApi == null) {
                    zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(getContext());
                }
            }
        }
        //atts 包括
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);
//        系统会在自定义属性前加上它所属的declare-styleable 的name_
        /*display_type = array.getInt(R.styleable.SettingItemView_display_type, 0);
        left_text = array.getString(R.styleable.SettingItemView_left_text);
        mid_text = array.getString(R.styleable.SettingItemView_mid_text);
        mid_hint = array.getString(R.styleable.SettingItemView_mid_hint);
        maxLength = array.getInteger(R.styleable.SettingItemView_maxLength, 30);
        default_str = array.getString(R.styleable.SettingItemView_default_str);
        hintColor = array.getColor(R.styleable.SettingItemView_hintColor, Color.parseColor("#000000"));
        isShowArrow = array.getBoolean(R.styleable.SettingItemView_isShowArrow, false);
        value = array.getString(R.styleable.SettingItemView_value);
        enabled = array.getBoolean(R.styleable.SettingItemView_enable,true);
        left_text_with = array.getDimension(R.styleable.SettingItemView_left_text_width,
                getResources().getDimensionPixelSize(R.dimen.settingItemView_left_text_width));
        isInputInt = array.getBoolean(R.styleable.SettingItemView_isInputInt, false);
        if (TextUtils.isEmpty(value)) {
            value = "-1";
        }*/
        array.recycle();//回收
        initView();
    }

    private void initView() {

        view = View.inflate(getContext(), R.layout.widget_offline_state, null);
        if (view != null) {
            tv_main_content = (TextView) view.findViewById(R.id.tv_main_content);
            tv_invite = (TextView) view.findViewById(R.id.tv_invite);
            pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
            tv_invite.setOnClickListener(this);
            setGravity(Gravity.CENTER);
            this.addView(view);
        }
    }

    /**
     * 设置view 的类型
     *
     * @param uid
     * @param data
     * @param listener
     */
    public void setViewType(String uid, OfflineMsgModel data, OfflineStateViewListener listener) {
        mUid = uid;
        mAname = data.getAname();
        this.listener = listener;
        mUstatus = data.getUstatus();
        setViewType(data.getStatus());
    }

    private void setViewType(String status) {
        tv_invite.setVisibility(View.GONE);
        switch (status) {
            case SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_ROBOT:
                //用户正在和机器人会话
                tv_main_content.setText(getContext().getString(R.string.online_user_chat_rebot));
                break;
            case SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_CUSTOMER:
                //用户正在和XX会话
                if (TextUtils.isEmpty(mAname)) {
                    tv_main_content.setText(getContext().getString(R.string.online_user_and_kefu) + "【" + mAname + "】" + getContext().getString(R.string.online_chat));
                } else {
                    tv_main_content.setText(getContext().getString(R.string.online_user_and_kefu_chat));
                }
                break;
            case SobotSocketConstant.TYPE_OFFLINEMSG_LINEUP:
                //用户正在排队
                tv_main_content.setText(getContext().getString(R.string.online_user_on_line));
                tv_invite.setVisibility(View.VISIBLE);
                break;
            case SobotSocketConstant.TYPE_OFFLINEMSG_VISITOR:
                //访客
                tv_main_content.setText(getContext().getString(R.string.online_user_see_webpage));
                tv_invite.setVisibility(View.VISIBLE);
                break;
            case SobotSocketConstant.TYPE_OFFLINEMSG_WECHAT_USER_OUTTIME:
                //微信超时
                tv_main_content.setText(getContext().getString(R.string.online_user_leave_gongzhonghao));
                break;
            case SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_INPUTBOX:
                //显示输入框
                listener.onOfflineStateCallBack(showinputbox);
                break;
            case SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_BLACK:
                tv_main_content.setText(getContext().getString(R.string.online_lahei_user_cant_chat));
                break;
            default:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_invite) {
            showProgress();
            inviteUser();
        }
    }

    private void inviteUser() {
        zhiChiApi.invite("", mUid, new SobotResultCallBack<OnlineCommonModel>() {
            @Override
            public void onSuccess(OnlineCommonModel data) {
                hideProgress();
                SobotOnlineLogUtils.i("邀请成功----status：" + data.getStatus());
                switch (Integer.parseInt(data.getStatus())) {
                    case 0:
                        //正在排队中

                        break;
                    case 1:
                        //已邀请
                        setViewType(SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_INPUTBOX);
                        break;
                    case 2:
                        //已被他人邀请
                        setViewType(SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_CUSTOMER);
                        break;
                    case 3:
                        //用户已经离线
                        setViewType(SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_INPUTBOX);
                        if (SobotSocketConstant.TYPE_OFFLINEMSG_LINEUP.equals(mUstatus)) {
                            SobotToastUtil.showCustomToast(getContext(), "用户已离线，您发送的消息将作为离线消息推送给用户");
                        }

                        break;
                    default:

                        break;
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                hideProgress();
                if (listener != null) {
                    listener.onOfflineStateCallBack(showNetError);
                }
            }
        });
    }

    private void showProgress() {
        pb_progress.setVisibility(View.VISIBLE);
        tv_invite.setVisibility(View.GONE);
    }

    private void hideProgress() {
        pb_progress.setVisibility(View.GONE);
        tv_invite.setVisibility(View.VISIBLE);
    }

    public interface OfflineStateViewListener {
        void onOfflineStateCallBack(int event);
    }


}
