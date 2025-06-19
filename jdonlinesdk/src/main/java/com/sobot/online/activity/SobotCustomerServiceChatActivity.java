package com.sobot.online.activity;

import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.SOBOT_CUSTOM_USER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.SobotPopUserStatusAdapter;
import com.sobot.online.base.SobotBaseFragment;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.dialog.SobotOnlineCommonDialog;
import com.sobot.online.dialog.SobotOnlineExitDialog;
import com.sobot.online.fragment.SobotOnlineHistoryFragment;
import com.sobot.online.fragment.SobotOnlineReceptionFragment;
import com.sobot.online.util.FragTools;
import com.sobot.online.util.SobotChatUtils;
import com.sobot.online.weight.SobotCustomPopWindow;
import com.sobot.online.weight.image.SobotRCImageView;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.control.CustomerServiceInfoModel;
import com.sobot.onlinecommon.control.OnlineMsgManager;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.OnlineServiceStatus;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.channel.Const;
import com.sobot.onlinecommon.socket.channel.SobotTCPServer;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.StServiceUtils;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 客服接待主页面
 * @Author: znw
 * @CreateDate: 2020/08/20 16:05
 * @Version: 1.0
 */
public class SobotCustomerServiceChatActivity extends SobotOnlineBaseActivity {
    public String currentFrag;
    private FragmentManager manager = getSupportFragmentManager();
    private List<Fragment> currentFragList;
    //头部返回按钮
    private TextView sobot_online_tab_back_tv;

    private LinearLayout ll_online_msg;
    private TextView tv_online_msg;
    private View view_online_msg_bottom;
    private LinearLayout ll_history_msg;
    private TextView tv_history_msg;
    private View view_history_msg_bottom;

    //头部客服头像
    private FrameLayout sobot_online_tab_hearder_image_fl;
    private SobotRCImageView service_hearder_image_srcv;
    //头部客服在线状态
    private ImageView sobot_online_tab_status_iv;
    private SobotCustomPopWindow mPopWindow;//切换客服状态弹窗
    private View mPopContentView;
    private RecyclerView rv_pop_user_status;
    private SobotPopUserStatusAdapter userStatusAdapter;
    List<OnlineServiceStatus> newStatusList;

    CustomerServiceInfoModel admin;
    private SobotOnlineExitDialog exitDialog;//客服掉线弹窗

    private List<SobotBaseFragment> mFragments;
    private SobotOnlineReceptionFragment onlineFragment;
    private SobotOnlineHistoryFragment historyFragment;

    private SobotOnlineCommonDialog outDialog;//离线弹窗

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_customer_service_chat;
    }


    /**
     * @param displayFrag 要显示的fragment
     * @author 王帅
     */
    private void display(Fragment displayFrag, String tag) {
        if (displayFrag == null) {
            return;
        }

        if (currentFragList == null) { // 第一次添加碎片
            if (displayFrag.isAdded()) {
                FragTools.removeFragmet(manager, displayFrag);
            }
            FragTools.addFragmet(manager, displayFrag, R.id.fl_message,
                    tag);
            currentFragList = new ArrayList<Fragment>();
            currentFragList.add(displayFrag);
        } else if (currentFragList.get(0) != displayFrag) { // 当前碎片不是要添加的碎片
            if (displayFrag.isAdded()) {
                FragTools.showFragment(manager, displayFrag);
            } else {
                FragTools.addFragmetWithAnim(manager, displayFrag,
                        R.id.fl_message, tag);
            }
            FragTools.hideFragment(manager, currentFragList.get(0));
            currentFragList.clear();
            currentFragList.add(displayFrag);
        }
    }

    @Override
    protected void initView() {
        admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
        ll_online_msg = findViewById(R.id.ll_online_msg);
        tv_online_msg = findViewById(R.id.tv_online_msg);
        view_online_msg_bottom = findViewById(R.id.view_online_msg_bottom);
        ll_history_msg = findViewById(R.id.ll_history_msg);
        tv_history_msg = findViewById(R.id.tv_history_msg);
        view_history_msg_bottom = findViewById(R.id.view_history_msg_bottom);
        sobot_online_tab_back_tv = findViewById(R.id.sobot_online_tab_back_tv);
        sobot_online_tab_hearder_image_fl = findViewById(R.id.sobot_online_tab_hearder_image_fl);
        sobot_online_tab_status_iv = findViewById(R.id.sobot_online_tab_status_iv);
        service_hearder_image_srcv = findViewById(R.id.sobot_online_tab_hearder_image_srcv);
        SobotBitmapUtil.display(getSobotContext(), admin.getFace(), service_hearder_image_srcv, R.drawable.sobot_service_header_def, R.drawable.sobot_service_header_def);
        initViewPager();
        //头部左侧返回按钮点击返回
        if (sobot_online_tab_back_tv != null) {
            sobot_online_tab_back_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        //切换在线状态
        if (sobot_online_tab_hearder_image_fl != null) {
            sobot_online_tab_hearder_image_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContentView();
                    if (mPopWindow == null) {
                        mPopWindow = new SobotCustomPopWindow.PopupWindowBuilder(getSobotContext())
                                .setView(mPopContentView)
                                .setFocusable(false)
                                .enableBackgroundDark(true)
                                .setOutsideTouchable(true)
                                .setWidthMatchParent(false)
                                .create();
                    }
                    if (mPopWindow != null && userStatusAdapter != null) {
                        userStatusAdapter.setListAll(newStatusList);
                        mPopWindow.showAsDropDown(sobot_online_tab_hearder_image_fl);
                    }
                }
            });
        }

        if (OnlineConstant.ADMIN_STATUS_ONLINE == admin.getStatus()) {
            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_green);
        } else if (OnlineConstant.ADMIN_STATUS_BUSY == admin.getStatus()) {
            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_red);
        } else if (OnlineConstant.ADMIN_STATUS_OUT == admin.getStatus()) {
            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_gray);
        } else {
            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_custom);
        }
        displayInNotch(sobot_online_tab_back_tv);

    }

    private View getContentView() {
        if (mPopContentView == null) {
            mPopContentView = LayoutInflater.from(getSobotContext()).inflate(R.layout.pop_layout_user_online_status, null);
            if (mPopContentView != null) {
                rv_pop_user_status = mPopContentView.findViewById(R.id.rv_pop_user_status);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getSobotActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rv_pop_user_status.setLayoutManager(layoutManager);
                userStatusAdapter = new SobotPopUserStatusAdapter(getSobotActivity());
                userStatusAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<OnlineServiceStatus>() {
                    @Override
                    public void onItemClick(View view, OnlineServiceStatus item, int position) {
                        if (mPopWindow != null) {
                            mPopWindow.dissmiss();
                        }
                        admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
                        if (admin != null && !TextUtils.isEmpty(item.getDictValue())) {
                            if (admin.getStatus() == Integer.parseInt(item.getDictValue())) {
                                //当前状态和选中状态相同，直接返回
                                return;
                            }
                            if (item.getDictValue().equals(OnlineConstant.STATUS_ONLINE)) {
                                //在线
                                updateServiceStatus(true, item.getDictValue());
                            } else if (item.getDictValue().equals(OnlineConstant.STATUS_BUSY)) {
                                //超管切换登录状态不需审核，直接切换
                                if (SobotSPUtils.getInstance().getBoolean(OnlineConstant.KEFU_LOGIN_STATUS_FLAG) && admin.getStatus() == 1 && 3333 != admin.getCusRoleId()) {
                                    Intent changeIntent = new Intent(getSobotActivity(), OnlineChangLoginStatusActivity.class);
                                    changeIntent.putExtra("nowStatus", admin.getStatus() + "");
                                    changeIntent.putExtra("cutStatus", item.getDictValue() + "");
                                    changeIntent.putExtra("tid", admin.getAid());
                                    startActivity(changeIntent);
                                } else {
                                    updateServiceStatus(false, item.getDictValue());
                                }
                            } else if (item.getDictValue().equals(OnlineConstant.STATUS_OFFLINE)) {
                                if (outDialog == null) {
                                    outDialog = new SobotOnlineCommonDialog(getSobotActivity(), getString(R.string.online_service_out), getString(R.string.online_ok), getString(R.string.online_cancle), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            outDialog.dismiss();
                                            if (v.getId() == R.id.sobot_btn_cancle) {
                                            } else if (v.getId() == R.id.sobot_btn_ok) {
                                                if (admin != null) {
                                                    out(admin.getPuid(), true);
                                                }
                                                //离线
                                                sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_gray);
                                            }
                                        }
                                    });
                                }
                                outDialog.show();
                            } else {
                                //其它状态   超管切换登录状态不需审核，直接切换
                                if (SobotSPUtils.getInstance().getBoolean(OnlineConstant.KEFU_LOGIN_STATUS_FLAG) && admin.getStatus() == 1 && 3333 != admin.getCusRoleId()) {
                                    Intent changeIntent = new Intent(getSobotActivity(), OnlineChangLoginStatusActivity.class);
                                    changeIntent.putExtra("nowStatus", admin.getStatus() + "");
                                    changeIntent.putExtra("cutStatus", item.getDictValue() + "");
                                    changeIntent.putExtra("tid", admin.getAid());
                                    startActivity(changeIntent);
                                } else {
                                    updateServiceStatus(false, item.getDictValue());
                                }
                            }
                        }
                    }
                });
                rv_pop_user_status.setAdapter(userStatusAdapter);
            }
        }
        return mPopContentView;
    }

    public void updateServiceStatus(boolean isOnline, final String statusValue) {
        zhiChiApi.busyOrOnline(getSobotActivity(), isOnline, statusValue, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode onlineBaseCode) {
                admin.setStatus(Integer.parseInt(statusValue));
                SobotSPUtils.getInstance().put(SOBOT_CUSTOM_USER, admin);
                if (OnlineConstant.STATUS_ONLINE.equals(statusValue)) {
                    sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_green);
                } else if (OnlineConstant.STATUS_BUSY.equals(statusValue)) {
                    sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_red);
                } else if (OnlineConstant.STATUS_OFFLINE.equals(statusValue)) {
                    sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_gray);
                } else {
                    sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_custom);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    //客服离线
    public void out(String uid, final boolean isExitSobot) {
        zhiChiApi.out(getSobotActivity(), uid, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode onlineBaseCode) {
                if (isExitSobot) {
                    SobotToastUtil.showCustomToastWithListenr(getSobotContext(), getString(R.string.online_offline_success), new SobotToastUtil.OnAfterShowListener() {
                        @Override
                        public void doAfter() {
                            //清除缓存用户信息
                            OnlineMsgManager.getInstance(getSobotContext()).setCustomerServiceInfoModel(null);
                            SobotSPUtils.getInstance().remove(SOBOT_CUSTOM_USER);
                            getSobotActivity().stopService(new Intent(getSobotContext(), SobotTCPServer.class));
                            //离线成功返回登录界面
                            SobotGlobalContext.getInstance().finishAllActivity();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }

    @Override
    protected void initData() {
        registBroadCast();
        newStatusList = new ArrayList<>();
        zhiChiApi.getServiceStatus(getSobotActivity(), new SobotResultCallBack<List<OnlineServiceStatus>>() {
            @Override
            public void onSuccess(List<OnlineServiceStatus> onlineServiceStatuses) {
                newStatusList.clear();
                newStatusList.add(new OnlineServiceStatus(OnlineConstant.STATUS_ONLINE, getString(R.string.online_zaixian)));
                newStatusList.add(new OnlineServiceStatus(OnlineConstant.STATUS_BUSY, getString(R.string.online_busy)));
                newStatusList.addAll(onlineServiceStatuses);
                newStatusList.add(new OnlineServiceStatus(OnlineConstant.STATUS_OFFLINE, getString(R.string.online_out)));
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }

    private void initViewPager() {
        tv_online_msg.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_nav_sel_tx_color));
        view_online_msg_bottom.setBackgroundColor(getSobotActivity().getResources().getColor(R.color.sobot_online_nav_indicator_color));
        tv_history_msg.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_nav_tx_color));
        view_history_msg_bottom.setBackgroundColor(getSobotActivity().getResources().getColor(R.color.sobot_online_transparent));
        currentFrag = "online";
        display(FragTools.getInstance(manager, currentFrag), "online");
        ll_online_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_online_msg.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_nav_tx_color));
                view_online_msg_bottom.setBackgroundColor(getSobotActivity().getResources().getColor(R.color.sobot_online_nav_indicator_color));
                tv_history_msg.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_nav_sel_tx_color));
                view_history_msg_bottom.setBackgroundColor(getSobotActivity().getResources().getColor(R.color.sobot_online_transparent));
                currentFrag = "online";
                display(FragTools.getInstance(manager, currentFrag), "online");
            }
        });
        ll_history_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_online_msg.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_nav_sel_tx_color));
                view_online_msg_bottom.setBackgroundColor(getSobotActivity().getResources().getColor(R.color.sobot_online_transparent));
                tv_history_msg.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_nav_tx_color));
                view_history_msg_bottom.setBackgroundColor(getSobotActivity().getResources().getColor(R.color.sobot_online_nav_indicator_color));
                currentFrag = "history";
                display(FragTools.getInstance(manager, currentFrag), "history");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SobotChatUtils.isServiceWork(getSobotActivity(), "com.sobot.onlinecommon.socket.channel.SobotTCPServer")) {
            SobotOnlineLogUtils.i("tcpserver 没运行");
            CustomerServiceInfoModel user = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
            if (user != null && !TextUtils.isEmpty(user.getAid()) && !TextUtils.isEmpty(user.getPuid())) {
                SobotOnlineLogUtils.i("tcpserver 重启");
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
            SobotOnlineLogUtils.i("tcpserver 正在运行");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getSobotActivity() != null) {
            Intent intent = new Intent(getSobotActivity(), SobotTCPServer.class);
            StServiceUtils.safeStopService(getSobotActivity(), intent);
            getSobotActivity().unregisterReceiver(receiver);
        }
        if (exitDialog != null) {
            exitDialog.dismiss();
            exitDialog = null;
        }
    }

    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_MSG);
        if (getSobotActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getSobotActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
            }else {
                getSobotActivity().registerReceiver(receiver, filter);
            }
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {

        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_MSG)) {
                String msgContentJson = intent.getStringExtra("msgContent");
                PushMessageModel result = (PushMessageModel) SobotGsonUtil.gsonToBean(msgContentJson, PushMessageModel.class);
                if (result == null) {
                    return;
                }
                result.setMarkStatus(result.getIsmark());
                if (result.getType() == SobotSocketConstant.KICKED && !SobotSocketConstant.push_custom_outline_logout.equals(result.getStatus())) {
                    OnlineMsgManager.getInstance(getSobotContext()).setCustomerServiceInfoModel(null);
                    Intent exitIntent = new Intent(context, SobotOnlineExitActivity.class);
                    exitIntent.putExtra("customKickStatus", result.getStatus());
                    context.startActivity(exitIntent);
                }
                if (result.getType() == SobotSocketConstant.UPDATE_USER_INFO) {
                    fillUserConfig();
                }
                if (result.getType() == SobotSocketConstant.MSG_TYPE_AUDIT_STATUS_SERVICE) {
                    CustomerServiceInfoModel admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
                    // Flag 1  审核通过 2 失败
                    if (admin != null && !TextUtils.isEmpty(result.getStatus()) && "1".equals(result.getFlag())) {
                        admin.setStatus(Integer.parseInt(result.getStatus()));
                        SobotSPUtils.getInstance().put(SOBOT_CUSTOM_USER, admin);
                        OnlineMsgManager.getInstance(getSobotContext()).setCustomerServiceInfoModel(admin);
                        if (OnlineConstant.ADMIN_STATUS_ONLINE == admin.getStatus()) {
                            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_green);
                        } else if (OnlineConstant.ADMIN_STATUS_BUSY == admin.getStatus()) {
                            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_red);
                        } else if (OnlineConstant.ADMIN_STATUS_OUT == admin.getStatus()) {
                            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_gray);
                        } else {
                            sobot_online_tab_status_iv.setBackgroundResource(R.drawable.sobot_status_custom);
                        }
                    } else {
                        SobotToastUtil.showCustomToast(getSobotContext(), getResString("online_chang_status_error"));
                    }
                }
            }
        }
    };
}
