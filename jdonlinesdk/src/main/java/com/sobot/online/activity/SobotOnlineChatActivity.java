package com.sobot.online.activity;

import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.MSG_TYPE_EVALUATE1;
import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.MSG_TYPE_SENSITIVE;
import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.SOBOT_CUSTOM_USER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.common.utils.SobotImageUtils;
import com.sobot.common.utils.SobotPathManager;
import com.sobot.network.http.callback.SobotFileResultCallBack;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.MessageType;
import com.sobot.online.adapter.SobotOnlineMsgAdapter;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.dialog.OnlineMsgPreviewDialog;
import com.sobot.online.dialog.OnlineReSendDialog;
import com.sobot.online.dialog.OnlineTipDialog;
import com.sobot.online.dialog.SobotDialogUtils;
import com.sobot.online.dialog.SobotOnlineCommonDialog;
import com.sobot.online.model.SobotTemplateName;
import com.sobot.online.util.SobotChatUtils;
import com.sobot.online.util.SobotOnlineImageUtils;
import com.sobot.online.util.voice.AudioPlayCallBack;
import com.sobot.online.util.voice.AudioPlayPresenter;
import com.sobot.online.util.voice.AudioTools;
import com.sobot.online.util.voice.ExtAudioRecorder;
import com.sobot.online.weight.OfflineStateView;
import com.sobot.online.weight.SobotAntoLineLayout;
import com.sobot.online.weight.SobotContainsEmojiEditText;
import com.sobot.online.weight.emoji.DisplayEmojiRules;
import com.sobot.online.weight.emoji.EmojiconNew;
import com.sobot.online.weight.emoji.SobotInputHelper;
import com.sobot.online.weight.kpswitch.CustomeChattingPanel;
import com.sobot.online.weight.kpswitch.util.KPSwitchConflictUtil;
import com.sobot.online.weight.kpswitch.util.KeyboardUtil;
import com.sobot.online.weight.kpswitch.view.CustomeViewFactory;
import com.sobot.online.weight.kpswitch.view.SobotChattingPanelEmoticonView;
import com.sobot.online.weight.kpswitch.view.SobotChattingPanelUploadView;
import com.sobot.online.weight.kpswitch.widget.KPSwitchPanelLinearLayout;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.SobotOnlineUrlApi;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.control.CustomerServiceInfoModel;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.ChatMessageAudioModel;
import com.sobot.onlinecommon.model.ChatMessageModel;
import com.sobot.onlinecommon.model.ChatMessageObjectModel;
import com.sobot.onlinecommon.model.ChatMessageRichTextModel;
import com.sobot.onlinecommon.model.ChatMessageVideoModel;
import com.sobot.onlinecommon.model.ChatMessageWhatsAppModel;
import com.sobot.onlinecommon.model.CidsModel;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.OfflineMsgModel;
import com.sobot.onlinecommon.model.OnlineCommonModel;
import com.sobot.onlinecommon.model.OnlineMsgModelResult;
import com.sobot.onlinecommon.model.SobotWhatsAppInfoModel;
import com.sobot.onlinecommon.socket.MsgCacheManager;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.channel.Const;
import com.sobot.onlinecommon.socket.channel.SobotTCPServer;
import com.sobot.onlinecommon.socket.module.ChatMessageMsgModel;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.ui.statusbar.SobotStatusBarCompat;
import com.sobot.onlinecommon.utils.SobotMD5Utils;
import com.sobot.onlinecommon.utils.SobotMediaFileUtils;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotResourceUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;
import com.sobot.onlinecommon.utils.SobotUtils;
import com.sobot.onlinecommon.utils.StServiceUtils;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.ui.SobotBaseConstant;
import com.sobot.widget.ui.permission.SobotPermissionListenerImpl;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @Description: 聊天主页面
 * @Author: znw
 * @CreateDate: 2020/08/28 10:05
 * @Version: 1.0
 */
public class SobotOnlineChatActivity extends SobotOnlineBaseActivity implements SobotRecyclerView.LoadingListener, View.OnClickListener
        , SobotChattingPanelEmoticonView.SobotEmoticonClickListener
        , SobotChattingPanelUploadView.SobotPlusClickListener, OfflineStateView.OfflineStateViewListener, SobotOnlineMsgAdapter.OnlineMsgCallBack {
    //头部返回按钮
    private TextView sobot_online_username_back_tv;
    private LinearLayout sobot_ll_bottom;//输入框的布局
    private LinearLayout sobot_ll_bottom_lock;//发送whatsapp消息时，底部布局
    private OfflineStateView rl_bottom_offline_state;//离线消息的view

    private LinearLayout ll_custom_menu;//底部快捷餐单控件
    private ImageView iv_online_user_pullblack;//右上角拉黑控件
    private ImageView iv_online_user_zhuanjie;//右上角转接控件
    private ImageView iv_online_user_biaoji;//右上标记控件
    private TextView tv_online_close_chat;//右上结束会话控件

    private ImageView iv_online_call_user;//右上角打电话
    private SobotOnlineCommonDialog closeDialog;//结束会话弹窗

    private LinearLayout ll_ordernum;//右上订单编号控件
    private TextView tv_ordernum;//右上订单编号控件
    //底部功能控件
    private TextView tv_sobot_chat_yaoping;//邀评
    private TextView tv_sobot_chat_quick_reply;//快捷回复
    private TextView tv_sobot_chat_intelligence_reply;//智能回复
    private TextView tv_sobot_chat_service_summary;//服务总结

    private TextView tv_sobot_chat_ask_for_location;//发送模板消息
    private TextView tv_sobot_chat_time_alert;//发送模板消息
    private TextView tv_sobot_chat_location_alert;//发送模板消息
    private TextView tv_sobot_chat_arrival_alert;//发送模板消息
    private TextView tv_sobot_chat_request_reply;//发送模板消息
    private TextView tv_sobot_chat_cod_confirm;//发送模板消息


    //消息列表控件
    private SobotRecyclerView srv_online_msg_list;
    private SobotOnlineMsgAdapter mMsgListAdapter;

    private KPSwitchPanelLinearLayout mPanelRoot; // 聊天下面的面板
    private ImageButton btn_emoticon_view; // 表情面板
    private Button sobot_btn_upload_view; // 显示底部菜单按钮
    private Button sobot_btn_send; // 发送消息按钮
    private SobotContainsEmojiEditText et_sendmessage;// 当前用户输入的信息
    //键盘监听
    private ViewTreeObserver.OnGlobalLayoutListener mKPSwitchListener;
    //键盘相关
    public int currentPanelId = 0;//切换聊天面板时 当前点击的按钮id 为了能切换到对应的view上

    private AudioPlayPresenter mAudioPlayPresenter = null;
    private AudioPlayCallBack mAudioPlayCallBack = null;

    List<ChatMessageModel> data = new ArrayList<>();

    private HistoryUserInfoModel userInfo;
    private String flag_from_page = "";
    private boolean hasSummary = false;//表示当前用户是否做过服务总结
    private String fromTab;//表示来自哪个页面 online   history
    private List<String> cids = new ArrayList<>();//cid的列表
    private int currentCidPosition = 0;//当前查询聊天记录所用的cid位置
    private boolean getCidsFinish = false;//表示查询cid的接口是否结束
    private boolean isInGethistory = false;//表示是否正在查询历史记录

    CustomerServiceInfoModel admin;//登录客服
    private int userSource = -1;//用户来源 0桌面1微信2app3微博4移动网站9企业微信，10微信小程序

    private SobotWhatsAppInfoModel whatsAppInfoModel;//whatsApp 模板消息  配置信息

    private ChatMessageWhatsAppModel location_confirm_noncod;//whatsApp模板消息 配置信息
    private ChatMessageWhatsAppModel location_confirm_cod;//whatsApp模板消息 配置信息

    private ChatMessageWhatsAppModel time_reminder;//time_alert模板消息
    private ChatMessageWhatsAppModel location_reminder;//location_alert模板消息
    private ChatMessageWhatsAppModel reach_reminder;//arrival_alert模板消息
    private ChatMessageWhatsAppModel request_reply;//request_reply模板消息
    private ChatMessageWhatsAppModel cod_confirm;//cod_confirm模板消息

    //语音相关的
    private ImageView btn_model_voice;// 语音模式
    private TextView voice_time_long;/*显示语音时长*/
    private LinearLayout voice_top_image;
    private ImageView image_endVoice;
    private ImageView mic_image;
    private ImageView mic_image_animate; // 图片的动画
    private ImageView recording_timeshort;// 语音太短的图片
    private TextView txt_speak_content; // 发送语音的文字
    private TextView recording_hint; // 上滑的显示文本；
    private LinearLayout btn_press_to_speak; // 说话view ;
    private LinearLayout recording_container;// 语音上滑的动画
    private AnimationDrawable animationDrawable;/* 语音的动画 */
    protected Timer voiceTimer;
    protected TimerTask voiceTimerTask;
    protected int voiceTimerLong = 0;
    protected String voiceTimeLongStr = "00:00";// 时间的定时的任务
    private int minRecordTime = 180;// 允许录音时间
    private int recordDownTime = minRecordTime - 10;// 允许录音时间 倒计时
    boolean isCutVoice;
    private String voiceMsgId = "";//  语音消息的Id
    private int currentVoiceLong = 0;
    private String mFileName = null;//音频文件
    private ExtAudioRecorder extAudioRecorder;
    // 听筒模式转换
    public AudioManager audioManager = null; // 声音管理器
    private AudioFocusRequest mFocusRequest;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private AudioAttributes mAttribute;


    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_chat_main;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle bundel = intent.getBundleExtra("bundle");
        if (bundel != null) {
            userInfo = (HistoryUserInfoModel) bundel.getSerializable("userInfo");
            whatsAppInfoModel = (SobotWhatsAppInfoModel) bundel.getSerializable("whatsAppInfoModel");
            flag_from_page = bundel.getString("flag");
            hasSummary = bundel.getBoolean("hasSummary", false);
            fromTab = bundel.getString("fromTab");
            if (userInfo != null) {
                MsgCacheManager.getInstance().delUnReadMsgCount(userInfo.getId());
                MsgCacheManager.getInstance().setUid(userInfo.getId());
            }
            if (mMsgListAdapter != null) {
                mMsgListAdapter.clear();
            }
            queryUserState();
            SobotOnlineLogUtils.i("flag_from_page=====" + flag_from_page);
        } else {
            return;
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void initView() {
        SobotStatusBarCompat.setStatusBarColor(getSobotActivity(), getResources().getColor(R.color.sobot_chat_status_bar_color));
        Bundle bundel = getIntent().getBundleExtra("bundle");
        if (bundel != null) {
            if (userInfo == null) {
                userInfo = (HistoryUserInfoModel) bundel.getSerializable("userInfo");
            }
            if (whatsAppInfoModel == null) {
                whatsAppInfoModel = (SobotWhatsAppInfoModel) bundel.getSerializable("whatsAppInfoModel");
            }
            flag_from_page = bundel.getString("flag");
            hasSummary = bundel.getBoolean("hasSummary", false);
            fromTab = bundel.getString("fromTab");
            if (userInfo != null) {
                MsgCacheManager.getInstance().delUnReadMsgCount(userInfo.getId());
                MsgCacheManager.getInstance().setUid(userInfo.getId());
            }
            SobotOnlineLogUtils.i("flag_from_page=====" + flag_from_page);
        } else {
            return;
        }

        //语音相关
        //语音相关
        btn_model_voice = findViewById(R.id.sobot_btn_model_voice);
        btn_press_to_speak = findViewById(R.id.sobot_btn_press_to_speak);
        // 开始语音的布局的信息
        voice_top_image = findViewById(R.id.sobot_voice_top_image);
        // 停止语音
        image_endVoice = findViewById(R.id.sobot_image_endVoice);
        // 动画的效果
        mic_image_animate = findViewById(R.id.sobot_mic_image_animate);
        // 时长的界面
        voice_time_long = findViewById(R.id.sobot_voiceTimeLong);
        txt_speak_content = findViewById(R.id.sobot_txt_speak_content);
        txt_speak_content.setText(R.string.sobot_press_say);
        recording_timeshort = findViewById(R.id.sobot_recording_timeshort);
        mic_image = findViewById(R.id.sobot_mic_image);
        recording_hint = findViewById(R.id.sobot_recording_hint);
        recording_container = findViewById(R.id.sobot_recording_container);
        btn_model_voice.setOnClickListener(this);
        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());


        admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
        sobot_online_username_back_tv = findViewById(R.id.sobot_online_username_back_tv);
        //头部左侧返回按钮点击返回
        if (sobot_online_username_back_tv != null) {
            sobot_online_username_back_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePanelAndKeyboard(mPanelRoot);
                    finish();
                }
            });
        }
        mPanelRoot = (KPSwitchPanelLinearLayout) findViewById(R.id.sobot_panel_root);
        sobot_ll_bottom = (LinearLayout) findViewById(R.id.sobot_ll_bottom);
        sobot_ll_bottom_lock = findViewById(R.id.sobot_ll_bottom_lock);
        ll_custom_menu = findViewById(R.id.sobot_custom_menu);
        rl_bottom_offline_state = (OfflineStateView) findViewById(R.id.rl_bottom_offline_state);
        sobot_btn_upload_view = (Button) findViewById(R.id.sobot_btn_upload_view);
        et_sendmessage = (SobotContainsEmojiEditText) findViewById(R.id.sobot_et_sendmessage);
        btn_emoticon_view = (ImageButton) findViewById(R.id.sobot_btn_emoticon_view);
        sobot_btn_send = (Button) findViewById(R.id.sobot_btn_send);
        sobot_btn_send.setText(getString(R.string.sobot_online_send));
        iv_online_user_pullblack = (ImageView) findViewById(R.id.iv_online_user_pullblack);
        iv_online_user_biaoji = (ImageView) findViewById(R.id.iv_online_user_biaoji);
        iv_online_user_zhuanjie = (ImageView) findViewById(R.id.iv_online_user_zhuanjie);
        tv_online_close_chat = findViewById(R.id.tv_online_close_chat);
        iv_online_call_user = (ImageView) findViewById(R.id.iv_online_call_user);
        ll_ordernum = findViewById(R.id.sobot_online_ordernum_ll);
        tv_ordernum = findViewById(R.id.sobot_online_ordernum_tv);

        int transferFunction = admin != null ? admin.getTransferFunction() : 1;
        if (transferFunction == 1) {
//            iv_online_user_zhuanjie.setVisibility(View.VISIBLE);
            iv_online_user_zhuanjie.setVisibility(View.GONE);
        } else {
            iv_online_user_zhuanjie.setVisibility(View.GONE);
        }

        tv_sobot_chat_yaoping = findViewById(R.id.tv_sobot_chat_yaoping);
        tv_sobot_chat_yaoping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanelAndKeyboard(mPanelRoot);
                if (userInfo != null) {
                    //是否黑名单 1 是 ，0 否
                    int isBlack = userInfo.getIsblack();
                    if (isBlack == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_already_lahei));
                        return;
                    }
                }
                if (isFromHistory()) {
                    SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_cannt_evaluate));
                } else {
                    //邀评
                    invateEvaluate();
                }
            }
        });
        tv_sobot_chat_ask_for_location = findViewById(R.id.tv_sobot_chat_ask_for_location);
        tv_sobot_chat_time_alert = findViewById(R.id.tv_sobot_chat_time_alert);
        tv_sobot_chat_location_alert = findViewById(R.id.tv_sobot_chat_location_alert);
        tv_sobot_chat_arrival_alert = findViewById(R.id.tv_sobot_chat_arrival_alert);
        tv_sobot_chat_request_reply = findViewById(R.id.tv_sobot_chat_request_reply);
        tv_sobot_chat_cod_confirm = findViewById(R.id.tv_sobot_chat_cod_confirm);

        tv_sobot_chat_quick_reply = findViewById(R.id.tv_sobot_chat_quick_reply);
        tv_sobot_chat_quick_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanelAndKeyboard(mPanelRoot);
                if (userInfo != null) {
                    //是否黑名单 1 是 ，0 否
                    int isBlack = userInfo.getIsblack();
                    if (isBlack == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_already_lahei));
                        return;
                    }
                }
                //进入快捷回复界面
                Intent intent = new Intent(getSobotActivity(), SobotQuickReplyActivity.class);
                startActivityForResult(intent, OnlineConstant.SOBOT_REQUEST_CODE_QUICK_REPLY);

            }
        });
        //未总结的会话才能跳转到服务总结页面
        tv_sobot_chat_service_summary = findViewById(R.id.tv_sobot_chat_service_summary);
        boolean canSummary = SobotSPUtils.getInstance().getBoolean(OnlineConstant
                .OPEN_SUMMARY_FLAG, false);
        if (canSummary) {
            tv_sobot_chat_service_summary.setVisibility(View.VISIBLE);
            if (hasSummary) {
                tv_sobot_chat_service_summary.setText(getString(R.string.sobot_online_has_zongjie));
                tv_sobot_chat_service_summary.setOnClickListener(null);
            } else {
                tv_sobot_chat_service_summary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userInfo != null) {
                            //是否黑名单 1 是 ，0 否
                            int isBlack = userInfo.getIsblack();
                            if (isBlack == 1) {
                                SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_already_lahei));
                                return;
                            }
                        }
                        hidePanelAndKeyboard(mPanelRoot);
                        //进入服务总结界面
                        Intent intent = new Intent(getSobotActivity(), SobotServiceSummaryActivity.class);
                        intent.putExtra("userInfo", userInfo);
                        intent.putExtra("cid", userInfo.getLastCid());
                        startActivityForResult(intent, OnlineConstant.SOBOT_REQUEST_CODE_SERVICE_SUMMARY);
                    }
                });
            }
        } else {
            tv_sobot_chat_service_summary.setVisibility(View.GONE);
        }

        tv_sobot_chat_intelligence_reply = findViewById(R.id.tv_sobot_chat_intelligence_reply);
        tv_sobot_chat_intelligence_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanelAndKeyboard(mPanelRoot);
                if (userInfo != null) {
                    //是否黑名单 1 是 ，0 否
                    int isBlack = userInfo.getIsblack();
                    if (isBlack == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_already_lahei));
                        return;
                    }
                }
                //进入智能回复界面
                Intent intent = new Intent(getSobotActivity(), SobotIntelligenceReplyActivity.class);
                startActivityForResult(intent, OnlineConstant.SOBOT_REQUEST_CODE_INTELLIGENCE_REPLY);

            }
        });

        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetBtnUploadAndSend();
            }
        });
        et_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEmoticonBtn2Blur();
                btn_emoticon_view.setBackgroundResource(R.drawable.sobot_emoticon_button_selector);
            }
        });
        et_sendmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean isFocused) {
                if (isFocused) {
                    int length = et_sendmessage.getText().toString().trim().length();
                    if (length != 0) {
                        sobot_btn_send.setVisibility(View.VISIBLE);
                        sobot_btn_upload_view.setVisibility(View.GONE);
                    }
                    //根据是否有焦点切换实际的背景
                    // edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_focus"));
                } else {
                    //  edittext_layout.setBackgroundResource(getResDrawableId("sobot_chatting_bottom_bg_blur"));
                }
            }
        });

        et_sendmessage.addTextChangedListener(new

                                                      TextWatcher() {
                                                          @Override
                                                          public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                                              resetBtnUploadAndSend();
                                                          }

                                                          @Override
                                                          public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                                          }

                                                          @Override
                                                          public void afterTextChanged(Editable arg0) {
                                                          }
                                                      });

        srv_online_msg_list = findViewById(R.id.srv_online_msg_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getSobotActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        srv_online_msg_list.setLayoutManager(layoutManager);
        srv_online_msg_list.setLoadingListener(this);
        srv_online_msg_list.setPullRefreshEnabled(true);
        srv_online_msg_list.setLoadingMoreEnabled(false);//禁止加载更多
        //滑动消息列表控件手指松开时隐藏键盘
        srv_online_msg_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    hidePanelAndKeyboard(mPanelRoot);
                }
                return false;
            }
        });

        showEmotionBtn();

        //监听聊天的面板
        mKPSwitchListener = KeyboardUtil.attach(

                getSobotActivity(), mPanelRoot,
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        resetEmoticonBtn();
                        if (isShowing && mMsgListAdapter != null) {
                            srv_online_msg_list.scrollToPosition(mMsgListAdapter.getItemCount());
                        }
                    }
                });
        KPSwitchConflictUtil.attach(mPanelRoot, sobot_btn_upload_view, et_sendmessage);

        btn_emoticon_view.setOnClickListener(this);
        sobot_btn_send.setOnClickListener(this);
        sobot_btn_upload_view.setOnClickListener(this);

        if (isFromBlack()) {
            sobot_ll_bottom.setVisibility(View.GONE);
            rl_bottom_offline_state.setVisibility(View.VISIBLE);
            OfflineMsgModel data = new OfflineMsgModel();
            data.setStatus(SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_BLACK);
            rl_bottom_offline_state.setViewType(userInfo.getId(), data, this);
        } else if (isFromHistory()) {
            sobot_ll_bottom.setVisibility(View.GONE);
            queryUserState();
        } else if (isFromSessionAlert()) {
            if (((CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER)).getAid().equals(userInfo.getStaffId())) {
                sobot_ll_bottom.setVisibility(View.VISIBLE);
                sobot_ll_bottom_lock.setVisibility(View.GONE);
                tv_online_close_chat.setVisibility(View.GONE);
//                iv_online_user_biaoji.setVisibility(View.VISIBLE);
                iv_online_user_biaoji.setVisibility(View.GONE);
            } else {
                sobot_ll_bottom.setVisibility(View.GONE);
                queryUserState();
            }
        } else if (isFromOnline()) {
            sobot_ll_bottom.setVisibility(View.VISIBLE);
            sobot_ll_bottom_lock.setVisibility(View.GONE);
            tv_online_close_chat.setVisibility(View.VISIBLE);
//            iv_online_user_biaoji.setVisibility(View.VISIBLE);
            iv_online_user_biaoji.setVisibility(View.GONE);
            queryUserState();
        } else {
            tv_online_close_chat.setVisibility(View.GONE);
            sobot_ll_bottom.setVisibility(View.GONE);
            if (userInfo == null) {
                queryUserState();
            }
        }

        iv_online_user_zhuanjie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanelAndKeyboard(mPanelRoot);
                if (userInfo != null) {
                    //是否黑名单 1 是 ，0 否
                    int isBlack = userInfo.getIsblack();
                    if (isBlack == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_already_lahei));
                        return;
                    }
                }
                if (isFromHistory()) {
                    SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_no_transfer));
                } else {
                    Intent transferIntent = new Intent(getSobotActivity(), SobotOnlineTransferActivity.class);
                    if (cids != null && cids.size() > 0) {
                        transferIntent.putExtra("cid", cids.get(cids.size() - 1));
                    }
                    transferIntent.putExtra("uid", userInfo.getId());
                    startActivity(transferIntent);
                }

            }
        });

        displayInNotch(sobot_online_username_back_tv);
        displayInNotch(srv_online_msg_list);
        displayInNotch(findViewById(R.id.sobot_ll_chat_bottom_root));
        String source = getIntent().getStringExtra("userSource");
        if (!TextUtils.isEmpty(source)) {
            userSource = Integer.parseInt(source);
        }
    }

    @Override
    protected void initData() {
        registBroadCast();
        if (userInfo == null) {
            return;
        }
        tv_sobot_chat_quick_reply.setVisibility(View.GONE);
        //右上角显示工单信息
        /*if (whatsAppInfoModel != null) {
            if (!TextUtils.isEmpty(whatsAppInfoModel.getOrderNumber())) {
                ll_ordernum.setVisibility(View.GONE);
                tv_ordernum.setText(whatsAppInfoModel.getOrderNumber());
            } else {
                ll_ordernum.setVisibility(View.GONE);
            }
        } else {
            ll_ordernum.setVisibility(View.GONE);
        }*/
//        if (!TextUtils.isEmpty(userInfo.getRealname())) {
//            ll_ordernum.setVisibility(View.GONE);
//            tv_ordernum.setText(userInfo.getRealname());
//        } else {
//            ll_ordernum.setVisibility(View.GONE);
//        }
        //输入框上边 4个模板消息 按钮是否显示
        if (location_confirm_noncod != null || location_confirm_cod != null) {
            tv_sobot_chat_ask_for_location.setVisibility(View.VISIBLE);
            tv_sobot_chat_ask_for_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whatsAppInfoModel != null) {
                        String msgContent = "";
                        if (whatsAppInfoModel.getTemplateName().equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName())) {
                            msgContent = location_confirm_noncod.getBodyContent();
                        } else if (whatsAppInfoModel.getTemplateName().equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                            msgContent = location_confirm_cod.getBodyContent();
                        }
                        OnlineMsgPreviewDialog previewDialog = new OnlineMsgPreviewDialog(getSobotActivity(), msgContent);
                        previewDialog.setOnClickListener(new OnlineMsgPreviewDialog.OnItemClick() {
                            @Override
                            public void OnClick(int type) {
                                if (type == 1) {// 1：确定
                                    sendTemplateMsg(whatsAppInfoModel.getTemplateName());
                                }
                            }
                        });
                        previewDialog.show();
                    }
                }
            });
        }
        if (time_reminder != null) {
            tv_sobot_chat_time_alert.setVisibility(View.VISIBLE);
            tv_sobot_chat_time_alert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whatsAppInfoModel != null) {
                        String msgContent = time_reminder.getBodyContent();
                        OnlineMsgPreviewDialog previewDialog = new OnlineMsgPreviewDialog(getSobotActivity(), msgContent);
                        previewDialog.setOnClickListener(new OnlineMsgPreviewDialog.OnItemClick() {
                            @Override
                            public void OnClick(int type) {
                                if (type == 1) {// 1：确定
                                    sendTemplateMsg(SobotTemplateName.TIME_REMINDER.getTemplateName());
                                }
                            }
                        });
                        previewDialog.show();
                    }
                }
            });
        }
        if (location_reminder != null) {
            tv_sobot_chat_location_alert.setVisibility(View.VISIBLE);
            tv_sobot_chat_location_alert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whatsAppInfoModel != null) {
                        String msgContent = location_reminder.getBodyContent();
                        OnlineMsgPreviewDialog previewDialog = new OnlineMsgPreviewDialog(getSobotActivity(), msgContent);
                        previewDialog.setOnClickListener(new OnlineMsgPreviewDialog.OnItemClick() {
                            @Override
                            public void OnClick(int type) {
                                if (type == 1) {// 1：确定
                                    sendTemplateMsg(SobotTemplateName.TIME_REMINDER.LOCATION_REMINDER.getTemplateName());
                                }
                            }
                        });
                        previewDialog.show();
                    }
                }
            });
        }
        if (reach_reminder != null) {
            tv_sobot_chat_arrival_alert.setVisibility(View.VISIBLE);
            tv_sobot_chat_arrival_alert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whatsAppInfoModel != null) {
                        String msgContent = reach_reminder.getBodyContent();
                        OnlineMsgPreviewDialog previewDialog = new OnlineMsgPreviewDialog(getSobotActivity(), msgContent);
                        previewDialog.setOnClickListener(new OnlineMsgPreviewDialog.OnItemClick() {
                            @Override
                            public void OnClick(int type) {
                                if (type == 1) {// 1：确定
                                    sendTemplateMsg(SobotTemplateName.REACH_REMINDER.getTemplateName());
                                }
                            }
                        });
                        previewDialog.show();
                    }
                }
            });
        }

        if (request_reply != null) {
            tv_sobot_chat_request_reply.setVisibility(View.VISIBLE);
            tv_sobot_chat_request_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whatsAppInfoModel != null) {
                        String msgContent = request_reply.getBodyContent();
                        OnlineMsgPreviewDialog previewDialog = new OnlineMsgPreviewDialog(getSobotActivity(), msgContent);
                        previewDialog.setOnClickListener(new OnlineMsgPreviewDialog.OnItemClick() {
                            @Override
                            public void OnClick(int type) {
                                if (type == 1) {// 1：确定
                                    sendTemplateMsg(SobotTemplateName.REQUEST_REPLY.getTemplateName());
                                }
                            }
                        });
                        previewDialog.show();
                    }
                }
            });
        }
        if (cod_confirm != null) {
            tv_sobot_chat_cod_confirm.setVisibility(View.VISIBLE);
            tv_sobot_chat_cod_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whatsAppInfoModel != null) {
                        String msgContent = cod_confirm.getBodyContent();
                        OnlineMsgPreviewDialog previewDialog = new OnlineMsgPreviewDialog(getSobotActivity(), msgContent);
                        previewDialog.setOnClickListener(new OnlineMsgPreviewDialog.OnItemClick() {
                            @Override
                            public void OnClick(int type) {
                                if (type == 1) {// 1：确定
                                    sendTemplateMsg(SobotTemplateName.COD_CONFIRM.getTemplateName());
                                }
                            }
                        });
                        previewDialog.show();
                    }
                }
            });
        }

        if (whatsAppInfoModel == null) {
            //隐藏输入框上边的几个模板按钮
            tv_sobot_chat_ask_for_location.setVisibility(View.GONE);
            tv_sobot_chat_time_alert.setVisibility(View.GONE);
            tv_sobot_chat_location_alert.setVisibility(View.GONE);
            tv_sobot_chat_arrival_alert.setVisibility(View.GONE);
            tv_sobot_chat_request_reply.setVisibility(View.GONE);
            tv_sobot_chat_cod_confirm.setVisibility(View.GONE);
        }


        SobotOnlineLogUtils.i("用户来源：" + userSource);
        mMsgListAdapter = new SobotOnlineMsgAdapter(this, userInfo, userSource, this);
        if (mMsgListAdapter != null) {
            mMsgListAdapter.setListAll(data);
            if (srv_online_msg_list != null) {
                srv_online_msg_list.setAdapter(mMsgListAdapter);
            }
        }

        if (userInfo != null) {
            if (userInfo.getIsblack() == 1) {
                iv_online_user_pullblack.setBackgroundResource(R.drawable.sobot_online_lahei_sel);
            } else {
                iv_online_user_pullblack.setBackgroundResource(R.drawable.sobot_online_lahei_def);
            }
            if (userInfo.getIsmark() == 1) {
                iv_online_user_biaoji.setBackgroundResource(R.drawable.sobot_online_biaoji_sel);
            } else {
                iv_online_user_biaoji.setBackgroundResource(R.drawable.sobot_online_biaoji_def);
            }
        }
        iv_online_user_pullblack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanelAndKeyboard(mPanelRoot);
                if (userInfo != null) {
                    int blackFunction = admin != null ? admin.getBlackFunction() : 0;
                    if (blackFunction == 1) {
                        //有权限拉黑功能
                        if (userInfo.getIsblack() == 1) {
                            //解除用户黑名单
                            //removeBlackUsers();
                        } else {
                            //打开拉黑pop
                            Intent pullBlackIntent = new Intent(getSobotActivity(), SobotOnlinePullBlackActivity.class);
                            pullBlackIntent.putExtra("userInfo", userInfo);
                            startActivityForResult(pullBlackIntent, OnlineConstant.SOBOT_REQUEST_CODE_LAHEI);
                        }
                    }
                }

            }
        });
        if (whatsAppInfoModel != null && !SobotStringUtils.isEmpty(whatsAppInfoModel.getUserTel())) {
            iv_online_call_user.setVisibility(View.GONE);
            iv_online_call_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("tel:" + whatsAppInfoModel.getUserTel()));// mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                        startActivity(intent);
                    } catch (Exception e) {
                        SobotToastUtil.showCustomToast(SobotOnlineChatActivity.this, getResources().getString(R.string.sobot_no_support_call));
                        e.printStackTrace();
                    }
                }
            });
        } else {
            iv_online_call_user.setVisibility(View.GONE);
        }

        iv_online_user_biaoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePanelAndKeyboard(mPanelRoot);
                if (userInfo != null) {
                    //是否黑名单 1 是 ，0 否
                    int isBlack = userInfo.getIsblack();
                    if (isBlack == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_user_already_lahei));
                        return;
                    }
                }
                if (userInfo != null) {
                    if (userInfo.getIsmark() == 1) {
                        //解除标记
                        markUsers(false);
                    } else {
                        //标记用户
                        markUsers(true);
                    }
                }

            }
        });

        if (isFromOnline()) {
            tv_online_close_chat.setVisibility(View.VISIBLE);
        } else {
            tv_online_close_chat.setVisibility(View.GONE);
        }
        tv_online_close_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeDialog == null) {
                    closeDialog = new SobotOnlineCommonDialog(getSobotActivity(), getString(R.string.online_exit_chat), getString(R.string.online_ok), getString(R.string.online_cancle), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.sobot_btn_cancle) {
                            } else if (v.getId() == R.id.sobot_btn_ok) {
                                zhiChiApi.leave(getSobotActivity(), userInfo.getLastCid(), userInfo.getId(), new SobotResultCallBack<OnlineBaseCode>() {
                                    @Override
                                    public void onSuccess(OnlineBaseCode onlineBaseCode) {
                                        Intent intent = new Intent();
                                        intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_LIST_SYNCHRONOUS_USERS);
                                        SobotUtils.getApp().sendBroadcast(intent);
                                        MsgCacheManager.getInstance().delUnReadMsgCount(userInfo.getId());
//                                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_user_xiaxian));
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Exception e, String des) {
                                        SobotToastUtil.showCustomToast(getSobotActivity(), des);
                                    }
                                });
                            }
                            closeDialog.dismiss();
                            closeDialog = null;
                        }
                    });
                }
                closeDialog.show();
            }
        });
    }


    /**
     * handler 消息实体message 更新ui界面
     *
     * @param messageAdapter
     * @param msg
     */
    public void updateUiMessage(SobotOnlineMsgAdapter messageAdapter, Message msg) {
        ChatMessageModel myMessage = (ChatMessageModel) msg.obj;
        List<ChatMessageModel> chatMessageModels = messageAdapter.getList();
        if (chatMessageModels != null) {
            boolean isOneMsgId = false;
            for (int i = chatMessageModels.size() - 1; i >= 0; i--) {
                if (i > (chatMessageModels.size() - 40)) {
                    if (!TextUtils.isEmpty(myMessage.getMsgId()) && !TextUtils.isEmpty(chatMessageModels.get(i).getMsgId()) && chatMessageModels.get(i).getMsgId().equals(myMessage.getMsgId())) {
                        isOneMsgId = true;
                        SobotOnlineLogUtils.i(isOneMsgId + "-----已经存在相同的msgId 消息了，不再添加--------" + chatMessageModels.get(i).getMsgId() + "====================" + myMessage.getMsgId());
                    }
                }
            }

            if (!isOneMsgId) {
                messageAdapter.addItemToLast(myMessage);
            }

        } else {
            messageAdapter.addItemToLast(myMessage);
        }
        messageAdapter.notifyDataSetChanged();

    }

    public void updateUiMessageStatus(SobotOnlineMsgAdapter messageAdapter, int position) {
        messageAdapter.notifyItemChanged(position);
    }

    public void updateUiMsg(SobotOnlineMsgAdapter messageAdapter, ChatMessageModel message) {
        messageAdapter.addItemToLast(message);
        messageAdapter.notifyDataSetChanged();
    }


    // 图片通知
    public void sendImageMessageToHandler(String imageUrl, Handler handler,
                                          String id, String lastCid) {
//        ChatMessageModel msgModel = new ChatMessageModel();
//        msgModel.setId(id);
//        msgModel.setCid(lastCid);
//        msgModel.setAction(5);
//        msgModel.setSenderType(2);
//        msgModel.setSender(MyApplication.getUser().getAid());
//        msgModel.setT(System.currentTimeMillis() + "");
//        msgModel.setMsgType(1);
//        msgModel.setMsg(imageUrl);
//        msgModel.setIsSendOk(1);
//        msgModel.setIsHistory(1);
//        if (MyApplication.getUser() != null && !TextUtils.isEmpty(MyApplication.getUser().getFace())) {
//            msgModel.setSenderFace(MyApplication.getUser().getFace());
//        }
//
//        Message message = new Message();
//        message.what = SobotSocketConstant.NEW_INFOMATION;//
//        message.obj = msgModel;
//        handler.sendMessage(message);
    }


    public Handler handler = new StaticHandler(this);

    private static class StaticHandler extends Handler {

        WeakReference<Context> weakReference = null;

        public StaticHandler(Context context) {
            weakReference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            //处理UI显示
            SobotOnlineChatActivity activity = (SobotOnlineChatActivity) weakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case SobotSocketConstant.NEW_INFOMATION:
                        activity.updateUiMessage(activity.mMsgListAdapter, msg);
                        activity.srv_online_msg_list.scrollToPosition(activity.mMsgListAdapter.getItemCount());
                        break;
                }
            }
        }
    }

    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_MSG);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_USERINFO);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_RESEND_IMG_MSG);/* 重新发送图片广播 */
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_RESEND_TEXT_MSG);/* 重新发送文本广播 */
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_QUICK_REPLY_SEARCH_CONTENT);/* 重新发送文本广播 */
        filter.addAction(SobotSocketConstant.BROADCAST_CUSTOM_COMITSUMMARY);//提交服务总结
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_REVOKE_MESSAGE);//撤回的消息重新编辑广播
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_ADD_BLACK);//撤回的消息重新编辑广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(receiver, filter);
        }
    }


    //被抢接的客服弹出提示
    private void scrambleFor() {
        OnlineTipDialog tipDialog = new OnlineTipDialog(getSobotContext(), getString(R.string.online_chat_cover_transfer), getString(R.string.online_know), new OnlineTipDialog.OnItemClick() {
            @Override
            public void OnClick(int type) {
                finish();
            }
        });
        tipDialog.setCanceledOnTouchOutside(false);
        tipDialog.show();
    }


    //从历史列表进来聊天页
    private boolean isFromHistory() {
        if ("history".equals(flag_from_page)) {
            return true;
        }
        return false;
    }

    //从在线用户列表进来聊天页
    private boolean isFromOnline() {
        if ("online".equals(flag_from_page)) {
            return true;
        }
        return false;
    }

    //从黑名单列表进来聊天页
    private boolean isFromBlack() {
        if ("black".equals(flag_from_page)) {
            // showRightView(0, 0, true);
            return true;
        }
        return false;
    }

    //从会话警报列表页进来聊天页
    private boolean isFromSessionAlert() {
        if ("sessionAlert".equals(flag_from_page)) {
            return true;
        }
        return false;
    }


    public String getMsgTip(ChatMessageMsgModel item) {
        if (item != null) {
            if (!TextUtils.isEmpty(item.getMsgType())) {
                //msgType：文本,图片,音频,视频,文件,对象
                //msgType：0,1,2,3,4,5
                //当msgType=5 时，根据content里边的 type 判断具体的时哪种消息 0-富文本 1-多伦会话 2-位置 3-小卡片 4-订单卡片
                if ("0".equals(item.getMsgType())) {
                    return item.getContent() != null ? (String) item.getContent() : "";
                } else if ("1".equals(item.getMsgType())) {
                    return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_tupian_biaoshi") + "]";
                } else if ("2".equals(item.getMsgType())) {
                    return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_yinpin_biaoshi") + "]";
                } else if ("3".equals(item.getMsgType())) {
                    return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_shipin_biaoshi") + "]";
                } else if ("4".equals(item.getMsgType())) {
                    return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_wenjian_biaoshi") + "]";
                }
                if ("5".equals(item.getMsgType().trim())) {
                    com.sobot.onlinecommon.socket.module.ChatMessageObjectModel messageObjectModel = null;
                    if (item.getContent() != null) {
                        String temp = SobotGsonUtil.gsonString(item.getContent());
                        if (!TextUtils.isEmpty(temp) && temp.contains("msg")) {
                            messageObjectModel = SobotGsonUtil.gsonToBean(temp, com.sobot.onlinecommon.socket.module.ChatMessageObjectModel.class);
                            if (messageObjectModel != null) {
                                switch (messageObjectModel.getType()) {
                                    case 0:
                                        return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_fuwenben_biaoshi") + "]";
                                    case 1:
                                        return SobotResourceUtils.getResString(getApplicationContext(), "online_a_new_message");
                                    case 2:
                                        return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_weizhi_biaoshi") + "]";
                                    case 3:
                                        return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_xiaokapian_biaoshi") + "]";
                                    case 4:
                                        return "[" + SobotResourceUtils.getResString(getApplicationContext(), "online_ordercard_biaoshi") + "]";
                                }

                            }
                        }
                    }
                    return "";
                }
            }
        }
        //未识别类型
        return "";
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            if (userInfo == null) {
                return;
            }
            SobotOnlineLogUtils.i("SobotOnlineChatActivity-->广播是   ：" + intent.getAction());

            if (SobotSocketConstant.BROADCAST_SOBOT_MSG.equals(intent.getAction())) {
                String msgContentJson = intent.getStringExtra("msgContent");
                SobotOnlineLogUtils.i(msgContentJson);
                PushMessageModel pushMsg = (PushMessageModel) SobotGsonUtil.gsonToBean(msgContentJson, PushMessageModel.class);
                if (pushMsg == null) {
                    return;
                }
                pushMsg.setMarkStatus(pushMsg.getIsmark());
                //新消息
                if (pushMsg.getType() == SobotSocketConstant.NEW_INFOMATION) {
                    if (userInfo.getId().equals(pushMsg.getUid())) {
                        if (pushMsg.getContent() != null) {
                            showMessage(pushMsg, 5);
                            MsgCacheManager.getInstance().delUnReadMsgCount(userInfo.getId());
                        }
                    } else {
                        //如果收到的消息不是该用户，就发送广播
                        Intent newMsgIntent = new Intent();
                        newMsgIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_NEW_MSG);
                        newMsgIntent.putExtra("noReadCount", MsgCacheManager.getInstance().getTotalUnReadMsgCount());
                        newMsgIntent.putExtra("msgContentJson", SobotGsonUtil.gsonString(pushMsg));
                        newMsgIntent.putExtra("msgContent", getMsgTip(pushMsg.getMessage()));
                        //发送新消息广播
                        SobotUtils.getApp().sendBroadcast(newMsgIntent);
                    }
                }

                if (pushMsg.getType() == SobotSocketConstant.VISIT_TRAIL) {
                    if (userInfo.getId().equals(pushMsg.getUid())) {
                        if (pushMsg.getContent() != null) {
                            //  showMessage(pushMsg, 5);
                        }
                    }
                }

                if (pushMsg.getType() == SobotSocketConstant.OFFLINE_USER) {
                    if (userInfo.getId().equals(pushMsg.getUid())) {
                        flag_from_page = "history";
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setId(System.currentTimeMillis() + "");
                        msgModel.setAction(10);
                        msgModel.setMsg(getString(R.string.online_user_xiaxian));
                        msgModel.setMessage(pushMsg.getMessage());
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
//                        handler.sendMessage(message);
                        tv_online_close_chat.setVisibility(View.GONE);
                    }
                }

                //用户评价过邀评，服务器发送过来的类型
                if (pushMsg.getType() == SobotSocketConstant.MESSAGE_USER_EVALUATE) {
                    ChatMessageModel msgModel = new ChatMessageModel();
                    msgModel.setId(System.currentTimeMillis() + "");
                    msgModel.setAction(MSG_TYPE_EVALUATE1);
                    msgModel.setTs(SobotTimeUtils.getNowString());
                    msgModel.setMsg(getString(R.string.online_user_already_evaluate));
                    msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_user_already_evaluate)));
                    Message message = new Message();
                    message.what = SobotSocketConstant.NEW_INFOMATION;
                    message.obj = msgModel;
//                    handler.sendMessage(message);
                }

                if (pushMsg.getType() == SobotSocketConstant.NEW_USER) {
                    if (userInfo.getId().equals(pushMsg.getUid())) {
                        flag_from_page = "online";
                        //在当前界面中用户如果重新上线了，那么需要更新cid
                        userInfo.setLastCid(pushMsg.getCid());
                        //重新上线以后，底部输入框显示，离线view影藏
                        sobot_ll_bottom.setVisibility(View.VISIBLE);
                        //显示底部快捷菜单
                        ll_custom_menu.setVisibility(View.VISIBLE);
                        rl_bottom_offline_state.setVisibility(View.GONE);
                        sobot_ll_bottom_lock.setVisibility(View.GONE);
                        tv_online_close_chat.setVisibility(View.VISIBLE);
//                        iv_online_user_biaoji.setVisibility(View.VISIBLE);
                        iv_online_user_biaoji.setVisibility(View.GONE);
                    }
                    tv_online_close_chat.setVisibility(View.VISIBLE);
                }

                if (pushMsg.getType() == SobotSocketConstant.INPUTIING) {
                    if (userInfo.getId().equals(pushMsg.getUid())) {
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setSenderType(0);
                        msgModel.setAction(5);
                        msgModel.setInputIng(true);
                        msgModel.setMessage(pushMsg.getMessage());
                        msgModel.setMsg(pushMsg.getContent());
                        msgModel.setSenderName(pushMsg.getUname());
                        if (!TextUtils.isEmpty(pushMsg.getFace())) {
                            msgModel.setSenderFace(pushMsg.getFace());
                        }
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
//                        handler.sendMessage(message);
                    }
                }

                if (pushMsg.getType() == SobotSocketConstant.VISIT_TRAIL) {
//                    if (userInfo.getId().equals(pushMsg.getUid())) {
//                        VisitTrailModel visitTrailModel = (VisitTrailModel) GsonUtil.jsonToBean(msgContentJson,
//                                VisitTrailModel.class);
//                        SobotLogUtils.i("访问轨迹-----" + visitTrailModel.toString());
//
//                        //添加聊天页面中的访问轨迹
//                        ChatMessageModel msgModel = new ChatMessageModel();
//                        msgModel.setId(System.currentTimeMillis() + "");
//                        msgModel.setAction(SobotSocketConstant.MSG_TYPE_ACTION_VISIT_TRAIL);
//                        msgModel.setVisitTrailModel(visitTrailModel);
//                        msgModel.setMessage(pushMsg.getMessage());
//
//                        Message message = handler.obtainMessage();
//                        message.what = SobotSocketConstant.NEW_INFOMATION;
//                        message.obj = msgModel;
//                        handler.sendMessage(message);
//
//                        //更新访问轨迹窗口中的数据
//                        if (spadapter != null) {
//                            List<VisitTrailModel> allData = spadapter.getAllData();
//                            allData.add(0, visitTrailModel);
//                            spadapter.clear();
//                            spadapter.addAll(allData);
//                        }
//                    }
                }

                if (pushMsg.getType() == SobotSocketConstant.MESSAGE_NOT_DELIVERED) {
                }

                //被别的客服抢接，服务器发送过来的类型
                if (pushMsg.getType() == SobotSocketConstant.ACTIVE_RECEPT) {
                    if (userInfo.getId().equals(pushMsg.getUid())) {
                        sobot_ll_bottom.setVisibility(View.GONE);
                        scrambleFor();//弹出被抢接的提示
                    }
                }

            } else if (SobotSocketConstant.BROADCAST_SOBOT_RESEND_IMG_MSG.equals(intent.getAction())) {
                String msgContentJson = intent.getStringExtra("msgContent");
                try {
                    JSONObject obj = new JSONObject(msgContentJson);
                    String filePath = obj.getString("context");
                    String id = obj.getString("id");
//                    sendFileMsg(isFromHistory(), filePath, OnlineConstant.SOBOT_MSG_TYPE_IMG, id);
                } catch (Exception e) {
                }
                SobotOnlineLogUtils.i("重新发送图片msgContentJson--->" + msgContentJson);
            } else if (SobotSocketConstant.BROADCAST_SOBOT_RESEND_TEXT_MSG.equals(intent.getAction())) {
                String msgContentJson = intent.getStringExtra("msgContent");
                boolean sensitive = intent.getBooleanExtra("sensitive", false);
                String posi = "";
                try {
                    JSONObject obj = new JSONObject(msgContentJson);
                    String msg = obj.getString("context");
                    String id = obj.getString("id");
                    posi = obj.getString("posi");
                    if (sensitive) {
                        posi = "0";
                    }
                    sendMsg(msg, OnlineConstant.SOBOT_MSG_TYPE_TEXT, "", isFromHistory(), null);
                } catch (Exception e) {
                }
                SobotOnlineLogUtils.i("重新发送文本msgContentJson--->" + msgContentJson + "----posi----" + posi);
            } else if (SobotSocketConstant.BROADCAST_SOBOT_UPDATE_USERINFO.equals(intent.getAction())) {
                setTitle(intent.getStringExtra("uname"));
            } else if (SobotSocketConstant.BROADCAST_SOBOT_QUICK_REPLY_SEARCH_CONTENT.equals(intent.getAction())) {
                hidePanelAndKeyboard(mPanelRoot);
                String content = intent.getStringExtra("searchContent");
                String from = intent.getStringExtra("from");
                if ("QuickReplySecondListActivity".equals(from)) {
                    sendClose1BroadCast();
                } else {
                    sendCloseBroadCast();
                }
                if (!TextUtils.isEmpty(content)) {
                    et_sendmessage.setText(content);
                    et_sendmessage.setSelection(content.length());
                    SobotInputHelper.toggleInputMode(getSobotActivity());
                }
            } else if (SobotSocketConstant.BROADCAST_CUSTOM_COMITSUMMARY.equals(intent.getAction())) {
                hasSummary = true;
                if (hasSummary) {
                    tv_sobot_chat_service_summary.setOnClickListener(null);
                    tv_sobot_chat_service_summary.setText(getString(R.string.sobot_online_has_zongjie));
                }
                hidePanelAndKeyboard(mPanelRoot);
//				setPanelView(mPanelRoot,btn_add.getId());
            } else if (SobotSocketConstant.BROADCAST_SOBOT_REVOKE_MESSAGE.equals(intent.getAction())) {
                String reovkeStr = intent.getStringExtra("message_revoke");
                if (!TextUtils.isEmpty(reovkeStr)) {
                    et_sendmessage.setText(reovkeStr);
                }
            } else if (SobotSocketConstant.BROADCAST_SOBOT_ADD_BLACK.equals(intent.getAction())) {
                sobot_ll_bottom.setVisibility(View.GONE);
                rl_bottom_offline_state.setVisibility(View.VISIBLE);
                OfflineMsgModel data = new OfflineMsgModel();
                data.setStatus(SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_BLACK);
                rl_bottom_offline_state.setViewType(userInfo.getId(), data, SobotOnlineChatActivity.this);
            }
        }
    };

    private void showMessage(PushMessageModel pushMsg, int action) {
        ChatMessageModel msgModel = new ChatMessageModel();
        if (!TextUtils.isEmpty(pushMsg.getMsgId())) {
            msgModel.setId(pushMsg.getMsgId());
            msgModel.setMsgId(pushMsg.getMsgId());
        } else {
            msgModel.setId(System.currentTimeMillis() + "");
            msgModel.setMsgId(pushMsg.getMsgId());
        }
        msgModel.setAction(action);
        msgModel.setSenderType(0);
        msgModel.setSenderName(pushMsg.getUname());
        msgModel.setSenderFace(pushMsg.getFace());
        msgModel.setIsHistory(1);
        msgModel.setT(pushMsg.getT());
        msgModel.setTs(pushMsg.getTs());
        if (!TextUtils.isEmpty(pushMsg.getFace())) {
            msgModel.setSenderFace(pushMsg.getFace());
        }
        msgModel.setMessage(pushMsg.getMessage());
        msgModel.setListend(true);
        if (handler == null) {
            handler = new StaticHandler(this);
        }
        Message message = handler.obtainMessage();
        message.what = SobotSocketConstant.NEW_INFOMATION;
        message.obj = msgModel;
        handler.sendMessage(message);
    }

    /**
     * 初始化查询cid的列表
     */
    private void queryCids() {
        getCidsFinish = false;
        if (userInfo == null) {
            return;
        }
        zhiChiApi.queryCids(getSobotActivity(), userInfo.getId(), new SobotResultCallBack<CidsModel>() {
            @Override
            public void onSuccess(CidsModel cidsModel) {
                getCidsFinish = true;
                if (cidsModel != null) {
                    cids = cidsModel.getCids();
                    if (cids != null) {
                        boolean hasRepet = false;
                        int pos = 0;
                        for (int i = 0; i < cids.size(); i++) {
                            if (cids.get(i).equals(userInfo.getLastCid())) {
                                hasRepet = true;
                                pos = i;
                                break;
                            }
                        }
                        if (!hasRepet) {
                            cids.add(userInfo.getLastCid());
                        } else {
                            //只显示最后一个会话之前的会话，避免筛选会话，同一个用户从中间的会话进入，这种情况只显示该会话之前的，不显示之后的会话
                            if (!TextUtils.isEmpty(userInfo.getLastCid())) {
                                List<String> tempList = new ArrayList<>();
                                for (int i = 0; i < cids.size(); i++) {
                                    if (cids.get(i).equals(userInfo.getLastCid())) {
                                        tempList.add(cids.get(i));
                                        break;
                                    } else {
                                        tempList.add(cids.get(i));
                                    }
                                }
                                SobotOnlineLogUtils.i("历史记录截取前 cids:" + cids.toString());
                                cids.clear();
                                cids.addAll(tempList);
                                SobotOnlineLogUtils.i("历史记录截取后 cids:" + cids.toString());
                            }
                        }
                        if (cids.size() > 0) {
                            Collections.reverse(cids);
                            SobotOnlineLogUtils.i("cids:" + cids.toString());
                            getHistoryMsg(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                getCidsFinish = true;
                e.printStackTrace();
            }
        });
    }

    /**
     * 根据当前cid的位置获取cid
     *
     * @return
     */
    public String getCurrentCid() {
        String currentCid = userInfo.getLastCid();
        if (currentCidPosition > 0) {
            if (currentCidPosition > cids.size() - 1) {
                currentCid = "-1";
            } else {
                currentCid = cids.get(currentCidPosition);
            }
        }
        return currentCid;
    }

    private void getHistoryMsg(final boolean isFirst) {
        if ((!getCidsFinish && !isFirst) || isInGethistory) {
            //1.查询cid接口没有结束时 又不是第一次查询历史记录  那么 直接什么也不做就返回
            //2.如果查询历史记录的接口正在跑   那么什么也不做
            srv_online_msg_list.refreshComplete();
        } else {
            final Map<String, Object> map = new HashMap<>();
            String currentCid = getCurrentCid();
            if ("-1".equals(currentCid)) {
                srv_online_msg_list.refreshComplete();
                return;
            }
            map.put("cid", currentCid);
            map.put("uid", userInfo.getId());
            SobotOnlineLogUtils.i("userInfo.getId() ----  >" + userInfo.getId());
            isInGethistory = true;
            zhiChiApi.queryHistoryRecords(getSobotActivity(), map, new SobotResultCallBack<List<ChatMessageModel>>() {
                @Override
                public void onSuccess(List<ChatMessageModel> messageModels) {
                    isInGethistory = false;
                    srv_online_msg_list.refreshComplete();
                    List<ChatMessageModel> tempdata = new ArrayList<>();
                    if (messageModels != null && messageModels.size() > 0) {
                        for (int i = 0; i < messageModels.size(); i++) {
                            ChatMessageModel model = messageModels.get(i);
                            //历史记录中系统消息不显示
                            //用户发送的消息 如果接收人是登录座席或者机器人显示；
                            //座席发送消息，是登录座席的也显示
                            //机器人发动的消息 都显示
                            boolean isShow = false;
                            if (model != null && model.getAction() == 5 && admin != null) {
                                isShow = ((model.getSenderType() == 0 && (model.getReceiver().equals(admin.getAid()))
                                        || model.getReceiverType() == 1) || (model.getSenderType() == 1)
                                        || (model.getSenderType() == 2 && model.getSender().equals(admin.getAid())));
                            } else {
                                isShow = true;
                            }
                            if (model != null && model.getAction() == 5 && isShow) {
                                if (1 == model.getUserNoSeeFlag()) {
                                    model.setIsSendOk(0);
                                    tempdata.add(model);
                                    ChatMessageModel msgModel = new ChatMessageModel();
                                    msgModel.setAction(MSG_TYPE_SENSITIVE);
                                    msgModel.setId(System.currentTimeMillis() + "");
                                    msgModel.setCid(userInfo.getLastCid());
                                    msgModel.setSenderType(2);
                                    msgModel.setMsgType(MessageType.MESSAGE_TYPE_SENSITIVE);
                                    msgModel.setIsHistory(1);
                                    msgModel.setMessage(model.getMessage());
                                    msgModel.setSender(((CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER)).getAid());
                                    msgModel.setSenderName(((CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER)).getNickName());
                                    msgModel.setRevokeFlag(1);
                                    msgModel.setSensitive(true);
                                    if (!model.isRevokeFlag()) {
                                        tempdata.add(msgModel);
                                    }
                                } else {
                                    tempdata.add(model);
                                }
                            }
                        }
                    }
                    data.addAll(tempdata);
                    mMsgListAdapter.addItemsToHead(tempdata);
                    currentCidPosition++;
                    if (isFirst) {
                        srv_online_msg_list.scrollToPosition(data.size());
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    isInGethistory = false;
                    srv_online_msg_list.refreshComplete();
                }
            });
        }

    }


    @Override
    public void onRefresh() {
        getHistoryMsg(false);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SobotChatUtils.isServiceWork(getSobotActivity(), "com.sobot.onlinecommon.socket.channel.SobotTCPServer")) {
            SobotOnlineLogUtils.i("SobotOnlineChatActivity tcpserver 没运行");
            CustomerServiceInfoModel user = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
            if (user != null && !TextUtils.isEmpty(user.getAid()) && !TextUtils.isEmpty(user.getPuid())) {
                SobotOnlineLogUtils.i("SobotOnlineChatActivity tcpserver 重启");
                Intent intent = new Intent(getSobotActivity(), SobotTCPServer.class);
                if (user.getWslinkBak() != null && user.getWslinkBak().size() > 0) {
                    intent.putExtra("wslinkBak", user.getWslinkBak().get(0));
                }
                intent.putExtra("wslinkDefault", user.getWslinkDefault());
                intent.putExtra("companyId", user.getCompanyId());
                intent.putExtra("aid", user.getAid());
                intent.putExtra("puid", user.getPuid());
                intent.putExtra("userType", Const.user_type_customer);
                StServiceUtils.safeStartService(getSobotActivity(), intent);
            }
        } else {
            SobotOnlineLogUtils.i("SobotOnlineChatActivity tcpserver 正在运行");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //判断是否有播放的，如果有播放的，暂停播放
        stopVoice();
        ////放弃音频焦点
        abandonAudioFocus();
        inorout("1");
    }

    //进是0 出是1
    private void inorout(String type) {
        if (userInfo != null) {
            //聊天页面进站出站 调用接口
            zhiChiApi.inorout(getSobotActivity(), userInfo != null ? userInfo.getId() : "", type, new SobotResultCallBack<OnlineBaseCode>() {
                @Override
                public void onSuccess(OnlineBaseCode baseCode) {

                }

                @Override
                public void onFailure(Exception e, String s) {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVoice();
        AudioTools.destory();
        KeyboardUtil.detach(getSobotActivity(), mKPSwitchListener);
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler = null;
            }
            if (receiver != null) {
                unregisterReceiver(receiver);
            }
            MsgCacheManager.getInstance().setUid("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置表情按钮的焦点键盘
     */
    public void resetEmoticonBtn() {
        String panelViewTag = getPanelViewTag(mPanelRoot);
        String instanceTag = CustomeViewFactory.getInstanceTag(getSobotContext(), btn_emoticon_view.getId());
        if (mPanelRoot.getVisibility() == View.VISIBLE && instanceTag.equals(panelViewTag)) {
            doEmoticonBtn2Focus();
        } else {
            doEmoticonBtn2Blur();
        }
    }

    /**
     * 使表情按钮获取焦点
     */
    public void doEmoticonBtn2Focus() {
        btn_emoticon_view.setSelected(true);
    }

    /**
     * 使表情按钮失去焦点
     */
    public void doEmoticonBtn2Blur() {
        btn_emoticon_view.setSelected(false);
    }

    /**
     * 获取当前显示的聊天面板的tag
     *
     * @param panelLayout
     */
    private String getPanelViewTag(final View panelLayout) {
        String str = "";
        if (panelLayout instanceof KPSwitchPanelLinearLayout) {
            KPSwitchPanelLinearLayout tmpView = (KPSwitchPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                str = customeChattingPanel.getPanelViewTag();
            }
        }
        return str;
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_btn_send) {// 发送消息按钮
            if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                btn_model_voice.setImageResource(R.drawable.sobot_vioce_button_selector);
                editModelToVoice(View.GONE);// 编辑模式隐藏 ，语音模式显示
            }
            //获取发送内容
            final String message_result = et_sendmessage.getText().toString().trim();
            if (!TextUtils.isEmpty(message_result)) {
                //转人工接口没跑完的时候  屏蔽发送，防止统计出现混乱
                resetEmoticonBtn();
                try {
                    sendMsg(message_result, OnlineConstant.SOBOT_MSG_TYPE_TEXT, "", isFromHistory(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (view == sobot_btn_upload_view) {// 点击加号按钮,显示功能菜单
            if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                btn_model_voice.setImageResource(R.drawable.sobot_vioce_button_selector);
                editModelToVoice(View.GONE);// 编辑模式隐藏 ，语音模式显示
            }
            pressSpeakSwitchPanelAndKeyboard(sobot_btn_upload_view);
            doEmoticonBtn2Blur();
            gotoLastItem();
        }

        if (view == btn_emoticon_view) {//显示表情面板
            if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                btn_model_voice.setImageResource(R.drawable.sobot_vioce_button_selector);
                editModelToVoice(View.GONE);// 编辑模式隐藏 ，语音模式显示
            }
            // 切换表情面板
            pressSpeakSwitchPanelAndKeyboard(btn_emoticon_view);
            //切换表情按钮的状态
            switchEmoticonBtn();
            gotoLastItem();
        }
        if (view == btn_model_voice) {
            //表情按钮还原
            doEmoticonBtn2Blur();
            btn_emoticon_view.setBackgroundResource(R.drawable.sobot_emoticon_button_selector);
            if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                //设置为输入状态
                btn_model_voice.setImageResource(R.drawable.sobot_vioce_button_selector);
                editModelToVoice(View.GONE);// 编辑模式隐藏 ，语音模式显示
            } else {
                //设置为语音状态
                btn_model_voice.setImageResource(R.drawable.sobot_keyboard_normal);
                //显示语音
                permissionListener = new SobotPermissionListenerImpl() {
                    @Override
                    public void onPermissionSuccessListener() {
                        showAudioRecorder();
                    }
                };
                if (checkIsShowPermissionPop(getResString("sobot_microphone"), getResString("sobot_microphone_yongtu"), 2, 3)) {
                    return;
                }
                if (!checkAudioPermission()) {
                    return;
                }
                showAudioRecorder();
            }

        }
    }

    private void gotoLastItem() {
        if (srv_online_msg_list != null) {
            srv_online_msg_list.scrollToPosition(srv_online_msg_list.getItemCount());
        }
    }

    /*
     * 切换键盘和面板的方法 一般都用这个就行
     * 参数是按下的那个按钮
     */
    public void pressSpeakSwitchPanelAndKeyboard(final View switchPanelKeyboardBtn) {
        //切换更多方法的面板
        switchPanelAndKeyboard(mPanelRoot, switchPanelKeyboardBtn, et_sendmessage);

    }

    /**
     * 切换表情按钮焦点
     */
    public void switchEmoticonBtn() {
        boolean flag = btn_emoticon_view.isSelected();
        if (flag) {
            doEmoticonBtn2Blur();
        } else {
            doEmoticonBtn2Focus();
        }
        //切换表情和键盘
        if (btn_emoticon_view.isSelected()) {
            btn_emoticon_view.setBackgroundResource(R.drawable.sobot_keyboard_normal);
        } else {
            btn_emoticon_view.setBackgroundResource(R.drawable.sobot_emoticon_button_selector);
        }
    }


    //切换键盘和面板的方法
    public void switchPanelAndKeyboard(final View panelLayout, final View switchPanelKeyboardBtn, final View focusView) {
        if (currentPanelId == 0 || currentPanelId == switchPanelKeyboardBtn.getId()) {
            //没选中的时候或者  点击是自身的时候正常切换面板和键盘
            boolean switchToPanel = panelLayout.getVisibility() != View.VISIBLE;
            if (!switchToPanel) {
                KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
            } else {
                KPSwitchConflictUtil.showPanel(panelLayout);
                setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
            }
        } else {
            //之前选过  但是现在点击的不是自己的时候  显示自己的面板
            KPSwitchConflictUtil.showPanel(panelLayout);
            setPanelView(panelLayout, switchPanelKeyboardBtn.getId());
        }
        currentPanelId = switchPanelKeyboardBtn.getId();
    }

    /**
     * 设置聊天面板的view
     *
     * @param panelLayout
     * @param btnId
     */
    private void setPanelView(final View panelLayout, int btnId) {
        if (panelLayout instanceof KPSwitchPanelLinearLayout) {
            KPSwitchPanelLinearLayout tmpView = (KPSwitchPanelLinearLayout) panelLayout;
            View childView = tmpView.getChildAt(0);
            if (childView != null && childView instanceof CustomeChattingPanel) {
                CustomeChattingPanel customeChattingPanel = (CustomeChattingPanel) childView;
                Bundle bundle = new Bundle();
                customeChattingPanel.setupView(btnId, bundle, this);
            }
        }
    }


    /**
     * 隐藏键盘和面板
     *
     * @param layout
     */
    public void hidePanelAndKeyboard(KPSwitchPanelLinearLayout layout) {
        KPSwitchConflictUtil.hidePanelAndKeyboard(layout);
        doEmoticonBtn2Blur();
        currentPanelId = 0;
    }


    /**
     * 根据输入框里的内容切换显示  发送按钮还是加号（更多方法）
     */
    private void resetBtnUploadAndSend() {
        if (et_sendmessage.getText().toString().length() > 0) {
            sobot_btn_upload_view.setVisibility(View.GONE);
            sobot_btn_send.setVisibility(View.VISIBLE);
        } else {
            sobot_btn_send.setVisibility(View.GONE);
            sobot_btn_upload_view.setVisibility(View.VISIBLE);
            sobot_btn_upload_view.setEnabled(true);
            sobot_btn_upload_view.setClickable(true);
            if (Build.VERSION.SDK_INT >= 11) {
                sobot_btn_upload_view.setAlpha(1f);
            }
        }
    }

    /**
     * 显示表情按钮   如果没有表情资源则不会显示此按钮
     */
    private void showEmotionBtn() {
        Map<String, String> mapAll = DisplayEmojiRules.getMapAll(getSobotContext());
        if (mapAll.size() > 0) {
            btn_emoticon_view.setVisibility(View.VISIBLE);
        } else {
            btn_emoticon_view.setVisibility(View.GONE);
        }
    }


    /**
     * 输入表情的方法
     *
     * @param item
     */
    @Override
    public void inputEmoticon(EmojiconNew item) {
        SobotInputHelper.input2OSC(et_sendmessage, item);
    }

    /**
     * 输入框删除的方法
     */
    @Override
    public void backspace() {
        SobotInputHelper.backspace(et_sendmessage);
    }

    /**
     * 提供给聊天面板执行的方法
     * 图库
     */
    @Override
    public void btnPicture() {
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromLocal();
        gotoLastItem();
    }

    /**
     * 提供给聊天面板执行的方法
     * 视频
     */
    @Override
    public void btnVedio() {
        hidePanelAndKeyboard(mPanelRoot);
        selectVedioFromLocal();
        gotoLastItem();
    }

    /**
     * 提供给聊天面板执行的方法
     * 照相
     */
    @Override
    public void btnCameraPicture() {
        hidePanelAndKeyboard(mPanelRoot);
        selectPicFromCamera(); // 拍照 上传
        gotoLastItem();
    }

    //发送消息 文本 图片 视频 音频
    private void sendMsg(final String msgContent, int msgType, final String filePath, boolean isOffline, List<ChatMessageRichTextModel.ChatMessageRichListModel> richListModels) {
        et_sendmessage.setText("");
        Map<String, Object> map = new HashMap<>();
        map.put("content", msgContent);
        map.put("uid", userInfo.getId());
        map.put("cid", userInfo.getLastCid());
        if (msgType == OnlineConstant.SOBOT_MSG_TYPE_RICH) {
            map.put("objMsgType", "0");
            map.put("msgType", "5");
        } else {
            map.put("msgType", msgType + "");
        }

        final ChatMessageModel msgModel = new ChatMessageModel();
        msgModel.setAdminSendMsgType(msgType);
        msgModel.setAdminSendContent(msgContent);
        msgModel.setAdminSendFilePath(filePath);
        msgModel.setSensitive(false);
        msgModel.setT(System.currentTimeMillis());
        msgModel.setTs(SobotTimeUtils.getNowString());
        msgModel.setCid(userInfo.getLastCid());
        msgModel.setAction(5);
        msgModel.setSenderType(2);
        msgModel.setSender(admin.getAid());
        msgModel.setSenderName(admin.getNickName());
        msgModel.setIsHistory(1);
        final ChatMessageMsgModel chatMessageMsgModel = new ChatMessageMsgModel();
        chatMessageMsgModel.setMsgType(msgType + "");
        if (OnlineConstant.SOBOT_MSG_TYPE_TEXT == msgType) {
            chatMessageMsgModel.setContent(msgContent);
        } else if (OnlineConstant.SOBOT_MSG_TYPE_IMG == msgType) {
            //图片类型
            chatMessageMsgModel.setContent(filePath);
        } else if (OnlineConstant.SOBOT_MSG_TYPE_AUDIO == msgType) {
            //音频类型
            ChatMessageAudioModel audioModel = new ChatMessageAudioModel();
            audioModel.setDuration(voiceTimeLongStr);
            audioModel.setUrl(filePath);
            chatMessageMsgModel.setContent(audioModel);
        } else if (OnlineConstant.SOBOT_MSG_TYPE_VIDEO == msgType) {
            //视频类型
            File videoFile = new File(filePath);
            String fName = SobotMD5Utils.getMD5Str(videoFile.getAbsolutePath());
            final ChatMessageVideoModel chatMessageVideoModel = new ChatMessageVideoModel();
            chatMessageVideoModel.setFileName(fName);
            chatMessageVideoModel.setUrl(filePath);
            chatMessageVideoModel.setSnapshot(SobotOnlineImageUtils.getThumbPath(filePath));
            chatMessageMsgModel.setContent(chatMessageVideoModel);
        } else if (OnlineConstant.SOBOT_MSG_TYPE_RICH == msgType && richListModels != null) {
            //富文本类型
            ChatMessageRichTextModel richTextModel = new ChatMessageRichTextModel();
            richTextModel.setRichList(richListModels);
            ChatMessageObjectModel messageObjectModel = new ChatMessageObjectModel(richTextModel, 0);
            chatMessageMsgModel.setContent(messageObjectModel);
            chatMessageMsgModel.setMsgType("5");
        }
        msgModel.setMessage(chatMessageMsgModel);
        //发送中
        msgModel.setIsSendOk(2);
        if (admin != null && !TextUtils.isEmpty(admin.getFace())) {
            msgModel.setSenderFace(admin.getFace());
        }
        Message message = handler.obtainMessage();
        message.what = SobotSocketConstant.NEW_INFOMATION;
        message.obj = msgModel;
        handler.sendMessage(message);
        zhiChiApi.send(getSobotActivity(), isOffline, map, filePath, new SobotFileResultCallBack<OnlineMsgModelResult>() {
            @Override
            public void onSuccess(OnlineMsgModelResult msgModelResult) {
                if (msgModelResult == null) {
                    msgModel.setIsSendOk(0);
                    mMsgListAdapter.notifyDataSetChanged();
                    return;
                }
                if (!TextUtils.isEmpty(msgModelResult.getData().getMsgId())) {
                    msgModel.setMsgId(msgModelResult.getData().getMsgId());
                } else {
                    msgModel.setMsgId(System.currentTimeMillis() + "");
                }
                if (isFromHistory()) {
                    //离线消息
                    if (!TextUtils.isEmpty(msgModelResult.getData().getStatus())) {
                        //1-机器人,2-人工，3-排队，4-访问页面，5-微信端大于48小时，6-可以发送离线消息
                        switch (msgModelResult.getData().getStatus()) {
                            case "1":
                            case "3":
                            case "4":
                            case "5":
                                //离线消息发送失败
                                msgModel.setIsSendOk(0);
                                queryUserState();
                                break;
                            case "2":
                                //登录人和发送人是否时同一人
                                if (TextUtils.isEmpty(msgModelResult.getData().getAid()) && admin.getAid().equals(msgModelResult.getData().getAid())) {
                                    msgModel.setIsSendOk(1);
                                } else {
                                    msgModel.setIsSendOk(0);
                                    queryUserState();
                                }
                                break;
                            case "6":
                                msgModel.setIsSendOk(1);
                                break;
                            default:
                                msgModel.setIsSendOk(1);
                                break;
                        }
                    }
                } else {
                    //在线消息
                    msgModel.setIsSendOk(1);
                }
                if (OnlineConstant.result_success_code_sensitive_words.equals(msgModelResult.getRetCode())) {
                    //包含敏感词，发送失败
                    msgModel.setIsSendOk(0);
                }
                if (admin != null && !TextUtils.isEmpty(admin.getFace())) {
                    msgModel.setSenderFace(admin.getFace());
                }
                if (msgModel.getMessage().getContent() instanceof ChatMessageAudioModel) {
                    //语音类型
                    ChatMessageAudioModel audioModel = (ChatMessageAudioModel) msgModel.getMessage().getContent();
                    if (audioModel == null) {
                        audioModel = new ChatMessageAudioModel();
                    }
                    audioModel.setUrl(!TextUtils.isEmpty(msgModelResult.getData().getFileUrl()) ? msgModelResult.getData().getFileUrl() : "");
                    mMsgListAdapter.notifyDataSetChanged();
                }
                if (msgModel.getMessage().getContent() instanceof ChatMessageVideoModel) {
                    //视频类型
                    ChatMessageVideoModel chatMessageVideoModel = new ChatMessageVideoModel();
                    chatMessageVideoModel.setUrl(filePath);
                    chatMessageVideoModel.setFileName(!TextUtils.isEmpty(msgModelResult.getData().getFileName()) ? msgModelResult.getData().getFileName() : "");
                    chatMessageVideoModel.setServiceUrl(!TextUtils.isEmpty(msgModelResult.getData().getFileUrl()) ? msgModelResult.getData().getFileUrl() : "");
                    msgModel.getMessage().setContent(chatMessageVideoModel);
                }
                if (OnlineConstant.result_success_code_sensitive_words.equals(msgModelResult.getRetCode())) {
                    ChatMessageModel msgModel1 = new ChatMessageModel();
                    msgModel1.setAction(MSG_TYPE_SENSITIVE);
                    msgModel1.setMsgId(msgModelResult.getData().getMsgId());
                    msgModel1.setId(msgModelResult.getData().getMsgId());
                    msgModel1.setCid(userInfo.getLastCid());
                    msgModel1.setSenderType(2);
                    msgModel1.setMsgType(MessageType.MESSAGE_TYPE_SENSITIVE);
                    //1历史记录消息 0当前会话消息
                    msgModel1.setIsHistory(1);
                    msgModel1.setSender(admin.getAid());
                    msgModel1.setSenderName(admin.getNickName());
                    msgModel1.setRevokeFlag(1);
                    msgModel1.setSensitive(true);
                    msgModel1.setMessage(new ChatMessageMsgModel(OnlineConstant.SOBOT_MSG_TYPE_TEXT + "", msgContent));

                    Message message1 = handler.obtainMessage();
                    message1.what = SobotSocketConstant.NEW_INFOMATION;
                    message1.obj = msgModel1;
                    handler.sendMessage(message1);
                }
                if (msgModel.getIsSendOk() == 1) {
                    //通知当前会话列表更新最后一次聊天数据
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("lastMsg", msgModel.getMessage());
                    intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_LAST_MSG);
                    intent.putExtra("uid", userInfo.getId());
                    intent.putExtra("ts", SobotTimeUtils.getNowString());
                    intent.putExtra("t", SobotTimeUtils.getNowMills());
                    intent.putExtras(bundle);
                    SobotUtils.getApp().sendBroadcast(intent);
                }
                mMsgListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e, String des) {
                msgModel.setIsSendOk(0);
                mMsgListAdapter.notifyDataSetChanged();
            }

            @Override
            public void inProgress(int currentProgress) {
                SobotOnlineLogUtils.i("-----进度: " + currentProgress + "%");
            }
        });
    }


    //发送whatsapp模板消息 文本 图片 视频
    private void sendTemplateMsg(final String templateName) {
        if (whatsAppInfoModel == null) {
            whatsAppInfoModel = new SobotWhatsAppInfoModel();
        }

        if (SobotStringUtils.isEmpty(templateName)) {
            return;
        }
        Map map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(whatsAppInfoModel));
        ChatMessageWhatsAppModel whatsAppModel = null;
        if (templateName.equals(SobotTemplateName.TIME_REMINDER.getTemplateName())) {
            whatsAppModel = time_reminder;
            whatsAppModel.setTemplateName(templateName);
//            if (map != null) {
//                map.put("customParam", "");
//            }
        } else if (templateName.equals(SobotTemplateName.LOCATION_REMINDER.getTemplateName())) {
            whatsAppModel = location_reminder;
            whatsAppModel.setTemplateName(templateName);
//            if (map != null) {
//                map.put("customParam", "");
//            }
        } else if (templateName.equals(SobotTemplateName.REACH_REMINDER.getTemplateName())) {
            whatsAppModel = reach_reminder;
            whatsAppModel.setTemplateName(templateName);
//            if (map != null) {
//                map.put("customParam", "");
//            }
        } else if (templateName.equals(SobotTemplateName.REQUEST_REPLY.getTemplateName())) {
            whatsAppModel = request_reply;
            whatsAppModel.setTemplateName(templateName);
//            if (map != null) {
//                map.put("customParam", "");
//            }
        } else if (templateName.equals(SobotTemplateName.COD_CONFIRM.getTemplateName())) {
            whatsAppModel = cod_confirm;
            whatsAppModel.setTemplateName(templateName);
//            if (map != null) {
//                map.put("customParam", "");
//            }
        } else if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName())) {
            whatsAppModel = location_confirm_noncod;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
            whatsAppModel = location_confirm_cod;
            whatsAppModel.setTemplateName(templateName);
        }
        if (whatsAppModel == null) {
            whatsAppModel = new ChatMessageWhatsAppModel();
            whatsAppModel.setBodyContent(" ");
        }
        if (map != null) {
            map.put("templateName", templateName);
        }
        final ChatMessageModel msgModel = new ChatMessageModel();
        msgModel.setMsgId(System.currentTimeMillis() + "");
        msgModel.setSensitive(false);
        msgModel.setT(System.currentTimeMillis());
        msgModel.setCid(userInfo.getLastCid());
        msgModel.setAction(5);
        msgModel.setSenderType(2);
        msgModel.setSender(admin.getAid());
        msgModel.setSenderName(admin.getNickName());
        msgModel.setIsHistory(1);
        final ChatMessageMsgModel chatMessageMsgModel = new ChatMessageMsgModel();
        //whatsApp 模板类型
        ChatMessageObjectModel messageObjectModel = new ChatMessageObjectModel(whatsAppModel, OnlineConstant.SOBOT_MSG_TYPE_WHATSAPP);
        chatMessageMsgModel.setContent(messageObjectModel);
        chatMessageMsgModel.setMsgType("5");
        msgModel.setMessage(chatMessageMsgModel);
        //发送中
        msgModel.setIsSendOk(2);
        if (admin != null && !TextUtils.isEmpty(admin.getFace())) {
            msgModel.setSenderFace(admin.getFace());
        }
        Message message = handler.obtainMessage();
        message.what = SobotSocketConstant.NEW_INFOMATION;
        message.obj = msgModel;
        handler.sendMessage(message);
        zhiChiApi.sendTemplateMsg(getSobotActivity(), userInfo != null ? userInfo.getId() : "", map, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode baseCode) {
                if (baseCode != null && !TextUtils.isEmpty(baseCode.getRetCode()) && OnlineConstant.result_success_code.equals(baseCode.getRetCode())) {
                    if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName()) || templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), "The message has been successfully sent to the customer, and after customer replying you can start chatting freely.");
                        Intent resultIntent = new Intent();
                        resultIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
                        Bundle resultbundle = new Bundle();
                        resultbundle.putSerializable("sobotWhatsAppInfoModel", whatsAppInfoModel);
                        resultbundle.putBoolean("isSuccess", true);
                        resultbundle.putString("resultMsg", baseCode.getRetMsg());
                        resultIntent.putExtras(resultbundle);
                        sendBroadcast(resultIntent);
                    }

                    if (baseCode == null) {
                        msgModel.setIsSendOk(0);
                        mMsgListAdapter.notifyDataSetChanged();
                        return;
                    }
                    msgModel.setIsSendOk(1);

                    if (admin != null && !TextUtils.isEmpty(admin.getFace())) {
                        msgModel.setSenderFace(admin.getFace());
                    }
                    if (msgModel.getIsSendOk() == 1) {
                        //通知当前会话列表更新最后一次聊天数据
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("lastMsg", msgModel.getMessage());
                        intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_LAST_MSG);
                        intent.putExtra("uid", userInfo.getId());
                        intent.putExtra("ts", SobotTimeUtils.getNowString());
                        intent.putExtra("t", SobotTimeUtils.getNowMills());
                        intent.putExtras(bundle);
                        SobotUtils.getApp().sendBroadcast(intent);
                    }
                    mMsgListAdapter.notifyDataSetChanged();

                } else {
                    if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName()) || templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                        Intent resultIntent = new Intent();
                        resultIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("sobotWhatsAppInfoModel", whatsAppInfoModel);
                        bundle.putBoolean("isSuccess", false);
                        bundle.putString("resultMsg", baseCode != null ? baseCode.getRetMsg() : "");
                        resultIntent.putExtras(bundle);
                        sendBroadcast(resultIntent);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String s) {
                msgModel.setIsSendOk(0);
                mMsgListAdapter.notifyDataSetChanged();
                SobotToastUtil.showCustomToast(getSobotActivity(), s);
                if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName()) || templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                    Intent resultIntent = new Intent();
                    resultIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sobotWhatsAppInfoModel", whatsAppInfoModel);
                    bundle.putBoolean("isSuccess", false);
                    bundle.putString("resultMsg", "net error：" + s);
                    resultIntent.putExtras(bundle);
                    sendBroadcast(resultIntent);
                }
            }
        });
    }

    //发送whatsapp模板消息 文本 图片 视频
    private void reSendTemplateMsg(final String templateName, final ChatMessageModel msgModel) {
        if (SobotStringUtils.isEmpty(templateName)) {
            return;
        }
        ChatMessageWhatsAppModel whatsAppModel = null;
        if (templateName.equals(SobotTemplateName.TIME_REMINDER.getTemplateName())) {
            whatsAppModel = time_reminder;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.LOCATION_REMINDER.getTemplateName())) {
            whatsAppModel = location_reminder;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.REACH_REMINDER.getTemplateName())) {
            whatsAppModel = reach_reminder;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.REQUEST_REPLY.getTemplateName())) {
            whatsAppModel = request_reply;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.COD_CONFIRM.getTemplateName())) {
            whatsAppModel = cod_confirm;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName())) {
            whatsAppModel = location_confirm_noncod;
            whatsAppModel.setTemplateName(templateName);
        } else if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
            whatsAppModel = location_confirm_cod;
            whatsAppModel.setTemplateName(templateName);
        }
        if (whatsAppModel == null) {
            whatsAppModel = new ChatMessageWhatsAppModel();
            whatsAppModel.setBodyContent(" ");
        }
        Map map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(whatsAppInfoModel));
        if (map != null) {
            map.put("templateName", templateName);
        }

        msgModel.setSensitive(false);
        msgModel.setT(System.currentTimeMillis());
        msgModel.setCid(userInfo.getLastCid());
        msgModel.setAction(5);
        msgModel.setSenderType(2);
        msgModel.setSender(admin.getAid());
        msgModel.setSenderName(admin.getNickName());
        msgModel.setIsHistory(1);
        final ChatMessageMsgModel chatMessageMsgModel = new ChatMessageMsgModel();
        //whatsApp 模板类型
        ChatMessageObjectModel messageObjectModel = new ChatMessageObjectModel(whatsAppModel, OnlineConstant.SOBOT_MSG_TYPE_WHATSAPP);
        chatMessageMsgModel.setContent(messageObjectModel);
        chatMessageMsgModel.setMsgType("5");
        msgModel.setMessage(chatMessageMsgModel);
        //发送中
        msgModel.setIsSendOk(2);
        if (admin != null && !TextUtils.isEmpty(admin.getFace())) {
            msgModel.setSenderFace(admin.getFace());
        }
        Message message = handler.obtainMessage();
        message.what = SobotSocketConstant.NEW_INFOMATION;
        message.obj = msgModel;
        handler.sendMessage(message);
        zhiChiApi.sendTemplateMsg(getSobotActivity(), userInfo != null ? userInfo.getId() : "", map, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode baseCode) {
                if (baseCode != null && !TextUtils.isEmpty(baseCode.getRetCode()) && OnlineConstant.result_success_code.equals(baseCode.getRetCode())) {
                    if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName()) || templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), "The message has been successfully sent to the customer, and after customer replying you can start chatting freely.");
                        Intent resultIntent = new Intent();
                        resultIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
                        Bundle resultbundle = new Bundle();
                        resultbundle.putSerializable("sobotWhatsAppInfoModel", whatsAppInfoModel);
                        resultbundle.putBoolean("isSuccess", true);
                        resultbundle.putString("resultMsg", baseCode.getRetMsg());
                        resultIntent.putExtras(resultbundle);
                        sendBroadcast(resultIntent);
                    }

                    if (baseCode == null && baseCode.getRetCode() != null) {
                        msgModel.setIsSendOk(0);
                        mMsgListAdapter.notifyDataSetChanged();
                        return;
                    }
                    msgModel.setIsSendOk(1);

                    if (admin != null && !TextUtils.isEmpty(admin.getFace())) {
                        msgModel.setSenderFace(admin.getFace());
                    }
                    if (msgModel.getIsSendOk() == 1) {
                        //通知当前会话列表更新最后一次聊天数据
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("lastMsg", msgModel.getMessage());
                        intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_LAST_MSG);
                        intent.putExtra("uid", userInfo.getId());
                        intent.putExtra("ts", SobotTimeUtils.getNowString());
                        intent.putExtra("t", SobotTimeUtils.getNowMills());
                        intent.putExtras(bundle);
                        SobotUtils.getApp().sendBroadcast(intent);
                    }
                    mMsgListAdapter.notifyDataSetChanged();

                } else {
                    if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName()) || templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                        Intent resultIntent = new Intent();
                        resultIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("sobotWhatsAppInfoModel", whatsAppInfoModel);
                        bundle.putBoolean("isSuccess", false);
                        bundle.putString("resultMsg", baseCode != null ? baseCode.getRetMsg() : "");
                        resultIntent.putExtras(bundle);
                        sendBroadcast(resultIntent);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String s) {
                msgModel.setIsSendOk(0);
                mMsgListAdapter.notifyDataSetChanged();
                SobotToastUtil.showCustomToast(getSobotActivity(), s);
                if (templateName.equals(SobotTemplateName.LOCATION_CONFIRM_NONCOD.getTemplateName()) || templateName.equals(SobotTemplateName.LOCATION_CONFIRM_COD.getTemplateName())) {
                    Intent resultIntent = new Intent();
                    resultIntent.setAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sobotWhatsAppInfoModel", whatsAppInfoModel);
                    bundle.putBoolean("isSuccess", false);
                    bundle.putString("resultMsg", "net error：" + s);
                    resultIntent.putExtras(bundle);
                    sendBroadcast(resultIntent);
                }
            }
        });
    }

    //发送消息 文本 图片 视频
    private void reSendMsg(final ChatMessageModel msgModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", msgModel.getAdminSendContent());
        map.put("uid", userInfo.getId());
        map.put("cid", userInfo.getLastCid());
        if (msgModel.getAdminSendMsgType() == OnlineConstant.SOBOT_MSG_TYPE_RICH) {
            map.put("objMsgType", "0");
            map.put("msgType", "5");
        } else {
            map.put("msgType", msgModel.getAdminSendMsgType() + "");
        }
        zhiChiApi.send(getSobotActivity(), isFromHistory(), map, msgModel.getAdminSendFilePath(), new SobotFileResultCallBack<OnlineMsgModelResult>() {
            @Override
            public void onSuccess(OnlineMsgModelResult msgModelResult) {
                if (msgModelResult == null) {
                    msgModel.setIsSendOk(0);
                    mMsgListAdapter.notifyDataSetChanged();
                    return;
                }
                msgModel.setMsgId(msgModelResult.getData().getMsgId());
                if (isFromHistory()) {
                    //离线消息
                    if (msgModelResult != null && !TextUtils.isEmpty(msgModelResult.getData().getUstatus())) {
                        //离线消息发送失败
                        msgModel.setIsSendOk(0);
                        queryUserState();
                    } else {
                        msgModel.setIsSendOk(1);
                    }
                } else {
                    //在线消息
                    msgModel.setIsSendOk(1);
                }
                if (OnlineConstant.result_success_code_sensitive_words.equals(msgModelResult.getRetCode())) {
                    //包含敏感词，发送失败
                    msgModel.setIsSendOk(0);
                }
                if (OnlineConstant.result_success_code_sensitive_words.equals(msgModelResult.getRetCode())) {
                    //包含敏感词，发送失败
                    msgModel.setIsSendOk(0);
                }
                msgModel.setT(System.currentTimeMillis());
                if (OnlineConstant.result_success_code_sensitive_words.equals(msgModelResult.getRetCode())) {
                    ChatMessageModel msgModel1 = new ChatMessageModel();
                    msgModel1.setAction(MSG_TYPE_SENSITIVE);
                    msgModel1.setMsgId(msgModelResult.getData().getMsgId());
                    msgModel1.setId(msgModelResult.getData().getMsgId());
                    msgModel1.setCid(userInfo.getLastCid());
                    msgModel1.setSenderType(2);
                    msgModel1.setMsgType(MessageType.MESSAGE_TYPE_SENSITIVE);
                    //1历史记录消息 0当前会话消息
                    msgModel1.setIsHistory(1);
                    msgModel1.setSender(admin.getAid());
                    msgModel1.setSenderName(admin.getNickName());
                    msgModel1.setRevokeFlag(1);
                    msgModel1.setSensitive(true);
                    msgModel1.setMessage(new ChatMessageMsgModel(OnlineConstant.SOBOT_MSG_TYPE_TEXT + "", msgModel.getAdminSendContent()));

                    Message message1 = handler.obtainMessage();
                    message1.what = SobotSocketConstant.NEW_INFOMATION;
                    message1.obj = msgModel1;
                    handler.sendMessage(message1);
                }
                if (msgModel.getIsSendOk() == 1) {
                    //通知当前会话列表更新最后一次聊天数据
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("lastMsg", msgModel.getMessage());
                    intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_LAST_MSG);
                    intent.putExtra("uid", userInfo.getId());
                    intent.putExtra("ts", SobotTimeUtils.getNowString());
                    intent.putExtra("t", SobotTimeUtils.getNowMills());
                    intent.putExtras(bundle);
                    SobotUtils.getApp().sendBroadcast(intent);
                }
                mMsgListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e, String des) {
                msgModel.setIsSendOk(0);
                mMsgListAdapter.notifyDataSetChanged();
            }

            @Override
            public void inProgress(int currentProgress) {
                SobotOnlineLogUtils.i("-----进度: " + currentProgress + "%");
            }
        });
    }


    /**
     * 查询用户当前状态
     * 用来作为是否能发送离线消息的判断依据
     */
    private void queryUserState() {
        Map map = null;
        if (whatsAppInfoModel != null) {
            map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(whatsAppInfoModel));
        }
        if ((userInfo == null || TextUtils.isEmpty(userInfo.getId().trim())) && whatsAppInfoModel == null) {
            //uid 和 模板配置信息必须有一个
            return;
        }
        zhiChiApi.getStatusNow(getSobotActivity(), userInfo == null ? "" : userInfo.getId(), map, new SobotResultCallBack<OfflineMsgModel>() {
            @Override
            public void onSuccess(OfflineMsgModel offlineMsgModel) {
                if (offlineMsgModel != null) {
                    if (SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_INPUTBOX.equals(offlineMsgModel.getStatus())) {
                        //发送离线消息
                        flag_from_page = "history";
                    }
                    if (userInfo == null) {
                        userInfo = offlineMsgModel.getUser();
                    } else {
                        if (offlineMsgModel.getUser() != null && !TextUtils.isEmpty(offlineMsgModel.getUser().getParams())) {
                            userInfo.setParams(offlineMsgModel.getUser().getParams());
                        }
                    }
                    if (userInfo != null) {
                        inorout("0");
                        if (!TextUtils.isEmpty(userInfo.getSource())) {
                            userSource = Integer.parseInt(userInfo.getSource());
                        }
                        if (TextUtils.isEmpty(userInfo.getUname()) && whatsAppInfoModel != null) {
                            userInfo.setUname(whatsAppInfoModel.getUserNick());
                        }
                        if (whatsAppInfoModel == null && !TextUtils.isEmpty(userInfo.getParams())) {
                            SobotWhatsAppInfoModel appInfoModel = SobotGsonUtil.gsonToBean(userInfo.getParams(), SobotWhatsAppInfoModel.class);
                            if (appInfoModel != null) {
                                whatsAppInfoModel = appInfoModel;
                            }
                        }
                        if (!TextUtils.isEmpty(offlineMsgModel.getUser().getRealname())) {
                            userInfo.setRealname(offlineMsgModel.getUser().getRealname());
                        }
                        //头部显示
                        sobot_online_username_back_tv.setText(userInfo != null ? userInfo.getUname() : "");
                        if (!TextUtils.isEmpty(userInfo.getRealname())) {
                            ll_ordernum.setVisibility(View.VISIBLE);
                            tv_ordernum.setText(userInfo.getRealname());
                        } else {
                            ll_ordernum.setVisibility(View.GONE);
                        }
                    }
                    location_confirm_noncod = offlineMsgModel.getLocation_confirm_noncod();
                    location_confirm_cod = offlineMsgModel.getLocation_confirm_cod();
                    time_reminder = offlineMsgModel.getTime_reminder();
                    location_reminder = offlineMsgModel.getLocation_reminder();
                    reach_reminder = offlineMsgModel.getReach_reminder();
                    request_reply = offlineMsgModel.getRequest_reply();
                    cod_confirm = offlineMsgModel.getCod_confirm();
                    currentCidPosition = 0;
                    data.clear();
                    //初始化查询cid
                    queryCids();
                    initData();
                }
                updataOfflineMsgUi(offlineMsgModel);
            }

            @Override
            public void onFailure(Exception e, String des) {
                //隐藏底部快捷菜单
                ll_custom_menu.setVisibility(View.VISIBLE);
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }

    private void updataOfflineMsgUi(OfflineMsgModel data) {
        if (data != null && !TextUtils.isEmpty(data.getStatus()) && (SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_ROBOT.equals(data.getStatus())
                || (SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_CUSTOMER.equals(data.getStatus()))
                || SobotSocketConstant.TYPE_OFFLINEMSG_LINEUP.equals(data.getStatus())
                || SobotSocketConstant.TYPE_OFFLINEMSG_VISITOR.equals(data.getStatus())
                || SobotSocketConstant.TYPE_OFFLINEMSG_WECHAT_USER_OUTTIME.equals(data.getStatus())
                || SobotSocketConstant.TYPE_OFFLINEMSG_DISPLAY_INPUTBOX.equals(data.getStatus()))
                || SobotSocketConstant.TYPE_LOCK_IN.equals(data.getStatus())) {
            flag_from_page = "history";
            if (SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_CUSTOMER.equals(data.getStatus())) {
                flag_from_page = "online";
            }
            hidePanelAndKeyboard(mPanelRoot);
            sobot_ll_bottom.setVisibility(View.GONE);
            //隐藏底部快捷菜单
            ll_custom_menu.setVisibility(View.VISIBLE);
            if (SobotSocketConstant.TYPE_LOCK_IN.equals(data.getStatus())) {
                sobot_ll_bottom_lock.setVisibility(View.VISIBLE);
                rl_bottom_offline_state.setVisibility(View.GONE);
                rl_bottom_offline_state.setViewType(userInfo.getId(), data, this);
                tv_online_close_chat.setVisibility(View.GONE);
                iv_online_user_biaoji.setVisibility(View.GONE);
            } else {
                rl_bottom_offline_state.setVisibility(View.VISIBLE);
                rl_bottom_offline_state.setViewType(userInfo.getId(), data, this);
                sobot_ll_bottom_lock.setVisibility(View.GONE);
            }
        } else {
            sobot_ll_bottom.setVisibility(View.VISIBLE);
            sobot_ll_bottom_lock.setVisibility(View.GONE);
            if (isFromOnline()) {
                tv_online_close_chat.setVisibility(View.VISIBLE);
            } else {
                tv_online_close_chat.setVisibility(View.GONE);
            }
            //                        iv_online_user_biaoji.setVisibility(View.VISIBLE);
            iv_online_user_biaoji.setVisibility(View.GONE);
        }
        if (data != null && SobotSocketConstant.TYPE_OFFLINEMSG_CHAT_TO_CUSTOMER.equals(data.getStatus()) && data.getAid().equals(admin.getAid())) {
            //如果当前客户正在和客服自己聊天，改为在线，可以继续聊天
            flag_from_page = "online";
            sobot_ll_bottom.setVisibility(View.VISIBLE);
            sobot_ll_bottom_lock.setVisibility(View.GONE);
            rl_bottom_offline_state.setVisibility(View.GONE);
            userInfo.setLastCid(data.getCid());
            //显示底部快捷菜单
            ll_custom_menu.setVisibility(View.VISIBLE);
            tv_online_close_chat.setVisibility(View.VISIBLE);
//                        iv_online_user_biaoji.setVisibility(View.VISIBLE);
            iv_online_user_biaoji.setVisibility(View.GONE);
        }
    }


    private void sendCloseBroadCast() {
        Intent intent = new Intent();
        intent.setAction("com.sobot.close.beforeactivity");
        sendBroadcast(intent);
    }

    private void sendClose1BroadCast() {
        Intent intent = new Intent();
        intent.setAction("com.sobot.close.beforeactivity1");
        sendBroadcast(intent);
    }

    @Override
    public void onOfflineStateCallBack(int eventType) {
        switch (eventType) {
            case OfflineStateView.showinputbox:
                sobot_ll_bottom.setVisibility(View.VISIBLE);
                sobot_ll_bottom_lock.setVisibility(View.GONE);
                ll_custom_menu.setVisibility(View.VISIBLE);
                rl_bottom_offline_state.setVisibility(View.GONE);
                //                        iv_online_user_biaoji.setVisibility(View.VISIBLE);
                iv_online_user_biaoji.setVisibility(View.GONE);
                break;
            case OfflineStateView.showNetError:
                break;

        }
    }

    /**
     * 解除用户黑名单
     */
    private void removeBlackUsers() {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getId())) {
            return;
        }
        SobotDialogUtils.startProgressDialog(getSobotContext());
        zhiChiApi.addOrDeleteBlackList(getSobotActivity(), userInfo.getId(), "", 0, SobotOnlineUrlApi.api_deleteBlackList, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode OnlineBaseCode) {
                SobotDialogUtils.stopProgressDialog(getSobotContext());
                if (OnlineBaseCode != null) {
                    SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_remove_user_black_success));
                    iv_online_user_pullblack.setBackgroundResource(R.drawable.sobot_online_lahei_def);
                    userInfo.setIsblack(0);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
                SobotDialogUtils.stopProgressDialog(getSobotContext());
            }
        });
    }

    /**
     * 标记用户的功能
     *
     * @param isAddOrDeleteMark true 标记用户 false 取消标记
     */
    private void markUsers(final boolean isAddOrDeleteMark) {
        final String url;
        if (isAddOrDeleteMark) {
            url = SobotOnlineUrlApi.api_addMarkList;
        } else {
            url = SobotOnlineUrlApi.api_deleteMarkList;
        }

        if (userInfo == null || TextUtils.isEmpty(userInfo.getId())) {
            return;
        }
        SobotDialogUtils.startProgressDialog(getSobotContext());
        zhiChiApi.addOrDeleteMarkList(getSobotActivity(), userInfo.getId(), url,
                new SobotResultCallBack<OnlineBaseCode>() {
                    @Override
                    public void onSuccess(OnlineBaseCode OnlineBaseCode) {
                        SobotDialogUtils.stopProgressDialog(getSobotContext());
                        if (OnlineBaseCode != null) {
                            if (isAddOrDeleteMark) {
                                userInfo.setIsmark(1);
                                SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_add_user_mark_success));
                                iv_online_user_biaoji.setBackgroundResource(R.drawable.sobot_online_biaoji_sel);
                            } else {
                                userInfo.setIsmark(0);
                                SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_remove_user_mark_success));
                                iv_online_user_biaoji.setBackgroundResource(R.drawable.sobot_online_biaoji_def);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.putExtra("uid", userInfo.getId());
                                    intent.putExtra("markStatus", userInfo.getIsmark());
                                    intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_MARK);
                                    SobotUtils.getApp().sendBroadcast(intent);
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String des) {
                        SobotToastUtil.showCustomToast(getSobotContext(), des);
                        SobotDialogUtils.stopProgressDialog(getSobotContext());
                    }
                });
    }

    public void invateEvaluate() {
        if (userInfo == null)
            return;
        //邀请评价
        SobotOnlineLogUtils.i("主动邀请评价 start");
        zhiChiApi.invateEvaluateInt(getSobotActivity(), userInfo.getLastCid(), new SobotResultCallBack<OnlineCommonModel>() {
            @Override
            public void onSuccess(OnlineCommonModel evaluateResult) {
                if (evaluateResult != null) {
                    //成功
                    if ("0".equals(evaluateResult.getStatus())) {
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setId(System.currentTimeMillis() + "");
                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                        msgModel.setTs(SobotTimeUtils.getNowString());
                        msgModel.setMsg(getString(R.string.online_send_evaluate));
                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_send_evaluate)));
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
                        handler.sendMessage(message);
                    } else if ("1".equals(evaluateResult.getStatus())) {
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setId(System.currentTimeMillis() + "");
                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                        msgModel.setMsg(getString(R.string.online_user_already_evaluate));
                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_user_already_evaluate)));
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
                        handler.sendMessage(message);
                    } else if ("2".equals(evaluateResult.getStatus())) {
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setId(System.currentTimeMillis() + "");
                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                        msgModel.setMsg(getString(R.string.online_no_support_evaluate));
                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_no_support_evaluate)));
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
                        handler.sendMessage(message);
                    } else if ("3".equals(evaluateResult.getStatus())) {
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setId(System.currentTimeMillis() + "");
                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                        msgModel.setMsg(getString(R.string.online_evaluate_need_zixun));
                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_evaluate_need_zixun)));
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
                        handler.sendMessage(message);
                    } else if ("4".equals(evaluateResult.getStatus())) {
                        ChatMessageModel msgModel = new ChatMessageModel();
                        msgModel.setId(System.currentTimeMillis() + "");
                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                        msgModel.setMsg(getString(R.string.online_fuwu_exception));
                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_fuwu_exception)));
                        Message message = new Message();
                        message.what = SobotSocketConstant.NEW_INFOMATION;
                        message.obj = msgModel;
                        handler.sendMessage(message);
                    } else if ("5".equals(evaluateResult.getStatus())) {
                        zhiChiApi.invateEvaluateIntOff(getSobotActivity(), userInfo.getLastCid(), new SobotResultCallBack<OnlineCommonModel>() {
                            @Override
                            public void onSuccess(OnlineCommonModel commonModelResult) {
                                if (commonModelResult != null && !TextUtils.isEmpty(commonModelResult.getStatus())) {
                                    //成功
                                    if (commonModelResult != null && "1".equals(commonModelResult.getStatus())) {
                                        ChatMessageModel msgModel = new ChatMessageModel();
                                        msgModel.setId(System.currentTimeMillis() + "");
                                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                                        msgModel.setMsg(getString(R.string.online_user_already_evaluate));
                                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_user_already_evaluate)))
                                        ;
                                        Message message = new Message();
                                        message.what = SobotSocketConstant.NEW_INFOMATION;
                                        message.obj = msgModel;
                                        handler.sendMessage(message);
                                    } else {
                                        ChatMessageModel msgModel = new ChatMessageModel();
                                        msgModel.setId(System.currentTimeMillis() + "");
                                        msgModel.setAction(MSG_TYPE_EVALUATE1);
                                        msgModel.setMsg(getString(R.string.online_cannt_evaluate));
                                        msgModel.setMessage(new ChatMessageMsgModel("0", getString(R.string.online_cannt_evaluate)));
                                        Message message = new Message();
                                        message.what = SobotSocketConstant.NEW_INFOMATION;
                                        message.obj = msgModel;
                                        handler.sendMessage(message);

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Exception e, String des) {

                            }
                        });
                    }
                    //Intent intent = new Intent();
                    //intent.setAction(SobotSocketConstant.BROADCAST_CUSTOM_COMITSUMMARY);
                    //intent.putExtra("cid", userInfo.getLastCid());
                    //sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            SobotOnlineLogUtils.i("onActivityResult返回的结果：" + requestCode + "--" + resultCode + "--" + data);
            if (resultCode == RESULT_OK) {
                if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_LAHEI) {
                    int isBlack = data.getIntExtra("isBlack", 0);
                    if (isBlack == 1) {
                        iv_online_user_pullblack.setBackgroundResource(R.drawable.sobot_online_lahei_sel);
                        if (userInfo != null) {
                            userInfo.setIsblack(isBlack);
                        }
                    }
                }

                if (requestCode == SobotBaseConstant.REQUEST_CODE_PICTURE) { //发送本地图片
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        String path = SobotImageUtils.getPath(getSobotActivity(), selectedImage);
                        File selectedFile = new File(path);
                        if (selectedFile.exists()) {
                            if (selectedFile.length() > 5 * 1024 * 1024) {
                                SobotToastUtil.showCustomToast(getApplicationContext(), getString(R.string.sobot_image_upload_failed));
                                return;
                            }
                        }
                        if (SobotMediaFileUtils.isVideoFileType(path)) {
                            File videoFile = new File(path);
                            if (videoFile.exists()) {
                                sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_VIDEO, path, isFromHistory(), null);
                            }
                        } else {
                            sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_IMG, path, isFromHistory(), null);
                        }
                    } else {
                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_did_not_get_picture_path));
                    }

                }
                if (requestCode == SobotBaseConstant.REQUEST_CODE_VIDEO) { // 发送本地视频
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        String path = SobotImageUtils.getPath(getSobotActivity(), selectedImage);
                        if (SobotMediaFileUtils.isVideoFileType(path)) {
                            File videoFile = new File(path);
                            if (videoFile.exists()) {
                                if (videoFile.length() > 15 * 1024 * 1024) {
                                    SobotToastUtil.showCustomToast(getApplicationContext(), getString(R.string.sobot_video_upload_failed));
                                    return;
                                }
                                sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_VIDEO, path, isFromHistory(), null);
                            }
                        } else {
                            sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_IMG, path, isFromHistory(), null);
                        }
                    } else {
                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_did_not_get_picture_path));
                    }

                }
                hidePanelAndKeyboard(mPanelRoot);
            }

            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_CAMERA) {
                int actionType = SobotCameraActivity.getActionType(data);
                if (actionType == SobotCameraActivity.ACTION_TYPE_VIDEO) {
                    File videoFile = new File(SobotCameraActivity.getSelectedVideo(data));
                    if (videoFile.exists()) {
                        if (videoFile.length() > 15 * 1024 * 1024) {
                            SobotToastUtil.showCustomToast(getApplicationContext(), getString(R.string.sobot_video_upload_failed));
                            return;
                        }
                        sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_VIDEO, videoFile.getAbsolutePath(), isFromHistory(), null);
                    } else {
                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_pic_select_again));
                    }
                } else {
                    File tmpPic = new File(SobotCameraActivity.getSelectedImage(data));
                    if (tmpPic.exists()) {
                        if (tmpPic.length() > 5 * 1024 * 1024) {
                            SobotToastUtil.showCustomToast(getApplicationContext(), getString(R.string.sobot_image_upload_failed));
                            return;
                        }
                        sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_IMG, tmpPic.getAbsolutePath(), isFromHistory(), null);
                    } else {
                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_pic_select_again));
                    }
                }
            }
            //发送快捷回复消息
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_QUICK_REPLY && data != null) {
                String sendContent = data.getStringExtra("sendContent");
                boolean isAutoSend = data.getBooleanExtra("isAutoSend", false);
                if (isAutoSend) {
                    if (TextUtils.isEmpty(sendContent)) {
                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_no_empty));
                        return;
                    }
                    sendMsg(sendContent, OnlineConstant.SOBOT_MSG_TYPE_TEXT, "", isFromHistory(), null);
                } else {
                    et_sendmessage.setText(TextUtils.isEmpty(sendContent) ? "" : sendContent);
                }
            }
            //发送智能回复消息
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_INTELLIGENCE_REPLY && data != null) {
                List<ChatMessageRichTextModel.ChatMessageRichListModel> richListModels = (List<ChatMessageRichTextModel.ChatMessageRichListModel>) data.getSerializableExtra("richList");
                String sendContent = data.getStringExtra("sendContent");
                int sendType = data.getIntExtra("sendType", -1);
                boolean isAutoSend = data.getBooleanExtra("isAutoSend", false);
                hidePanelAndKeyboard(mPanelRoot);
                if (isAutoSend) {
                    if (TextUtils.isEmpty(sendContent)) {
                        SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_no_empty));
                        return;
                    }
                    sendMsg(sendContent, sendType, "", isFromHistory(), richListModels);
                } else {
                    et_sendmessage.setText(TextUtils.isEmpty(sendContent) ? "" : sendContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clickAudioItem(final ChatMessageModel message, ImageView ivAudioPlay) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isVoideIsPlaying()) {
                data.get(i).setVoideIsPlaying(false);
            }
        }
        initAudioManager();
        requestAudioFocus();
        if (mAudioPlayPresenter == null) {
            mAudioPlayPresenter = new AudioPlayPresenter(getSobotContext());
        }
        if (mAudioPlayCallBack == null) {
            mAudioPlayCallBack = new AudioPlayCallBack() {
                @Override
                public void onPlayStart(ChatMessageModel messageModel, ImageView ivAudioPlay) {
                    startAnim(message, ivAudioPlay, messageModel.getSenderType() == 0);
                }

                @Override
                public void onPlayEnd(ChatMessageModel messageModel, ImageView ivAudioPlay) {
                    stopAnim(message, ivAudioPlay);
                }
            };
        }
        mAudioPlayPresenter.clickAudio(message, mAudioPlayCallBack, ivAudioPlay);
    }

    @Override
    public void reSendMsg(ChatMessageModel message, ImageView sendStatusIV) {
        if (message != null) {
            if (message.getMessage() != null && !TextUtils.isEmpty(message.getMessage().getMsgType())) {
                //whatsap模板消息重复单独处理
                if ("5".equals(message.getMessage().getMsgType().trim())) {
                    ChatMessageObjectModel messageObjectModel = null;
                    if (message.getMessage().getContent() != null) {
                        String temp = SobotGsonUtil.gsonString(message.getMessage().getContent());
                        if (!TextUtils.isEmpty(temp) && temp.contains("msg")) {
                            messageObjectModel = SobotGsonUtil.gsonToBean(temp, ChatMessageObjectModel.class);
                            if (messageObjectModel != null && messageObjectModel.getType() == OnlineConstant.SOBOT_MSG_TYPE_WHATSAPP && messageObjectModel.getMsg() != null) {
                                String msgtemp = SobotGsonUtil.gsonString(messageObjectModel.getMsg());
                                if (!TextUtils.isEmpty(msgtemp)) {
                                    ChatMessageWhatsAppModel appModel = SobotGsonUtil.gsonToBean(msgtemp, ChatMessageWhatsAppModel.class);
                                    if (appModel != null) {
                                        reSendTemplateMsg(appModel.getTemplateName(), message);
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            }
            reSendMsg(message);
        }
    }

    // 开始播放
    public void startAnim(ChatMessageModel message, ImageView voicePlay, boolean isLeft) {
        message.setVoideIsPlaying(true);
        Drawable playDrawable = voicePlay.getDrawable();
        if (playDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) playDrawable).start();
        } else {
            voicePlay.setImageResource(isLeft ?
                    R.drawable.sobot_voice_from_icon : R.drawable.sobot_voice_to_icon);
            Drawable rePlayDrawable = voicePlay.getDrawable();
            if (rePlayDrawable != null
                    && rePlayDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) rePlayDrawable).start();
            }
        }
    }

    // 关闭播放
    public void stopAnim(ChatMessageModel message, ImageView voicePlay) {
        message.setVoideIsPlaying(false);
        Drawable playDrawable = voicePlay.getDrawable();
        if (playDrawable != null
                && playDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) playDrawable).stop();
            ((AnimationDrawable) playDrawable).selectDrawable(2);
        }
    }

    private void showAudioRecorder() {
        try {
            mFileName = SobotPathManager.getInstance().getVoiceDir() + "sobot_tmp.amr";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                SobotLogUtils.i("SD Card is not mounted,It is  " + state + ".");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                SobotLogUtils.i("Path to file could not be created");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(true);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    hidePanelAndKeyboard(mPanelRoot);
                    editModelToVoice(View.VISIBLE);// 编辑模式显示
                    if (btn_press_to_speak.getVisibility() == View.VISIBLE) {
                        btn_press_to_speak.setVisibility(View.VISIBLE);
                        btn_press_to_speak.setClickable(true);
                        btn_press_to_speak.setOnTouchListener(new PressToSpeakListen());
                        btn_press_to_speak.setEnabled(true);
                        txt_speak_content.setText(getResString("sobot_press_say"));
                        txt_speak_content.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNoPermission() {
                    SobotToastUtil.showCustomToast(getApplicationContext(), getResString("sobot_app_no_record_audio_permission"));
                }
            });
            stopVoice();
        } catch (Exception e) {
            SobotOnlineLogUtils.i("prepare() failed");
        }
    }

    // 键盘编辑模式转换为语音模式
    private void editModelToVoice(int typeModel) {
        btn_press_to_speak.setVisibility(View.GONE != typeModel ? View.VISIBLE
                : View.GONE);
        et_sendmessage.setVisibility(View.VISIBLE == typeModel ? View.GONE
                : View.VISIBLE);

    }

    /**
     * 开始录音
     */
    private void startVoice() {
        try {
            stopVoice();
            mFileName = SobotPathManager.getInstance().getVoiceDir() + UUID.randomUUID().toString() + ".amr";
            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                SobotOnlineLogUtils.i("sd卡被卸载了");
            }
            File directory = new File(mFileName).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                SobotOnlineLogUtils.i("文件夹创建失败");
            }
            extAudioRecorder = ExtAudioRecorder.getInstanse(true);
            extAudioRecorder.setOutputFile(mFileName);
            extAudioRecorder.prepare();
            extAudioRecorder.start(new ExtAudioRecorder.AudioRecorderListener() {
                @Override
                public void onHasPermission() {
                    startMicAnimate();
                    SobotLogUtils.d("=====start===========");
                    startVoiceTimeTask();
                    sendVoiceMap(0, voiceMsgId);
                }

                @Override
                public void onNoPermission() {
                    SobotToastUtil.showCustomToast(getApplicationContext(), getString(R.string.sobot_app_no_record_audio_permission));
                }
            });
        } catch (Exception e) {
            SobotOnlineLogUtils.i("prepare() failed");
        }
    }

    /* 停止录音 */
    private void stopVoice() {
        /* 布局的变化 */
        try {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isVoideIsPlaying()) {
                    data.get(i).setVoideIsPlaying(true);
                }
                mMsgListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
        try {
            if (extAudioRecorder != null) {
                stopVoiceTimeTask();
                extAudioRecorder.stop();
                extAudioRecorder.release();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 录音的时间控制
     */
    public void startVoiceTimeTask() {
        SobotLogUtils.d("===startVoiceTimeTask========");
        voiceTimerLong = 0;
        stopVoiceTimeTask();
        voiceTimer = new Timer();
        voiceTimerTask = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                voiceTimerLong += 500;
                sendVoiceTimeTask();
            }
        };
        // 500ms进行定时任务
        voiceTimer.schedule(voiceTimerTask, 0, 500);
    }

    /**
     * 发送声音的定时的任务
     */
    public void sendVoiceTimeTask() {
        // 录音的时间超过一分钟的时间切断进行发送语音
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SobotLogUtils.d("========sendVoiceTimeTask:" + voiceTimerLong);
                if (voiceTimerLong >= minRecordTime * 1000) {
                    isCutVoice = true;
                    voiceCuttingMethod();
                    voiceTimerLong = 0;
                    recording_hint.setText(R.string.sobot_voiceTooLong);
                    recording_hint.setBackgroundResource(R.drawable.sobot_recording_text_hint_bg);
                    recording_timeshort.setVisibility(View.VISIBLE);
                    mic_image.setVisibility(View.GONE);
                    mic_image_animate.setVisibility(View.GONE);
                    closeVoiceWindows(2);
                    btn_press_to_speak.setPressed(false);
                    currentVoiceLong = 0;
                } else {
                    final int time = voiceTimerLong;
                    //					LogUtils.i("录音定时任务的时长：" + time);
                    currentVoiceLong = time;
                    if (time < recordDownTime * 1000) {
                        if (time % 1000 == 0) {
                            voiceTimeLongStr = SobotTimeUtils.calculatTime(time);
                            voice_time_long.setText(voiceTimeLongStr.substring(0));
                        }
                    } else if (time <= minRecordTime * 1000) {
                        if (time % 1000 == 0) {
                            voiceTimeLongStr = SobotTimeUtils.calculatTime(time);
                            voice_time_long.setText(getString(R.string.sobot_count_down) + (minRecordTime * 1000 - time) / 1000);
                        }
                    } else {
                        voice_time_long.setText(getString(R.string.sobot_voiceTooLong));
                    }
                }
            }
        });
    }

    // 当时间超过1秒的时候自动发送
    public void voiceCuttingMethod() {
        voiceTimeLongStr = SobotTimeUtils.calculatTime(minRecordTime * 1000);
        stopVoice();
        sendVoiceMap(1, voiceMsgId);
        voice_time_long.setText(voiceTimeLongStr);
    }

    public void stopVoiceTimeTask() {
        if (voiceTimer != null) {
            voiceTimer.cancel();
            voiceTimer = null;
        }
        if (voiceTimerTask != null) {
            voiceTimerTask.cancel();
            voiceTimerTask = null;
        }
        voiceTimerLong = 0;
    }

    /**
     * 发送语音的方式
     *
     * @param type       0：正在录制语音。  1：发送语音。2：取消正在录制的语音显示
     * @param voiceMsgId 语音消息ID
     */
    private void sendVoiceMap(int type, String voiceMsgId) {
        // 发送语音的界面
        if (type == 0) {

        } else if (type == 2) {

        } else {
            // 发送http 返回发送成功的按钮
            sendMsg("", OnlineConstant.SOBOT_MSG_TYPE_AUDIO, mFileName, isFromHistory(), null);
        }
        gotoLastItem();
    }

    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            isCutVoice = false;
            // 获取说话位置的点击事件
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    //暂停播放语音
                    if (AudioTools.getInstance().isPlaying()) {
                        //停止播放
                        AudioTools.getInstance().stop();
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).isVoideIsPlaying()) {
                                data.get(i).setVoideIsPlaying(false);
                            }
                        }
                        mMsgListAdapter.notifyDataSetChanged();

                    }
                    //放弃音频焦点
                    abandonAudioFocus();
                    voiceMsgId = System.currentTimeMillis() + "";
                    stopVoiceTimeTask();
                    v.setPressed(true);
                    voice_time_long.setText("00:00");
                    voiceTimeLongStr = "00:00";
                    voiceTimerLong = 0;
                    currentVoiceLong = 0;
                    recording_container.setVisibility(View.VISIBLE);
                    voice_top_image.setVisibility(View.VISIBLE);
                    mic_image.setVisibility(View.VISIBLE);
                    mic_image_animate.setVisibility(View.VISIBLE);
                    voice_time_long.setVisibility(View.VISIBLE);
                    recording_timeshort.setVisibility(View.GONE);
                    image_endVoice.setVisibility(View.GONE);
                    txt_speak_content.setText(getResString("sobot_up_send"));
                    // 设置语音的定时任务
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            startVoice();
                        }
                    });
                    return true;
                // 第二根手指按下
                case MotionEvent.ACTION_POINTER_DOWN:
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    return true;
                case MotionEvent.ACTION_MOVE: {

                    if (event.getY() < 10) {
                        // 取消界面的显示
                        voice_top_image.setVisibility(View.GONE);
                        image_endVoice.setVisibility(View.VISIBLE);
                        mic_image.setVisibility(View.GONE);
                        mic_image_animate.setVisibility(View.GONE);
                        recording_timeshort.setVisibility(View.GONE);
                        txt_speak_content.setText(R.string.sobot_release_to_cancel);
                        recording_hint.setText(R.string.sobot_release_to_cancel);
                        recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                    } else {
                        if (voiceTimerLong != 0) {
                            txt_speak_content.setText(R.string.sobot_up_send);
                            voice_top_image.setVisibility(View.VISIBLE);
                            mic_image_animate.setVisibility(View.VISIBLE);
                            image_endVoice.setVisibility(View.GONE);
                            mic_image.setVisibility(View.VISIBLE);
                            recording_timeshort.setVisibility(View.GONE);
                            recording_hint.setText(R.string.sobot_move_up_to_cancel);
                            recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg1"));
                        }
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    // 手指抬起的操作
                    int toLongOrShort = 0;
                    v.setPressed(false);
                    txt_speak_content.setText(R.string.sobot_press_say);
                    stopVoiceTimeTask();
                    stopVoice();
                    if (recording_container.getVisibility() == View.VISIBLE
                            && !isCutVoice) {
                        hidePanelAndKeyboard(mPanelRoot);
                        if (animationDrawable != null) {
                            animationDrawable.stop();
                        }
                        voice_time_long.setText("00:00");
                        voice_time_long.setVisibility(View.INVISIBLE);
                        if (event.getY() < 0) {
                            recording_container.setVisibility(View.GONE);
                            sendVoiceMap(2, voiceMsgId);
                            return true;
                            // 取消发送语音
                        } else {
                            // 发送语音
                            if (currentVoiceLong < 1 * 1000) {
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(R.string.sobot_voice_time_short);
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                voice_time_long.setVisibility(View.VISIBLE);
                                voice_time_long.setText("00:00");
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                                toLongOrShort = 0;
                                sendVoiceMap(2, voiceMsgId);
                            } else if (currentVoiceLong < minRecordTime * 1000) {
                                recording_container.setVisibility(View.GONE);
                                sendVoiceMap(1, voiceMsgId);
                                return true;
                            } else if (currentVoiceLong > minRecordTime * 1000) {
                                toLongOrShort = 1;
                                voice_top_image.setVisibility(View.VISIBLE);
                                recording_hint.setText(R.string.sobot_voiceTooLong);
                                recording_hint.setBackgroundResource(getResDrawableId("sobot_recording_text_hint_bg"));
                                recording_timeshort.setVisibility(View.VISIBLE);
                                mic_image.setVisibility(View.GONE);
                                mic_image_animate.setVisibility(View.GONE);
                            } else {
                                sendVoiceMap(2, voiceMsgId);
                            }
                        }
                        currentVoiceLong = 0;
                        closeVoiceWindows(toLongOrShort);
                    } else {
                        sendVoiceMap(2, voiceMsgId);
                    }
                    voiceTimerLong = 0;
                    return true;
                default:
                    sendVoiceMap(2, voiceMsgId);
                    closeVoiceWindows(2);
                    return true;
            }
        }
    }

    /**
     * 按住说话动画开始
     */
    private void startMicAnimate() {
        mic_image_animate.setBackgroundResource(R.drawable.sobot_voice_animation);
        animationDrawable = (AnimationDrawable) mic_image_animate.getBackground();
        mic_image_animate.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });
        recording_hint.setText(R.string.sobot_move_up_to_cancel);
        recording_hint.setBackgroundResource(R.drawable.sobot_recording_text_hint_bg1);
    }

    public void closeVoiceWindows(final int toLongOrShort) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txt_speak_content.setText(R.string.sobot_press_say);
                currentVoiceLong = 0;
                recording_container.setVisibility(View.GONE);

                if (toLongOrShort == 0) {
                    for (int i = data.size() - 1; i > 0; i--) {
                        if (data.get(i).getSenderType() == 8) {
                            data.remove(i);
                            break;
                        }
                    }
                }
            }
        }, 500);
    }

    // 设置听筒模式或者是正常模式的转换
    public void initAudioManager() {
        if (audioManager == null)
            audioManager = (AudioManager) getSobotActivity().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(true);// 打开扬声器
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // TBD 继续播放
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        // TBD 停止播放
                        if (AudioTools.getInstance().isPlaying()) {
                            AudioTools.getInstance().stop();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // TBD 暂停播放
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // TBD 混音播放
                        break;
                    default:
                        break;
                }

            }
        };
        //android 版本 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAttribute = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
        }
        //android 版本 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setWillPauseWhenDucked(true)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener, new Handler())
                    .setAudioAttributes(mAttribute)
                    .build();
        }
    }

    //请求音频焦点
    public void requestAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null)
                    audioManager.requestAudioFocus(mFocusRequest);
            } else {
                if (audioFocusChangeListener != null)
                    //AUDIOFOCUS_GAIN_TRANSIENT 只是短暂获得，一会就释放焦点
                    audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        }

    }

    //放弃音频焦点
    public void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null)
                    audioManager.abandonAudioFocusRequest(mFocusRequest);
            } else {
                if (audioFocusChangeListener != null) {
                    audioManager.abandonAudioFocus(audioFocusChangeListener);
                    audioFocusChangeListener = null;
                }
            }
            audioManager = null;
        }
    }

}
