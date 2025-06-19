package com.sobot.online;

import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.SOBOT_CUSTOM_USER;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sobot.common.login.callback.SobotResultBlock;
import com.sobot.common.login.callback.SobotResultCode;
import com.sobot.common.utils.SobotCommonApi;
import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.activity.SobotCustomerServiceChatActivity;
import com.sobot.online.activity.SobotOnlineChatActivity;
import com.sobot.online.api.ZhiChiOnlineApiFactory;
import com.sobot.onlinecommon.api.ZhiChiOnlineApi;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.api.apiutils.SobotOnlineBaseUrl;
import com.sobot.onlinecommon.control.CustomerServiceInfoModel;
import com.sobot.onlinecommon.control.OnlineMsgManager;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.ConversationConfigModelResult;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.OfflineMsgModel;
import com.sobot.onlinecommon.model.OnlineTokenModel;
import com.sobot.onlinecommon.model.SobotWhatsAppInfoModel;
import com.sobot.onlinecommon.model.UnReadMsgUserModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.channel.Const;
import com.sobot.onlinecommon.socket.channel.SobotTCPServer;
import com.sobot.onlinecommon.utils.SobotMD5Utils;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotUtils;
import com.sobot.onlinecommon.utils.StServiceUtils;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 接口输出类
 */
public class SobotOnlineService {

    private static String Tag = SobotOnlineService.class.getSimpleName();

    /**
     * 初始化SDK，设置域名和认证token
     *
     * @param host 可以为空，如果需要，请设置自己的域名
     */
    public static void initWithHost(Application application, String host) {
        if (application == null) {
            SobotOnlineLogUtils.e("initWithHost 参数 application不能为空");
            return;
        }
        SobotUtils.init(application);
        SobotCommonApi.init(application);
        if (!TextUtils.isEmpty(host)) {
            SobotOnlineBaseUrl.setApi_Host(host);
        }
        clearSPConfig(application);
    }

    /**
     * 启动客服主界面
     *
     * @param context
     * @param appid             公司appid 不能为空
     * @param appkey            商户appkey 不能为空
     * @param account           客服账户(邮箱) 不能为空
     * @param recipientPhone    收件人手机号
     * @param whatsAppInfoModel whatsApp 模板消息 配置信息
     * @param block             执行结果
     */
    public static void startChat(Context context, String appid, String appkey, String account, String recipientPhone, SobotWhatsAppInfoModel whatsAppInfoModel, SobotResultBlock block) {
        if (context == null) {
            SobotOnlineLogUtils.e("startAuthWithAcount 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "doLoginWithAccount方法 参数 appid,appkey不能为空");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            SobotOnlineLogUtils.e("账号不能为空");
            return;
        }
        SobotLogUtils.d("=======调用方法 startChat=====");
        String custom_kick_status = SobotSPUtils.getInstance().getString(SobotSocketConstant.custom_kick_status);
        if (!TextUtils.isEmpty(custom_kick_status) && custom_kick_status.equals(SobotSocketConstant.push_custom_outline_kick)) {
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
            SobotSPUtils.getInstance().remove(SobotSocketConstant.custom_kick_status);
        }
        String oldAccount = SobotSPUtils.getInstance().getString(OnlineConstant.SOBOT_CUSTOM_ACCOUNT);
        if (TextUtils.isEmpty(oldAccount) || !oldAccount.equals(account)) {
            Log.i(Tag, "账号发生变化或者之前没有登录过");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }
        long loginTime = SobotSPUtils.getInstance().getLong(OnlineConstant.KEY_LOGIN_TIMR);

        if ((System.currentTimeMillis() - loginTime) > (48 * 60 * 60 * 1000)) {
            Log.i(Tag, "登录时间已经超时48小时，需要重新登录");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }

        final CustomerServiceInfoModel admin = OnlineMsgManager.getInstance(context).getCustomerServiceInfoModel();
        if (admin != null) {
            if (TextUtils.isEmpty(recipientPhone)) {
                Log.i(Tag, "已登录，直接进入首页");
                Intent intent = new Intent(context, SobotCustomerServiceChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Log.i(Tag, "收件人手机不为空，直接进入聊天页");
                Bundle bundle = new Bundle();
                bundle.putSerializable("whatsAppInfoModel", whatsAppInfoModel);
                Intent intent = new Intent(context, SobotOnlineChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);
            }
        } else {
            Log.i(Tag, "进入聊天页 没有登录，需要重新登录已登录");
            final String tempRecipientPhone = recipientPhone;
            final Context tempContext = context;
            final SobotWhatsAppInfoModel tempWhatsAppInfoModel = whatsAppInfoModel;
            final SobotResultBlock tempBlock = block;
            doLoginWithAccount(context, account, appid, appkey, new SobotResultBlock() {
                @Override
                public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                    if (sobotResultCode == SobotResultCode.CODE_SUCCEEDED) {
                        if (TextUtils.isEmpty(tempRecipientPhone)) {
                            Log.i(Tag, "进入聊天页: 登录成功，直接进入首页");
                            Intent intent = new Intent(tempContext, SobotCustomerServiceChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            tempContext.startActivity(intent);
                        } else {
                            Log.i(Tag, "进入聊天页:登录成功,手机人手机不为空，直接进入聊天页");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("whatsAppInfoModel", tempWhatsAppInfoModel);
                            Intent intent = new Intent(tempContext, SobotOnlineChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("bundle", bundle);
                            tempContext.startActivity(intent);
                        }
                        if (tempBlock != null) {
                            tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, s, o);
                        }
                    } else {
                        if (tempBlock != null) {
                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, o);
                        }
                    }
                }
            });
        }
    }


    /**
     * 仅登录客服，不执行页面逻辑
     *
     * @param account 客服账户(邮箱) 不能为空
     * @param appid   公司appid 不能为空
     * @param appkey  商户appkey
     * @param block   执行结果
     */
    public static void doLoginWithAccount(Context context, String account, String appid, String appkey, SobotResultBlock block) {
        if (context == null) {
            SobotOnlineLogUtils.e("doLoginWithAccount方法 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "doLoginWithAccount方法 参数 appid,appkey不能为空");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            SobotOnlineLogUtils.e("账号不能为空");
            return;
        }
        SobotLogUtils.d("=======调用方法 doLoginWithAccount=====");
        OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        final ZhiChiOnlineApi zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(context);
        final Map<String, String> tokebMap = new HashMap<>();
        String nowMills = System.currentTimeMillis() + "";
        tokebMap.put("appid", appid);
        tokebMap.put("create_time", nowMills);
        tokebMap.put("sign", SobotMD5Utils.getMD5Str(appid + nowMills + appkey));
        SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_APPID, appid);
        SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_APPKEY, appkey);
        SobotSPUtils.getInstance().put(OnlineConstant.SOBOT_CUSTOM_ACCOUNT, account);
        final String tempAccount = account;
        final SobotResultBlock tempBlock = block;
        final Context tempContext = context;
        zhiChiApi.getToken(account, tokebMap, new SobotResultCallBack<OnlineTokenModel>() {
            @Override
            public void onSuccess(OnlineTokenModel onlineTokenModel) {
                if (onlineTokenModel.getItem() != null && !TextUtils.isEmpty(onlineTokenModel.getRet_code()) && OnlineConstant.result_success_code.equals(onlineTokenModel.getRet_code())) {
                    // 登录状态 0:忙碌，1:在线
                    zhiChiApi.login(tempContext, tempAccount, 1 + "", onlineTokenModel.getItem().getToken(), new SobotResultCallBack<CustomerServiceInfoModel>() {
                        @Override
                        public void onSuccess(CustomerServiceInfoModel user) {
                            if (user != null && !TextUtils.isEmpty(user.getAid()) && !TextUtils.isEmpty(user.getPuid())) {
                                Intent intent = new Intent(tempContext, SobotTCPServer.class);
                                if (user.getWslinkBak() != null && user.getWslinkBak().size() > 0) {
                                    intent.putExtra("wslinkBak", user.getWslinkBak().get(0));
                                }
                                intent.putExtra("wslinkDefault", user.getWslinkDefault());
                                intent.putExtra("companyId", user.getCompanyId());
                                intent.putExtra("aid", user.getAid());
                                intent.putExtra("puid", user.getPuid());
                                intent.putExtra("userType", Const.user_type_customer);
                                StServiceUtils.safeStartService(tempContext, intent);
                                SobotSPUtils.getInstance().put(OnlineConstant.ONLINE_LOGIN_STATUS, 1);
                                SobotSPUtils.getInstance().put(SOBOT_CUSTOM_USER, user);
                                OnlineMsgManager.getInstance(tempContext).setCustomerServiceInfoModel(user);
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
                                SobotSPUtils.getInstance().put(SobotSocketConstant.WSLINK_BAK, user.getWslinkDefault());
                                SobotSPUtils.getInstance().put(SobotSocketConstant.TOPFLAG, user.getTopFlag());
                                SobotSPUtils.getInstance().put(SobotSocketConstant.SORTFLAG, user.getSortFlag());
                                fillUserConfig(tempContext);
                                if (tempBlock != null)
                                    tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, tempContext.getResources().getString(R.string.online_login_success), null);
                            } else {
                                if (tempBlock != null)
                                    tempBlock.resultBolok(SobotResultCode.CODE_FAILED, tempContext.getResources().getString(R.string.online_login_failed), null);
                            }
                        }

                        @Override
                        public void onFailure(Exception e, String des) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            Log.e(Tag, des);
                            if (tempBlock != null)
                                tempBlock.resultBolok(SobotResultCode.CODE_FAILED, des, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                e.printStackTrace();
            }
        });

    }


    /**
     * 设置是否开启消息提醒   默认不提醒
     *
     * @param context
     * @param flag      true 开启 ； false 关闭
     * @param smallIcon 小图标的id 设置通知栏中的小图片，尺寸一般建议在24×24
     */
    public static void setNotificationFlag(Context context, boolean flag, int smallIcon) {
        if (context == null) {
            return;
        }
        SobotSPUtils.getInstance().put(SobotSocketConstant.SOBOT_NOTIFICATION_FLAG, flag);
        SobotSPUtils.getInstance().put(SobotSocketConstant.SOBOT_NOTIFICATION_SMALL_ICON, smallIcon);
    }

    /**
     * 日志显示设置
     *
     * @param isShowLog true 显示日志信息 默认false不显示
     */
    public static void setShowDebug(Boolean isShowLog) {
        SobotOnlineLogUtils.setShowDebug(isShowLog);
        SobotCommonApi.setShowLogDebug(isShowLog);
    }

    /**
     * 填充用户配置信息
     */
    public static void fillUserConfig(Context context) {
        final ZhiChiOnlineApi zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(context);
        zhiChiApi.conversationConfig(context, new SobotResultCallBack<ConversationConfigModelResult.ConversationConfigModel>() {
            @Override
            public void onSuccess(ConversationConfigModelResult.ConversationConfigModel conversationConfigModel) {

                SobotSPUtils.getInstance().put(OnlineConstant
                        .SCAN_PATH_FLAG, conversationConfigModel.getScanPathFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .ISINVITE_FLAG, conversationConfigModel.getIsInvite() == 0);

                //服务总结
                SobotSPUtils.getInstance().put(OnlineConstant
                        .OPEN_SUMMARY_FLAG, conversationConfigModel.getOpenSummaryFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_OPERATION_FLAG, conversationConfigModel.getSummaryOperationFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_OPERATION_INPUT_FLAG, conversationConfigModel.getSummaryOperationInputFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_TYPE_FLAG, conversationConfigModel.getSummaryTypeFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_STATUS_FLAG, conversationConfigModel.getSummaryStatusFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_STATUS_INPUT_FLAG, conversationConfigModel.getSummaryStatusInputFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .QDESCRIBE_SHOW_FLAG, conversationConfigModel.getQDescribeShowFlag() == 1);
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    /**
     * 指定使用国际化语言
     *
     * @param isUse    是否使用指定语言  是false时，清理语言包
     * @param language 指定语言 Locale 如果为空，跟随系统语言走
     */
    public static void setInternationalLanguage(Context context, Locale language, boolean isUse) {
        if (context == null) {
            return;
        }
        //清空sdk 语言设置
        SobotSharedPreferencesUtil.getInstance(context).remove("SobotLanguage");
        SobotSPUtils.getInstance().remove("SobotLanguageStr");
        if (language == null) {
            return;
        }
        if (!isUse) {
            //不使用指定语言,直接返回,使用sdk自带的国际化语言
            return;
        }
        //添加sdk语言，设置成指定语言
        SobotSharedPreferencesUtil.getInstance(context).put("SobotLanguage", language);
        SobotSPUtils.getInstance().put("SobotLanguageStr", language.getLanguage());
    }

    /**
     * 清理SharedPreferences 里边的缓存信息
     * SharedPreferences的有关配置都需要在初始化之后配置
     */
    private static void clearSPConfig(Context context) {
        //清空sdk 语言设置
        SobotSharedPreferencesUtil.getInstance(context).remove("SobotLanguage");
        SobotSPUtils.getInstance().remove("SobotLanguageStr");
    }

    /**
     * 获取有未读消息的在线用户列表
     *
     * @param context
     * @param appid   公司appid 不能为空
     * @param appkey  商户appkey 不能为空
     * @param account 客服账户(邮箱) 不能为空
     */
    public static void getUserListByUnReadMsg(final Context context, String appid, String appkey, String account, SobotResultBlock block) {
        if (context == null) {
            SobotOnlineLogUtils.e("getUserListByUnReadMsg 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "getUserListByUnReadMsg 参数 appid,appkey不能为空");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            SobotOnlineLogUtils.e("账号不能为空");
            return;
        }
        SobotLogUtils.d("=======调用方法 getUserListByUnReadMsg=====");
        String custom_kick_status = SobotSPUtils.getInstance().getString(SobotSocketConstant.custom_kick_status);
        if (!TextUtils.isEmpty(custom_kick_status) && custom_kick_status.equals(SobotSocketConstant.push_custom_outline_kick)) {
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
            SobotSPUtils.getInstance().remove(SobotSocketConstant.custom_kick_status);
        }
        String oldAccount = SobotSPUtils.getInstance().getString(OnlineConstant.SOBOT_CUSTOM_ACCOUNT);
        if (TextUtils.isEmpty(oldAccount) || !oldAccount.equals(account)) {
            Log.i(Tag, "账号发生变化或者之前没有登录过");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }
        long loginTime = SobotSPUtils.getInstance().getLong(OnlineConstant.KEY_LOGIN_TIMR);

        if ((System.currentTimeMillis() - loginTime) > (48 * 60 * 60 * 1000)) {
            Log.i(Tag, "登录时间已经超时48小时，需要重新登录");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }

        CustomerServiceInfoModel admin = OnlineMsgManager.getInstance(context).getCustomerServiceInfoModel();
        final SobotResultBlock tempBlock = block;
        final ZhiChiOnlineApi zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(context);
        if (admin != null) {
            if (zhiChiApi != null) {
                zhiChiApi.unReadMsg(context, new SobotResultCallBack<List<UnReadMsgUserModel>>() {
                    @Override
                    public void onSuccess(List<UnReadMsgUserModel> unReadMsgUserModels) {
                        if (tempBlock != null) {
                            tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", unReadMsgUserModels);
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String s) {
                        if (tempBlock != null)
                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                    }
                });
            }
        } else {
            Log.i(Tag, "进入聊天页 没有登录，需要重新登录已登录");
            final Context tempContext = context;
            doLoginWithAccount(context, account, appid, appkey, new SobotResultBlock() {
                @Override
                public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                    if (sobotResultCode == SobotResultCode.CODE_SUCCEEDED) {
                        if (zhiChiApi != null) {
                            zhiChiApi.unReadMsg(context, new SobotResultCallBack<List<UnReadMsgUserModel>>() {
                                @Override
                                public void onSuccess(List<UnReadMsgUserModel> unReadMsgUserModels) {
                                    if (tempBlock != null) {
                                        SobotLogUtils.d("=======获取未读数：=====" + ((unReadMsgUserModels != null) ? unReadMsgUserModels.size() : 0));
                                        tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", unReadMsgUserModels);
                                    }
                                }

                                @Override
                                public void onFailure(Exception e, String s) {
                                    if (tempBlock != null)
                                        tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                                }
                            });
                        }
                    } else {
                        if (tempBlock != null)
                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                    }
                }
            });
        }
    }

    /**
     * 获取有未读消息的在线用户数
     *
     * @param context
     * @param appid   公司appid 不能为空
     * @param appkey  商户appkey 不能为空
     * @param account 客服账户(邮箱) 不能为空
     */
    public static void getUserListByUnReadMsgNum(final Context context, String appid, String appkey, String account, SobotResultBlock block) {
        if (context == null) {
            SobotOnlineLogUtils.e("getUserListByUnReadMsg 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "getUserListByUnReadMsg 参数 appid,appkey不能为空");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            SobotOnlineLogUtils.e("账号不能为空");
            return;
        }
        SobotLogUtils.d("=======调用方法 getUserListByUnReadMsgNum=====");
        String custom_kick_status = SobotSPUtils.getInstance().getString(SobotSocketConstant.custom_kick_status);
        if (!TextUtils.isEmpty(custom_kick_status) && custom_kick_status.equals(SobotSocketConstant.push_custom_outline_kick)) {
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
            SobotSPUtils.getInstance().remove(SobotSocketConstant.custom_kick_status);
        }
        String oldAccount = SobotSPUtils.getInstance().getString(OnlineConstant.SOBOT_CUSTOM_ACCOUNT);
        if (TextUtils.isEmpty(oldAccount) || !oldAccount.equals(account)) {
            Log.i(Tag, "账号发生变化或者之前没有登录过");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }
        long loginTime = SobotSPUtils.getInstance().getLong(OnlineConstant.KEY_LOGIN_TIMR);

        if ((System.currentTimeMillis() - loginTime) > (48 * 60 * 60 * 1000)) {
            Log.i(Tag, "登录时间已经超时48小时，需要重新登录");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }

        CustomerServiceInfoModel admin = OnlineMsgManager.getInstance(context).getCustomerServiceInfoModel();
        final SobotResultBlock tempBlock = block;
        final ZhiChiOnlineApi zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(context);
        if (admin != null) {
            if (zhiChiApi != null) {
                zhiChiApi.unReadMsg(context, new SobotResultCallBack<List<UnReadMsgUserModel>>() {
                    @Override
                    public void onSuccess(List<UnReadMsgUserModel> unReadMsgUserModels) {
                        if (tempBlock != null) {
                            SobotLogUtils.d("=======num获取未读数：=====" + ((unReadMsgUserModels != null) ? unReadMsgUserModels.size() : 0));
                            tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", unReadMsgUserModels == null ? 0 : unReadMsgUserModels.size());
                        }
                    }

                    @Override
                    public void onFailure(Exception e, String s) {
                        if (tempBlock != null)
                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                    }
                });
            }
        } else {
            Log.i(Tag, "进入聊天页 没有登录，需要重新登录已登录");
            final Context tempContext = context;
            doLoginWithAccount(context, account, appid, appkey, new SobotResultBlock() {
                @Override
                public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                    if (sobotResultCode == SobotResultCode.CODE_SUCCEEDED) {
                        if (zhiChiApi != null) {
                            zhiChiApi.unReadMsg(context, new SobotResultCallBack<List<UnReadMsgUserModel>>() {
                                @Override
                                public void onSuccess(List<UnReadMsgUserModel> unReadMsgUserModels) {
                                    if (tempBlock != null) {
                                        tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", unReadMsgUserModels == null ? 0 : unReadMsgUserModels.size());
                                    }
                                }

                                @Override
                                public void onFailure(Exception e, String s) {
                                    if (tempBlock != null)
                                        tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                                }
                            });
                        }
                    } else {
                        if (tempBlock != null)
                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                    }
                }
            });
        }
    }

    /**
     * 批量给用户发送WhatsApp模板消息
     *
     * @param context
     * @param appid                     公司appid 不能为空
     * @param appkey                    商户appkey 不能为空
     * @param account                   客服账户(邮箱) 不能为空
     * @param batchWhatsAppMsgModelList 批量发送的模板消息集合
     */
    public static void batchSendWhatsAppMsg(final Context context, String appid, String appkey, String account, final List<SobotWhatsAppInfoModel> batchWhatsAppMsgModelList, SobotResultBlock block) {
        if (context == null) {
            SobotOnlineLogUtils.e("getUserListByUnReadMsg 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(appid) || TextUtils.isEmpty(appkey)) {
            Log.e(Tag, "getUserListByUnReadMsg 参数 appid,appkey不能为空");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            SobotOnlineLogUtils.e("账号不能为空");
            return;
        }
        if (batchWhatsAppMsgModelList == null) {
            SobotOnlineLogUtils.e("批量发送的模板消息集合不能为空");
            return;
        }
        if (batchWhatsAppMsgModelList.size() == 0) {
            SobotOnlineLogUtils.e("批量发送的模板消息的数量大于0个");
            return;
        }
        String custom_kick_status = SobotSPUtils.getInstance().getString(SobotSocketConstant.custom_kick_status);
        if (!TextUtils.isEmpty(custom_kick_status) && custom_kick_status.equals(SobotSocketConstant.push_custom_outline_kick)) {
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
            SobotSPUtils.getInstance().remove(SobotSocketConstant.custom_kick_status);
        }
        String oldAccount = SobotSPUtils.getInstance().getString(OnlineConstant.SOBOT_CUSTOM_ACCOUNT);
        if (TextUtils.isEmpty(oldAccount) || !oldAccount.equals(account)) {
            Log.i(Tag, "账号发生变化或者之前没有登录过");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }
        long loginTime = SobotSPUtils.getInstance().getLong(OnlineConstant.KEY_LOGIN_TIMR);

        if ((System.currentTimeMillis() - loginTime) > (48 * 60 * 60 * 1000)) {
            Log.i(Tag, "登录时间已经超时48小时，需要重新登录");
            OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
        }

        CustomerServiceInfoModel admin = OnlineMsgManager.getInstance(context).getCustomerServiceInfoModel();
        final SobotResultBlock tempBlock = block;
        final ZhiChiOnlineApi zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(context);
        if (admin != null) {
            if (zhiChiApi != null && batchWhatsAppMsgModelList != null && batchWhatsAppMsgModelList.size() > 0) {
                for (int i = 0; i < batchWhatsAppMsgModelList.size(); i++) {
                    SobotWhatsAppInfoModel whatsAppInfoModel = batchWhatsAppMsgModelList.get(i);
                    Map map = null;
                    if (whatsAppInfoModel != null) {
                        map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(whatsAppInfoModel));
                    }
                    final SobotWhatsAppInfoModel batchWhatsAppMsgModel = whatsAppInfoModel;
                    zhiChiApi.getStatusNow(context, null, map, new SobotResultCallBack<OfflineMsgModel>() {
                        @Override
                        public void onSuccess(OfflineMsgModel offlineMsgModel) {
                            if (offlineMsgModel != null) {
                                HistoryUserInfoModel userInfo = offlineMsgModel.getUser();
                                if (userInfo != null) {
                                    Map map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(batchWhatsAppMsgModel));
                                    zhiChiApi.sendTemplateMsg(context, userInfo.getId(), map, new SobotResultCallBack<OnlineBaseCode>() {
                                        @Override
                                        public void onSuccess(OnlineBaseCode baseCode) {
                                            if (baseCode != null && !TextUtils.isEmpty(baseCode.getRetCode()) && OnlineConstant.result_success_code.equals(baseCode.getRetCode())) {
                                                if (tempBlock != null) {
                                                    tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "SEND_SUCCEEDED", batchWhatsAppMsgModel);
                                                }
                                            } else {
                                                if (tempBlock != null) {
                                                    tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Exception e, String s) {
                                            if (tempBlock != null) {
                                                tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                            }
                                        }
                                    });
                                } else {
                                    if (tempBlock != null) {
                                        tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e, String des) {
                            if (tempBlock != null) {
                                tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                            }
                        }
                    });

                }
            }
        } else {
            doLoginWithAccount(context, account, appid, appkey, new SobotResultBlock() {
                @Override
                public void resultBolok(SobotResultCode sobotResultCode, String s, Object o) {
                    if (sobotResultCode == SobotResultCode.CODE_SUCCEEDED) {
                        if (zhiChiApi != null && batchWhatsAppMsgModelList != null && batchWhatsAppMsgModelList.size() > 0) {
                            for (int i = 0; i < batchWhatsAppMsgModelList.size(); i++) {
                                SobotWhatsAppInfoModel whatsAppInfoModel = batchWhatsAppMsgModelList.get(i);
                                Map map = null;
                                if (whatsAppInfoModel != null) {
                                    map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(whatsAppInfoModel));
                                }
                                final SobotWhatsAppInfoModel batchWhatsAppMsgModel = whatsAppInfoModel;
                                zhiChiApi.getStatusNow(context, null, map, new SobotResultCallBack<OfflineMsgModel>() {
                                    @Override
                                    public void onSuccess(OfflineMsgModel offlineMsgModel) {
                                        if (offlineMsgModel != null) {
                                            HistoryUserInfoModel userInfo = offlineMsgModel.getUser();
                                            if (userInfo != null) {
                                                Map map = SobotGsonUtil.gsonToMaps(SobotGsonUtil.gsonString(batchWhatsAppMsgModel));
                                                zhiChiApi.sendTemplateMsg(context, userInfo.getId(), map, new SobotResultCallBack<OnlineBaseCode>() {
                                                    @Override
                                                    public void onSuccess(OnlineBaseCode baseCode) {
                                                        if (baseCode != null && !TextUtils.isEmpty(baseCode.getRetCode()) && OnlineConstant.result_success_code.equals(baseCode.getRetCode())) {
                                                            if (tempBlock != null) {
                                                                tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "SEND_SUCCEEDED", batchWhatsAppMsgModel);
                                                            }
                                                        } else {
                                                            if (tempBlock != null) {
                                                                tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Exception e, String s) {
                                                        if (tempBlock != null) {
                                                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                                        }
                                                    }
                                                });
                                            } else {
                                                if (tempBlock != null) {
                                                    tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e, String des) {
                                        if (tempBlock != null) {
                                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "SEND_FAILED", batchWhatsAppMsgModel);
                                        }
                                    }
                                });

                            }
                        }
                    } else {
                        if (tempBlock != null)
                            tempBlock.resultBolok(SobotResultCode.CODE_FAILED, s, null);
                    }
                }
            });
        }
    }

    /**
     * 退出登录
     *
     * @param context 上下文
     * @param block   回调
     */
    public static void outAdmin(final Context context, SobotResultBlock block) {
        SobotLogUtils.d("=======调用方法 outAdmin=====" + (context == null));
        final ZhiChiOnlineApi zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(context);
        CustomerServiceInfoModel admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
        if (admin != null) {
            final SobotResultBlock tempBlock = block;
            zhiChiApi.out(context, admin.getPuid(), new SobotResultCallBack<OnlineBaseCode>() {
                @Override
                public void onSuccess(OnlineBaseCode onlineBaseCode) {
                    //清除缓存用户信息
                    OnlineMsgManager.getInstance(context).setCustomerServiceInfoModel(null);
                    SobotSPUtils.getInstance().remove(SOBOT_CUSTOM_USER);
                    disconnChannel(context);
                    context.stopService(new Intent(context, SobotTCPServer.class));
                    SobotGlobalContext.getInstance().finishAllActivity();
                    if (tempBlock != null) {
                        tempBlock.resultBolok(SobotResultCode.CODE_SUCCEEDED, "Exit successful", null);
                    }

                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotOnlineLogUtils.e(des);
                    if (tempBlock != null) {
                        tempBlock.resultBolok(SobotResultCode.CODE_FAILED, "Exit failed", null);
                    }
                }
            });
        } else {
            if (block != null) {
                block.resultBolok(SobotResultCode.CODE_FAILED, "获取的登录用户为空", null);
            }
        }
    }

    /**
     * 关闭通道
     *
     * @param context
     */
    private static void disconnChannel(Context context) {
        if (context != null) {
            context.sendBroadcast(new Intent(Const.SOBOT_CUSTOME_DISCONNCHANNEL));
        }
    }
}