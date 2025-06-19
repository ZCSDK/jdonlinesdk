package com.sobot.online.dialog;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.online.R;
import com.sobot.online.adapter.SobotPopCommonAdapter;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.onlinecommon.model.OnlineCustomPopModel;

import java.util.ArrayList;
import java.util.List;

public class OnlineCustomPopActivity extends SobotOnlineDialogBaseActivity {

    private TextView tv_online_pop_header_cancle;
    private EditText et_online_search_content;
    private View view_online_search_split;
    private TextView tv_online_pop_header_title;
    private RecyclerView srv_online_common_list;
    private SobotPopCommonAdapter popCommonAdapter;

    private List<OnlineCustomPopModel> customPopModelList;
    private List<OnlineCustomPopModel> searchPopModelList;
    private String title;//头部title
    private boolean isShowSeach;//是否显示搜索
    private int selectIndex = -1;//默认没有选

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_online_pop_common;
    }


    @Override
    public void initView() {
        searchPopModelList = new ArrayList<>();
        tv_online_pop_header_cancle = findViewById(R.id.tv_online_pop_header_cancle);
        tv_online_pop_header_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_online_pop_header_title = findViewById(R.id.tv_online_pop_header_title);
        view_online_search_split = findViewById(R.id.view_online_search_split);
        et_online_search_content = findViewById(R.id.et_online_search_content);
        et_online_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (customPopModelList != null && customPopModelList.size() > 0 && searchPopModelList != null) {
                    searchPopModelList.clear();
                    for (int i = 0; i < customPopModelList.size(); i++) {
                        //检测是否包含用户搜索的内容
                        if (customPopModelList.get(i).getsValue().contains(s)) {
                            searchPopModelList.add(customPopModelList.get(i));
                        }
                        //刷新列表
                        popCommonAdapter.setListAll(searchPopModelList);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        srv_online_common_list = findViewById(R.id.srv_online_common_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getSobotActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        srv_online_common_list.setLayoutManager(layoutManager);

        displayInNotch(et_online_search_content);
        displayInNotch(srv_online_common_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initData() {
        popCommonAdapter = new SobotPopCommonAdapter(getSobotActivity());
        int selectPosition = getIntent().getIntExtra("selectPosition", -1);
        if (selectPosition >= 0) {
            popCommonAdapter.setSelectPosition(selectPosition);
        }
        popCommonAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<OnlineCustomPopModel>() {
            @Override
            public void onItemClick(View view, OnlineCustomPopModel item, int position) {
                selectIndex = item.getsPosition();
                Intent intent = new Intent();
                intent.putExtra("selectIndex", selectIndex);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        srv_online_common_list.setAdapter(popCommonAdapter);
        customPopModelList = (List<OnlineCustomPopModel>) getIntent().getSerializableExtra("customPopModelList");
        if (customPopModelList != null) {
            popCommonAdapter.setListAll(customPopModelList);
        }
        title = getIntent().getStringExtra("title");
        tv_online_pop_header_title.setText(!TextUtils.isEmpty(title) ? title : "");
        isShowSeach = getIntent().getBooleanExtra("isShowSeach", false);
        if (isShowSeach && customPopModelList != null && customPopModelList.size() > 0) {
            et_online_search_content.setVisibility(View.VISIBLE);
            view_online_search_split.setVisibility(View.VISIBLE);
        } else {
            et_online_search_content.setVisibility(View.GONE);
            view_online_search_split.setVisibility(View.GONE);
        }
    }
}