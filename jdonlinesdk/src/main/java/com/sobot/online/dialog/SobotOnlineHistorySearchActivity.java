package com.sobot.online.dialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.SobotOnlineChatActivity;
import com.sobot.online.adapter.SobotHistoryChatAdapter;
import com.sobot.online.adapter.SobotHistoryUserChatAdapter;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.kpswitch.util.KeyboardUtil;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.HistoryChatModel;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//历史记录搜索

public class SobotOnlineHistorySearchActivity extends SobotOnlineDialogBaseActivity implements SobotRecyclerView.LoadingListener, View.OnClickListener {

    private LinearLayout tv_online_pop_search_root;
    private TextView tv_online_pop_search_cancle;
    private EditText et_online_search_content;
    SobotSwipeMenuRecyclerView ssmrv_online_search;

    private SobotHistoryUserChatAdapter historyUserChatAdapter;
    private SobotHistoryChatAdapter historyChatAdapter;
    private int currentPage = 1;//用户列表当前页
    private int pageSize = 20;//每页显示数量
    List<HistoryUserInfoModel> dataList = new ArrayList<>();
    List<HistoryChatModel> historyChatModelList = new ArrayList<>();

    private int isChatOrUser;//0筛选会话，1筛选用户

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = SobotScreenUtils.getScreenHeight() * 39 / 40;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_online_pop_history_search;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SobotKeyboardUtils.hideSoftInput(getSobotActivity());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        tv_online_pop_search_cancle = findViewById(R.id.tv_online_pop_search_cancle);
        tv_online_pop_search_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_online_pop_search_root = findViewById(R.id.tv_online_pop_search_root);
        et_online_search_content = findViewById(R.id.et_online_search_content);
        et_online_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isChatOrUser == 0) {
                    currentPage = 1;
                    historyChatModelList.clear();
                    getHistoryChatList(1, pageSize, et_online_search_content.getText().toString().trim());
                } else {
                    currentPage = 1;
                    dataList.clear();
                    queryConversationList(1, pageSize, OnlineConstant.TYPE_ALL, et_online_search_content.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        showSoftInputFromWindow(et_online_search_content);
        ssmrv_online_search = findViewById(R.id.ssmrv_online_search);

        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getSobotActivity());
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_search.setLayoutManager(chatLayoutManager);
        ssmrv_online_search.setPullRefreshEnabled(true);
        ssmrv_online_search.setLoadingMoreEnabled(true);
        ssmrv_online_search.setLoadingListener(this);
        ssmrv_online_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    View focusView = getSobotActivity().getCurrentFocus();
                    if (focusView != null) {
                        KeyboardUtil.hideKeyboard(getSobotActivity().getCurrentFocus());
                        focusView.clearFocus();
                    }
                }
                return false;
            }
        });
        displayInNotch(et_online_search_content);
    }

    @Override
    public void initData() {
        isChatOrUser = getIntent().getIntExtra("isChatOrUser", 0);
        if (isChatOrUser == 0) {
            historyChatAdapter = new SobotHistoryChatAdapter(getSobotActivity());
            historyChatAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<HistoryChatModel>() {
                @Override
                public void onItemClick(View view, HistoryChatModel chatModel, int position) {
                    Intent intent = new Intent(getSobotContext(),
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
            ssmrv_online_search.setAdapter(historyChatAdapter);
        } else {
            historyUserChatAdapter = new SobotHistoryUserChatAdapter(getSobotActivity());
            historyUserChatAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<HistoryUserInfoModel>() {
                @Override
                public void onItemClick(View view, HistoryUserInfoModel userInfo, int position) {
                    Intent intent = new Intent(getSobotActivity(),
                            SobotOnlineChatActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userInfo);
                    if (userInfo != null && userInfo.getIsblack() == 1) {
                        bundle.putString("flag", "black");
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
                    startActivity(intent);
                }
            });
            ssmrv_online_search.setAdapter(historyUserChatAdapter);
        }

        if (isChatOrUser == 0) {
            currentPage = 1;
            historyChatModelList.clear();
            historyChatAdapter.setListAll(historyChatModelList);
        } else {
            currentPage = 1;
            dataList.clear();
            historyUserChatAdapter.setListAll(dataList);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        getSobotActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    @Override
    public void onRefresh() {
        if (isChatOrUser == 0) {
            currentPage = 1;
            historyChatModelList.clear();
            getHistoryChatList(1, pageSize, et_online_search_content.getText().toString().trim());
        } else {
            currentPage = 1;
            dataList.clear();
            queryConversationList(1, pageSize, OnlineConstant.TYPE_ALL, et_online_search_content.getText().toString().trim());
        }
    }

    @Override
    public void onLoadMore() {
        if (isChatOrUser == 0) {
            currentPage++;
            getHistoryChatList(currentPage, pageSize, et_online_search_content.getText().toString().trim());
        } else {
            currentPage++;
            queryConversationList(currentPage, pageSize, OnlineConstant.TYPE_ALL, et_online_search_content.getText().toString().trim());
        }
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

    //查询历史用户会话
    public void queryConversationList(int pageNow, final int pageSize, int type, String searchContent) {
        if (TextUtils.isEmpty(searchContent)) {
            dataList.clear();
            historyUserChatAdapter.setListAll(dataList);
            return;
        }
        Map params = new HashMap();
        if (!TextUtils.isEmpty(searchContent)) {
            params.put("uname", searchContent);
        }
        zhiChiApi.queryConversationList(getSobotActivity(), pageNow, pageSize, type, params, new SobotResultCallBack<List<HistoryUserInfoModel>>() {

            @Override
            public void onSuccess(List<HistoryUserInfoModel> userInfos) {
                ssmrv_online_search.refreshComplete();
                ssmrv_online_search.loadMoreComplete();
                if (userInfos != null) {
                    dataList.addAll(userInfos);
                    historyUserChatAdapter.setListAll(dataList);
                    if (userInfos.size() < pageSize) {
                        ssmrv_online_search.setLoadingMoreEnabled(false);
                    } else {
                        ssmrv_online_search.setLoadingMoreEnabled(true);
                    }
                } else {
                    ssmrv_online_search.setLoadingMoreEnabled(false);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ssmrv_online_search.refreshComplete();
                ssmrv_online_search.loadMoreComplete();
                e.printStackTrace();
            }
        });
    }

    public void getHistoryChatList(int pageNow, final int pageSize, String searchContent) {
        Map params = new HashMap();
        if (!TextUtils.isEmpty(searchContent)) {
            params.put("msgTxt", searchContent);
        }
        zhiChiApi.getHistoryChatList(getSobotActivity(), pageNow, pageSize, params, new SobotResultCallBack<List<HistoryChatModel>>() {

            @Override
            public void onSuccess(List<HistoryChatModel> chatModels) {
                ssmrv_online_search.refreshComplete();
                ssmrv_online_search.loadMoreComplete();
                if (chatModels != null) {
                    historyChatModelList.addAll(chatModels);
                    historyChatAdapter.setListAll(historyChatModelList);
                    if (chatModels.size() < pageSize) {
                        ssmrv_online_search.setLoadingMoreEnabled(false);
                    } else {
                        ssmrv_online_search.setLoadingMoreEnabled(true);
                    }
                } else {
                    ssmrv_online_search.setLoadingMoreEnabled(false);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ssmrv_online_search.refreshComplete();
                ssmrv_online_search.loadMoreComplete();
                e.printStackTrace();
            }
        });
    }
}