package com.sobot.online.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.model.UnitTypeInfoModel;

import java.util.ArrayList;
import java.util.List;

//服务总结 业务分类 adapter
public class SobotPopUnitTypeAdapter extends HelperRecyclerViewAdapter<UnitTypeInfoModel> {
    private Context mContext;
    private OnCheckTypeChangeListener mCheckTypeChangeListener;
    private ArrayList<UnitTypeInfoModel> userSelectUnitTypeList;//选中的业务类型集合，最多只能3个

    public SobotPopUnitTypeAdapter(Context context, ArrayList<UnitTypeInfoModel> selectUnitTypeList, OnCheckTypeChangeListener onCheckTypeChangeListener) {
        super(context, R.layout.adapter_unit_type_item);
        mContext = context;
        mCheckTypeChangeListener = onCheckTypeChangeListener;
        if (selectUnitTypeList == null) {
            userSelectUnitTypeList = new ArrayList<>();
        } else {
            userSelectUnitTypeList = selectUnitTypeList;
        }
    }

    //选中的业务类型集合
    public ArrayList<UnitTypeInfoModel> getUserSelectUnitTypeList() {
        return userSelectUnitTypeList;
    }

    public void setUserSelectUnitTypeList(ArrayList<UnitTypeInfoModel> userSelectUnitTypeList) {
        this.userSelectUnitTypeList = userSelectUnitTypeList;
    }

    public void removeParentTypeForSelectTypeList(UnitTypeInfoModel selectType) {
        List<UnitTypeInfoModel> tempUserSelectUnitTypeList = new ArrayList<>();
        for (int i = 0; i < userSelectUnitTypeList.size(); i++) {
            UnitTypeInfoModel temp = userSelectUnitTypeList.get(i);
            if (selectType.getTypeStr().contains(temp.getTypeId())) {
            } else {
                tempUserSelectUnitTypeList.add(temp);
            }
        }
        userSelectUnitTypeList.clear();
        userSelectUnitTypeList.addAll(tempUserSelectUnitTypeList);
    }

    public boolean isHasSelectType(String typeId) {
        for (int i = 0; i < userSelectUnitTypeList.size(); i++) {
            UnitTypeInfoModel temp = userSelectUnitTypeList.get(i);
            if (temp.getTypeId().equals(typeId)) {
                return true;
            }
        }
        return false;
    }

    public void removeSelectType(String typeId) {
        List<UnitTypeInfoModel> tempUserSelectUnitTypeList = new ArrayList<>();
        for (int i = 0; i < userSelectUnitTypeList.size(); i++) {
            UnitTypeInfoModel temp = userSelectUnitTypeList.get(i);
            if (!temp.getTypeId().equals(typeId)) {
                tempUserSelectUnitTypeList.add(temp);
            }
        }
        userSelectUnitTypeList.clear();
        userSelectUnitTypeList.addAll(tempUserSelectUnitTypeList);
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, final UnitTypeInfoModel item) {
        TextView view = viewHolder.getView(R.id.tv_common_pop_item_text);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckTypeChangeListener != null&&!TextUtils.isEmpty(item.getNodeFlag()) && "1".equals(item.getNodeFlag())) {
                    mCheckTypeChangeListener.onCheckTypeChange(item);
                }
            }
        });
        viewHolder.setText(R.id.tv_common_pop_item_text, item.getTypeName());
        final ImageView iv_common_pop_item_select = viewHolder.getView(R.id.iv_common_pop_item_select);
        if (isHasSelectType(item.getTypeId())) {
            iv_common_pop_item_select.setImageResource(R.drawable.sobot_checkbot_button_selected);
        } else {
            iv_common_pop_item_select.setImageResource(R.drawable.sobot_pull_black_button_no_selected);
        }
        ImageView iv_common_pop_item_has_child = viewHolder.getView(R.id.iv_common_pop_item_has_child);
        if (!TextUtils.isEmpty(item.getNodeFlag()) && "1".equals(item.getNodeFlag())) {
            iv_common_pop_item_has_child.setVisibility(View.VISIBLE);
        } else {
            iv_common_pop_item_has_child.setVisibility(View.GONE);
        }
        iv_common_pop_item_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHasSelectType(item.getTypeId())) {
                    iv_common_pop_item_select.setImageResource(R.drawable.sobot_pull_black_button_no_selected);
                    removeSelectType(item.getTypeId());
                } else {
                    if (userSelectUnitTypeList.size() > 2) {
                        SobotToastUtil.showCustomToast(mContext, mContext.getResources().getString(R.string.online_unit_type_max_three));
                        return;
                    }
                    removeParentTypeForSelectTypeList(item);
                    iv_common_pop_item_select.setImageResource(R.drawable.sobot_checkbot_button_selected);
                    userSelectUnitTypeList.add(item);
                }
                if (mCheckTypeChangeListener != null) {
                    mCheckTypeChangeListener.onUserCheckTypeChange(item);
                }
            }
        });
    }

    public interface OnCheckTypeChangeListener {
        //用户点击类型名字时，业务类型导航发生变化
        void onCheckTypeChange(UnitTypeInfoModel unitTypeInfoModel);

        //用户选中的某个业务类型发生变化
        void onUserCheckTypeChange(UnitTypeInfoModel unitTypeInfoModel);
    }
}
