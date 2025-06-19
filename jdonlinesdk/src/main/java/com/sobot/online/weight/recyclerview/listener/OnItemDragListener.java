package com.sobot.online.weight.recyclerview.listener;

import androidx.recyclerview.widget.RecyclerView;

/**
 * <p>描述:设置拖拽监听<br/></p>
 */
public interface OnItemDragListener {
    void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos);

    void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to);

    void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos);
}
