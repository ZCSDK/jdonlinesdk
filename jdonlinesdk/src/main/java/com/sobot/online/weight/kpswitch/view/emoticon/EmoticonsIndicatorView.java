package com.sobot.online.weight.kpswitch.view.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sobot.online.R;
import com.sobot.online.weight.kpswitch.widget.data.PageSetEntity;
import com.sobot.onlinecommon.utils.SobotSizeUtils;

import java.util.ArrayList;

public class EmoticonsIndicatorView extends LinearLayout {

    private static final int MARGIN_LEFT = 4;
    protected Context mContext;
    protected ArrayList<ImageView> mImageViews;
    protected Drawable mDrawableSelect;
    protected Drawable mDrawableNomal;
    protected LayoutParams mLeftLayoutParams;

    public EmoticonsIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.setOrientation(HORIZONTAL);
        if (mDrawableNomal == null) {
            mDrawableNomal = getResources().getDrawable(R.drawable.sobot_indicator_point_nomal);
        }
        if (mDrawableSelect == null) {
            mDrawableSelect = getResources().getDrawable(R.drawable.sobot_indicator_point_select);
        }
        mLeftLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLeftLayoutParams.height = SobotSizeUtils.dp2px(5);
        mLeftLayoutParams.width = SobotSizeUtils.dp2px(5);
        mLeftLayoutParams.leftMargin = SobotSizeUtils.dp2px(MARGIN_LEFT);
    }

    public void playTo(int position, PageSetEntity pageSetEntity) {
        if (!checkPageSetEntity(pageSetEntity)) {
            return;
        }

        updateIndicatorCount(pageSetEntity.getPageCount());

        if (mImageViews != null && mImageViews.size() > 0) {
            for (ImageView iv : mImageViews) {
                iv.setImageDrawable(mDrawableNomal);
            }
            if (mImageViews.get(position) != null) {
                mImageViews.get(position).setImageDrawable(mDrawableSelect);
            }
        }
    }

    public void playBy(int startPosition, int nextPosition, PageSetEntity pageSetEntity) {
        if (!checkPageSetEntity(pageSetEntity)) {
            return;
        }

        updateIndicatorCount(pageSetEntity.getPageCount());

        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition) {
            startPosition = nextPosition = 0;
        }

        if (startPosition < 0) {
            startPosition = nextPosition = 0;
        }

        final ImageView imageViewStrat = mImageViews.get(startPosition);
        final ImageView imageViewNext = mImageViews.get(nextPosition);

        imageViewStrat.setImageDrawable(mDrawableNomal);
        imageViewNext.setImageDrawable(mDrawableSelect);
    }

    protected boolean checkPageSetEntity(PageSetEntity pageSetEntity) {
        if (pageSetEntity != null && pageSetEntity.isShowIndicator()) {
            setVisibility(VISIBLE);
            return true;
        } else {
            setVisibility(GONE);
            return false;
        }
    }

    protected void updateIndicatorCount(int count) {
        if (mImageViews == null) {
            mImageViews = new ArrayList<>();
        }
        if (count > mImageViews.size()) {
            for (int i = mImageViews.size(); i < count; i++) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageDrawable(i == 0 ? mDrawableSelect : mDrawableNomal);
                this.addView(imageView, mLeftLayoutParams);
                mImageViews.add(imageView);
            }
        }

        //如果只有一页不显示indicator
        if (count == 1) {
            for (int i = 0; i < mImageViews.size(); i++) {
                mImageViews.get(i).setVisibility(GONE);
            }
        } else {
            for (int i = 0; i < mImageViews.size(); i++) {
                if (i >= count) {
                    mImageViews.get(i).setVisibility(GONE);
                } else {
                    mImageViews.get(i).setVisibility(VISIBLE);
                }
            }
        }
    }
}