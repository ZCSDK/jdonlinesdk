package com.sobot.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.onlinecommon.control.CusRobotConfigModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//智能回复 机器人列表 adapter
public class SobotPopRebotAdapter extends HelperRecyclerViewAdapter<CusRobotConfigModel> {
    private Context mContext;
    private List<String> selectRebotIdList;
    private List<String> selectRebotNameList;

    public SobotPopRebotAdapter(Context context, String selectRebotFlags, String selectRebotNames) {
        super(context, R.layout.adapter_rebot_item);
        mContext = context;
        selectRebotIdList = new ArrayList<>();
        selectRebotNameList = new ArrayList<>();
        if (!TextUtils.isEmpty(selectRebotFlags)) {
            selectRebotIdList.addAll(Arrays.asList(selectRebotFlags.split(",")));
            selectRebotNameList.addAll(Arrays.asList(selectRebotNames.split(",")));
        }
    }

    public void addAllRebotFlag() {
        List<CusRobotConfigModel> cusRobotConfigModels = getList();
        if (cusRobotConfigModels != null && cusRobotConfigModels.size() > 0) {
            selectRebotIdList.clear();
            for (int i = 0; i < cusRobotConfigModels.size(); i++) {
                selectRebotIdList.add(cusRobotConfigModels.get(i).getRobotFlag() + "");
                selectRebotNameList.add(cusRobotConfigModels.get(i).getRobotName() + "");
            }
        }
    }

    public String getSelectRebotFlags() {
        StringBuffer selectRebotFlags = new StringBuffer();
        if (selectRebotIdList != null && selectRebotIdList.size() > 0) {
            for (int i = 0; i < selectRebotIdList.size(); i++) {
                if (i == (selectRebotIdList.size() - 1)) {
                    selectRebotFlags.append(selectRebotIdList.get(i));
                } else {
                    selectRebotFlags.append(selectRebotIdList.get(i)).append(",");
                }

            }
        }
        return selectRebotFlags.toString();
    }

    public String getSelectRebotNames() {
        StringBuffer selectRebotNames = new StringBuffer();
        if (selectRebotNameList != null && selectRebotNameList.size() > 0) {
            for (int i = 0; i < selectRebotNameList.size(); i++) {
                if (i == (selectRebotNameList.size() - 1)) {
                    selectRebotNames.append(selectRebotNameList.get(i));
                } else {
                    selectRebotNames.append(selectRebotNameList.get(i)).append(",");
                }

            }
        }
        return selectRebotNames.toString();
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, final CusRobotConfigModel item) {
        viewHolder.setText(R.id.tv_common_pop_item_text, item.getRobotName());
        CheckBox checkBox = viewHolder.getView(R.id.cb_common_pop_item_select);
        checkBox.setOnCheckedChangeListener(null);
        if (selectRebotIdList.contains(item.getRobotFlag() + "")) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mContext.getResources().getString(R.string.online_all_rebot).equals(item.getRobotName())) {
                    if (isChecked) {
                        selectRebotIdList.clear();
                        selectRebotNameList.clear();
                        addAllRebotFlag();
                        notifyDataSetChanged();
                    } else {
                        selectRebotIdList.clear();
                        selectRebotNameList.clear();
                        notifyDataSetChanged();
                    }
                } else {
                    if (isChecked) {
                        if (!selectRebotIdList.contains(item.getRobotFlag().toString())) {
                            selectRebotIdList.add(item.getRobotFlag().toString());
                            selectRebotNameList.add(item.getRobotName());
                        }
                    } else {
                        selectRebotIdList.remove(item.getRobotFlag().toString());
                        selectRebotNameList.remove(item.getRobotName());
                    }
                }
            }
        });
    }

}
