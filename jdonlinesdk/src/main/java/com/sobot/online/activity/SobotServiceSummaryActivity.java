package com.sobot.online.activity;

import static com.sobot.onlinecommon.socket.SobotSocketConstant.BROADCAST_CUSTOM_COMITSUMMARY;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.dialog.OnlineCustomPopActivity;
import com.sobot.online.dialog.OnlineSummarySearchActivity;
import com.sobot.online.dialog.SobotOnlineUnitTypeActivity;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.OnlineCustomPopModel;
import com.sobot.onlinecommon.model.SummaryInfoModelResult;
import com.sobot.onlinecommon.model.SummaryModel;
import com.sobot.onlinecommon.model.UnitInfoModel;
import com.sobot.onlinecommon.model.UnitTypeAndFieldModel;
import com.sobot.onlinecommon.model.UnitTypeInfoModel;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 服务总结界面
 * @Author: znw
 * @Date: 2020/10/20 下午2:10
 * @Version: 1.0
 */
public class SobotServiceSummaryActivity extends SobotOnlineBaseActivity implements View.OnClickListener {
    public final static int SUBMITTYPE_VALID_SESSION = 0;//有效会话
    public final static int SUBMITTYPE_INVALID_SESSION = 1;//无效会话
    private HistoryUserInfoModel userInfo;
    private String cid;

    private LinearLayout ll_summary_status;//处理状态
    private TextView tv_summary_status;
    private LinearLayout ll_summary_search_business;//所属业务
    private TextView tv_summary_search_business;
    private LinearLayout ll_summary_business_type;//业务类型
    private LinearLayout ll_online_select_unit_type;//已选的业务类型
    private TextView tv_summary_business_type;
    private LinearLayout ll_summary_remark;//备注
    private EditText et_summary_remark;
    private TextView tv_sobot_online_save;//保存提交
    private TextView tv_online_search_context;//模糊搜索

    private ArrayList<OnlineCustomPopModel> statusList;//解决状态列表
    private ArrayList<UnitInfoModel> unitInfoModelList;//咨询业务列表
    private ArrayList<UnitTypeInfoModel> unitTypeCheckedList;//选中的业务类型集合，最多只能3个


    int statueSelectIndex = -1;//选择的服务总结状态 已解决: "1"  未解决："0"，默认-1 未选不能提交
    private Map<String, String> gender;

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_service_summary;
    }

    @Override
    protected void initView() {
        setHearderTitle(getResString("online_service_summary"));
        getHearderLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobotKeyboardUtils.hideSoftInput(getSobotActivity());
                finish();
            }
        });
        getHearderRightView().setVisibility(View.VISIBLE);
        getHearderRightView().setText(getResString("online_invalid_session"));
        getHearderRightView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSummary(SUBMITTYPE_INVALID_SESSION);
            }
        });
        ll_summary_status = findViewById(R.id.ll_summary_status);
        tv_summary_status = findViewById(R.id.tv_summary_status);
        ll_summary_status.setOnClickListener(this);
        ll_summary_search_business = findViewById(R.id.ll_summary_search_business);
        tv_summary_search_business = findViewById(R.id.tv_summary_search_business);
        ll_summary_search_business.setOnClickListener(this);
        ll_summary_business_type = findViewById(R.id.ll_summary_business_type);
        tv_summary_business_type = findViewById(R.id.tv_summary_business_type);
        ll_summary_business_type.setOnClickListener(this);
        ll_summary_remark = findViewById(R.id.ll_summary_remark);
        et_summary_remark = findViewById(R.id.et_summary_remark);
        tv_sobot_online_save = findViewById(R.id.tv_sobot_online_save);
        tv_sobot_online_save.setOnClickListener(this);
        ll_online_select_unit_type = findViewById(R.id.ll_online_select_unit_type);
        tv_online_search_context = findViewById(R.id.tv_online_search_context);
        tv_online_search_context.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        unitTypeCheckedList = new ArrayList<>();
        gender = new HashMap<>();
        gender.put("-1", getString(R.string.online_summary_select_business));
        gender.put("1", getString(R.string.online_status_ok));
        gender.put("0", getString(R.string.online_status_no));
        unitInfoModelList = new ArrayList<>();
        userInfo = (HistoryUserInfoModel) getIntent().getSerializableExtra("userInfo");
        cid = getIntent().getStringExtra("cid");
        queryConversation(cid);
    }

    @Override
    public void onClick(View v) {
        if (v == ll_summary_status) {
            //选择处理状态
            statusList = new ArrayList<>();
            statusList.add(new OnlineCustomPopModel(getString(R.string.online_status_ok), 1));
            statusList.add(new OnlineCustomPopModel(getString(R.string.online_status_no), 0));
            Intent userGradeIntent = new Intent(getSobotActivity(), OnlineCustomPopActivity.class);
            userGradeIntent.putExtra("title", getString(R.string.online_summary_status));
            userGradeIntent.putExtra("isShowSeach", false);
            userGradeIntent.putExtra("selectPosition", getSummaryStatusListLevelIndex(tv_summary_status.getText().toString()));
            Bundle bundle = new Bundle();
            bundle.putSerializable("customPopModelList", statusList);
            userGradeIntent.putExtras(bundle);
            startActivityForResult(userGradeIntent, OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_STATUS);
        }
        if (v == ll_summary_search_business) {
            queryUnit("");
        }
        if (v == ll_summary_business_type) {
            if (tv_summary_search_business.getTag() != null && !TextUtils.isEmpty(tv_summary_search_business.getTag().toString())) {
                getUnifoInfoById(tv_summary_search_business.getTag().toString());
            }
        }
        if (v == tv_sobot_online_save) {
            submitSummary(SUBMITTYPE_VALID_SESSION);
        }
        if (tv_online_search_context == v) {
            Intent searchIntent = new Intent(getSobotActivity(), OnlineSummarySearchActivity.class);
            searchIntent.putExtra("cid", cid);
            startActivityForResult(searchIntent, OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_SEARCH);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_STATUS) {
                //选中的处理状态的索引
                //已解决: "1"  未解决："0"
                statueSelectIndex = data.getIntExtra("selectIndex", -1);
                if (statueSelectIndex > -1 && statusList != null && statusList.size() > 0) {
                    for (int i = 0; i < statusList.size(); i++) {
                        if (statueSelectIndex == statusList.get(i).getsPosition()) {
                            tv_summary_status.setText(statusList.get(i).getsValue());
                        }
                    }
                }
            }
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_BUSINESS) {
                //选中的业务
                int businessSelectIndex = data.getIntExtra("selectIndex", -1);
                if (!tv_summary_search_business.getText().toString().equals(unitInfoModelList.get(businessSelectIndex).getUnitName())) {
                    ll_online_select_unit_type.removeAllViews();
                    if (unitTypeCheckedList != null) {
                        unitTypeCheckedList.clear();
                    }
                }
                if (businessSelectIndex > -1 && unitInfoModelList != null && unitInfoModelList.size() > 0) {
                    tv_summary_search_business.setText(unitInfoModelList.get(businessSelectIndex).getUnitName());
                    tv_summary_search_business.setTag(unitInfoModelList.get(businessSelectIndex).getUnitId());
                }
            }
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_UNIT_TYPE) {
                //选中的业务类型
                unitTypeCheckedList = (ArrayList<UnitTypeInfoModel>) data.getSerializableExtra("unitTypeCheckedList");
                if (unitTypeCheckedList != null) {
                    addSelectUnitTypeLayout(unitTypeCheckedList);
                }
            }

            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_SEARCH) {
                //选中的业务或者业务类型
                UnitInfoModel unitInfoModel = (UnitInfoModel) data.getSerializableExtra("selectUnitInfo");
                tv_summary_search_business.setText(unitInfoModel.getUnitName());
                tv_summary_search_business.setTag(unitInfoModel.getUnitId());
                if (unitInfoModel.getTypes() != null && unitInfoModel.getTypes().size() > 0) {
                    UnitTypeInfoModel unitTypeInfoModel = unitInfoModel.getTypes().get(unitInfoModel.getTypes().size() - 1);
                    unitTypeCheckedList.clear();
                    unitTypeCheckedList.add(unitTypeInfoModel);
                }
                addSelectUnitTypeLayout(unitTypeCheckedList);
            }
        }
    }

    //根据服务总结选择对应的索引
    private int getSummaryStatusListLevelIndex(String itemValue) {
        int itemIndex = -1;
        if (statusList != null) {
            for (int i = 0; i < statusList.size(); i++) {
                if (itemValue.equals(statusList.get(i).getsValue())) {
                    return i;
                }
            }
        }
        return itemIndex;
    }

    /**
     * 查询咨询业务
     */
    public void queryUnit(String keyword) {
        zhiChiApi.queryUnit(this, keyword, new SobotResultCallBack<List<UnitInfoModel>>() {
            @Override
            public void onSuccess(List<UnitInfoModel> unitInfoModels) {
                if (unitInfoModels == null || unitInfoModels.size() == 0) {
                    SobotToastUtil.showCustomToast(getSobotContext(), getResString("online_summary_no_business"));
                    return;
                }
                ArrayList<OnlineCustomPopModel> customPopModelList = new ArrayList<>();

                unitInfoModelList.clear();
                unitInfoModelList.addAll(unitInfoModels);
                for (int i = 0; i < unitInfoModels.size(); i++) {
                    UnitInfoModel unitInfoModel = unitInfoModels.get(i);
                    customPopModelList.add(new OnlineCustomPopModel(unitInfoModel.getUnitName(), i));
                }
                Intent vipIntent = new Intent(getSobotActivity(), OnlineCustomPopActivity.class);
                vipIntent.putExtra("title", getString(R.string.online_summary_business));
                vipIntent.putExtra("isShowSeach", false);
                vipIntent.putExtra("selectPosition", getBusinessListLevelIndex(tv_summary_search_business.getText().toString()));
                Bundle bundle = new Bundle();
                bundle.putSerializable("customPopModelList", customPopModelList);
                vipIntent.putExtras(bundle);
                startActivityForResult(vipIntent, OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_BUSINESS);
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }

    /**
     * 根据单元id查询业务类型
     */
    public void getUnifoInfoById(final String unitId) {
        zhiChiApi.getUnifoInfoById(this, unitId, "", new SobotResultCallBack<UnitTypeAndFieldModel>() {
            @Override
            public void onSuccess(UnitTypeAndFieldModel unitTypeAndFieldModel) {
                SobotOnlineLogUtils.i(unitTypeAndFieldModel.getTypeList().toString());
                if (unitTypeAndFieldModel.getTypeList() != null) {
                    //业务类型pop
                    Intent typeIntent = new Intent(getSobotActivity(), SobotOnlineUnitTypeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("unitId", unitId);
                    bundle.putSerializable("unit_type_list", unitTypeAndFieldModel.getTypeList());
                    bundle.putSerializable("select_unit_type_list", unitTypeCheckedList);
                    typeIntent.putExtras(bundle);
                    startActivityForResult(typeIntent, OnlineConstant.SOBOT_REQUEST_CODE_SUMMARY_UNIT_TYPE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    /**
     * 根据单元id查询业务类型
     */
    public void queryConversation(String cid) {
        zhiChiApi.queryConversation(this, cid, new SobotResultCallBack<SummaryInfoModelResult.SummaryInfoModel>() {
            @Override
            public void onSuccess(SummaryInfoModelResult.SummaryInfoModel summaryInfoModel) {
                if (summaryInfoModel != null) {
                    UnitInfoModel unitInfo = summaryInfoModel.getUnitInfo();
                    if (unitInfo != null) {
                        updateUnitInfoUI(unitInfo);
                        String unitDoc = unitInfo.getUnitDoc();
                        et_summary_remark.setHint(unitDoc);
                    }
                    if (!"-1".equals(summaryInfoModel.getQuestionStatus()) && !TextUtils.isEmpty(summaryInfoModel.getQuestionStatus())) {
                        tv_summary_status.setText(gender.get(summaryInfoModel.getQuestionStatus()));
                        statueSelectIndex = TextUtils.isEmpty(summaryInfoModel.getQuestionStatus()) ? -1 : Integer.parseInt(summaryInfoModel.getQuestionStatus());
                    }

                    if (!TextUtils.isEmpty(summaryInfoModel.getQuestionRemark())) {
                        et_summary_remark.setText(summaryInfoModel.getQuestionRemark());
                    }
                    if (summaryInfoModel.getUpdateTime() != 0) {
                        unitTypeCheckedList = summaryInfoModel.getUnitTypeInfo();
                        addSelectUnitTypeLayout(unitTypeCheckedList);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }

    private void updateUnitInfoUI(UnitInfoModel model) {
        if (tv_summary_search_business.getTag() != null && !tv_summary_search_business.getTag().equals(model.getUnitId())) {
            if (unitTypeCheckedList != null) {
                unitTypeCheckedList.clear();
            }
            addSelectUnitTypeLayout(unitTypeCheckedList);
        }
        tv_summary_search_business.setTag(model.getUnitId());
        tv_summary_search_business.setText(model.getUnitName());
    }


    private int getBusinessListLevelIndex(String itemValue) {
        int itemIndex = -1;//默认无业务
        if (unitInfoModelList != null) {
            for (int i = 0; i < unitInfoModelList.size(); i++) {
                if (itemValue.equals(unitInfoModelList.get(i).getUnitName())) {
                    return i;
                }
            }
        }
        return itemIndex;
    }

    /**
     * 提交服务总结
     *
     * @param type SUBMITTYPE_VALID_SESSION
     *             SUBMITTYPE_INVALID_SESSION
     */
    public void submitSummary(final int type) {
        zhiChiApi.summarySubmit(this, getSubmitModel(type), new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode onlineBaseCode) {
                if (onlineBaseCode != null) {
                    //成功
                    if (SUBMITTYPE_INVALID_SESSION == type) {
                        SobotToastUtil.showCustomToast(getSobotContext(), getResString("online_invalid_success_tip"));
                    } else {
                        SobotToastUtil.showCustomToast(getSobotContext(), getResString("online_success_tip"));
                    }
                    Intent intent = new Intent();
                    intent.setAction(BROADCAST_CUSTOM_COMITSUMMARY);
                    intent.putExtra("cid", userInfo.getLastCid());
                    sendBroadcast(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
            }
        });
    }

    /**
     * 根据提交类型获取  需要提交的参数
     *
     * @param type
     * @return
     */
    private SummaryModel getSubmitModel(int type) {
        SummaryModel summaryModel = new SummaryModel();
        summaryModel.setCid(userInfo.getLastCid());
        summaryModel.setUid(userInfo.getId());

        switch (type) {
            case SUBMITTYPE_VALID_SESSION:
                //有效会话
                if (!TextUtils.isEmpty(tv_summary_search_business.getText().toString())) {
                    summaryModel.setOperationId((String) tv_summary_search_business.getTag());
                    summaryModel.setOperationName(tv_summary_search_business.getText().toString());
                }

                String reqTypeId = "";
                if (unitTypeCheckedList != null) {
                    for (int i = 0; i < unitTypeCheckedList.size(); i++) {
                        reqTypeId += unitTypeCheckedList.get(i).getTypeId() + ",";
                    }
                }

                if (!TextUtils.isEmpty(reqTypeId)) {
                    reqTypeId = reqTypeId.substring(0, reqTypeId.length() - 1);
                    summaryModel.setReqTypeId(reqTypeId);
                }

                if (et_summary_remark.getText().toString().length() > 200) {
                    SobotToastUtil.showCustomToast(getSobotContext(), getResString("online_remark_max_length"));
                }
                summaryModel.setQuestionDescribe(et_summary_remark.getText().toString());
                summaryModel.setQuestionStatus(statueSelectIndex);
                break;
            case SUBMITTYPE_INVALID_SESSION:
                //无效会话 只传cid uid
                summaryModel.setQuestionStatus(-1);
                summaryModel.setInvalidSession(true);
                break;
        }
        SobotOnlineLogUtils.i("summaryModel:" + summaryModel.toString());
        return summaryModel;
    }

    //动态添加已选的业务类型
    public void addSelectUnitTypeLayout(final List<UnitTypeInfoModel> selectUnitTypeList) {
        ll_online_select_unit_type.removeAllViews();
        if (selectUnitTypeList.size() > 0) {
            tv_summary_business_type.setVisibility(View.GONE);
            ll_online_select_unit_type.setVisibility(View.VISIBLE);
        } else {
            ll_online_select_unit_type.setVisibility(View.GONE);
            tv_summary_business_type.setVisibility(View.VISIBLE);
            return;
        }
        for (int i = 0; i < selectUnitTypeList.size(); i++) {
            final LinearLayout rootView = (LinearLayout) LayoutInflater.from(getSobotContext()).inflate(getResLayoutId("online_select_unit_type_delete"), ll_online_select_unit_type, false);
            TextView nameTV = rootView.findViewById(R.id.tv_item_name);
            ImageView deleteIV = rootView.findViewById(R.id.iv_item_delete);
            final UnitTypeInfoModel unitTypeInfoModel = selectUnitTypeList.get(i);
            nameTV.setText(unitTypeInfoModel.getTypeName());
            deleteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unitTypeCheckedList.remove(unitTypeInfoModel);
                    ll_online_select_unit_type.removeView(rootView);
                }
            });
            ll_online_select_unit_type.addView(rootView);
        }
    }
}