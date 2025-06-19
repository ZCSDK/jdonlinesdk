package com.sobot.online.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.SobotTransferKefuAdapter;
import com.sobot.online.adapter.SobotTransferKefuGroupAdapter;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.dialog.SobotDialogUtils;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.model.OnLineGroupModel;
import com.sobot.onlinecommon.model.OnLineServiceModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.utils.SobotUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 转接界面
 * @Author: znw
 * @CreateDate: 2020/09/25 10:05
 * @Version: 1.0
 */
public class SobotOnlineTransferActivity extends SobotOnlineBaseActivity implements View.OnClickListener {

    private TextView tv_online_transfer_kefu;//转接客服
    private TextView tv_online_transfer_kefu_group;//转接客服组
    //头部返回按钮
    private View backView;
    private TextView tv_online_transfer_refresh;

    private EditText et_online_search_content;

    private int currentpage = 0;//当前页面 0转接客服页面，1转接客服组页面

    SobotSwipeMenuRecyclerView ssmrv_online_transfer_kefu;
    SobotSwipeMenuRecyclerView ssmrv_online_transfer_kefu_group;
    private SobotTransferKefuAdapter transferKefuAdapter;
    private SobotTransferKefuGroupAdapter transferKefuGroupAdapter;
    List<OnLineServiceModel> serviceModels = new ArrayList<>();
    List<OnLineGroupModel> groupModels = new ArrayList<>();
    private String cid;
    private String uid;

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_layout_transfer;
    }

    @Override
    protected void initView() {
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
        setHearderTitle(getString(R.string.online_transfer));
        tv_online_transfer_refresh = findViewById(R.id.tv_online_transfer_refresh);
        tv_online_transfer_kefu = findViewById(R.id.tv_online_transfer_kefu);
        tv_online_transfer_kefu_group = findViewById(R.id.tv_online_transfer_kefu_group);
        tv_online_transfer_kefu.setOnClickListener(this);
        tv_online_transfer_kefu_group.setOnClickListener(this);
        ssmrv_online_transfer_kefu = findViewById(R.id.ssmrv_online_transfer_kefu);
        ssmrv_online_transfer_kefu_group = findViewById(R.id.ssmrv_online_transfer_kefu_group);
        et_online_search_content = findViewById(R.id.et_online_search_content);
        ssmrv_online_transfer_kefu.setPullRefreshEnabled(false);//禁止下拉
        ssmrv_online_transfer_kefu.setLoadingMoreEnabled(false);//禁止加载更多
        ssmrv_online_transfer_kefu_group.setPullRefreshEnabled(false);//禁止下拉
        ssmrv_online_transfer_kefu_group.setLoadingMoreEnabled(false);//禁止加载更多

        tv_online_transfer_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentpage == 0) {
                    serviceModels.clear();
                    transferKefuAdapter.setListAll(serviceModels);
                    getOtherAdmins(et_online_search_content.getText().toString().trim(), true);
                } else {
                    groupModels.clear();
                    transferKefuGroupAdapter.setListAll(groupModels);
                    queryTransferGroup(et_online_search_content.getText().toString().trim(), true);
                }
            }
        });
    }

    @Override
    protected void initData() {
        cid = getIntent().getStringExtra("cid");
        uid = getIntent().getStringExtra("uid");
        LinearLayoutManager transferKefuLayoutManager = new LinearLayoutManager(getSobotActivity());
        transferKefuLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_transfer_kefu.setLayoutManager(transferKefuLayoutManager);
        ssmrv_online_transfer_kefu.setPullRefreshEnabled(false);
        ssmrv_online_transfer_kefu.setLoadingMoreEnabled(false);
        transferKefuAdapter = new SobotTransferKefuAdapter(getSobotActivity(), new SobotTransferKefuAdapter.OnTransferKefuListener() {
            @Override
            public void onTransferKefu(OnLineServiceModel onLineServiceModel, int position) {
                transfer(onLineServiceModel, position);
            }
        });
        ssmrv_online_transfer_kefu.setAdapter(transferKefuAdapter);
        LinearLayoutManager transferKefuGroupLayoutManager = new LinearLayoutManager(getSobotActivity());
        transferKefuLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_transfer_kefu_group.setLayoutManager(transferKefuGroupLayoutManager);
        ssmrv_online_transfer_kefu_group.setPullRefreshEnabled(false);
        ssmrv_online_transfer_kefu_group.setLoadingMoreEnabled(false);
        transferKefuGroupAdapter = new SobotTransferKefuGroupAdapter(getSobotActivity(), new SobotTransferKefuGroupAdapter.OnTransferKefuGroupListener() {
            @Override
            public void onTransferKefuGroup(OnLineGroupModel groupModel, int position) {
                transferToGroup(groupModel, position);
            }
        });

        ssmrv_online_transfer_kefu_group.setAdapter(transferKefuGroupAdapter);
        queryTransferGroup("", false);
        getOtherAdmins("", true);
        et_online_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentpage == 0) {
                    //搜索转接客服
                    getOtherAdmins(s.toString(), false);
                }
                if (currentpage == 1) {
                    //搜索转接客服组
                    queryTransferGroup(s.toString(), false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tv_online_transfer_kefu) {
            currentpage = 0;
            et_online_search_content.setText("");
            tv_online_transfer_kefu.setTextColor(getResources().getColor(R.color.sobot_online_color));
            tv_online_transfer_kefu_group.setTextColor(getResources().getColor(R.color.sobot_online_common_gray2));
            ssmrv_online_transfer_kefu.setVisibility(View.VISIBLE);
            ssmrv_online_transfer_kefu_group.setVisibility(View.GONE);
        }
        if (v == tv_online_transfer_kefu_group) {
            currentpage = 1;
            et_online_search_content.setText("");
            tv_online_transfer_kefu_group.setTextColor(getResources().getColor(R.color.sobot_online_color));
            tv_online_transfer_kefu.setTextColor(getResources().getColor(R.color.sobot_online_common_gray2));
            ssmrv_online_transfer_kefu.setVisibility(View.GONE);
            ssmrv_online_transfer_kefu_group.setVisibility(View.VISIBLE);

        }
    }

    //查询其它客服列表
    public void getOtherAdmins(String keyWord, final boolean isshowLoading) {
        if (isshowLoading) {
            SobotDialogUtils.startProgressDialog(getSobotContext());
        }
        zhiChiApi.getOtherAdmins(getSobotActivity(), keyWord, new SobotResultCallBack<List<OnLineServiceModel>>() {

            @Override
            public void onSuccess(List<OnLineServiceModel> serviceModelList) {
                if (isshowLoading) {
                    SobotDialogUtils.stopProgressDialog(getSobotContext());
                }
                ssmrv_online_transfer_kefu.refreshComplete();
                ssmrv_online_transfer_kefu.loadMoreComplete();
                if (serviceModelList != null) {
                    serviceModels.clear();
                    serviceModels.addAll(serviceModelList);
                    transferKefuAdapter.setListAll(serviceModelList);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (isshowLoading) {
                    SobotDialogUtils.stopProgressDialog(getSobotContext());
                }
                ssmrv_online_transfer_kefu.refreshComplete();
                ssmrv_online_transfer_kefu.loadMoreComplete();
                e.printStackTrace();
            }
        });
    }

    //转接其它客服
    public void transfer(OnLineServiceModel onLineServiceModel, final int position) {
        SobotDialogUtils.startProgressDialog(getSobotContext());
        zhiChiApi.transfer(getSobotActivity(), cid, onLineServiceModel.getId(), uid, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode onlineBaseCode) {
                SobotDialogUtils.stopProgressDialog(getSobotContext());
                transferKefuAdapter.removeToIndex(position);
                SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_transfer_successful));
                Intent intent = new Intent();
                intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_TRANSFER);
                SobotUtils.getApp().sendBroadcast(intent);
                SobotGlobalContext.getInstance().finishActivity();
                SobotGlobalContext.getInstance().finishActivity();
            }

            @Override
            public void onFailure(Exception e, String s) {
                SobotDialogUtils.stopProgressDialog(getSobotContext());
                SobotToastUtil.showCustomToast(getSobotActivity(), s);
            }
        });
    }

    //查询客服组列表
    public void queryTransferGroup(String keyWord, final boolean isshowLoading) {
        if (isshowLoading) {
            SobotDialogUtils.startProgressDialog(getSobotContext());
        }
        zhiChiApi.queryTransferGroup(getSobotActivity(), keyWord, new SobotResultCallBack<List<OnLineGroupModel>>() {

            @Override
            public void onSuccess(List<OnLineGroupModel> groupModelList) {
                if (isshowLoading) {
                    SobotDialogUtils.stopProgressDialog(getSobotContext());
                }
                ssmrv_online_transfer_kefu_group.refreshComplete();
                ssmrv_online_transfer_kefu_group.loadMoreComplete();
                if (groupModelList != null) {
                    groupModels.clear();
                    groupModels.addAll(groupModelList);
                    transferKefuGroupAdapter.setListAll(groupModels);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (isshowLoading) {
                    SobotDialogUtils.stopProgressDialog(getSobotContext());
                }
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
                ssmrv_online_transfer_kefu_group.refreshComplete();
                ssmrv_online_transfer_kefu_group.loadMoreComplete();
                e.printStackTrace();
            }
        });
    }

    //转接其它客服组
    public void transferToGroup(OnLineGroupModel onLineGroupModel, final int position) {
        SobotDialogUtils.startProgressDialog(getSobotContext());
        zhiChiApi.transferToGroup(getSobotActivity(), cid, onLineGroupModel.getGroupId(), onLineGroupModel.getGroupName(), uid,
                new SobotResultCallBack<OnlineBaseCode>() {
                    @Override
                    public void onSuccess(OnlineBaseCode onlineBaseCode) {
                        SobotDialogUtils.stopProgressDialog(getSobotContext());
                        transferKefuGroupAdapter.removeToIndex(position);
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_transfer_successful));
                        Intent intent = new Intent();
                        intent.setAction(SobotSocketConstant.BROADCAST_SOBOT_TRANSFER);
                        SobotUtils.getApp().sendBroadcast(intent);
                        SobotGlobalContext.getInstance().finishActivity();
                        SobotGlobalContext.getInstance().finishActivity();
                    }

                    @Override
                    public void onFailure(Exception e, String s) {
                        SobotDialogUtils.stopProgressDialog(getSobotContext());
                        SobotToastUtil.showCustomToast(getSobotActivity(), s);
                    }
                });
    }
}
