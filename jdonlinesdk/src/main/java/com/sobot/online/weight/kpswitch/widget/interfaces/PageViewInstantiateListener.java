package com.sobot.online.weight.kpswitch.widget.interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.sobot.online.weight.kpswitch.widget.data.PageEntity;


public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}