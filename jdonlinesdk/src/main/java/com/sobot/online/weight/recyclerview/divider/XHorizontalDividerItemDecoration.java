package com.sobot.online.weight.recyclerview.divider;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.sobot.online.weight.recyclerview.SobotRecyclerView;

/**
 * <p>描述：垂直滑动水平分割线设置  - LinearLayoutManager</p>
 * XHorizontalDividerItemDecoration 主要是针对使用XRecyclerView做了处理，添加的头和尾会自动没有分割线
 */
public class XHorizontalDividerItemDecoration extends HorizontalDividerItemDecoration {

    public XHorizontalDividerItemDecoration(HorizontalDividerItemDecoration.Builder builder) {
        super(builder);
    }

    public static class Builder extends HorizontalDividerItemDecoration.Builder {
        VisibilityProvider mVisibilityProvider = new VisibilityProvider() {
            @Override
            public boolean shouldHideDivider(int position, RecyclerView parent) {
                if (parent instanceof SobotRecyclerView) {
                    SobotRecyclerView recyclerView = ((SobotRecyclerView) parent);
                    int len = (recyclerView.isLoadingMoreEnabled() ? 2 : 1);
                    return isNeedSkip(position, recyclerView.getItemCount(), recyclerView.getHeadersCount() + 1
                            , recyclerView.getFootersCount() + len);
                }
                return false;
            }
        };

        //跳过添加的头和尾不画分割线
        private boolean isNeedSkip(int groupIndex, int itemcount, int startSkipCount, int endSkipCount) {
            //Log.i("test", groupIndex + " itemcount:" + itemcount + " startSkipCount:" + startSkipCount + " endSkipCount:" + endSkipCount);
            if (groupIndex < startSkipCount) {
                return true;
            }
            if (itemcount - groupIndex <= endSkipCount) {
                return true;
            }
            // 默认不跳过
            return false;
        }

        public Builder(Context context) {
            super(context);
        }

        @Override
        public XHorizontalDividerItemDecoration build() {
            visibilityProvider(mVisibilityProvider);
            return new XHorizontalDividerItemDecoration(this);
        }
    }
}