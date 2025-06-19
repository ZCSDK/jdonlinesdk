package com.sobot.online.dialog;

import android.app.Activity;
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
import com.sobot.online.adapter.OnlineSummaryUnitAndTypeAdapter;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.onlinecommon.model.UnitInfoModel;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;
import com.sobot.onlinecommon.utils.SobotScreenUtils;

import java.util.ArrayList;
import java.util.List;

//服务总结 - 业务搜索
public class OnlineSummarySearchActivity extends SobotOnlineDialogBaseActivity {

    private TextView tv_online_pop_search_cancle;
    private EditText et_online_search_content;
    SobotSwipeMenuRecyclerView ssmrv_online_search;

    private OnlineSummaryUnitAndTypeAdapter unitAndTypeAdapter;
    List<UnitInfoModel> dataList;
    private String cid;


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
    public void initView() {
        tv_online_pop_search_cancle = findViewById(R.id.tv_online_pop_search_cancle);
        tv_online_pop_search_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobotKeyboardUtils.hideSoftInput(getSobotActivity());
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
                selectOperationType(cid, et_online_search_content.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ssmrv_online_search = findViewById(R.id.ssmrv_online_search);

        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getSobotActivity());
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_search.setLayoutManager(chatLayoutManager);
        ssmrv_online_search.setPullRefreshEnabled(false);
        ssmrv_online_search.setLoadingMoreEnabled(false);
        displayInNotch(et_online_search_content);
        showSoftInputFromWindow(getSobotActivity(), et_online_search_content);
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void initData() {
        cid = getIntent().getStringExtra("cid");
        dataList = new ArrayList<>();
        unitAndTypeAdapter = new OnlineSummaryUnitAndTypeAdapter(getSobotActivity());
        unitAndTypeAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<UnitInfoModel>() {
            @Override
            public void onItemClick(View view, UnitInfoModel unitInfoModel, int position) {
                SobotKeyboardUtils.hideSoftInput(getSobotActivity());
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectUnitInfo", unitInfoModel);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ssmrv_online_search.setAdapter(unitAndTypeAdapter);
        dataList.clear();
        selectOperationType(cid, et_online_search_content.getText().toString().trim());
    }


    //查询历史用户会话
    public void selectOperationType(String cid, String searchContent) {
        zhiChiApi.selectOperationType(getSobotActivity(), cid, searchContent, new SobotResultCallBack<List<UnitInfoModel>>() {

            @Override
            public void onSuccess(List<UnitInfoModel> unitInfoModels) {
                ssmrv_online_search.refreshComplete();
                ssmrv_online_search.loadMoreComplete();
                if (unitInfoModels != null) {
                    dataList.addAll(unitInfoModels);
                    unitAndTypeAdapter.setListAll(dataList);
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