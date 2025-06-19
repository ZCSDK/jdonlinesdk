package com.sobot.online.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.OnlineEnterPriseAdapter;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.onlinecommon.model.OnlineEnterPriseModel;
import com.sobot.onlinecommon.utils.SobotScreenUtils;

import java.util.ArrayList;
import java.util.List;

//客户信息 公司搜索
public class OnlineEnterPriseSearchActivity extends SobotOnlineDialogBaseActivity implements SobotRecyclerView.LoadingListener, View.OnClickListener {

    private TextView tv_online_pop_header_cancle;
    private TextView tv_online_pop_header_title;
    private EditText et_online_search_content;
    SobotSwipeMenuRecyclerView ssmrv_online_search;

    private OnlineEnterPriseAdapter enterPriseAdapter;
    private int currentPage = 1;//用户列表当前页
    private int pageSize = 20;//每页显示数量
    List<OnlineEnterPriseModel> enterPriseModelList = new ArrayList<>();

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
        return R.layout.sobot_online_pop_enterprise_search;
    }


    @Override
    public void initView() {
        tv_online_pop_header_cancle = findViewById(R.id.tv_online_pop_header_cancle);
        tv_online_pop_header_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_online_pop_header_title = findViewById(R.id.tv_online_pop_header_title);
        tv_online_pop_header_title.setText(getResString("online_select_enterprise"));
        et_online_search_content = findViewById(R.id.et_online_search_content);
        et_online_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPage = 1;
                enterPriseModelList.clear();
                getAppEnterpriseList(1, pageSize, et_online_search_content.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ssmrv_online_search = findViewById(R.id.ssmrv_online_search);

        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getSobotActivity());
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_search.setLayoutManager(chatLayoutManager);
        ssmrv_online_search.setPullRefreshEnabled(true);
        ssmrv_online_search.setLoadingMoreEnabled(true);
        ssmrv_online_search.setLoadingListener(this);
        displayInNotch(et_online_search_content);
    }

    @Override
    public void initData() {
        enterPriseAdapter = new OnlineEnterPriseAdapter(getSobotActivity(), getIntent().getStringExtra("selectEnterpriseId"));
        enterPriseAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<OnlineEnterPriseModel>() {
            @Override
            public void onItemClick(View view, OnlineEnterPriseModel enterPriseModel, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("enterPriseModel", enterPriseModel);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ssmrv_online_search.setAdapter(enterPriseAdapter);

        currentPage = 1;
        enterPriseModelList.clear();
        getAppEnterpriseList(1, pageSize, et_online_search_content.getText().toString().trim());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        enterPriseModelList.clear();
        getAppEnterpriseList(1, pageSize, et_online_search_content.getText().toString().trim());
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        getAppEnterpriseList(currentPage, pageSize, et_online_search_content.getText().toString().trim());
    }

    //查询公司列表
    public void getAppEnterpriseList(int pageNow, final int pageSize, String searchContent) {
        zhiChiApi.getAppEnterpriseList(getSobotActivity(), pageNow, pageSize, searchContent,"3", new SobotResultCallBack<List<OnlineEnterPriseModel>>() {

            @Override
            public void onSuccess(List<OnlineEnterPriseModel> enterPriseModels) {
                ssmrv_online_search.refreshComplete();
                ssmrv_online_search.loadMoreComplete();
                if (enterPriseModels != null) {
                    enterPriseModelList.addAll(enterPriseModels);
                    enterPriseAdapter.setListAll(enterPriseModelList);
                    if (enterPriseModels.size() < pageSize) {
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