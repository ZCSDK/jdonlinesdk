package com.sobot.online.dialog;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.SobotPopUnitTypeAdapter;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.onlinecommon.model.UnitTypeAndFieldModel;
import com.sobot.onlinecommon.model.UnitTypeInfoModel;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotScreenUtils;
import com.sobot.onlinecommon.utils.SobotSizeUtils;

import java.util.ArrayList;
import java.util.List;

//服务总结  业务类型选择弹窗
public class SobotOnlineUnitTypeActivity extends SobotOnlineDialogBaseActivity implements View.OnClickListener {

    private SobotSwipeMenuRecyclerView ssmrv_online_unit_type;
    private SobotPopUnitTypeAdapter unitTypeAdapter;
    List<UnitTypeInfoModel> unitTypeInfoModelList;
    private TextView tv_online_pop_header_title;//头部标题
    private TextView tv_online_pop_header_cancle;//头部取消按钮
    private LinearLayout ll_online_select_unit_type;
    private TextView tv_pop_user_select_count;//用户选中的业务类型数量
    private TextView tv_pop_user_select_commit;//确定
    private List<UnitTypeInfoModel> unitTypeNavList;//业务类型导航路径

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = SobotScreenUtils.getScreenHeight() * 34 / 40;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_online_pop_unit_type;
    }


    @Override
    public void initView() {
        unitTypeInfoModelList = new ArrayList<>();
        unitTypeNavList = new ArrayList<>();
        unitTypeNavList.add(new UnitTypeInfoModel("0", getString(R.string.online_all)));
        ll_online_select_unit_type = findViewById(R.id.ll_online_select_unit_type);
        tv_online_pop_header_title = findViewById(R.id.tv_online_pop_header_title);
        tv_online_pop_header_title.setText(getString(R.string.online_summary_business_type));
        tv_pop_user_select_count = findViewById(R.id.tv_pop_user_select_count);
        tv_pop_user_select_commit = findViewById(R.id.tv_pop_user_select_commit);
        tv_pop_user_select_commit.setOnClickListener(this);
        tv_online_pop_header_cancle = findViewById(R.id.tv_online_pop_header_cancle);
        tv_online_pop_header_cancle.setOnClickListener(this);
        ssmrv_online_unit_type = findViewById(R.id.ssmrv_online_unit_type);
        LinearLayoutManager chatLayoutManager = new LinearLayoutManager(getSobotActivity());
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_unit_type.setLayoutManager(chatLayoutManager);
        ssmrv_online_unit_type.setPullRefreshEnabled(false);
        ssmrv_online_unit_type.setLoadingMoreEnabled(false);
        addUnitTypeNavLayout(unitTypeNavList);
    }

    @Override
    public void initData() {
        ArrayList<UnitTypeInfoModel> selectUnitTypeList = (ArrayList<UnitTypeInfoModel>) getIntent().getSerializableExtra("select_unit_type_list");
        unitTypeAdapter = new SobotPopUnitTypeAdapter(getSobotActivity(), selectUnitTypeList, new SobotPopUnitTypeAdapter.OnCheckTypeChangeListener() {
            @Override
            public void onCheckTypeChange(UnitTypeInfoModel selectUnitType) {
                getUnifoInfoById(selectUnitType, true);
            }

            @Override
            public void onUserCheckTypeChange(UnitTypeInfoModel unitTypeInfoModel) {
                tv_pop_user_select_count.setText(unitTypeAdapter.getUserSelectUnitTypeList().size() + "/3");
            }
        });
        unitTypeAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<UnitTypeInfoModel>() {
            @Override
            public void onItemClick(View view, UnitTypeInfoModel userInfo, int position) {
            }
        });
        ssmrv_online_unit_type.setAdapter(unitTypeAdapter);
        List<UnitTypeInfoModel> unit_type_list = (List<UnitTypeInfoModel>) getIntent().getSerializableExtra("unit_type_list");
        if (unit_type_list != null) {
            unitTypeInfoModelList.clear();
            unitTypeInfoModelList.addAll(unit_type_list);
            unitTypeAdapter.setListAll(unit_type_list);
        }
        if (selectUnitTypeList != null) {
            tv_pop_user_select_count.setText(unitTypeAdapter.getUserSelectUnitTypeList().size() + "/3");
        }
    }

    public List<UnitTypeInfoModel> removeUnitTypeInfoAfterId(String typeId) {
        List<UnitTypeInfoModel> tempList = new ArrayList<>();
        tempList.clear();
        for (int i = 0; i < unitTypeNavList.size(); i++) {
            tempList.add(unitTypeNavList.get(i));
            if (typeId.equals(unitTypeNavList.get(i).getTypeId())) {
                break;
            }
        }
        unitTypeNavList.clear();
        unitTypeNavList.addAll(tempList);
        return tempList;
    }


    //动态添加分类导航
    public void addUnitTypeNavLayout(final List<UnitTypeInfoModel> unitTypeNavList) {
        ll_online_select_unit_type.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, SobotSizeUtils.dp2px(8), 0);
        if (unitTypeNavList.size() == 1) {
            final TextView textView = new TextView(getSobotContext());
            Drawable drawable = getResources().getDrawable(R.drawable.sobot_online_right_arrow);
            drawable.setBounds(0, 0, SobotSizeUtils.dp2px(6), SobotSizeUtils.dp2px(12));//必要,不然会不显示
            textView.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
            textView.setCompoundDrawables(null, null, drawable, null);
            textView.setCompoundDrawablePadding(SobotSizeUtils.dp2px(8));
            textView.setText(unitTypeNavList.get(0).getTypeName());
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            layoutParams.setMargins(0, 0, SobotSizeUtils.dp2px(8), 0);
            textView.setLayoutParams(layoutParams);
            ll_online_select_unit_type.addView(textView);
            return;
        }

        for (int i = 0; i < unitTypeNavList.size(); i++) {
            if (i == (unitTypeNavList.size() - 1)) {
                TextView textView = new TextView(getSobotContext());
                textView.setTextColor(getResources().getColor(R.color.sobot_online_common_gray2)); //设置背景
                textView.setText(unitTypeNavList.get(i).getTypeName());
                textView.setTextSize(15);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
                textView.setLayoutParams(layoutParams);
                ll_online_select_unit_type.addView(textView);
            } else {
                final TextView textView = new TextView(getSobotContext());
                Drawable drawable = getResources().getDrawable(R.drawable.sobot_online_right_arrow);
                drawable.setBounds(0, 0, SobotSizeUtils.dp2px(6), SobotSizeUtils.dp2px(12));//必要,不然会不显示
                textView.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
                textView.setCompoundDrawables(null, null, drawable, null);
                textView.setCompoundDrawablePadding(SobotSizeUtils.dp2px(8));
                textView.setText(unitTypeNavList.get(i).getTypeName());
                textView.setTextSize(15);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                layoutParams.setMargins(0, 0, SobotSizeUtils.dp2px(8), 0);
                textView.setLayoutParams(layoutParams);
                final String typeId = unitTypeNavList.get(i).getTypeId();
                final int selIndex = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addUnitTypeNavLayout(removeUnitTypeInfoAfterId(typeId));
                        if (unitTypeInfoModelList != null)
                            getUnifoInfoById(unitTypeNavList.get(selIndex), false);
                    }
                });
                ll_online_select_unit_type.addView(textView);
            }

        }
    }


    @Override
    public void onClick(View v) {
        if (v == tv_online_pop_header_cancle) {
            finish();
        }
        if (v == tv_pop_user_select_commit) {
            if (unitTypeAdapter != null && unitTypeAdapter.getUserSelectUnitTypeList() != null && unitTypeAdapter.getUserSelectUnitTypeList().size() > 0) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("unitTypeCheckedList", unitTypeAdapter.getUserSelectUnitTypeList());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
            }
            finish();
        }

    }


    /**
     * 根据单元id查询业务类型
     */
    public void getUnifoInfoById(final UnitTypeInfoModel selectUnitType, final boolean isAddNav) {
        String unitId = getIntent().getStringExtra("unitId");
        zhiChiApi.getUnifoInfoById(this, unitId, selectUnitType == null ? "" : selectUnitType.getTypeId(), new SobotResultCallBack<UnitTypeAndFieldModel>() {
            @Override
            public void onSuccess(UnitTypeAndFieldModel unitTypeAndFieldModel) {
                SobotOnlineLogUtils.i(unitTypeAndFieldModel.getTypeList().toString());
                if (unitTypeAndFieldModel.getTypeList() != null && unitTypeAndFieldModel.getTypeList().size() > 0) {
                    unitTypeAdapter.setListAll(unitTypeAndFieldModel.getTypeList());
                    if (isAddNav) {
                        unitTypeNavList.add(selectUnitType);
                        addUnitTypeNavLayout(unitTypeNavList);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }
}