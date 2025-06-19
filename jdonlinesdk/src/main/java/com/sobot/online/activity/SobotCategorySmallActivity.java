package com.sobot.online.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sobot.online.R;
import com.sobot.online.adapter.CategorySmallAdapter;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.onlinecommon.model.CusFieldConfigModel;
import com.sobot.onlinecommon.model.CusFieldDataInfoList;
import com.sobot.onlinecommon.socket.SobotSocketConstant;
import com.sobot.onlinecommon.utils.SobotKeyboardUtils;

import java.util.ArrayList;
import java.util.List;


public class SobotCategorySmallActivity extends SobotOnlineDialogBaseActivity implements View.OnClickListener {

    private int fieldType;
    private CusFieldConfigModel cusFieldConfig;
    private List<CusFieldDataInfoList> infoLists = new ArrayList<>();
    private List<CusFieldDataInfoList> tempInfoLists = new ArrayList<>();
    private CategorySmallAdapter adapter;
    private StringBuffer dataName = new StringBuffer();
    private StringBuffer fieldId = new StringBuffer();
    private StringBuffer dataValue = new StringBuffer();

    ListView mListView;
    RelativeLayout custom_field_top;
    public EditText custom_field_etSearch;
    public RelativeLayout custom_field_nothing;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_category_small;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.activity_work_order_category_small_listview);
        custom_field_top = findViewById(R.id.custom_field_top);
        custom_field_etSearch = findViewById(R.id.custom_field_etSearch);
        custom_field_nothing = findViewById(R.id.custom_field_nothing);
        getHearderLeftView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobotKeyboardUtils.hideSoftInput(getSobotActivity());
                finish();
            }
        });
        getHearderRightView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataName.length() != 0 && fieldId.length() != 0 && dataValue.length() != 0) {
                    Intent intent = new Intent();
                    intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
                    intent.putExtra("fieldType", fieldType);
                    intent.putExtra("category_typeName", dataName + "");
                    intent.putExtra("category_typeValue", dataValue + "");
                    intent.putExtra("category_fieldId", fieldId + "");
                    setResult(SobotSocketConstant.work_order_list_display_type_category, intent);
                }
                finish();
            }
        });


        custom_field_nothing.setVisibility(View.GONE);
        if (cusFieldConfig != null && 1 == cusFieldConfig.getQueryFlag() && cusFieldConfig.getFieldType() == SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE) {
            custom_field_top.setVisibility(View.VISIBLE);
        } else {
            custom_field_top.setVisibility(View.GONE);
        }

        if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE != fieldType) {
            custom_field_etSearch.setOnClickListener(this);
        }

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (infoLists != null && infoLists.size() != 0) {

                    if (fieldType == SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE) {
                        dataName.delete(0, dataName.length());
                        dataValue.delete(0, dataValue.length());
                        fieldId.delete(0, fieldId.length());
                        if (infoLists.get(position).isChecked()) {
                            infoLists.get(position).setChecked(false);
                        } else {
                            infoLists.get(position).setChecked(true);
                        }

                        for (int i = 0; i < infoLists.size(); i++) {
                            if (infoLists.get(i).isChecked()) {
                                dataName.append(infoLists.get(i).getDataName()).append(",");
                                fieldId.append(infoLists.get(i).getFieldId()).append(",");
                                dataValue.append(infoLists.get(i).getDataValue()).append(",");
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
                        intent.putExtra("fieldType", fieldType);

                        infoLists.get(position).setChecked(true);
                        for (int i = 0; i < infoLists.size(); i++) {
                            if (i != position) {
                                infoLists.get(i).setChecked(false);
                            }
                        }
                        intent.putExtra("category_typeName", infoLists.get(position).getDataName());
                        intent.putExtra("category_fieldId", infoLists.get(position).getFieldId());
                        intent.putExtra("category_typeValue", infoLists.get(position).getDataValue());
                        setResult(SobotSocketConstant.work_order_list_display_type_category, intent);
                        adapter.notifyDataSetChanged();
                        SobotKeyboardUtils.hideSoftInput(getSobotActivity());
                        finish();
                    }
                }
            }
        });

        searchListener();
    }
    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        fieldType = bundle.getInt("fieldType");
        if (bundle.getSerializable("cusFieldConfig") != null) {
            cusFieldConfig = (CusFieldConfigModel) bundle.getSerializable("cusFieldConfig");
        }

        if (cusFieldConfig != null && !TextUtils.isEmpty(cusFieldConfig.getFieldName())) {
            setHearderTitle(cusFieldConfig.getFieldName());
        }

        if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            getHearderRightView().setText(getResStringId("online_save"));
            getHearderRightView().setVisibility(View.VISIBLE);
        }
        if (cusFieldConfig != null) {
            infoLists = cusFieldConfig.getCusFieldDataInfoList();
            if (infoLists != null && infoLists.size() != 0) {
                for (int i = 0; i < infoLists.size(); i++) {
                    if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
                        String tmpData[];
                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue())) {
                            cusFieldConfig.setFieldValue(cusFieldConfig.getFieldValue() + ",");
                            tmpData = convertStrToArray(cusFieldConfig.getFieldValue());
                            if (tmpData != null && tmpData.length != 0) {
                                if (tmpData.length == 1) {
                                    if (tmpData[0].equals(infoLists.get(i).getDataName())) {
                                        infoLists.get(i).setChecked(true);
                                    }
                                } else {
                                    for (String aTmpData : tmpData) {
                                        if (aTmpData.equals(infoLists.get(i).getDataName())) {
                                            infoLists.get(i).setChecked(true);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue()) && cusFieldConfig.getFieldValue().equals(infoLists.get(i).getDataName())) {
                            infoLists.get(i).setChecked(true);
                        }
                    }
                }

                if (adapter == null) {
                    adapter = new CategorySmallAdapter(getSobotActivity(), infoLists, fieldType, this);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    // 使用String的split 方法把字符串截取为字符串数组
    private String[] convertStrToArray(String str) {
        String[] strArray;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    private void searchListener() {

//        RxTextView.textChanges(custom_field_etSearch)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .filter(new Predicate<CharSequence>() {
//                    @Override
//                    public boolean test(CharSequence charSequence) throws Exception {
//                        return charSequence.toString().trim().length() > 0;
//                    }
//                })
//                .observeOn(Schedulers.io())
//                .switchMap(new Function<CharSequence, ObservableSource<List<CusFieldDataInfoList>>>() {
//                    @Override
//                    public ObservableSource<List<CusFieldDataInfoList>> apply(CharSequence searchStr) throws Exception {
//                        List<CusFieldDataInfoList> tmpList = new ArrayList<>();
//                        tmpList.clear();
//                        if (tempInfoLists != null && tempInfoLists.size() > 0) {
//                            infoLists.clear();
//                            infoLists.addAll(tempInfoLists);
//                        }
//
//                        for (int i = 0; i < infoLists.size(); i++) {
//                            if (infoLists.get(i).getDataName().contains(searchStr)) {
//                                tmpList.add(infoLists.get(i));
//                            }
//                        }
//                        return Observable.just(tmpList);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<CusFieldDataInfoList>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(List<CusFieldDataInfoList> cusFieldDataInfoLists) {
//                        if (cusFieldDataInfoLists != null && cusFieldDataInfoLists.size() > 0) {
//                            tempInfoLists.clear();
//                            tempInfoLists.addAll(infoLists);
//                            infoLists.clear();
//                            infoLists.addAll(cusFieldDataInfoLists);
//                            adapter.setData(cusFieldDataInfoLists);
//                            adapter.notifyDataSetChanged();
//                            custom_field_nothing.setVisibility(View.GONE);
//                            mListView.setVisibility(View.VISIBLE);
//                        } else {
//                            if (!TextUtils.isEmpty(custom_field_etSearch.getText().toString())) {
//                                custom_field_nothing.setVisibility(View.VISIBLE);
//                                mListView.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });

        custom_field_etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (TextUtils.isEmpty(custom_field_etSearch.getText())) {
                        custom_field_nothing.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                        if (tempInfoLists != null && tempInfoLists.size() > 0) {
                            infoLists.clear();
                            infoLists.addAll(tempInfoLists);
                            adapter.setData(infoLists);
                        } else {
                            adapter.setData(infoLists);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==custom_field_etSearch){
            getHearderView().setVisibility(View.GONE);
        }
    }
}