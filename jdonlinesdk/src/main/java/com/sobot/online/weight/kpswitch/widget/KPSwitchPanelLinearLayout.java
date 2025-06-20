/*
 * Copyright (C) 2015-2016 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sobot.online.weight.kpswitch.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sobot.online.weight.kpswitch.IPanelConflictLayout;
import com.sobot.online.weight.kpswitch.IPanelHeightTarget;
import com.sobot.online.weight.kpswitch.handler.KPSwitchPanelLayoutHandler;


/**
 * <p/>
 * The panel container linear layout.
 * Resolve the layout-conflict from switching the keyboard and the Panel.
 * <p/>
 * For full-screen theme window, please use {@link KPSwitchFSPanelLinearLayout} instead.
 *
 * @see KPSwitchPanelFrameLayout
 * @see KPSwitchPanelRelativeLayout
 * @see KPSwitchPanelLayoutHandler
 */
public class KPSwitchPanelLinearLayout extends LinearLayout implements IPanelHeightTarget,
        IPanelConflictLayout {


    private KPSwitchPanelLayoutHandler panelLayoutHandler;

    public KPSwitchPanelLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public KPSwitchPanelLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public KPSwitchPanelLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(final AttributeSet attrs) {
        panelLayoutHandler = new KPSwitchPanelLayoutHandler(this, attrs);
    }

    @Override
    public void refreshHeight(int panelHeight) {
        panelLayoutHandler.resetToRecommendPanelHeight(panelHeight);
    }

    @Override
    public void onKeyboardShowing(boolean showing) {
        panelLayoutHandler.setIsKeyboardShowing(showing);
    }

    @Override
    public boolean isKeyboardShowing() {
        return panelLayoutHandler.isKeyboardShowing();
    }

    @Override
    public void setVisibility(int visibility) {
        if (panelLayoutHandler.filterSetVisibility(visibility)) {
            return;
        }
        super.setVisibility(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int[] processedMeasureWHSpec = panelLayoutHandler.processOnMeasure(widthMeasureSpec,
                heightMeasureSpec);

        super.onMeasure(processedMeasureWHSpec[0], processedMeasureWHSpec[1]);
    }

    @Override
    public boolean isVisible() {
        return panelLayoutHandler.isVisible();
    }


    @Override
    public void handleShow() {
        super.setVisibility(View.VISIBLE);
    }


    @Override
    public void handleHide() {
        panelLayoutHandler.handleHide();
    }

    @Override
    public void setIgnoreRecommendHeight(boolean isIgnoreRecommendHeight) {
        panelLayoutHandler.setIgnoreRecommendHeight(isIgnoreRecommendHeight);
    }

}
