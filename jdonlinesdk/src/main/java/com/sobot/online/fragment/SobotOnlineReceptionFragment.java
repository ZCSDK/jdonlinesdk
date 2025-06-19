package com.sobot.online.fragment;

import static com.sobot.onlinecommon.api.apiutils.OnlineConstant.SOBOT_CUSTOM_USER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.SobotCustomerServiceChatActivity;
import com.sobot.online.activity.SobotOnlineChatActivity;
import com.sobot.online.activity.SobotServiceSummaryActivity;
import com.sobot.online.adapter.SobotPaiduiAdapter;
import com.sobot.online.adapter.SobotReceptionAdapter;
import com.sobot.online.api.MemoryCacheManager;
import com.sobot.online.base.SobotOnlineBaseFragment;
import com.sobot.online.dialog.SobotOnlineCommonDialog;
import com.sobot.online.dialog.SobotOnlineReceptionSearchActivity;
import com.sobot.online.util.OrderUtils;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.SobotOnlineUrlApi;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.control.CustomerServiceInfoModel;
import com.sobot.onlinecommon.control.OnlineMsgManager;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.OfflineMsgModel;
import com.sobot.onlinecommon.model.OnlineCommonModel;
import com.sobot.onlinecommon.model.QueueUserModel;
import com.sobot.onlinecommon.model.SobotWhatsAppInfoModel;
import com.sobot.onlinecommon.model.SynChronousModel;
import com.sobot.onlinecommon.model.UnReadMsgUserModel;
import com.sobot.onlinecommon.socket.MsgCacheManager;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.module.ChatMessageMsgModel;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;
import com.sobot.utils.SobotStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 当前会话页面
 * @Author: znw
 * @CreateDate: 2020/08/20 09:45
 * @Version: 1.0
 */
public class SobotOnlineReceptionFragment extends SobotOnlineBaseFragment implements View.OnClickListener, SobotRecyclerView.LoadingListener {
    private View mRootView;
    private TextView tv_online_reception_on;//会话中
    private TextView tv_online_reception_screen_on;//排队中

    SobotSwipeMenuRecyclerView ssmrv_online_reception;//用户列表
    SobotSwipeMenuRecyclerView ssmrv_online_paidui;//排队列表
    private SobotReceptionAdapter receptionAdapter;
    private SobotPaiduiAdapter paiduiAdapter;
    CustomerServiceInfoModel admin;//登录用户
    private String cid;
    //以下为配置
    private int mTopflag;//星标置顶 0不置顶 1置顶
    private int mSortflag;//会话排序 0 按接入顺序 1 按新消息时间
    //排序比较器
    private OrderUtils.OrderComparator mOrderComparator;
    boolean flag_has = false;

    List<PushMessageModel> dataList = new ArrayList<>();//会话中用户
    List<QueueUserModel.QueueUser> queueUserList = new ArrayList<>();//排队中用户

    private int currentPage = 1;//当前页
    private int pageSize = 20;//排队用户列表每页显示数量

    private SobotOnlineCommonDialog closeDialog;//客服掉线弹窗

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_online_reception, container, false);
        initView();
        initData();
        return mRootView;
    }

    private void initData() {
        registBroadCast();
        logicEmptyView();
        loadUserConfig();
        paiduiAdapter = new SobotPaiduiAdapter(getSobotActivity(), new SobotPaiduiAdapter.OnInviteListener() {
            @Override
            public void onInvite(QueueUserModel.QueueUser queueUser) {
                if (queueUser != null) {
                    invite(queueUser);
                }
            }
        });
        receptionAdapter = new SobotReceptionAdapter(getSobotActivity(), true, new SobotReceptionAdapter.OnReceptionSwipeListener() {
            @Override
            public void onClose(final PushMessageModel item, final int position) {
                if (closeDialog == null) {
                    final PushMessageModel tempModel = item;
                    boolean show_summary = SobotSPUtils.getInstance().getBoolean(OnlineConstant
                            .OPEN_SUMMARY_FLAG, false);
                    if (show_summary) {
                        //若服务总结已总结，则关闭，若服务总结未总结，则提示服务总结
                        if (!MemoryCacheManager.getInstance().hasSummaryCid(item.getCid())) {
                            SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_please_submit_service_summary));
                            return;
                        }
                    }
                    closeDialog = new SobotOnlineCommonDialog(getSobotActivity(), getString(R.string.online_exit_chat), getString(R.string.online_ok), getString(R.string.online_cancle), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.sobot_btn_cancle) {
                            } else if (v.getId() == R.id.sobot_btn_ok) {
                                zhiChiApi.leave(getSobotActivity(), tempModel.getCid(), tempModel.getUserId(), new SobotResultCallBack<OnlineBaseCode>() {
                                    @Override
                                    public void onSuccess(OnlineBaseCode onlineBaseCode) {
                                        MsgCacheManager.getInstance().delUnReadMsgCount(tempModel.getUserId());
                                        if (isVisible()) {
                                            receptionAdapter.removeToIndex(position);
                                            receptionAdapter.myNotifyDataSetChanged();
                                            tv_online_reception_on.setText(getString(R.string.sobot_reception_on) + " (" + getOnlineChatCount() + ")");
                                        }
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

            @Override
            public void onMark(final PushMessageModel item, final int position) {
                String url;
                if (item.getMarkStatus() == 1) {
                    //删除星标
                    url = SobotOnlineUrlApi.api_deleteMarkList;
                } else {
                    //添加星标
                    url = SobotOnlineUrlApi.api_addMarkList;
                }
                zhiChiApi.addOrDeleteMarkList(getSobotActivity(), item.getUid(), url, new SobotResultCallBack<OnlineBaseCode>() {
                    @Override
                    public void onSuccess(OnlineBaseCode onlineBaseCode) {
                        if (item.getMarkStatus() == 1) {
                            item.setMarkStatus(0);
                        } else {
                            item.setMarkStatus(1);
                        }
                        receptionAdapter.notifyItemChanged(position + 1);
                    }

                    @Override
                    public void onFailure(Exception e, String des) {

                    }
                });
            }

            @Override
            public void onSummary(PushMessageModel item, int position) {
                HistoryUserInfoModel userInfo = formatUserInfo(item);
                //进入服务总结界面
                Intent intent = new Intent(getSobotActivity(), SobotServiceSummaryActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.putExtra("cid", userInfo.getLastCid());
                startActivityForResult(intent, OnlineConstant.SOBOT_REQUEST_CODE_SERVICE_SUMMARY);
            }
        });

        receptionAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<PushMessageModel>() {
            @Override
            public void onItemClick(View view, PushMessageModel item, int position) {
                if (item == null)
                    return;
                MsgCacheManager.getInstance().delUnReadMsgCount(item.getUid());
                //清空当前用户的未读消息数字
                item.setUnReadCount(0);
                receptionAdapter.notifyItemChanged(position + 1);
                HistoryUserInfoModel userInfo = formatUserInfo(item);
                Intent intent = new Intent(getActivity(),
                        SobotOnlineChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", userInfo);
                bundle.putBoolean("hasSummary", MemoryCacheManager.getInstance().hasSummaryCid(item.getCid()));
                if (userInfo.isOnline()) {
                    bundle.putString("flag", "online");
                } else {
                    bundle.putString("flag", "history");
                }
                //表示是由哪个页面点击打开的
                bundle.putString("fromTab", "online");
                intent.putExtra("bundle", bundle);
                intent.putExtra("userSource", userInfo.getSource());
                SobotSPUtils.getInstance().put("uid",
                        userInfo.getId());
                SobotSPUtils.getInstance().put("lastCid",
                        userInfo.getLastCid());
                SobotSPUtils.getInstance().put("push_cid",
                        item.getCid());
                cid = item.getCid();
                startActivity(intent);
            }
        });
        receptionAdapter.setOnItemLongClickListener(new BaseRecyclerViewAdapter.OnItemLongClickListener<PushMessageModel>() {

            @Override
            public void onItemLongClick(View view, PushMessageModel item, int position) {
                if (item == null)
                    return;
                MsgCacheManager.getInstance().delUnReadMsgCount(item.getUid());
                //清空当前用户的未读消息数字
                item.setUnReadCount(0);
                receptionAdapter.notifyItemChanged(position + 1);
                HistoryUserInfoModel userInfo = formatUserInfo(item);
                Intent intent = new Intent(getActivity(),
                        SobotOnlineChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", userInfo);
                bundle.putBoolean("hasSummary", MemoryCacheManager.getInstance().hasSummaryCid(item.getCid()));
                if (userInfo.isOnline()) {
                    bundle.putString("flag", "online");
                } else {
                    bundle.putString("flag", "history");
                }
                //表示是由哪个页面点击打开的
                bundle.putString("fromTab", "online");
                intent.putExtra("bundle", bundle);
                intent.putExtra("userSource", userInfo.getSource());
                SobotSPUtils.getInstance().put("uid",
                        userInfo.getId());
                SobotSPUtils.getInstance().put("lastCid",
                        userInfo.getLastCid());
                SobotSPUtils.getInstance().put("push_cid",
                        item.getCid());
                cid = item.getCid();
                startActivity(intent);
            }
        });
        ssmrv_online_reception.setAdapter(receptionAdapter);
        ssmrv_online_paidui.setAdapter(paiduiAdapter);
        synChronous();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (receptionAdapter != null) {
            receptionAdapter.myNotifyDataSetChanged();
        }
    }

    //同步更新会话中列表
    private void synChronous() {
        dataList.clear();
        flag_has = false;
        receptionAdapter.setListAll(dataList);
        zhiChiApi.synChronous(getSobotActivity(), "", new SobotResultCallBack<SynChronousModel>() {
            @Override
            public void onSuccess(SynChronousModel synChronousModel) {
                if (synChronousModel != null && synChronousModel.getUserList() != null) {
                    final List<PushMessageModel> userList = synChronousModel.getUserList();
                    OnlineMsgManager.getInstance(getSobotActivity()).setTempMsgList(null);
                    if (userList != null && userList.size() > 0) {
                        if (MsgCacheManager.getInstance().getPushMessageModel() == null || MsgCacheManager.getInstance().getPushMessageModel().size() == 0) {
                            zhiChiApi.unReadMsg(getSobotActivity(), new SobotResultCallBack<List<UnReadMsgUserModel>>() {
                                @Override
                                public void onSuccess(List<UnReadMsgUserModel> unReadMsgUserModels) {
                                    if (unReadMsgUserModels != null && unReadMsgUserModels.size() > 0) {
                                        for (int i = 0; i < unReadMsgUserModels.size(); i++) {
                                            UnReadMsgUserModel unReadMsgUserModel = unReadMsgUserModels.get(i);
                                            for (int j = 0; j < userList.size(); j++) {
                                                PushMessageModel messageModel = userList.get(j);
                                                if (!TextUtils.isEmpty(unReadMsgUserModel.getUserTel()) && !TextUtils.isEmpty(messageModel.getTel()) && unReadMsgUserModel.getUserTel().equals(messageModel.getTel())) {
                                                    messageModel.setUid(messageModel.getUserId());
                                                    MsgCacheManager.getInstance().addPushMessageModel(messageModel);
                                                }
                                            }
                                        }
                                    }
                                    dataList.addAll(userList);
                                    receptionAdapter.setListAll(dataList);
                                    receptionAdapter.myNotifyDataSetChanged();
                                    for (int j = 0; j < userList.size(); j++) {
                                        PushMessageModel messageModel = userList.get(j);
                                        if (messageModel != null) {
                                            updateUserInfo(messageModel.getUid());
                                        }
                                    }
                                    if (isAdded()) {
//                        tv_online_reception_on.setText(getString(R.string.sobot_reception_on) + " (" + +getOnlineChatCount() + ")");
//                        tv_online_reception_screen_on.setText(getString(R.string.sobot_screen_on) + " (" + synChronousModel.getWaitSize() + ")");
                                    }
                                }

                                @Override
                                public void onFailure(Exception e, String s) {

                                }
                            });
                        } else {
                            dataList.addAll(userList);
                            receptionAdapter.setListAll(dataList);
                            receptionAdapter.myNotifyDataSetChanged();
                            if (isAdded()) {
//                        tv_online_reception_on.setText(getString(R.string.sobot_reception_on) + " (" + +getOnlineChatCount() + ")");
//                        tv_online_reception_screen_on.setText(getString(R.string.sobot_screen_on) + " (" + synChronousModel.getWaitSize() + ")");
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    //获取排队中用户列表
    private void queryWaitUser() {
//        zhiChiApi.queryWaitUser(getSobotActivity(), currentPage, pageSize, new SobotResultCallBack<QueueUserModel>() {
//            @Override
//            public void onSuccess(QueueUserModel queueUserModel) {
//                ssmrv_online_paidui.refreshComplete();
//                ssmrv_online_paidui.loadMoreComplete();
//                if (queueUserModel != null && queueUserModel.getList() != null) {
//                    List<QueueUserModel.QueueUser> tempList = queueUserModel.getList();
//                    queueUserList.addAll(tempList);
//                    paiduiAdapter.setListAll(queueUserList);
//                    tv_online_reception_screen_on.setText(getString(R.string.sobot_screen_on") + " (" + queueUserModel.getWaitSize() + ")");
//                    if (queueUserList.size() < pageSize) {
//                        ssmrv_online_paidui.setLoadingMoreEnabled(false);
//                    } else {
//                        ssmrv_online_paidui.setLoadingMoreEnabled(true);
//                    }
//                } else {
//                    ssmrv_online_paidui.setLoadingMoreEnabled(false);
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e, String des) {
//                ssmrv_online_paidui.refreshComplete();
//                ssmrv_online_paidui.loadMoreComplete();
//            }
//        });
    }


    //邀请排队用户
    private void invite(final QueueUserModel.QueueUser queueUser) {
        zhiChiApi.invite(getSobotActivity(), queueUser.getUid(), new SobotResultCallBack<OnlineCommonModel>() {
            @Override
            public void onSuccess(OnlineCommonModel data) {
                SobotOnlineLogUtils.i("邀请成功----status：" + data.getStatus());
                SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_invite_success));
                paiduiAdapter.remove(queueUser);
                synChronous();
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }


    protected void initView() {
        admin = (CustomerServiceInfoModel) SobotSPUtils.getInstance().getObject(SOBOT_CUSTOM_USER);
        tv_online_reception_on = mRootView.findViewById(R.id.tv_online_reception_on);
        tv_online_reception_screen_on = mRootView.findViewById(R.id.tv_online_reception_screen_on);
        ssmrv_online_reception = mRootView.findViewById(R.id.ssmrv_online_reception);
        ssmrv_online_paidui = mRootView.findViewById(R.id.ssmrv_online_paidui);

        tv_online_reception_on.setText(getString(R.string.sobot_reception_on));
        tv_online_reception_screen_on.setText(getString(R.string.sobot_screen_on));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getSobotActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_reception.setLayoutManager(layoutManager);
        ssmrv_online_reception.setPullRefreshEnabled(false);//禁止下拉
        ssmrv_online_reception.setLoadingMoreEnabled(false);//禁止加载更多

        LinearLayoutManager paiduiLayoutManager = new LinearLayoutManager(getSobotActivity());
        paiduiLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_paidui.setLayoutManager(paiduiLayoutManager);
        ssmrv_online_paidui.setPullRefreshEnabled(true);
        ssmrv_online_paidui.setLoadingMoreEnabled(true);
        ssmrv_online_paidui.setLoadingListener(this);

        tv_online_reception_on.setOnClickListener(this);
        tv_online_reception_screen_on.setOnClickListener(this);

        //头部搜索布局
        TextView tv_online_chat_search_context = mRootView.findViewById(R.id.tv_online_search_context);
        tv_online_chat_search_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开搜索pop
                Intent searchIntent = new Intent(getSobotActivity(), SobotOnlineReceptionSearchActivity.class);
                searchIntent.putExtra("keyword", 0);//0筛选会话，1筛选用户
                startActivity(searchIntent);
            }
        });
    }

    private HistoryUserInfoModel formatUserInfo(PushMessageModel pushMessageModel) {
        HistoryUserInfoModel userInfo = new HistoryUserInfoModel();
        userInfo.setId(pushMessageModel.getUid());
        userInfo.setLastCid(pushMessageModel.getCid());
        userInfo.setIsmark(pushMessageModel.getMarkStatus());
        userInfo.setSource(pushMessageModel.getUsource() + "");
        userInfo.setUname(pushMessageModel.getUname());
        userInfo.setChatType(pushMessageModel.getChatType());
        userInfo.setOnline(pushMessageModel.isOnline());
        return userInfo;
    }


    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_MSG);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_TRANSFER);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_ADD_BLACK);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_MARK);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_REMOVE_MARK);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_LIST_SYNCHRONOUS_USERS);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_USERINFO);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_CUSTOMER);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_RELOGIN_RESET_UNREAD);
        filter.addAction(SobotSocketConstant.SESSION_SEQUENCE_CONFIG_CHANGED);
        filter.addAction(SobotSocketConstant.BROADCAST_CUSTOM_COMITSUMMARY);//提交服务总结
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_LAST_MSG);//用户在聊天详情也发送消息成功，更新会回列表
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
            } else {
                getActivity().registerReceiver(receiver, filter);
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
            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_RELOGIN_RESET_UNREAD)) {
                return;
            }
            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_MSG)) {
                String msgContentJson = intent.getStringExtra("msgContent");
                PushMessageModel result = SobotGsonUtil.gsonToBean(msgContentJson, PushMessageModel.class);
                if (result == null) {
                    return;
                }
                result.setMarkStatus(result.getIsmark());
                if (result.getType() == SobotSocketConstant.UPDATE_USER_INFO) {
                    if (receptionAdapter != null) {
                        ((SobotCustomerServiceChatActivity) getActivity()).fillUserConfig();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (receptionAdapter != null) {
                                    receptionAdapter.setCanSummary(SobotSPUtils.getInstance().getBoolean(OnlineConstant
                                            .OPEN_SUMMARY_FLAG, false));
                                    receptionAdapter.notifyDataSetChanged();
                                }
                            }
                        }, 2 * 1000);

                    }
                    return;
                }
                result.setUserId(result.getUid());
                if (result.getType() == SobotSocketConstant.ACTIVE_RECEPT) {
                    if (admin.getAid().equals(result.getTid()) && receptionAdapter.getList().size() > 0) {
                        for (int i = receptionAdapter.getItemCount() - 1; i >= 0; i--) {
                            PushMessageModel item = (PushMessageModel) receptionAdapter.getData(i);
                            if (item != null && !TextUtils.isEmpty(result.getUid()) && result.getUid().equals(item.getUid())) {
                                item.setTimeOrder(result.getTimeOrder());
                                item.setAcceptTimeOrder(result.getAcceptTimeOrder());
                                item.setMessage(result.getMessage());
                                item.setTs(SobotTimeUtils.getNowString());
                                item.setT(SobotTimeUtils.getNowMills());
                                flag_has = true;
                                OnlineMsgManager.getInstance(getSobotActivity()).setTempMsgList((ArrayList<PushMessageModel>) receptionAdapter.getList());
                                item.setIsOnline(false);
                                item.setFace(null);
                                receptionAdapter.myNotifyDataSetChanged();
                                break;
                            }
                        }
                        tv_online_reception_on.setText(getString(R.string.sobot_reception_on) + " (" + getOnlineChatCount() + ")");
                    }
                }

                processNewData(result);
            }

            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_ADD_BLACK)) {
                HistoryUserInfoModel userInfo = (HistoryUserInfoModel) intent.getSerializableExtra("userInfo");
                List<PushMessageModel> allData = receptionAdapter.getList();
                for (int i = 0; i < allData.size(); i++) {
                    if (userInfo != null && !TextUtils.isEmpty(userInfo.getId()) && allData.get(i).getUid().equals(userInfo.getId())) {
                        receptionAdapter.removeToIndex(i);
                        break;
                    }
                }

                logicEmptyView();
            }


            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_LIST_SYNCHRONOUS_USERS) || intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_TRANSFER)) {
                synChronous();
            }

            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_MARK)) {
                String uid = intent.getStringExtra("uid");
                int markStatus = intent.getIntExtra("markStatus", 0);
                if (TextUtils.isEmpty(uid) || receptionAdapter == null && receptionAdapter.getList() == null) {
                    return;
                }
                for (int i = 0; i < receptionAdapter.getList().size(); i++) {
                    if (uid.equals(receptionAdapter.getList().get(i).getUid())) {
                        PushMessageModel pushMessageModel = receptionAdapter.getList().get(i);
                        pushMessageModel.setMarkStatus(markStatus);
                    }
                }
                receptionAdapter.notifyDataSetChanged();
            }

            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_USERINFO)) {
                String uid = intent.getStringExtra("uid");
                if (TextUtils.isEmpty(uid) || receptionAdapter == null && receptionAdapter.getList() == null) {
                    return;
                }
                List<PushMessageModel> allData = receptionAdapter.getList();
                for (int i = 0; i < allData.size(); i++) {
                    if (uid.equals(allData.get(i).getUid())) {
                        allData.get(i).setUname(intent.getStringExtra("uname"));
                        break;
                    }
                }
            }

            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_CUSTOM_COMITSUMMARY)) {
                String tempCid = intent.getStringExtra("cid");
                if (!TextUtils.isEmpty(tempCid)) {
                    MemoryCacheManager.getInstance().putSummaryCid(tempCid);
                    receptionAdapter.notifyDataSetChanged();
                }
            }

            if (intent.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_UPDATE_LAST_MSG)) {
                String uid = intent.getStringExtra("uid");
                String ts = intent.getStringExtra("ts");
                long t = intent.getLongExtra("t", 0);
                ChatMessageMsgModel lastMsg = (ChatMessageMsgModel) intent.getSerializableExtra("lastMsg");
                for (int i = 0; i < receptionAdapter.getList().size(); i++) {
                    if (uid.equals(receptionAdapter.getList().get(i).getUid())) {
                        PushMessageModel pushMessageModel = receptionAdapter.getList().get(i);
                        pushMessageModel.setTs(ts);
                        pushMessageModel.setT(t);
                        pushMessageModel.setMessage(lastMsg);
                    }
                }
                receptionAdapter.notifyDataSetChanged();
            }

            if (intent.getAction().equals(SobotSocketConstant.SESSION_SEQUENCE_CONFIG_CHANGED)) {
                //刷新数据
                loadUserConfig();
            }

            orderAndRefreshList();
        }
    };

    private void orderAndRefreshList() {
        tv_online_reception_on.setText(getString(R.string.sobot_reception_on) + " (" + getOnlineChatCount() + ")");
    }

    /**
     * 加载用户配置
     * 登录接口直接给的配置在initdata中就直接加载
     */
    private void loadUserConfig() {
        if (getActivity() != null) {
            mTopflag = SobotSPUtils.getInstance().getInt(SobotSocketConstant.TOPFLAG, 0);
            mSortflag = SobotSPUtils.getInstance().getInt(SobotSocketConstant.SORTFLAG, 0);
            mOrderComparator = new OrderUtils.OrderComparator(mSortflag, mTopflag);
        }
    }

    @Override
    public void onClick(View v) {
//        if (v == tv_online_reception_on) {
//            tv_online_reception_on.setTextColor(ContextCompat.getColor(getSobotActivity(), SobotResourceUtils.getResColorId(getContext(), "sobot_online_color")));
//            tv_online_reception_screen_on.setTextColor(ContextCompat.getColor(getSobotActivity(), SobotResourceUtils.getResColorId(getContext(), "sobot_online_common_gray2")));
//            ssmrv_online_paidui.setVisibility(View.GONE);
//            ssmrv_online_reception.setVisibility(View.VISIBLE);
////            synChronous();
//        }
//        if (v == tv_online_reception_screen_on) {
//            tv_online_reception_screen_on.setTextColor(ContextCompat.getColor(getSobotActivity(), SobotResourceUtils.getResColorId(getContext(), "sobot_online_color")));
//            tv_online_reception_on.setTextColor(ContextCompat.getColor(getSobotActivity(), SobotResourceUtils.getResColorId(getContext(), "sobot_online_common_gray2")));
//            ssmrv_online_paidui.setVisibility(View.VISIBLE);
//            ssmrv_online_reception.setVisibility(View.GONE);
//            currentPage = 1;
//            queueUserList.clear();
//            queryWaitUser();
//        }
    }


    /**
     * 根据数据判断是否显示空态页面
     */
    private void logicEmptyView() {
        if (receptionAdapter == null) {
            // 当没有数据的时候显示没有数据的提示按钮
//            second_chat.setText("暂无在线访客");
//            img_nochat.setBackgroundResource(R.drawable.no_online_user);
//            rl_online.setVisibility(View.VISIBLE);
        } else {
            if (receptionAdapter.getItemCount() > 0) {
//                rl_online.setVisibility(View.GONE);
            } else {
                // 当没有数据的时候显示没有数据的提示按钮
//                second_chat.setText("暂无在线访客");
//                img_nochat.setBackgroundResource(R.drawable.no_online_user);
//                rl_online.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (closeDialog != null) {
            closeDialog.dismiss();
            closeDialog = null;
        }
    }

    private void processNewData(PushMessageModel result) {
        if (result == null) {
            return;
        }
        int type = result.getType();
        //如果收到排队消息，刷新排队中列表
        if (type == SobotSocketConstant.QUEUE) {
            currentPage = 1;
            queueUserList.clear();
            queryWaitUser();
        }
        // 只处理三种类型用户上线，下线，新消息,排队
        if (type == SobotSocketConstant.NEW_USER || type == SobotSocketConstant.OFFLINE_USER || type == SobotSocketConstant.NEW_INFOMATION) {
			/*if (!TextUtils.isEmpty(result.getTs())) {
				result.setTimeOrder(result.getTs());
				result.setTs(DateUtil.formatDateTime(result.getTs()));
			} else {
				result.setTimeOrder(DateUtil.toDate(System.currentTimeMillis(), DateUtil.DATE_FORMAT));
				result.setTs(DateUtil.toDate(System.currentTimeMillis(), DateUtil.DATE_FORMAT0));
			}*/
            refershTime(result);
            refershOrderTime(result);
            if (type == SobotSocketConstant.NEW_USER || type == SobotSocketConstant.OFFLINE_USER) {
                setAcceptTime(result, true);
            }
            if (type == SobotSocketConstant.NEW_USER) {
                updateUserInfo(result.getUid());
            }
            if (receptionAdapter.getList().size() > 0) {
                List<PushMessageModel> allData = receptionAdapter.getList();
                for (int i = 0; i < allData.size(); i++) {
                    if (result.getUid().equals(allData.get(i).getUid())) {
                        PushMessageModel item = (PushMessageModel) receptionAdapter.getData(i);
                        item.setType(type);
                        item.setTimeOrder(result.getTimeOrder());
                        item.setAcceptTimeOrder(result.getAcceptTimeOrder());
                        item.setMessage(result.getMessage());
                        item.setTs(result.getTs());
                        item.setT(result.getT());

                        flag_has = true;
                        if (type == SobotSocketConstant.NEW_INFOMATION) {
                            item.setFace(result.getFace());
                            OnlineMsgManager.getInstance(getSobotActivity()).setTempMsgList((ArrayList<PushMessageModel>) receptionAdapter.getList());
                        }
                        if (type == SobotSocketConstant.OFFLINE_USER) {
                            OnlineMsgManager.getInstance(getSobotActivity()).setTempMsgList((ArrayList<PushMessageModel>) receptionAdapter.getList());
                            item.setIsOnline(false);
                            item.setFace(null);
                        }

                        if (type == SobotSocketConstant.NEW_USER) {
                            item.setFace(result.getFace());
                            item.setCid(result.getCid());
                            item.setIsOnline(true);
                            result.setTs(SobotTimeUtils.getNowString());
                            result.setT(SobotTimeUtils.getNowMills());
                            item.setMessage(new ChatMessageMsgModel("0", "[" + getString(R.string.online_new_user_online) + "]"));
                        }
                        receptionAdapter.notifyItemChanged(i + 1);
                        break;
                    } else {
                        flag_has = false;
                    }
                }

                if (!flag_has) {
                    if (type == SobotSocketConstant.NEW_USER) {
                        result.setIsOnline(true);
                        result.setTs(SobotTimeUtils.getNowString());
                        result.setT(SobotTimeUtils.getNowMills());
                        result.setMessage(new ChatMessageMsgModel("0", "[" + getString(R.string.online_new_user_online) + "]"));
                    }
                    receptionAdapter.addItemToHead(result);
                }
            } else {
                if (type == SobotSocketConstant.NEW_USER) {
                    result.setIsOnline(true);
                    result.setTs(SobotTimeUtils.getNowString());
                    result.setT(SobotTimeUtils.getNowMills());
                    result.setMessage(new ChatMessageMsgModel("0", "[" + getString(R.string.online_new_user_online) + "]"));
                } else if (type == SobotSocketConstant.NEW_INFOMATION) {
                }
                receptionAdapter.addItemToHead(result);
            }
            tv_online_reception_on.setText(getString(R.string.sobot_reception_on) + " (" + getOnlineChatCount() + ")");
            logicEmptyView();
            receptionAdapter.myNotifyDataSetChanged();
        }
    }

    //获取在线会话总数
    private int getOnlineChatCount() {
        int count = 0;
        if (receptionAdapter != null) {
            List<PushMessageModel> pushMessageModels = receptionAdapter.getList();
            for (int i = 0; i < pushMessageModels.size(); i++) {
                if (pushMessageModels.get(i).isOnline()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * 格式化显示时间
     *
     * @param info 推送过来的model
     */
    private void refershTime(PushMessageModel info) {
        if (TextUtils.isEmpty(info.getTs())) {
            info.setTs(SobotTimeUtils.getNowString());
            info.setT(SobotTimeUtils.getNowMills());
        } else {
            info.setTs(info.getTs());
            info.setT(info.getT());
        }
    }

    /**
     * 设置接入时间
     *
     * @param info
     */
    private void setAcceptTime(PushMessageModel info, boolean isNow) {
        if (TextUtils.isEmpty(info.getTs())) {
            info.setAcceptTimeOrder(SobotTimeUtils.getNowString());
        } else {
            if (!isNow) {
                info.setAcceptTimeOrder(info.getTs());
            } else {
                info.setAcceptTimeOrder(SobotTimeUtils.getNowString());
            }
        }
    }

    /**
     * 新消息时间
     *
     * @param info
     */
    private void refershOrderTime(PushMessageModel info) {
//		if(TextUtils.isEmpty(info.getTs())) {
        info.setTimeOrder(SobotTimeUtils.getNowString());
//		}else{
//			info.setTimeOrder(info.getTs());
//		}
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        queueUserList.clear();
        queryWaitUser();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        queryWaitUser();
    }

    private void updateUserInfo(final String uid) {
        if (SobotStringUtils.isEmpty(uid)) {
            return;
        }
        zhiChiApi.getStatusNow(getSobotActivity(), uid, null, new SobotResultCallBack<OfflineMsgModel>() {
            @Override
            public void onSuccess(OfflineMsgModel offlineMsgModel) {
                if (offlineMsgModel != null) {
                    HistoryUserInfoModel userInfo = offlineMsgModel.getUser();
                    if (userInfo != null) {
                        if (!TextUtils.isEmpty(userInfo.getRealname())) {
                            if (receptionAdapter.getList().size() > 0) {
                                List<PushMessageModel> allData = receptionAdapter.getList();
                                for (int i = 0; i < allData.size(); i++) {
                                    if (uid.equals(allData.get(i).getUid())) {
                                        PushMessageModel item = (PushMessageModel) receptionAdapter.getData(i);
                                        item.setUname(userInfo.getRealname());
                                        receptionAdapter.notifyItemChanged(i + 1);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
            }
        });
    }
}
