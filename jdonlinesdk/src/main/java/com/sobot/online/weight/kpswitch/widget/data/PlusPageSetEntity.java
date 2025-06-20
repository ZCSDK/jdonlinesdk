package com.sobot.online.weight.kpswitch.widget.data;

import com.sobot.online.weight.kpswitch.widget.interfaces.PageViewInstantiateListener;

import java.util.List;

/**
 * 更多菜单页面的和实体合集
 * @author Created by jinxl on 2018/7/31.
 */
public class PlusPageSetEntity<T> extends PageSetEntity<EmoticonPageEntity>{
    final int mLine;
    final int mRow;
    final List<T> mDataList;

    public PlusPageSetEntity(Builder builder) {
        super(builder);
        this.mLine = builder.line;
        this.mRow = builder.row;
        this.mDataList = builder.dataList;
    }

    public int getLine() {
        return mLine;
    }

    public int getRow() {
        return mRow;
    }


    public List<T> getDataList() {
        return mDataList;
    }

    public static class Builder<T> extends PageSetEntity.Builder {

        /**
         * 每页行数
         */
        protected int line;
        /**
         * 每页列数
         */
        protected int row;
        /**
         * 数据源
         */
        protected List<T> dataList;

        protected PageViewInstantiateListener pageViewInstantiateListener;

        public Builder() {
        }

        public Builder setLine(int line) {
            this.line = line;
            return this;
        }

        public Builder setRow(int row) {
            this.row = row;
            return this;
        }


        public Builder setDataList(List<T> dataList) {
            this.dataList = dataList;
            return this;
        }

        public Builder setIPageViewInstantiateItem(PageViewInstantiateListener pageViewInstantiateListener) {
            this.pageViewInstantiateListener = pageViewInstantiateListener;
            return this;
        }

        public Builder setShowIndicator(boolean showIndicator) {
            isShowIndicator = showIndicator;
            return this;
        }

        public Builder setIconUri(String iconUri) {
            this.iconUri = iconUri;
            return this;
        }

        public Builder setIconUri(int iconUri) {
            this.iconUri = "" + iconUri;
            return this;
        }

        public Builder setSetName(String setName) {
            this.setName = setName;
            return this;
        }

        public PlusPageSetEntity<T> build() {
            int dataSetSum = dataList.size();
            int everyPageMaxSum = row * line;
            pageCount = (int) Math.ceil((double) dataList.size() / everyPageMaxSum);

            int start = 0;
            int end = everyPageMaxSum > dataSetSum ? dataSetSum : everyPageMaxSum;

            if (!pageEntityList.isEmpty()) {
                pageEntityList.clear();
            }

            for (int i = 0; i < pageCount; i++) {
                PlusPageEntity<T> pageEntity = new PlusPageEntity<T>();
                pageEntity.setLine(line);
                pageEntity.setRow(row);
                pageEntity.setDataList(dataList.subList(start, end));
                pageEntity.setIPageViewInstantiateItem(pageViewInstantiateListener);
                pageEntityList.add(pageEntity);

                start = everyPageMaxSum + i * everyPageMaxSum;
                end = everyPageMaxSum + (i + 1) * everyPageMaxSum;
                if (end >= dataSetSum) {
                    end = dataSetSum;
                }
            }
            return new PlusPageSetEntity<>(this);
        }
    }
}
