package com.sobot.online.activity;

import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.SOBOT_CUSTOM_USER;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.dialog.SobotDialogUtils;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.control.CustomerServiceInfoModel;
import com.sobot.onlinecommon.control.OnlineMsgManager;
import com.sobot.onlinecommon.model.OnlineTokenModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.channel.Const;
import com.sobot.onlinecommon.socket.channel.SobotServiceConnection;
import com.sobot.onlinecommon.socket.channel.SobotTCPServer;
import com.sobot.onlinecommon.utils.SobotMD5Utils;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;
import com.sobot.onlinecommon.utils.StServiceUtils;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 在线客服模块, 选择接待状态认证后, 才能进入接待页面
 * @Author: znw
 * @CreateDate: 2020/08/17 11:05
 * @Version: 1.0
 */
public class SobotAuthorActivity extends SobotOnlineBaseActivity {
    private RelativeLayout rv_sobot_online_online_status, rv_sobot_online_onbusy_status;
    private TextView tv_sobot_online_online_status, tv_sobot_online_onbusy_status;
    private ImageView iv_sobot_online_online_status, iv_sobot_online_onbusy_status;
    //开始接待控件
    private TextView tv_sobot_online_start_reception;
    //头部返回按钮
    private View backView;
    //是否记住本次选择
    private CheckBox cb_sobot_online_keep_status;
    private String account;//登录账号
    private int loginStatus = 1;//登录状态 2:忙碌，1:在线
    private String loginToken;//登录token

    private SobotServiceConnection sobotServiceConnection;

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_author;
    }

    @Override
    protected void initView() {
        setHearderTitle(getString(R.string.sobot_online_select_status));
        rv_sobot_online_online_status = findViewById(R.id.rv_sobot_online_online_status);
        rv_sobot_online_onbusy_status = findViewById(R.id.rv_sobot_online_onbusy_status);
        tv_sobot_online_online_status = findViewById(R.id.tv_sobot_online_online_status);
        tv_sobot_online_onbusy_status = findViewById(R.id.tv_sobot_online_onbusy_status);
        iv_sobot_online_online_status = findViewById(R.id.iv_sobot_online_online_status);
        iv_sobot_online_onbusy_status = findViewById(R.id.iv_sobot_online_onbusy_status);
        cb_sobot_online_keep_status = findViewById(R.id.cb_sobot_online_keep_status);
        tv_sobot_online_start_reception = findViewById(R.id.tv_sobot_online_start_reception);
        backView = getHearderLeftView();
        //头部左侧返回按钮点击返回
        if (backView != null) {
            backView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        //在线接待按钮点击
        if (rv_sobot_online_online_status != null) {
            displayInNotch(rv_sobot_online_onbusy_status);
            rv_sobot_online_online_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginStatus = 1;
                    rv_sobot_online_online_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_select"));
                    rv_sobot_online_onbusy_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_normal"));
                    iv_sobot_online_online_status.setVisibility(View.VISIBLE);
                    iv_sobot_online_onbusy_status.setVisibility(View.GONE);
                    tv_sobot_online_online_status.setTextColor(getResColor("sobot_online_color"));
                    tv_sobot_online_onbusy_status.setTextColor(getResColor("sobot_online_common_gray1"));
                }
            });
        }
        //忙碌接待按钮点击
        if (rv_sobot_online_online_status != null) {
            displayInNotch(rv_sobot_online_online_status);
            rv_sobot_online_onbusy_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginStatus = 2;
                    rv_sobot_online_online_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_normal"));
                    rv_sobot_online_onbusy_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_select"));
                    iv_sobot_online_online_status.setVisibility(View.GONE);
                    iv_sobot_online_onbusy_status.setVisibility(View.VISIBLE);
                    tv_sobot_online_online_status.setTextColor(getResColor("sobot_online_common_gray1"));
                    tv_sobot_online_onbusy_status.setTextColor(getResColor("sobot_online_color"));
                }
            });
        }

        //记住本次选择
        if (cb_sobot_online_keep_status != null) {
            cb_sobot_online_keep_status.setChecked(SobotSPUtils.getInstance().getBoolean(OnlineConstant.ONLINE_KEEP_LOGIN_STATUS, false));
        }

        if (tv_sobot_online_start_reception != null) {
            if (SobotWidgetApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv_sobot_online_start_reception.getLayoutParams();
                lp.topMargin = 100;
            }
            tv_sobot_online_start_reception.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(loginToken) && !TextUtils.isEmpty(account)) {
                        login(account, loginStatus, loginToken);
                    } else {
                        getTokenAndLogin();
                    }

                }
            });
        }
    }


    @Override
    protected void initData() {
        StServiceUtils.safeStartService(getSobotContext(), new Intent(getSobotActivity(), SobotTCPServer.class));
        account = getIntent().getStringExtra("account");
        int lStatus = getIntent().getIntExtra("loginStatus", -1);
        loginToken = getIntent().getStringExtra("loginToken");
        if (lStatus != -1 && !TextUtils.isEmpty(loginToken) && !TextUtils.isEmpty(account)) {
            //如果启动时传了登录状态和登录token，不登录-1，登录token不为空，直接登录
            login(account, loginStatus, loginToken);
        } else {
            if (SobotSPUtils.getInstance().getBoolean(OnlineConstant.ONLINE_KEEP_LOGIN_STATUS, false)) {
                if (SobotSPUtils.getInstance().getInt(OnlineConstant.ONLINE_LOGIN_STATUS, -1) == 1) {
                    //空闲
                    rv_sobot_online_online_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_select"));
                    rv_sobot_online_onbusy_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_normal"));
                    iv_sobot_online_online_status.setVisibility(View.VISIBLE);
                    iv_sobot_online_onbusy_status.setVisibility(View.GONE);
                    tv_sobot_online_online_status.setTextColor(getResColor("sobot_online_color"));
                    tv_sobot_online_onbusy_status.setTextColor(getResColor("sobot_online_common_gray1"));
                } else {
                    //忙碌
                    rv_sobot_online_online_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_normal"));
                    rv_sobot_online_onbusy_status.setBackgroundResource(getResDrawableId("sobot_shape_hollow_border_select"));
                    iv_sobot_online_online_status.setVisibility(View.GONE);
                    iv_sobot_online_onbusy_status.setVisibility(View.VISIBLE);
                    tv_sobot_online_online_status.setTextColor(getResColor("sobot_online_common_gray1"));
                    tv_sobot_online_onbusy_status.setTextColor(getResColor("sobot_online_color"));
                }

            }
        }
    }

    public void getTokenAndLogin() {
        //清空缓存
        OnlineMsgManager.getInstance(getSobotContext()).setCustomerServiceInfoModel(null);
        CustomerServiceInfoModel user = null;
        SobotSPUtils.getInstance().put(SOBOT_CUSTOM_USER, user);
        final String appid = getIntent().getStringExtra("appid");
        final String appkey = getIntent().getStringExtra("appkey");

        final Map<String, String> tokebMap = new HashMap<>();
        String nowMills = SobotTimeUtils.getSecondTimestamp(new Date()) + "";
        tokebMap.put("appid", appid);
        tokebMap.put("create_time", nowMills);
        tokebMap.put("sign", SobotMD5Utils.getMD5Str(appid + nowMills + appkey));

        SobotDialogUtils.startProgressDialog(getSobotContext());
        zhiChiApi.getToken(getSobotActivity(), tokebMap, new SobotResultCallBack<OnlineTokenModel>() {
            @Override
            public void onSuccess(OnlineTokenModel onlineTokenModel) {
                if (onlineTokenModel.getItem() != null && !TextUtils.isEmpty(onlineTokenModel.getRet_code()) && "000000".equals(onlineTokenModel.getRet_code())) {
                    login(account, loginStatus, onlineTokenModel.getItem().getToken());
                    SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_APPID, appid);
                    SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_APPKEY, appkey);
                } else {
                    SobotDialogUtils.stopProgressDialog(getSobotContext());
                    SobotToastUtil.showCustomToast(getSobotContext(), onlineTokenModel.getRet_msg());
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotDialogUtils.stopProgressDialog(getSobotContext());
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }

    public void login(final String account, final int loginStatus, String loginToken) {
        if (TextUtils.isEmpty(account)) {
            SobotOnlineLogUtils.e("登录账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(loginToken)) {
            SobotOnlineLogUtils.e("登录loginToken不能为空");
            return;
        }
        disconnChannel();
        zhiChiApi.login(getSobotActivity(), account, loginStatus + "", loginToken, new SobotResultCallBack<CustomerServiceInfoModel>() {
            @Override
            public void onSuccess(CustomerServiceInfoModel user) {
                SobotDialogUtils.stopProgressDialog(getSobotContext());
                if (cb_sobot_online_keep_status.isChecked()) {
                    //记住本次登录状态
                    SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_KEEP_LOGIN_STATUS, true);
                } else {
                    //不记住本次登录状态
                    SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_KEEP_LOGIN_STATUS, false);
                }
                SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_LOGIN_STATUS, loginStatus);
                SobotSPUtils.getInstance().put(SOBOT_CUSTOM_USER, user);
                OnlineMsgManager.getInstance(getSobotContext()).setCustomerServiceInfoModel(user);
                SobotSPUtils.getInstance().put(OnlineConstant.SOBOT_CUSTOM_ACCOUNT, account);
                SobotSPUtils.getInstance().put(OnlineConstant.KEY_TEMP_ID, user.getTempId());
                SobotSPUtils.getInstance().put(OnlineConstant.KEY_TOKEN, user.getToken());
                SobotSPUtils.getInstance().put(OnlineConstant.KEY_LOGIN_TIMR, System.currentTimeMillis());
                SobotSPUtils.getInstance().put(OnlineConstant.TRANSFER_AUDIT_FLAG, user.getTransferAuditFlag() == 1 ? true : false);
                if (3333 == user.getCusRoleId()) {
                    //超管切换登录状态不需审核，直接切换
                    SobotSPUtils.getInstance().put(OnlineConstant.KEFU_LOGIN_STATUS_FLAG, false);
                } else {
                    SobotSPUtils.getInstance().put(OnlineConstant.KEFU_LOGIN_STATUS_FLAG, user.getAuditFlag() == 1 ? true : false);
                }
                connChannel(user.getWslinkBak(), user.getWslinkDefault
                        (), user.getAid(), user.getCompanyId(), user.getPuid());
                SobotSPUtils.getInstance().put(SobotSocketConstant.WSLINK_BAK, user.getWslinkDefault());
                SobotSPUtils.getInstance().put(SobotSocketConstant.TOPFLAG, user.getTopFlag());
                SobotSPUtils.getInstance().put(SobotSocketConstant.SORTFLAG, user.getSortFlag());
                fillUserConfig();
                finish();
                startActivity(new Intent(getSobotActivity(), SobotCustomerServiceChatActivity.class));
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotDialogUtils.stopProgressDialog(getSobotContext());
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }


//    public void bindChannelServer(final SobotTCPServer.SobotTcpServerListener s) {
//        //1.以bind方式开启服务
//        Intent intent = new Intent(this, SobotTCPServer.class);
//        sobotServiceConnection = new SobotServiceConnection(new SobotServiceConnectionListener() {
//            @Override
//            public void onServiceConnected(SobotIServiceAidl soketIService) {
//                s.onConnected(soketIService);
//            }
//        });
//        bindService(intent, sobotServiceConnection, BIND_AUTO_CREATE);
//    }
//
//    public void unBindChannelServer() {
//        if (sobotServiceConnection != null) {
//            unbindService(sobotServiceConnection);
//        }
//    }

    /*************通道相关*********************/
    protected void disconnChannel() {
        sendBroadcast(new Intent(Const.SOBOT_CUSTOME_DISCONNCHANNEL));
    }


    public void connChannel(List<String> wslinkBak, String wslinkDefault, String aid, String companyId, String puid) {

        if (!TextUtils.isEmpty(aid) && !TextUtils.isEmpty(puid)) {
            Intent intent = new Intent(Const.SOBOT_CUSTOME_CONNCHANNEL);
            if (wslinkBak != null && wslinkBak.size() > 0) {
                intent.putExtra("wslinkBak", wslinkBak.get(0));
            }
            intent.putExtra("wslinkDefault", wslinkDefault);
            intent.putExtra("companyId", companyId);
            intent.putExtra("aid", aid);
            intent.putExtra("puid", puid);
            intent.putExtra("userType", Const.user_type_customer);
            sendBroadcast(intent);
        }
    }
}
