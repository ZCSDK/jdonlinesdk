package com.sobot.online.weight.kpswitch.widget.interfaces;

import android.view.ViewGroup;

import com.sobot.online.weight.kpswitch.widget.adpater.PlusAdapter;

/**
 * @author Created by jinxl on 2018/7/31.
 */
public interface PlusDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, PlusAdapter.ViewHolder viewHolder, T t);
}
