package com.sobot.online.weight.recyclerview.refresh;

import android.view.View;

/**
 * <p>描述：自定义加载更多的接口</p>
 */
public interface IMoreFooter {
    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    public void setLoadingHint(String hint);

    public void setNoMoreHint(String hint);

    public void setLoadingDoneHint(String hint);

    public void setProgressStyle(int style);

    public boolean isLoadingMore();

    public void setState(int state);

    /**
     * 返回当前自定义更多对象 this
     *
     * @return
     */
    View getFooterView();
}
