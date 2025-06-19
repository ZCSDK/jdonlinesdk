package com.sobot.online.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.sobot.online.R;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.adapter.HelperRecyclerViewHolder;
import com.sobot.onlinecommon.model.UnitInfoModel;

//业务和业务类型模糊查询 adapter
public class OnlineSummaryUnitAndTypeAdapter extends HelperRecyclerViewAdapter<UnitInfoModel> {
    private Context mContext;

    public OnlineSummaryUnitAndTypeAdapter(Context context) {
        super(context, R.layout.adapter_string_item);
        mContext = context;
    }

    @Override
    protected void HelperBindData(HelperRecyclerViewHolder viewHolder, int position, UnitInfoModel item) {
        if (item.getTypes() != null && item.getTypes().size() > 0) {
            StringBuffer nameStr = new StringBuffer();
            for (int i = 0; i < item.getTypes().size(); i++) {
                nameStr.append("/").append(item.getTypes().get(i).getTypeName());
            }
            viewHolder.setText(R.id.tv_common_pop_item_text, item.getUnitName() + nameStr.toString());
        } else {
            viewHolder.setText(R.id.tv_common_pop_item_text, item.getUnitName());
        }
        ImageView ivStatus = viewHolder.getView(R.id.iv_common_pop_item_img);
        ivStatus.setVisibility(View.GONE);

    }

}
