package com.sobot.online.dialog;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.SobotOnlineChatActivity;
import com.sobot.online.adapter.SobotReceptionAdapter;
import com.sobot.online.api.MemoryCacheManager;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.kpswitch.util.KeyboardUtil;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.SynChronousModel;
import com.sobot.onlinecommon.socket.MsgCacheManager;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.socket.module.PushMessageModel;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.onlinecommon.utils.SobotScreenUtils;
import com.sobot.onlinecommon.utils.SobotUtils;

import java.util.ArrayList;
import java.util.List;

//当前会话搜索

public class SobotOnlineReceptionSearchActivity extends SobotOnlineDialogBaseActivity implements View.OnClickListener {
    private TextView tv_online_pop_search_cancle;
    private EditText et_online_search_content;
    SobotSwipeMenuRecyclerView ssmrv_online_search;

    private SobotReceptionAdapter receptionAdapter;
    List<PushMessageModel> dataList = new ArrayList<>();//会话中用户


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

    @Override
    public void initView() {
        tv_online_pop_search_cancle = findViewById(R.id.tv_online_pop_search_cancle);
        tv_online_pop_search_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_online_search_content = findViewById(R.id.et_online_search_content);
        et_online_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataList.clear();
                synChronous(et_online_search_content.getText().toString());
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
        ssmrv_online_search.setPullRefreshEnabled(false);
        ssmrv_online_search.setLoadingMoreEnabled(false);
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
        receptionAdapter = new SobotReceptionAdapter(getSobotActivity(),false, null);
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
                Intent intent = new Intent(getSobotActivity(),
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
                startActivity(intent);
                Intent intent1 = new Intent();
                intent1.setAction(SobotSocketConstant.BROADCAST_SOBOT_LIST_SYNCHRONOUS_USERS);
                SobotUtils.getApp().sendBroadcast(intent1);
            }
        });
        ssmrv_online_search.setAdapter(receptionAdapter);
        dataList.clear();
        receptionAdapter.setListAll(dataList);
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

    private void synChronous(String searchKey) {
        if(TextUtils.isEmpty(searchKey)){
            dataList.clear();
            receptionAdapter.setListAll(dataList);
            return;
        }
        dataList.clear();
        receptionAdapter.setListAll(dataList);
        zhiChiApi.synChronous(getSobotActivity(), searchKey, new SobotResultCallBack<SynChronousModel>() {
            @Override
            public void onSuccess(SynChronousModel synChronousModel) {
                if (synChronousModel != null && synChronousModel.getUserList() != null) {
                    dataList.clear();
                    List<PushMessageModel> userList = synChronousModel.getUserList();
                    dataList.addAll(userList);
                    receptionAdapter.setListAll(dataList);
                    receptionAdapter.myNotifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }
}