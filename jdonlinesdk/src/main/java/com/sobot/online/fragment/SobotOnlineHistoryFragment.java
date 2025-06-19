package com.sobot.online.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.SobotOnlineChatActivity;
import com.sobot.online.adapter.SobotHistoryChatAdapter;
import com.sobot.online.adapter.SobotHistoryUserChatAdapter;
import com.sobot.online.api.MemoryCacheManager;
import com.sobot.online.base.SobotOnlineBaseFragment;
import com.sobot.online.dialog.SobotOnlineHistorySearchActivity;
import com.sobot.online.dialog.SobotOnlineScreenActivity;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.HistoryChatModel;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.OfflineMsgModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;
import com.sobot.utils.SobotStringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 历史会话页面
 * @Author: znw
 * @CreateDate: 2020/08/20 09:55
 * @Version: 1.0
 * //1.使用setListAll（覆盖数据）后就不需要再调用notifyDataSetChanged（）
 * //2.如果是addAll()追加
 */
public class SobotOnlineHistoryFragment extends SobotOnlineBaseFragment implements SobotRecyclerView.LoadingListener, View.OnClickListener {
    private View mRootView;
    private TextView tv_online_history_saixuan_user;//筛选客户
    private TextView tv_online_history_saixuan_chat;//筛选会话

    private View chatSearchView;
    private View searchView;

    SobotSwipeMenuRecyclerView ssmrv_online_user;
    SobotSwipeMenuRecyclerView ssmrv_online_chat;
    private SobotHistoryUserChatAdapter historyUserChatAdapter;
    private SobotHistoryChatAdapter historyChatAdapter;
    private int currentUserPage = 1;//用户列表当前页
    private int pageSize = 20;//每页显示数量
    private int currentChatPage = 1;//会话列表当前页
    private int currentFragment = 0;//0:前显示的历史会话列表  1：当前显示的历史用户列表
    List<HistoryUserInfoModel> dataList = new ArrayList<>();
    List<HistoryChatModel> historyChatModelList = new ArrayList<>();


    //-----筛选搜索参数
    private String uname;//昵称
    private String startDate;//会话开始时间
    private String endDate;//会话结束时间
    private int receiveType;//会话0 自己接待   1全公司接待
    private String userStartDate;//用户开始时间
    private String userEndDate;//用户结束时间
    private int userReceiveType;//用户0 自己接待   1全公司接待

    private String ismark = "0";//星标 1已星标  2未星标 0全部
    private String summaryStatus = "0"; //已总结 1 未总结; 2 ;0全部
    private String questionStatus = "0";//已处理 1 未处理; 2 ;0全部

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_online_history, container, false);
        initView();
        initData();
        return mRootView;
    }

    protected void initView() {
        tv_online_history_saixuan_user = mRootView.findViewById(R.id.tv_online_history_saixuan_user);
        tv_online_history_saixuan_chat = mRootView.findViewById(R.id.tv_online_history_saixuan_chat);
        tv_online_history_saixuan_user.setOnClickListener(this);
        tv_online_history_saixuan_chat.setOnClickListener(this);
        ssmrv_online_user = mRootView.findViewById(R.id.ssmrv_online_user);
        ssmrv_online_chat = mRootView.findViewById(R.id.ssmrv_online_chat);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getSobotActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_user.setLayoutManager(layoutManager);
        ssmrv_online_user.setPullRefreshEnabled(true);
        ssmrv_online_user.setLoadingMoreEnabled(true);
        ssmrv_online_user.setLoadingListener(this);
        //筛选客户头部搜索布局
        searchView = mRootView.findViewById(R.id.sobot_online_layout_search_user);
        if (searchView != null) {
            TextView tv_online_search_context = searchView.findViewById(R.id.tv_online_search_context);
            TextView tv_online_search_screen = searchView.findViewById(R.id.tv_online_search_screen);
            tv_online_search_screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开筛选用户pop
                    Intent searchIntent = new Intent(getSobotActivity(), SobotOnlineScreenActivity.class);
                    searchIntent.putExtra("isChatOrUser", 1);//0筛选会话，1筛选用户
                    searchIntent.putExtra("startDate", userStartDate);
                    searchIntent.putExtra("endDate", userEndDate);
                    searchIntent.putExtra("ismark", ismark);
                    searchIntent.putExtra("receiveType", userReceiveType);
                    startActivityForResult(searchIntent, OnlineConstant.SOBOT_REQUEST_CODE_SHAIXUAN_USER);
                }
            });
            tv_online_search_context.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开搜索pop
                    Intent searchIntent = new Intent(getSobotActivity(), SobotOnlineHistorySearchActivity.class);
                    searchIntent.putExtra("isChatOrUser", 1);//0筛选会话，1筛选用户
                    startActivity(searchIntent);
                }
            });
        }

        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getSobotActivity());
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_chat.setLayoutManager(chatLayoutManager);
        ssmrv_online_chat.setPullRefreshEnabled(true);
        ssmrv_online_chat.setLoadingMoreEnabled(true);
        ssmrv_online_chat.setLoadingListener(this);
        //筛选会话头部搜索布局
        chatSearchView = mRootView.findViewById(R.id.sobot_online_layout_search_chat);
        if (chatSearchView != null) {
            TextView tv_online_chat_search_context = chatSearchView.findViewById(R.id.tv_online_search_context);
            TextView tv_online_chat_search_screen = chatSearchView.findViewById(R.id.tv_online_search_screen);
            tv_online_chat_search_screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开筛选用户pop
                    Intent searchIntent = new Intent(getSobotActivity(), SobotOnlineScreenActivity.class);
                    searchIntent.putExtra("isChatOrUser", 0);//0筛选会话，1筛选用户
                    searchIntent.putExtra("startDate", startDate);
                    searchIntent.putExtra("endDate", endDate);
                    searchIntent.putExtra("receiveType", receiveType);
                    searchIntent.putExtra("summaryStatus", summaryStatus);
                    startActivityForResult(searchIntent, OnlineConstant.SOBOT_REQUEST_CODE_SHAIXUAN_CHAT);
                }
            });
            tv_online_chat_search_context.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开搜索pop
                    Intent searchIntent = new Intent(getSobotActivity(), SobotOnlineHistorySearchActivity.class);
                    searchIntent.putExtra("isChatOrUser", 0);//0筛选会话，1筛选用户
                    startActivity(searchIntent);
                }
            });
        }
    }

    private void initData() {
        registBroadCast();
        historyUserChatAdapter = new SobotHistoryUserChatAdapter(getSobotActivity());
        historyUserChatAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<HistoryUserInfoModel>() {
            @Override
            public void onItemClick(View view, HistoryUserInfoModel userInfo, int position) {
                Intent intent = new Intent(getActivity(),
                        SobotOnlineChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", userInfo);
                if (userInfo != null && userInfo.getIsblack() == 1) {
                    bundle.putString("flag", "black");
                } else {
                    bundle.putString("flag", "history");
                }
                bundle.putBoolean("hasSummary", MemoryCacheManager.getInstance().hasSummaryCid(userInfo.getLastCid()));
                //表示是由哪个页面点击打开的
                bundle.putString("fromTab", "online");
                intent.putExtra("bundle", bundle);
                intent.putExtra("userSource", userInfo.getSource());
                SobotSPUtils.getInstance().put("uid",
                        userInfo.getId());
                SobotSPUtils.getInstance().put("lastCid",
                        userInfo.getLastCid());
                startActivity(intent);
            }
        });
        ssmrv_online_user.setAdapter(historyUserChatAdapter);

        historyChatAdapter = new SobotHistoryChatAdapter(getSobotActivity());
        historyChatAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<HistoryChatModel>() {
            @Override
            public void onItemClick(View view, HistoryChatModel chatModel, int position) {
                Intent intent = new Intent(getActivity(),
                        SobotOnlineChatActivity.class);
                Bundle bundle = new Bundle();
                HistoryUserInfoModel userInfo = formatUserInfo(chatModel);
                bundle.putSerializable("userInfo", userInfo);
                if (userInfo != null && userInfo.getIsblack() == 1) {
                    bundle.putString("flag", "black");
                } else {
                    bundle.putString("flag", "history");
                }
                intent.putExtra("userSource", userInfo.getSource());
                bundle.putBoolean("hasSummary", MemoryCacheManager.getInstance().hasSummaryCid(userInfo.getLastCid()));
                //表示是由哪个页面点击打开的
                bundle.putString("fromTab", "online");
                intent.putExtra("bundle", bundle);
                SobotSPUtils.getInstance().put("uid",
                        chatModel.getVisitorId());
                SobotSPUtils.getInstance().put("lastCid",
                        chatModel.getCid());
                startActivity(intent);
            }
        });
        ssmrv_online_chat.setAdapter(historyChatAdapter);

        tv_online_history_saixuan_user.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
        tv_online_history_saixuan_chat.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
        ssmrv_online_user.setVisibility(View.VISIBLE);
//        searchView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        ssmrv_online_chat.setVisibility(View.GONE);
        chatSearchView.setVisibility(View.GONE);
        ssmrv_online_user.scrollToPosition(1);
        currentUserPage = 1;
        currentFragment = 1;
        dataList.clear();
        queryConversationList(currentUserPage, pageSize, OnlineConstant.TYPE_ALL);
    }

    @Override
    public void onRefresh() {
        if (currentFragment == 1) {
            currentUserPage = 1;
            dataList.clear();
            queryConversationList(1, pageSize, OnlineConstant.TYPE_ALL);
        } else {
            currentChatPage = 1;
            historyChatModelList.clear();
            getHistoryChatList(1, pageSize);
        }
    }

    @Override
    public void onLoadMore() {
        if (currentFragment == 1) {
            currentUserPage++;
            queryConversationList(currentUserPage, pageSize, OnlineConstant.TYPE_ALL);
        } else {
            currentChatPage++;
            getHistoryChatList(currentChatPage, pageSize);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tv_online_history_saixuan_chat) {
            tv_online_history_saixuan_chat.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
            tv_online_history_saixuan_user.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
            ssmrv_online_user.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            ssmrv_online_chat.setVisibility(View.VISIBLE);
//            chatSearchView.setVisibility(View.VISIBLE);
            chatSearchView.setVisibility(View.GONE);
            ssmrv_online_chat.scrollToPosition(1);
            currentFragment = 0;
            currentChatPage = 1;
            historyChatModelList.clear();
            getHistoryChatList(currentChatPage, pageSize);
        }
        if (v == tv_online_history_saixuan_user) {
            tv_online_history_saixuan_user.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
            tv_online_history_saixuan_chat.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
            ssmrv_online_user.setVisibility(View.VISIBLE);
//            searchView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            ssmrv_online_chat.setVisibility(View.GONE);
            chatSearchView.setVisibility(View.GONE);
            ssmrv_online_user.scrollToPosition(1);
            currentUserPage = 1;
            currentFragment = 1;
            dataList.clear();
            queryConversationList(currentUserPage, pageSize, OnlineConstant.TYPE_ALL);
        }
    }

    //查询历史用户会话
    public void queryConversationList(int pageNow, final int pageSize, int type) {
        Map params = new HashMap();
        if (!TextUtils.isEmpty(userStartDate) && !TextUtils.isEmpty(userEndDate)) {
            params.put("startDate", userStartDate);
            params.put("endDate", userEndDate);
            params.put("ismark", ismark);
            params.put("receiveType", userReceiveType + "");
        }
        zhiChiApi.queryConversationList(getSobotActivity(), pageNow, pageSize, type, params, new SobotResultCallBack<List<HistoryUserInfoModel>>() {

            @Override
            public void onSuccess(List<HistoryUserInfoModel> userInfos) {
                ssmrv_online_user.refreshComplete();
                ssmrv_online_user.loadMoreComplete();
                if (userInfos != null) {
                    dataList.addAll(userInfos);
                    historyUserChatAdapter.setListAll(dataList);
                    if (userInfos.size() < pageSize) {
                        ssmrv_online_user.setLoadingMoreEnabled(false);
                    } else {
                        ssmrv_online_user.setLoadingMoreEnabled(true);
                    }
                    for (int j = 0; j < dataList.size(); j++) {
                        HistoryUserInfoModel messageModel = dataList.get(j);
                        if (messageModel != null) {
                            updateUserInfo(messageModel.getId());
                        }
                    }
                } else {
                    ssmrv_online_user.setLoadingMoreEnabled(false);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ssmrv_online_user.refreshComplete();
                ssmrv_online_user.loadMoreComplete();
                e.printStackTrace();
            }
        });
    }

    public void getHistoryChatList(int pageNow, final int pageSize) {
        Map params = new HashMap();
        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            params.put("startDate", startDate);
            params.put("endDate", endDate);
        }
        params.put("receiveType", receiveType + "");
        params.put("summaryStatus", summaryStatus);
        params.put("questionStatus", questionStatus);
        zhiChiApi.getHistoryChatList(getSobotActivity(), pageNow, pageSize, params, new SobotResultCallBack<List<HistoryChatModel>>() {

            @Override
            public void onSuccess(List<HistoryChatModel> chatModels) {
                ssmrv_online_chat.refreshComplete();
                ssmrv_online_chat.loadMoreComplete();
                if (chatModels != null) {
                    historyChatModelList.addAll(chatModels);
                    historyChatAdapter.setListAll(historyChatModelList);
                    if (chatModels.size() < pageSize) {
                        ssmrv_online_chat.setLoadingMoreEnabled(false);
                    } else {
                        ssmrv_online_chat.setLoadingMoreEnabled(true);
                    }
                } else {
                    ssmrv_online_chat.setLoadingMoreEnabled(false);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ssmrv_online_chat.refreshComplete();
                ssmrv_online_chat.loadMoreComplete();
                e.printStackTrace();
            }
        });
    }

    private HistoryUserInfoModel formatUserInfo(HistoryChatModel chatModel) {
        HistoryUserInfoModel userInfo = new HistoryUserInfoModel();
        userInfo.setId(chatModel.getVisitorId());
        userInfo.setLastCid(chatModel.getCid());
        userInfo.setSource(chatModel.getSource() + "");
        userInfo.setUname(chatModel.getUname());
        userInfo.setIsblack(chatModel.getIsBlack());
        userInfo.setIsmark(chatModel.getMarkStatus());
        userInfo.setOnline(false);
        return userInfo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_SHAIXUAN_USER) {
                userStartDate = data.getStringExtra("startDate");
                userEndDate = data.getStringExtra("endDate");
                userReceiveType = data.getIntExtra("receiveType", 0);
                ismark = data.getStringExtra("ismark");
                currentUserPage = 1;
                dataList.clear();
                queryConversationList(1, pageSize, OnlineConstant.TYPE_ALL);
                TextView tv_online_search_screen = searchView.findViewById(R.id.tv_online_search_screen);
                if ("0".equals(ismark) && userReceiveType == 0 && SobotTimeUtils.getPastDate(7).equals(userStartDate) && SobotTimeUtils.date2String(new Date(), "yyyy-MM-dd").equals(userEndDate)) {
                    Drawable norDrawable = getSobotActivity().getResources().getDrawable(R.drawable.sobot_online_filter_icon);
                    norDrawable.setBounds(0, 0, norDrawable.getMinimumWidth(),
                            norDrawable.getMinimumHeight());
                    tv_online_search_screen.setCompoundDrawables(norDrawable, null, null, null);

                } else {
                    Drawable drawable = getSobotActivity().getResources().getDrawable(R.drawable.sobot_online_filter_sel_icon);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    tv_online_search_screen.setCompoundDrawables(drawable, null, null, null);
                }
            }
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_SHAIXUAN_CHAT) {
                startDate = data.getStringExtra("startDate");
                endDate = data.getStringExtra("endDate");
                receiveType = data.getIntExtra("receiveType", 0);
                summaryStatus = data.getStringExtra("summaryStatus");
                questionStatus = data.getStringExtra("questionStatus");
                currentChatPage = 1;
                historyChatModelList.clear();
                getHistoryChatList(1, pageSize);
                TextView tv_online_chat_search_screen = chatSearchView.findViewById(R.id.tv_online_search_screen);
                if ("0".equals(summaryStatus) && receiveType == 0 && SobotTimeUtils.getPastDate(7).equals(startDate) && SobotTimeUtils.date2String(new Date(), "yyyy-MM-dd").equals(endDate)) {
                    Drawable norDrawable = getSobotActivity().getResources().getDrawable(R.drawable.sobot_online_filter_icon);
                    norDrawable.setBounds(0, 0, norDrawable.getMinimumWidth(),
                            norDrawable.getMinimumHeight());
                    tv_online_chat_search_screen.setCompoundDrawables(norDrawable, null, null, null);
                } else {
                    Drawable selDrawable = getSobotActivity().getResources().getDrawable(R.drawable.sobot_online_filter_sel_icon);
                    selDrawable.setBounds(0, 0, selDrawable.getMinimumWidth(),
                            selDrawable.getMinimumHeight());
                    tv_online_chat_search_screen.setCompoundDrawables(selDrawable, null, null, null);
                }
            }
        }
    }


    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_MARK);
        filter.addAction(SobotSocketConstant.BROADCAST_SOBOT_ADD_BLACK);
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
            }else {
                getActivity().registerReceiver(receiver, filter);
            }
        }
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent data) {
            if (data == null || data.getAction() == null) {
                return;
            }
            if (data.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_MARK) || data.getAction().equals(SobotSocketConstant.BROADCAST_SOBOT_ADD_BLACK)) {
                currentUserPage = 1;
                dataList.clear();
                queryConversationList(1, pageSize, OnlineConstant.TYPE_ALL);
                currentChatPage = 1;
                historyChatModelList.clear();
                getHistoryChatList(currentChatPage, pageSize);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(receiver);
        }
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
                            if (historyUserChatAdapter.getList().size() > 0) {
                                List<HistoryUserInfoModel> userInfos  = historyUserChatAdapter.getList();
                                for (int i = 0; i < userInfos.size(); i++) {
                                    if (uid.equals(userInfos.get(i).getId())) {
                                        HistoryUserInfoModel item =  historyUserChatAdapter.getData(i);
                                        item.setUname(userInfo.getRealname());
                                        historyUserChatAdapter.notifyItemChanged(i + 1);
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