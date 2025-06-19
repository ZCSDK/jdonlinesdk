package com.sobot.online.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.activity.SobotCategorySmallActivity;
import com.sobot.onlinecommon.model.CusFieldDataInfoList;
import com.sobot.onlinecommon.socket.SobotSocketConstant;

import java.util.List;

public class CategorySmallAdapter extends MyBaseAdapter<CusFieldDataInfoList> {

    private Context mContext;
    private int fieldType;
    private SobotCategorySmallActivity activity;

    public CategorySmallAdapter(Context context, List<CusFieldDataInfoList> list, int fieldType, SobotCategorySmallActivity act) {
        super(context, list);
        this.mContext = context;
        this.fieldType = fieldType;
        this.activity = act;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.activity_work_order_category_small_items, null);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        myViewHolder.categorySmallTitle.setText(Html.fromHtml(mList.get(position).getDataName().replaceFirst(
                activity.custom_field_etSearch.getText().toString(), getColorStr(activity.custom_field_etSearch.getText().toString()))));

        if (SobotSocketConstant.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            myViewHolder.categorySmallCheckBox.setVisibility(View.VISIBLE);
            if (mList.get(position).isChecked()) {
                myViewHolder.categorySmallCheckBox.setBackgroundResource(R.drawable.sobot_checkbot_button_selected);
            } else {
                myViewHolder.categorySmallCheckBox.setBackgroundResource(R.drawable.sobot_pull_black_button_no_selected);
            }
        } else {
            myViewHolder.categorySmallCheckBox.setVisibility(View.GONE);
            if (mList.get(position).isChecked()) {
                myViewHolder.categorySmallIshave.setVisibility(View.VISIBLE);
                myViewHolder.categorySmallIshave.setBackgroundResource(R.drawable.sobot_online_correct_icon);
            } else {
                myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class MyViewHolder {

        private TextView categorySmallTitle;
        private ImageView categorySmallIshave;
        private ImageView categorySmallCheckBox;

        MyViewHolder(View view) {
            categorySmallTitle = (TextView) view.findViewById(R.id.work_order_category_small_title);
            categorySmallIshave = (ImageView) view.findViewById(R.id.work_order_category_small_ishave);
            categorySmallCheckBox = (ImageView) view.findViewById(R.id.work_order_category_small_checkbox);
        }
    }

    private String getColorStr(String str) {
        return "<font color='#09aeb0' >" + str + "</font>";
    }
}