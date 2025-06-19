package com.sobot.online.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.SobotCategorySmallActivity;
import com.sobot.online.base.SobotOnlineBaseFragment;
import com.sobot.online.dialog.OnlineCustomPopActivity;
import com.sobot.online.dialog.OnlineEnterPriseSearchActivity;
import com.sobot.online.model.SobotProvinceModel;
import com.sobot.online.util.DecimalDigitsInputFilter;
import com.sobot.online.weight.pickerview.builder.OptionsPickerBuilder;
import com.sobot.online.weight.pickerview.builder.TimePickerBuilder;
import com.sobot.online.weight.pickerview.listener.OnOptionsSelectListener;
import com.sobot.online.weight.pickerview.listener.OnTimeSelectListener;
import com.sobot.online.weight.pickerview.view.OptionsPickerView;
import com.sobot.online.weight.pickerview.view.TimePickerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.gson.JsonObject;
import com.sobot.onlinecommon.gson.SobotGsonUtil;
import com.sobot.onlinecommon.model.CreateWorkOrderUser;
import com.sobot.onlinecommon.model.CreateWorkOrderUserResult;
import com.sobot.onlinecommon.model.CusFieldConfigList;
import com.sobot.onlinecommon.model.CusFieldConfigModel;
import com.sobot.onlinecommon.model.CusFieldDataInfoModel;
import com.sobot.onlinecommon.model.EditUserInfoResult;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.OnlineCustomPopModel;
import com.sobot.onlinecommon.model.OnlineEnterPriseModel;
import com.sobot.onlinecommon.model.TicketCusFieldModel;
import com.sobot.onlinecommon.model.TicketResultListModel;
import com.sobot.onlinecommon.model.UserInfoModel;
import com.sobot.onlinecommon.model.WorkOrderUser;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;
import com.sobot.onlinecommon.utils.SobotRegexUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 客户信息页面
 * @Author: znw
 * @CreateDate: 2020/09/7 19:55
 * @Version: 1.0
 */
public class SobotOnlineUserinfoFragment extends SobotOnlineBaseFragment implements View.OnClickListener {
    private View mRootView;
    private TextView tv_user_partnerId;
    private TextView tv_visitor_id;
    private TextView et_user_nickname;
    private TextView tv_user_from;
    private TextView tv_user_lable;
    private TextView tv_user_grade;
    private LinearLayout ll_user_grade;
    private TextView tv_user_vip_grade;
    private LinearLayout ll_user_vip_grade;
    private View v_user_vip_grade_split;
    private TextView tv_user_city;
    private LinearLayout ll_user_city;
    private LinearLayout work_order_user_cusfield;
    private View v_work_order_user_cusfield;


    private EditText et_user_realname;
    private TextView tv_user_enterpriseName;
    private EditText et_user_phone;
    private ImageView iv_add_user_phone;
    private EditText et_user_email;
    private ImageView iv_add_user_email;
    private EditText et_user_qq;
    private EditText et_user_remark;

    private TextView tv_sobot_online_save_user;//保存用户

    private HistoryUserInfoModel userInfoModel;
    private WorkOrderUser workOrderUser;//编辑时的客户信息
    private boolean workFlag;//true 表示编辑客户信息，false表示新增客户信息
    private List<CusFieldConfigModel> cusFieldList = new ArrayList<>();//客户自定义字段列表
    private CusFieldConfigModel mCusFieldConfig;


    //城市三级联动选择器
    OptionsPickerView pvOptions;
    private String proviceName = "";
    private String cityName = "";
    private String areaName = "";
    private String proviceId = "";
    private String cityId = "";
    private String areaId = "";
    private int proviceIndex = 0;
    private int cityIndex = 0;
    private int areaIndex = 0;

    private LinearLayout ll_add_user_email;
    private LinearLayout ll_add_user_phone;
    private LinearLayout ll_user_enterprise;
    private ArrayList<OnlineCustomPopModel> isVipPopModelList;//所有的客户级别列表
    private List<CusFieldDataInfoModel> vipFieldDataInfoLists;//所有的vip级别列表

    private List<EditText> listEmailView = new ArrayList<>();
    private List<EditText> listPhoneView = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_user_info, container, false);
        initView();
        initData();
        return mRootView;
    }

    private void initData() {
        userInfoModel = (HistoryUserInfoModel) getSobotActivity().getIntent().getSerializableExtra("userinfo");
        if (userInfoModel != null) {
            zhiChiApi.getUserInfo(getSobotActivity(), userInfoModel.getId(), new SobotResultCallBack<UserInfoModel>() {
                @Override
                public void onSuccess(final UserInfoModel resultModel) {
                    if (!isAdded()) {
                        return;
                    }
                    if (resultModel != null) {
                        if (!TextUtils.isEmpty(resultModel.getCustomerId())) {
                            zhiChiApi.getAppUserInfoByUserId(getSobotActivity(), resultModel.getCustomerId(), new SobotResultCallBack<WorkOrderUser>() {
                                @Override
                                public void onSuccess(WorkOrderUser workOrderUserModle) {
                                    if (workOrderUserModle != null) {
                                        workOrderUser = workOrderUserModle;
                                        workFlag = true;
                                        setWorkOrderUserInfo();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e, String des) {
                                    SobotToastUtil.showCustomToast(getSobotActivity(), des);
                                }
                            });
                        } else {
                            if (workOrderUser == null) {
                                workOrderUser = new WorkOrderUser();
                            }
                            workOrderUser.setPartnerId(resultModel.getPartnerId());
                            workOrderUser.setNick(resultModel.getUname());
                            workOrderUser.setUname(resultModel.getRealname());
                            workOrderUser.setSource(userInfoModel.getSource());
                            workOrderUser.setVisitorIds(resultModel.getUserId());
                            workOrderUser.setEmail(resultModel.getEmail());
                            workOrderUser.setTel(resultModel.getTel());
                            workOrderUser.setRemark(resultModel.getRemark());
                            workOrderUser.setQq(resultModel.getQq());
                            workFlag = false;
                            setWorkOrderUserInfo();
                        }
                    } else {
                        if (workOrderUser == null) {
                            workOrderUser = new WorkOrderUser();
                        }
                        workOrderUser.setUname(userInfoModel.getUname());
                        workOrderUser.setSource(userInfoModel.getSource());
                        workOrderUser.setVisitorIds(userInfoModel.getId());
                        workFlag = false;
                        setWorkOrderUserInfo();
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotToastUtil.showCustomToast(getSobotActivity(), des);
                }
            });
        }
    }


    protected void initView() {
        initJsonData();
        vipFieldDataInfoLists = new ArrayList<>();
        tv_user_partnerId = mRootView.findViewById(R.id.tv_user_partnerId);
        tv_visitor_id = mRootView.findViewById(R.id.tv_visitor_id);
        et_user_nickname = mRootView.findViewById(R.id.et_user_nickname);
        tv_user_from = mRootView.findViewById(R.id.tv_user_from);
        tv_user_lable = mRootView.findViewById(R.id.tv_user_lable);
        tv_user_grade = mRootView.findViewById(R.id.tv_user_grade);
        ll_user_grade = mRootView.findViewById(R.id.ll_user_grade);
        ll_user_vip_grade = mRootView.findViewById(R.id.ll_user_vip_grade);
        tv_user_vip_grade = mRootView.findViewById(R.id.tv_user_vip_grade);
        v_user_vip_grade_split = mRootView.findViewById(R.id.v_user_vip_grade_split);
        tv_user_city = mRootView.findViewById(R.id.tv_user_city);
        ll_user_city = mRootView.findViewById(R.id.ll_user_city);
        tv_sobot_online_save_user = mRootView.findViewById(R.id.tv_sobot_online_save_user);

        et_user_remark = mRootView.findViewById(R.id.et_user_remark);
        et_user_realname = mRootView.findViewById(R.id.et_user_realname);
        tv_user_enterpriseName = mRootView.findViewById(R.id.tv_user_enterpriseName);
        et_user_phone = mRootView.findViewById(R.id.et_user_phone);
        iv_add_user_phone = mRootView.findViewById(R.id.iv_add_user_phone);
        ll_add_user_phone = mRootView.findViewById(R.id.ll_add_user_phone);
        iv_add_user_email = mRootView.findViewById(R.id.iv_add_user_email);
        et_user_email = mRootView.findViewById(R.id.et_user_email);
        ll_add_user_email = mRootView.findViewById(R.id.ll_add_user_email);
        et_user_qq = mRootView.findViewById(R.id.et_user_qq);
        ll_user_enterprise = mRootView.findViewById(R.id.ll_user_enterprise);
        work_order_user_cusfield = (LinearLayout) mRootView.findViewById(R.id.work_order_user_cusfield);
        work_order_user_cusfield.setVisibility(View.GONE);
        v_work_order_user_cusfield = mRootView.findViewById(R.id.v_work_order_user_cusfield);

        ll_user_vip_grade.setOnClickListener(this);
        ll_user_grade.setOnClickListener(this);
        ll_user_city.setOnClickListener(this);
        iv_add_user_email.setOnClickListener(this);
        iv_add_user_phone.setOnClickListener(this);
        tv_sobot_online_save_user.setOnClickListener(this);
        ll_user_enterprise.setOnClickListener(this);
    }

    //显示客户来源
    private String getSourceText(String sourceType) {
        if (TextUtils.isEmpty(sourceType)) {
            return "";
        }
        String sourceName;
        switch (sourceType) {
            case "0":
                sourceName = getString(R.string.online_desktop_website);
                break;
            case "1":
                sourceName = getString(R.string.online_wechat);
                break;
            case "2":
                sourceName = "APP";
                break;
            case "3":
                sourceName = getString(R.string.online_micro_blog);
                break;
            case "4":
                sourceName = getString(R.string.online_mobile_website);
                break;
            case "5":
                sourceName = getString(R.string.online_fusion_cloud);
                break;
            case "6":
                sourceName = getString(R.string.online_call_center);
                break;
            case "7":
                sourceName = getString(R.string.online_work_order);
                break;
            case "8":
                sourceName = getString(R.string.online_customer_center);
                break;
            case "9":
                sourceName = getString(R.string.customer_source_wecom);
                break;
            case "10":
                sourceName = getString(R.string.customer_source_wechat_applet);
                break;
            case "11":
                sourceName = getString(R.string.customer_source_mail);
                break;
            case "12":
                sourceName = getString(R.string.customer_source_baidu_market);
                break;
            case "13":
                sourceName = getString(R.string.customer_source_toutiao);
                break;
            case "14":
                sourceName = getString(R.string.customer_source_360);
                break;
            case "15":
                sourceName = getString(R.string.customer_source_wolong);
                break;
            case "16":
                sourceName = getString(R.string.customer_source_sougou);
                break;
            case "17":
                sourceName = getString(R.string.customer_source_wechat_service);
                break;
            case "18":
                sourceName = getString(R.string.customer_source_interface);
                break;
            case "22":
                sourceName = getString(R.string.customer_source_facebook);
                break;
            case "23":
                sourceName = getString(R.string.customer_source_whatsapp);
                break;
            case "24":
                sourceName = getString(R.string.customer_source_instagram);
                break;
            case "25":
                sourceName = "Line";
                break;
            case "26":
                sourceName = "Discord";
                break;
            case "33":
                sourceName = "Telegram";
                break;

            default:
                sourceName = getString(R.string.online_desktop_website);
                break;
        }
        return sourceName;
    }

    //编辑用户时，显示用户已有信息
    private void setWorkOrderUserInfo() {
        if (workOrderUser == null) {
            return;
        }

        String[] tmpUserEmail = null;
        if (!TextUtils.isEmpty(workOrderUser.getEmail())) {
            tmpUserEmail = workOrderUser.getEmail().split(";");
        }

        String[] tmpUserPhone = null;
        if (!TextUtils.isEmpty(workOrderUser.getTel())) {
            tmpUserPhone = workOrderUser.getTel().split(";");
        }
        tv_visitor_id.setText(!TextUtils.isEmpty(workOrderUser.getId()) ? workOrderUser.getId() : "");
        et_user_nickname.setText(!TextUtils.isEmpty(workOrderUser.getNick()) ? workOrderUser.getNick() : "");
        et_user_realname.setText(!TextUtils.isEmpty(workOrderUser.getUname()) ? workOrderUser.getUname() : "");
        tv_user_from.setText(getSourceText(workOrderUser.getSource()));
        tv_user_from.setTag(workOrderUser.getSource());

        if (workOrderUser.isVip()) {
            tv_user_grade.setText(getString(R.string.online_vip_customers));
        } else {
            tv_user_grade.setText(getString(R.string.online_common_customers));
        }

        tv_user_vip_grade.setText(workOrderUser.getVipLevelName());
        tv_user_vip_grade.setTag(workOrderUser.getVipLevel());

        if (tmpUserEmail != null && tmpUserEmail.length > 1) {
            et_user_email.setText(tmpUserEmail[0]);
            for (int i = 1; i < tmpUserEmail.length; i++) {
                addUserEmail(tmpUserEmail[i]);
            }
        } else {
            et_user_email.setText(!TextUtils.isEmpty(workOrderUser.getEmail()) ? workOrderUser.getEmail() : "");
        }

        if (tmpUserPhone != null && tmpUserPhone.length > 1) {
            et_user_phone.setText(tmpUserPhone[0]);
            for (int i = 1; i < tmpUserPhone.length; i++) {
                //  addUserPhone(tmpUserPhone[i]);
            }
        } else {
            et_user_phone.setText(!TextUtils.isEmpty(workOrderUser.getTel()) ? workOrderUser.getTel() : "");
        }

        if (!TextUtils.isEmpty(workOrderUser.getAreaName())) {
            tv_user_city.setText(workOrderUser.getProviceName() + "-" + workOrderUser.getCityName() + "-" + workOrderUser.getAreaName());
        } else if (TextUtils.isEmpty(workOrderUser.getCityName())) {
            tv_user_city.setText(workOrderUser.getProviceName());
        }

        proviceName = workOrderUser.getProviceName();
        cityName = workOrderUser.getCityName();
        areaName = workOrderUser.getAreaName();
        proviceId = workOrderUser.getProviceId();
        cityId = workOrderUser.getCityId();
        areaId = workOrderUser.getAreaId();
        tv_user_enterpriseName.setText(workOrderUser.getEnterpriseName());
        tv_user_enterpriseName.setTag(workOrderUser.getEnterpriseId());
        et_user_qq.setText(workOrderUser.getQq());
        if (!TextUtils.isEmpty(workOrderUser.getPartnerId())) {
            tv_user_partnerId.setVisibility(View.VISIBLE);
            tv_user_partnerId.setText(workOrderUser.getPartnerId());
        } else {
            tv_user_partnerId.setText("");
        }
        et_user_remark.setText(workOrderUser.getRemark());
        getAppCusFieldConfigInfoList();
    }


    //新增和编辑完客户，提交时，获取到客户信息
    private WorkOrderUser getNewWorkOrderUser() {
        WorkOrderUser user = new WorkOrderUser();
        if (workOrderUser != null && !TextUtils.isEmpty(workOrderUser.getId())) {
            user.setId(workOrderUser.getId());
        }

        if (workOrderUser != null && !TextUtils.isEmpty(workOrderUser.getPartnerId())) {
            user.setPartnerId(workOrderUser.getPartnerId());
        }

        if (workOrderUser != null && !TextUtils.isEmpty(workOrderUser.getVisitorIds())) {
            user.setVisitorIds(workOrderUser.getVisitorIds());
        }

        if (workOrderUser != null && !TextUtils.isEmpty(workOrderUser.getImg())) {
            user.setImg(workOrderUser.getImg());
        }
        user.setNick(!TextUtils.isEmpty(et_user_nickname.getText().toString()) ? et_user_nickname.getText().toString() : "");
        user.setUname(!TextUtils.isEmpty(et_user_realname.getText().toString()) ? et_user_realname.getText().toString() : "");
        if (tv_user_from.getTag() != null) {
            user.setSource(tv_user_from.getTag().toString());
        } else {
            user.setSource(2 + "");
        }

        if (getString(R.string.online_vip_customers).equals(tv_user_grade.getText().toString())) {
            user.setVip(true);
            if (tv_user_vip_grade.getTag() != null) {
                user.setVipLevel(tv_user_vip_grade.getTag().toString());
            }
            user.setVipLevelName(!TextUtils.isEmpty(tv_user_vip_grade.getText().toString()) ? tv_user_vip_grade.getText().toString() : "");
        } else {
            user.setVip(false);
        }

        StringBuilder email = new StringBuilder();
        if (listEmailView != null && listEmailView.size() > 0) {
            EditText view;
            for (int i = 0; i < listEmailView.size(); i++) {
                view = listEmailView.get(i);
                email.append(view.getText() + ";");
            }
        }

        if (!TextUtils.isEmpty(email)) {
            if (!TextUtils.isEmpty(et_user_email.getText().toString())) {
                user.setEmail(et_user_email.getText().toString() + ";" + email);
            } else {
                user.setEmail(email.toString());
            }
        } else {
            user.setEmail(!TextUtils.isEmpty(et_user_email.getText().toString()) ? et_user_email.getText().toString() : "");
        }

        StringBuilder phone = new StringBuilder();
        if (listPhoneView != null && listPhoneView.size() > 0) {
            EditText view;
            for (int i = 0; i < listPhoneView.size(); i++) {
                view = listPhoneView.get(i);
                phone.append(view.getText() + ";");
            }
        }

        if (!TextUtils.isEmpty(phone)) {
            if (!TextUtils.isEmpty(et_user_phone.getText().toString())) {
                user.setTel(et_user_phone.getText().toString() + ";" + phone);
            } else {
                user.setTel(phone.toString());
            }
        } else {
            user.setTel(!TextUtils.isEmpty(et_user_phone.getText().toString()) ? et_user_phone.getText().toString() : "");
        }

        user.setProviceId(!TextUtils.isEmpty(proviceId) ? proviceId : "");
        user.setProviceName(!TextUtils.isEmpty(proviceName) ? proviceName : "");
        if (!TextUtils.isEmpty(cityName) && !cityName.equals(getString(R.string.online_please_choose))) {
            user.setCityId(cityId);
            user.setCityName(cityName);
        }

        if (!TextUtils.isEmpty(areaName) && !areaName.equals(getString(R.string.online_please_choose))) {
            user.setAreaId(areaId);
            user.setAreaName(areaName);
        }


        if (tv_user_enterpriseName.getTag() != null) {
            user.setEnterpriseId(tv_user_enterpriseName.getTag().toString());
        }

        user.setEnterpriseName(!TextUtils.isEmpty(tv_user_enterpriseName.getText().toString()) ? tv_user_enterpriseName.getText().toString() : "");
        user.setQq(!TextUtils.isEmpty(et_user_qq.getText().toString()) ? et_user_qq.getText().toString() : "");
        user.setPartnerId(!TextUtils.isEmpty(tv_user_partnerId.getText().toString()) ? tv_user_partnerId.getText().toString() : "");
        user.setRemark(!TextUtils.isEmpty(et_user_remark.getText().toString()) ? et_user_remark.getText().toString() : "");
        user.setCustomFields(SobotGsonUtil.gsonString(getCustomFieldsValue()));
        return user;
    }

    //获取客户自定义字段value
    private List<TicketCusFieldModel> getCustomFieldsValue() {
        List<TicketCusFieldModel> cusFieldModelList = new ArrayList<>();
        TicketCusFieldModel cusFieldModel;
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                cusFieldModel = new TicketCusFieldModel();
                if (cusFieldList.get(i).getFieldType() == 6 || cusFieldList.get(i).getFieldType() == 7 || cusFieldList.get(i).getFieldType() == 8) {
                    if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldDataValue())) {
                        cusFieldModel.setId(cusFieldList.get(i).getFieldId());
                        cusFieldModel.setValue(cusFieldList.get(i).getFieldDataValue());
                    } else {
                        continue;
                    }
                } else {
                    if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                        cusFieldModel.setId(cusFieldList.get(i).getFieldId());
                        cusFieldModel.setValue(cusFieldList.get(i).getFieldValue());
                    } else {
                        continue;
                    }
                }
                cusFieldModelList.add(cusFieldModel);
            }
        }
        return cusFieldModelList;
    }


    @Override
    public void onClick(View v) {
        hideSoftInput();
        if (v == ll_user_grade) {
            isVipPopModelList = new ArrayList<>();
            //只有两种客户等级 普通客户 VIP客户
            isVipPopModelList.add(new OnlineCustomPopModel(getString(R.string.online_common_customers), 0));
            isVipPopModelList.add(new OnlineCustomPopModel(getString(R.string.online_vip_customers), 1));
            Intent userGradeIntent = new Intent(getSobotActivity(), OnlineCustomPopActivity.class);
            userGradeIntent.putExtra("title", getString(R.string.online_user_grade));
            userGradeIntent.putExtra("isShowSeach", false);
            userGradeIntent.putExtra("selectPosition", getUserListLevelIndex(tv_user_grade.getText().toString()));
            Bundle bundle = new Bundle();
            bundle.putSerializable("customPopModelList", isVipPopModelList);
            userGradeIntent.putExtras(bundle);
            startActivityForResult(userGradeIntent, OnlineConstant.SOBOT_REQUEST_CODE_IS_VIP);
        }
        if (v == ll_user_vip_grade) {
            zhiChiApi.getAppUserLevelDataInfo(getSobotActivity(), new SobotResultCallBack<List<CusFieldDataInfoModel>>() {
                @Override
                public void onSuccess(List<CusFieldDataInfoModel> cusFieldDataInfoLists) {
                    ArrayList<OnlineCustomPopModel> customPopModelList = new ArrayList<>();
                    if (cusFieldDataInfoLists != null) {
                        vipFieldDataInfoLists.clear();
                        vipFieldDataInfoLists.addAll(cusFieldDataInfoLists);
                        for (int i = 0; i < cusFieldDataInfoLists.size(); i++) {
                            CusFieldDataInfoModel cusFieldDataInfoModel = cusFieldDataInfoLists.get(i);
                            customPopModelList.add(new OnlineCustomPopModel(cusFieldDataInfoModel.getDataName(), i));
                        }
                    }
                    Intent vipIntent = new Intent(getSobotActivity(), OnlineCustomPopActivity.class);
                    vipIntent.putExtra("title", getString(R.string.online_vip_grade));
                    vipIntent.putExtra("isShowSeach", false);
                    vipIntent.putExtra("selectPosition", getUserVipListLevelIndex(tv_user_vip_grade.getText().toString()));
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("customPopModelList", customPopModelList);
                    vipIntent.putExtras(bundle);
                    startActivityForResult(vipIntent, OnlineConstant.SOBOT_REQUEST_CODE_VIP);
                }

                @Override
                public void onFailure(Exception e, String des) {

                }
            });
        }
        if (v == ll_user_city) {
            //选择客户所属城市
            onAddressPicker();
        }
        if (v == tv_sobot_online_save_user) {
            if (!addUserLegalEmail()) {
                SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_email) + getString(R.string.online_regex_error));
                return;
            }

//        if (!addUserLegalPhone()) {
//            str = "手机号码格式不正确!";
//            showCustomToast(str);
//            return;
//        }

            if (!TextUtils.isEmpty(et_user_qq.getText()) && et_user_qq.getText().length() < 5) {
                SobotToastUtil.showCustomToast(getSobotActivity(), "QQ" + getString(R.string.online_regex_error));
                return;
            }
            setCustomerFieldValue();
            if (workFlag) {
                editWorkOrderUser();
            } else {
                addWorkOrderUser();
            }
        }
        if (v == iv_add_user_email) {
            addUserEmail("");
        }
        if (v == iv_add_user_phone) {
            addUserPhone("");
        }
        if (v == ll_user_enterprise) {
            Intent enterPriseIntent = new Intent(getSobotActivity(), OnlineEnterPriseSearchActivity.class);
            enterPriseIntent.putExtra("selectEnterpriseId", TextUtils.isEmpty((String) tv_user_enterpriseName.getTag()) ? "" : (String) tv_user_enterpriseName.getTag());
            startActivityForResult(enterPriseIntent, OnlineConstant.SOBOT_REQUEST_CODE_USER_ENTERPRISE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_IS_VIP) {
                //选中的客户等级对应的索引
                int isVipSelectIndex = data.getIntExtra("selectIndex", -1);
                if (isVipSelectIndex > -1 && isVipPopModelList != null && isVipPopModelList.size() > 0) {
                    tv_user_grade.setText(isVipPopModelList.get(isVipSelectIndex).getsValue());
                    if (getString(R.string.online_vip_customers).equals(tv_user_grade.getText().toString())) {
                        ll_user_vip_grade.setVisibility(View.VISIBLE);
                        v_user_vip_grade_split.setVisibility(View.VISIBLE);
                        if (workOrderUser != null) {
                            tv_user_vip_grade.setText(workOrderUser.getVipLevelName());
                            tv_user_vip_grade.setTag(workOrderUser.getVipLevel());
                        }
                    } else {
                        ll_user_vip_grade.setVisibility(View.GONE);
                        v_user_vip_grade_split.setVisibility(View.GONE);
                    }
                }
            }
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_VIP) {
                //选中的客户VIP等级对应的索引
                int vipLevelSelectIndex = data.getIntExtra("selectIndex", -1);
                if (vipLevelSelectIndex > -1 && vipFieldDataInfoLists != null && vipFieldDataInfoLists.size() > 0) {
                    tv_user_vip_grade.setText(vipFieldDataInfoLists.get(vipLevelSelectIndex).getDataName());
                    tv_user_vip_grade.setTag(vipFieldDataInfoLists.get(vipLevelSelectIndex).getDataValue());
                }
            }
            if (requestCode == OnlineConstant.SOBOT_REQUEST_CODE_USER_ENTERPRISE) {
                //选中公司
                OnlineEnterPriseModel enterPriseModel = (OnlineEnterPriseModel) data.getSerializableExtra("enterPriseModel");
                if (enterPriseModel != null) {
                    tv_user_enterpriseName.setText(enterPriseModel.getEnterpriseName());
                    tv_user_enterpriseName.setTag(enterPriseModel.getEnterpriseId());
                }
            }
        }
        if (data != null) {
            if ("CATEGORYSMALL".equals(data.getStringExtra("CATEGORYSMALL")) && mCusFieldConfig != null) {
                if (-1 != data.getIntExtra("fieldType", -1) && SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == data.getIntExtra("fieldType", -1)) {
                    String value = data.getStringExtra("category_typeName");
                    String dataValue = data.getStringExtra("category_typeValue");
                    if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(dataValue)) {
                        for (int i = 0; i < cusFieldList.size(); i++) {
                            if (mCusFieldConfig.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                                cusFieldList.get(i).setFieldValue(value.substring(0, value.length() - 1));
                                cusFieldList.get(i).setFieldDataValue(dataValue.substring(0, dataValue.length() - 1));
                                View view = work_order_user_cusfield.findViewWithTag(cusFieldList.get(i).getFieldId());
                                TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                                textClick.setText(cusFieldList.get(i).getFieldValue());
                                break;
                            }
                        }
                    }
                } else {
                    if (mCusFieldConfig.getFieldId().equals(data.getStringExtra("category_fieldId"))) {
                        for (int i = 0; i < cusFieldList.size(); i++) {
                            if (mCusFieldConfig.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                                cusFieldList.get(i).setFieldValue(data.getStringExtra("category_typeName"));
                                cusFieldList.get(i).setFieldDataValue(data.getStringExtra("category_typeValue"));
                                View view = work_order_user_cusfield.findViewWithTag(cusFieldList.get(i).getFieldId());
                                TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                                textClick.setText(cusFieldList.get(i).getFieldValue());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    private int getUserListLevelIndex(String itemValue) {
        int itemIndex = 0;//默认普通用户
        if (isVipPopModelList != null) {
            for (int i = 0; i < isVipPopModelList.size(); i++) {
                if (itemValue.equals(isVipPopModelList.get(i).getsValue())) {
                    return i;
                }
            }
        }
        return itemIndex;
    }

    private int getUserVipListLevelIndex(String itemValue) {
        int itemIndex = -1;//默认无vip等级
        if (vipFieldDataInfoLists != null) {
            for (int i = 0; i < vipFieldDataInfoLists.size(); i++) {
                if (itemValue.equals(vipFieldDataInfoLists.get(i).getDataName())) {
                    return i;
                }
            }
        }
        return itemIndex;
    }


    private List<SobotProvinceModel> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<SobotProvinceModel.CityBean>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<SobotProvinceModel.AreaBean>>> options3Items = new ArrayList<>();

    public void onAddressPicker() {
        if (pvOptions == null) {
            pvOptions = new OptionsPickerBuilder(getSobotActivity(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    proviceName = options1Items.get(options1).getAreaName();
                    cityName = options2Items.get(options1).get(option2).getAreaName();
                    areaName = options3Items.get(options1).get(option2).get(options3).getAreaName();
                    proviceId = options1Items.get(options1).getAreaId();
                    cityId = options2Items.get(options1).get(option2).getAreaId();
                    areaId = options3Items.get(options1).get(option2).get(options3).getAreaId();
                    proviceIndex = options1;
                    cityIndex = option2;
                    areaIndex = options3;
                    tv_user_city.setText(proviceName + cityName + areaName);
                }
            }).setSubmitText(getString(R.string.online_ok))//确定按钮文字
                    .setCancelText(getString(R.string.online_cancle))//取消按钮文字
                    .setSelectOptions(proviceIndex, cityIndex, areaIndex)  //设置默认选中项
                    .build();
            pvOptions.setPicker(options1Items, options2Items, options3Items);
        }
        if (pvOptions != null && !pvOptions.isShowing()) {
            pvOptions.show();
        }
    }

    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         * */
        String JsonData = getJson(getSobotActivity(), "province.json");//获取assets目录下的json文件数据
        List<SobotProvinceModel> jsonBean = SobotGsonUtil.jsonToList(JsonData, SobotProvinceModel.class);//用Gson 转成实体
        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<SobotProvinceModel.CityBean> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<SobotProvinceModel.AreaBean>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCities().size(); c++) {//遍历该省份的所有城市
                SobotProvinceModel.CityBean cityBean = jsonBean.get(i).getCities().get(c);
                cityList.add(cityBean);//添加城市
                ArrayList<SobotProvinceModel.AreaBean> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCities().get(c).getCounties());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }
    }

    private void setCustomerFieldValue() {
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                if (cusFieldList.get(i).getOpenFlag() != 1) {
                    continue;
                }
                View view = work_order_user_cusfield.findViewWithTag(cusFieldList.get(i).getFieldId());
                if (cusFieldList.get(i).getFieldType() == 1) {
                    EditText singleLine = (EditText) view.findViewById(R.id.work_order_customer_field_text_single);
                    cusFieldList.get(i).setFieldValue(singleLine.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 2) {
                    EditText moreContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_more_content);
                    cusFieldList.get(i).setFieldValue(moreContent.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 3) {
                    TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 4) {
                    TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 5) {
                    EditText numberContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_number);
                    cusFieldList.get(i).setFieldValue(numberContent.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 6) {
                    TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 7) {
                    TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                } else if (cusFieldList.get(i).getFieldType() == 8) {
                    TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                }
            }
        }
    }

    //新增客户
    public void addWorkOrderUser() {
        String nick = et_user_nickname.getText().toString();
        final String tel = et_user_phone.getText().toString();
        final String email = et_user_email.getText().toString();

        if (!TextUtils.isEmpty(email) && !SobotRegexUtils.isEmail(email)) {
            SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_email) + getString(R.string.online_regex_error));
            return;
        }
        if (!checkCusFieldRequired()) {
            return;
        }
        final WorkOrderUser tmpUser = getNewWorkOrderUser();
        zhiChiApi.addAppUserInfo(getSobotActivity(), tmpUser, new SobotResultCallBack<CreateWorkOrderUserResult>() {
            @Override
            public void onSuccess(CreateWorkOrderUserResult createWorkOrderUserResult) {
                CreateWorkOrderUser user = createWorkOrderUserResult.getItem();
                if (createWorkOrderUserResult.totalCount == 0) {
                    if (user != null && !TextUtils.isEmpty(user.getId())) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_save_success));
//                        LiveDataBus.get().with(LiveDataBusKey.createWorkOrderUser).setValue(true);
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", user.getVisitorIds());
                        map.put("userNickName", user.getNick());
//                        LiveDataBus.get().with(LiveDataBusKey.update_talk_user_nick).setValue(map);
                    }
                    getSobotActivity().finish();
                } else {
                    int fieldType = -1;
                    JsonObject[] items = createWorkOrderUserResult.getItems();
                    if (items != null && items.length > 0) {
                        // fieldType (0 邮箱冲突 1 手机号冲突）
                        fieldType = items[0].get("fieldType").getAsInt();
                    }
                    if (fieldType == 0) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_email_repeat));
                    } else if (fieldType == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_phone_repeat));
                    } else {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_save_error));
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }

    //编辑客户
    public void editWorkOrderUser() {
        String nick = et_user_nickname.getText().toString();
        final String tel = et_user_phone.getText().toString();
        final String email = et_user_email.getText().toString();
        if (!TextUtils.isEmpty(email) && !SobotRegexUtils.isEmail(email)) {
            SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_email) + getString(R.string.online_regex_error));
            return;
        }
        if (!checkCusFieldRequired()) {
            return;
        }
        final WorkOrderUser tmpUser = getNewWorkOrderUser();
        zhiChiApi.updateAppUserInfo(getSobotActivity(), tmpUser, new SobotResultCallBack<EditUserInfoResult>() {
            @Override
            public void onSuccess(EditUserInfoResult editUserInfoResult) {
                WorkOrderUser user = editUserInfoResult.getItem();
                if (editUserInfoResult.getTotalCount() == 0) {
                    if (user != null && !TextUtils.isEmpty(user.getId())) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_save_success));
//                        if (cusFieldList != null && cusFieldList.size() > 0 && workOrderUser.getResultList() != null && workOrderUser.getResultList().size() > 0) {
//                            for (int i = 0; i < cusFieldList.size(); i++) {
//                                for (int j = 0; j < workOrderUser.getResultList().size(); j++) {
//                                    if (cusFieldList.get(i).getFieldId().equals(workOrderUser.getResultList().get(j).getFieldId())) {
//                                        if (cusFieldList.get(i).getFieldType() == 6 || cusFieldList.get(i).getFieldType() == 7 || cusFieldList.get(i).getFieldType() == 8) {
//                                            workOrderUser.getResultList().get(j).setValue(cusFieldList.get(i).getFieldDataValue());
//                                            workOrderUser.getResultList().get(j).setText(cusFieldList.get(i).getFieldValue());
//                                        } else {
//                                            workOrderUser.getResultList().get(j).setValue(cusFieldList.get(i).getFieldValue());
//                                        }
//                                    }
//                                }
//                            }
//                        }
                        user.setResultList(workOrderUser.getResultList());
                        user.setVipLevelName(tmpUser.getVipLevelName());
//                        LiveDataBus.get().with(LiveDataBusKey.updateWorkOrderUser).setValue(user);
//                        LiveDataBus.get().with(LiveDataBusKey.createWorkOrderUser).setValue(true);
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", user.getVisitorIds());
                        map.put("userNickName", user.getNick());
//                        LiveDataBus.get().with(LiveDataBusKey.update_talk_user_nick).setValue(map);
                    }
                    getSobotActivity().finish();
                } else {
                    int fieldType = -1;
                    JsonObject[] items = editUserInfoResult.getItems();
                    if (items != null && items.length > 0) {
                        // fieldType (0 邮箱冲突 1 手机号冲突）
                        fieldType = items[0].get("fieldType").getAsInt();
                    }
                    if (fieldType == 0) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_email_repeat));
                    } else if (fieldType == 1) {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_phone_repeat));
                    } else {
                        SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_save_error));
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }

    //检测自定义字段是否必填并提示
    private boolean checkCusFieldRequired() {
        if (cusFieldList != null && cusFieldList.size() > 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                CusFieldConfigModel fieldConfig = cusFieldList.get(i);
                if (1 == fieldConfig.getOpenFlag() && 1 == fieldConfig.getFillFlag()) {
                    if (!TextUtils.isEmpty(fieldConfig.getFieldValue())) {
                    } else {
                        SobotToastUtil.showCustomToast(getSobotActivity(), fieldConfig.getFieldName() + getString(R.string.online_no_can_empty));
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private void addUserEmail(String userEmail) {
        if (TextUtils.isEmpty(et_user_email.getText().toString())) {
            return;
        }
        if (listEmailView.size() >= 9) {
            return;
        }
        if (listEmailView != null && listEmailView.size() > 0) {
            if (TextUtils.isEmpty(listEmailView.get(listEmailView.size() - 1).getText().toString().trim())) {
                return;
            }
        }
        final LinearLayout emailLLView = (LinearLayout) View.inflate(getSobotActivity(), R.layout.item_user_add_info, null);
        final EditText emailEditText = emailLLView.findViewById(R.id.et_item_user_add_info);
        ImageView delIV = emailLLView.findViewById(R.id.iv_item_user_add_info_del);
        if (emailEditText == null) {
            return;
        }
        listEmailView.add(emailEditText);
        emailEditText.setHint(getString(R.string.online_please_input));
        emailEditText.requestFocus();
        if (!TextUtils.isEmpty(userEmail)) {
            emailEditText.setText(userEmail);
        }
        if (delIV != null) {
            delIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_add_user_email.removeView(emailLLView);
                    listEmailView.remove(emailEditText);
                }
            });
        }
        ll_add_user_email.addView(emailLLView);
        ll_add_user_email.setVisibility(View.VISIBLE);
    }

    private void addUserPhone(String userPhone) {
        if (TextUtils.isEmpty(et_user_phone.getText().toString())) {
            return;
        }
        if (listPhoneView.size() >= 9) {
            return;
        }
        if (listPhoneView != null && listPhoneView.size() > 0) {
            if (TextUtils.isEmpty(listPhoneView.get(listPhoneView.size() - 1).getText().toString().trim())) {
                return;
            }
        }
        final LinearLayout phoneLLView = (LinearLayout) View.inflate(getSobotActivity(), R.layout.item_user_add_info, null);
        final EditText phoneEditText = phoneLLView.findViewById(R.id.et_item_user_add_info);
        ImageView delIV = phoneLLView.findViewById(R.id.iv_item_user_add_info_del);
        if (phoneEditText == null) {
            return;
        }
        listPhoneView.add(phoneEditText);
        phoneEditText.setHint(getString(R.string.online_please_input));
        phoneEditText.requestFocus();
        if (!TextUtils.isEmpty(userPhone)) {
            phoneEditText.setText(userPhone);
        }
        if (delIV != null) {
            delIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_add_user_phone.removeView(phoneLLView);
                    listPhoneView.remove(phoneEditText);
                }
            });
        }
        ll_add_user_phone.addView(phoneLLView);
        ll_add_user_phone.setVisibility(View.VISIBLE);
    }


    //获取客户自定义字段
    private void getAppCusFieldConfigInfoList() {
        zhiChiApi.getAppCusFieldConfigInfoList(getSobotActivity(), "1", "1", new SobotResultCallBack<List<CusFieldConfigModel>>() {
            @Override
            public void onSuccess(List<CusFieldConfigModel> list) {
                if (list != null) {
                    cusFieldList.clear();
                    cusFieldList.addAll(list);
                    if (cusFieldList != null && cusFieldList.size() > 0) {
                        v_work_order_user_cusfield.setVisibility(View.VISIBLE);
                        if (workOrderUser != null && workOrderUser.getResultList() != null && workOrderUser.getResultList().size() > 0) {
                            List<TicketResultListModel> listData = workOrderUser.getResultList();

                            for (int i = 0; i < cusFieldList.size(); i++) {
                                for (int j = 0; j < listData.size(); j++) {
                                    if (cusFieldList.get(i).getFieldId().equals(listData.get(j).getFieldId())) {
                                        if (cusFieldList.get(i).getFieldType() == 6 || cusFieldList.get(i).getFieldType() == 7 || cusFieldList.get(i).getFieldType() == 8) {
                                            cusFieldList.get(i).setFieldValue(listData.get(j).getText());
                                            cusFieldList.get(i).setFieldDataValue(listData.get(j).getValue());
                                        } else {
                                            cusFieldList.get(i).setFieldValue(listData.get(j).getValue());
                                        }
                                    }
                                }
                            }
                        }
                        addWorkOrderUserCusFields(getSobotActivity(), cusFieldList, work_order_user_cusfield);
                    } else {
                        work_order_user_cusfield.setVisibility(View.GONE);
                        v_work_order_user_cusfield.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    public void addWorkOrderUserCusFields(final Context context, final List<CusFieldConfigModel> cusFieldList, LinearLayout containerLayout) {
        if (containerLayout != null) {
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.removeAllViews();
            if (cusFieldList != null && cusFieldList.size() != 0) {
                int size = cusFieldList.size();
                for (int i = 0; i < cusFieldList.size(); i++) {
                    final CusFieldConfigModel cusFieldConfig = cusFieldList.get(i);
                    if (cusFieldConfig.getOpenFlag() != 1) {
                        continue;
                    }
                    View view = View.inflate(context, R.layout.activity_create_work_order_cusfield_list_item, null);
                    view.setTag(cusFieldConfig.getFieldId());
                    View bottomLine = view.findViewById(R.id.work_order_customer_field_text_bootom_line);
                    if (cusFieldList.size() == 1 || i == (size - 1)) {
                        bottomLine.setVisibility(View.GONE);
                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
                    }
                    LinearLayout ll_more_text_layout = (LinearLayout) view.findViewById(R.id.work_order_customer_field_more_relativelayout);

                    LinearLayout ll_group_text_layout = (LinearLayout) view.findViewById(R.id.work_order_customer_field_group_relativelayout);
                    RelativeLayout ll_group_layout = (RelativeLayout) view.findViewById(R.id.work_order_customer_field_group__rl_add);
                    ll_group_text_layout.setVisibility(View.GONE);
                    ll_group_layout.setVisibility(View.GONE);

                    TextView fieldMoreName = (TextView) view.findViewById(R.id.work_order_customer_field_more_text_lable);
                    EditText moreContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_more_content);

                    RelativeLayout ll_text_layout = (RelativeLayout) view.findViewById(R.id.work_order_customer_field_text);
                    TextView fieldName = (TextView) view.findViewById(R.id.work_order_customer_field_text_lable);
                    final TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    EditText fieldValue = (EditText) view.findViewById(R.id.work_order_customer_field_text_content);
                    EditText numberContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_number);

                    EditText singleContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_single);
                    ImageView fieldImg = (ImageView) view.findViewById(R.id.work_order_customer_field_text_img);

                    if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.VISIBLE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        singleContent.setHint(cusFieldList.get(i).getFieldRemark());
                        singleContent.setHint(getString(R.string.online_please_input));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }
                        singleContent.setSingleLine(true);
                        singleContent.setMaxEms(11);
                        singleContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            singleContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            singleContent.setEnabled(false);
                        }

                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.GONE);
                        fieldMoreName.setText(cusFieldList.get(i).getFieldName());
                        moreContent.setHint(cusFieldList.get(i).getFieldRemark());
                        moreContent.setHint(getString(R.string.online_please_input));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldMoreName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }
                        moreContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        //设置EditText的显示方式为多行文本输入
                        moreContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        //文本显示的位置在EditText的最上方
                        moreContent.setGravity(Gravity.TOP);
                        //改变默认的单行模式
                        moreContent.setSingleLine(false);
                        //水平滚动设置为False
                        moreContent.setHorizontallyScrolling(false);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            moreContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            moreContent.setEnabled(false);
                        }
                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(getString(R.string.online_please_choose));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TimePickerView pvTime = new TimePickerBuilder(getSobotActivity(), new OnTimeSelectListener() {
                                        @Override
                                        public void onTimeSelect(Date date, View v) {
                                            textClick.setText(SobotTimeUtils.date2String(date, fieldType == SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? "yyyy-MM-dd" : "HH:mm"));

                                        }
                                    }).setType(new boolean[]{true, true, true, false, false, false}).setSubmitColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).setCancelColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).build();
                                    pvTime.show();
                                    SobotKeyboardUtils.hideSoftInput(getSobotActivity());
                                }
                            });
                        }
                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(getString(R.string.online_please_choose));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TimePickerView pvTime = new TimePickerBuilder(getSobotActivity(), new OnTimeSelectListener() {
                                        @Override
                                        public void onTimeSelect(Date date, View v) {
                                            textClick.setText(SobotTimeUtils.date2String(date, fieldType == SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? "yyyy-MM-dd" : "HH:mm"));

                                        }
                                    }).setType(new boolean[]{false, false, false, true, true, false}).setSubmitColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).setCancelColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).build();
                                    pvTime.show();
                                    SobotKeyboardUtils.hideSoftInput(getSobotActivity());
                                }
                            });
                        }

                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE == cusFieldList.get(i).getFieldType()) {
                        try {
                            if (!TextUtils.isEmpty(cusFieldList.get(i).getLimitOptions())) {
                                JSONArray array = new JSONArray(cusFieldList.get(i).getLimitOptions());
                                if (array.length() > 0) {
                                    if (array.optInt(0) == 2) {
                                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                                    } else if (array.optInt(0) == 3) {
                                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                        DecimalDigitsInputFilter filter = new DecimalDigitsInputFilter();
                                        filter.setDigits(2);
                                        numberContent.setFilters(new InputFilter[]{filter});
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.VISIBLE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        numberContent.setSingleLine(true);
                        numberContent.setHint(cusFieldList.get(i).getFieldRemark());
                        numberContent.setHint(getString(R.string.online_please_input));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            numberContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            numberContent.setEnabled(false);
                        }

                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(getString(R.string.online_please_choose));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startCategorySmallActivity(cusFieldConfig);
                                }
                            });
                        }

                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(getString(R.string.online_please_choose));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " ※" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    LiveDataBus.get().with(LiveDataBusKey.gone_delete_textview).setValue(true);
                                    startCategorySmallActivity(cusFieldConfig);
                                }
                            });
                        }

                    } else if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(getString(R.string.online_please_choose));
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + "※" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startCategorySmallActivity(cusFieldConfig);
                                }
                            });
                        }
                    }
                    containerLayout.addView(view);
                }
            }
        }
    }


    public void startCategorySmallActivity(CusFieldConfigModel cusFieldConfig) {
        mCusFieldConfig = cusFieldConfig;
        Intent intent = new Intent(getSobotActivity(), SobotCategorySmallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", mCusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, mCusFieldConfig.getFieldType());
    }

    //是否是合法邮箱
    private boolean addUserLegalEmail() {
        if (listEmailView != null && listEmailView.size() > 0) {
            EditText view;
            for (int i = 0; i < listEmailView.size(); i++) {
                view = listEmailView.get(i);
                String email = view.getText().toString();
                if (!TextUtils.isEmpty(email)) {
                    if (SobotRegexUtils.isEmail(email)) {
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
