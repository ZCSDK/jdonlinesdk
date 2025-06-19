package com.sobot.online.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @Description: 自定义Viewpager, 禁止左右滑动
 * @Author: znw
 * @Date: 2020-08-21 18:06
 * @Version: 1.0
 */
public class SobotNoScrollViewPager extends ViewPager {
    //是否禁止左右滑动,默认可以滑动
    private boolean noScroll = false;

    public SobotNoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SobotNoScrollViewPager(Context context) {
        super(context);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}