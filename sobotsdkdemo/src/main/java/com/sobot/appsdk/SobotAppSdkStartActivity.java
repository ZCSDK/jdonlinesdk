package com.sobot.appsdk;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.appsdk.utils.ConstantUtils;
import com.sobot.common.login.callback.SobotResultBlock;
import com.sobot.common.login.callback.SobotResultCode;
import com.sobot.online.SobotOnlineService;
import com.sobot.online.weight.SobotContainsEmojiEditText;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.SobotWhatsAppInfoModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.ui.toast.SobotToastUtil;

import java.util.Locale;


//智齿app sdk demo 启动
public class SobotAppSdkStartActivity extends Activity implements View.OnClickListener {

    private TextView ac_demo_start_tv;
    private TextView ac_demo_exit_tv;

    private TextView ac_demo_login_tv;

    private TextView ac_demo_start_getunreaduser;
    private SobotAppSdkStartActivity.MyReceiver receiver;
    private SobotContainsEmojiEditText account;

    private SobotContainsEmojiEditText et_langue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sobot_app_start);
        ac_demo_start_tv = findViewById(R.id.ac_demo_start_tv);
        ac_demo_start_tv.setOnClickListener(this);
        ac_demo_login_tv = findViewById(R.id.ac_demo_login_tv);
        ac_demo_login_tv.setOnClickListener(this);
        ac_demo_start_getunreaduser = findViewById(R.id.ac_demo_start_getunreaduser);
        ac_demo_start_getunreaduser.setOnClickListener(this);
        account = findViewById(R.id.account);
        ac_demo_exit_tv = findViewById(R.id.ac_demo_exit_tv);
        ac_demo_exit_tv.setOnClickListener(this);

        et_langue = findViewById(R.id.et_langue);

        //注册广播获取新收到的信息和未读消息数
        if (receiver == null) {
            receiver = new SobotAppSdkStartActivity.MyReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_NEW_MSG);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(receiver, filter);
        }
        SobotOnlineService.setNotificationFlag(SobotAppSdkStartActivity.this, true, R.drawable.logo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_demo_login_tv:
                if (TextUtils.isEmpty(account.getText().toString().trim())) {
                    Toast.makeText(this, "请输入账号", Toast.LENGTH_LONG).show();
                } else {
                    SobotOnlineService.doLoginWithAccount(SobotAppSdkStartActivity.this, account.getText().toString().trim(), ConstantUtils.APPID,
                            ConstantUtils.APPKEY, new SobotResultBlock() {
                                @Override
                                public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                                    if (sobotResultCode == SobotResultCode.CODE_SUCCEEDED) {
                                        SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, SobotStringUtils.isEmpty(s) ? "登录成功" : s);
                                    } else {
                                        SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, SobotStringUtils.isEmpty(s) ? "登录失败" : s);
                                    }
                                }
                            });
                }
                break;
            case R.id.ac_demo_start_tv:
                if (!TextUtils.isEmpty(et_langue.getText().toString().trim())) {
                    Locale locale = new Locale(et_langue.getText().toString().trim());
                    SobotOnlineService.setInternationalLanguage(this, locale, true);
                } else {
                    SobotOnlineService.setInternationalLanguage(this, null, false);
                }
                SobotOnlineService.startChat(SobotAppSdkStartActivity.this, ConstantUtils.APPID,
                        ConstantUtils.APPKEY, TextUtils.isEmpty(account.getText().toString().trim()) ? "zhichi@11111.com" : account.getText().toString().trim(), null, null, new SobotResultBlock() {
                            @Override
                            public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                                SobotOnlineLogUtils.i("--------------------" + s);
                            }
                        });

                break;


            case R.id.ac_demo_start_getunreaduser:
                if (TextUtils.isEmpty(account.getText().toString().trim())) {
                    Toast.makeText(this, "请输入账号", Toast.LENGTH_LONG).show();
                } else {
                    SobotOnlineService.getUserListByUnReadMsgNum(SobotAppSdkStartActivity.this, ConstantUtils.APPID,
                            ConstantUtils.APPKEY, account.getText().toString().trim(), new SobotResultBlock() {
                                @Override
                                public void resultBolok(SobotResultCode sobotResultCode, String s, Object obj) {
                                    SobotOnlineLogUtils.i("--------------------" + obj);
                                    if(obj!=null) {
                                        int unm = (int) obj;
                                        SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, "获取有未读消息的在线用户列表:" + unm);
                                    }
                                }
                            });
                    break;
                }

            case R.id.ac_demo_exit_tv:
                SobotOnlineService.outAdmin(SobotAppSdkStartActivity.this, new SobotResultBlock() {
                    @Override
                    public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                        if (sobotResultCode == SobotResultCode.CODE_SUCCEEDED) {
                            SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, SobotStringUtils.isEmpty(s) ? "退出成功" : s);
                        } else {
                            SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, SobotStringUtils.isEmpty(s) ? "退出失败" : s);
                        }
                    }
                });
                break;
        }

    }

    //设置广播获取新收到的信息
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SobotSocketConstant.BROADCAST_SOBOT_NEW_MSG.equals(intent.getAction())) {
                int noReadCount = intent.getIntExtra("noReadCount", 0);
                //新消息内容
                String content = intent.getStringExtra("msgContent");
                //完整内容json
                String contentJson = intent.getStringExtra("msgContentJson");
                if (!TextUtils.isEmpty(content)) {
                    PushMessageModel result = (PushMessageModel) SobotGsonUtil
                            .gsonToBean(contentJson, PushMessageModel.class);
                    if (result != null) {
//                        SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, "收到用户手机号:" + result.getUserTel() + "发送的新消息完整内容::" + contentJson);
                    } else {
//                        SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, "收到新消息完整内容:" + contentJson);
                    }
                }
//                SobotLogUtils.i(" 未读消息总数-----------:" + noReadCount + " 新消息内容:" + content + "   完整内容:" + contentJson);
            }
            if (SobotSocketConstant.BROADCAST_SOBOT_SEND_WHATSAPP_MSG_RESULT.equals(intent.getAction())) {
                //WhatsApp模板消息发送结果 true 成功 false 失败
                boolean isSuccess = intent.getBooleanExtra("isSuccess", false);
                //新消息内容
                String resultMsg = intent.getStringExtra("resultMsg");
                //完整内容json
                SobotWhatsAppInfoModel infoModel = (SobotWhatsAppInfoModel) intent.getSerializableExtra("sobotWhatsAppInfoModel");
                if (infoModel != null) {
//                    SobotToastUtil.showCustomToast(SobotAppSdkStartActivity.this, "发送结果:" + isSuccess + ";     " + "结果描述：" + resultMsg + ";        " + "发送的模板消息内容：" + infoModel.toString());
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
