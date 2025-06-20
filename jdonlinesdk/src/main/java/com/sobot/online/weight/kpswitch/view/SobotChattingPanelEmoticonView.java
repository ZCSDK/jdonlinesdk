package com.sobot.online.weight.kpswitch.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.online.R;
import com.sobot.online.weight.emoji.DisplayEmojiRules;
import com.sobot.online.weight.emoji.EmojiconNew;
import com.sobot.online.weight.kpswitch.view.emoticon.EmoticonPageView;
import com.sobot.online.weight.kpswitch.view.emoticon.EmoticonsFuncView;
import com.sobot.online.weight.kpswitch.view.emoticon.EmoticonsIndicatorView;
import com.sobot.online.weight.kpswitch.widget.adpater.EmoticonsAdapter;
import com.sobot.online.weight.kpswitch.widget.adpater.PageSetAdapter;
import com.sobot.online.weight.kpswitch.widget.data.EmoticonPageEntity;
import com.sobot.online.weight.kpswitch.widget.data.EmoticonPageSetEntity;
import com.sobot.online.weight.kpswitch.widget.data.PageSetEntity;
import com.sobot.online.weight.kpswitch.widget.interfaces.EmoticonClickListener;
import com.sobot.online.weight.kpswitch.widget.interfaces.EmoticonDisplayListener;
import com.sobot.online.weight.kpswitch.widget.interfaces.PageViewInstantiateListener;

/**
 * 聊天面板   表情
 */
public class SobotChattingPanelEmoticonView extends BaseChattingPanelView implements EmoticonsFuncView.OnEmoticonsPageViewListener {

    protected EmoticonsFuncView mEmoticonsFuncView;
    protected EmoticonsIndicatorView mEmoticonsIndicatorView;
    SobotEmoticonClickListener mListener;

    public SobotChattingPanelEmoticonView(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        final View view = View.inflate(context, R.layout.sobot_emoticon_layout, null);
        return view;
    }

    @Override
    public void initData() {
        mEmoticonsFuncView = (EmoticonsFuncView) getRootView().findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) getRootView().findViewById(R.id.view_eiv));
        mEmoticonsFuncView.setOnIndicatorListener(this);
        setAdapter();
    }

    @Override
    public void setListener(SobotBasePanelListener listener) {
        if (listener != null && listener instanceof SobotEmoticonClickListener) {
            mListener = (SobotEmoticonClickListener) listener;
        }
    }


    public void setAdapter() {
        PageSetAdapter pageSetAdapter = new PageSetAdapter();
        EmoticonPageSetEntity kaomojiPageSetEntity
                = new EmoticonPageSetEntity.Builder()
                .setLine(context.getResources().getInteger(R.integer.sobot_emotiocon_line))
                .setRow(context.getResources().getInteger(R.integer.sobot_emotiocon_row))
                .setEmoticonList(DisplayEmojiRules.getListAll(context))
                .setIPageViewInstantiateItem(new PageViewInstantiateListener<EmoticonPageEntity>() {
                    //每个表情加载的回调
                    @Override
                    public View instantiateItem(ViewGroup container, int position, EmoticonPageEntity pageEntity) {
                        if (pageEntity.getRootView() == null) {
                            //下面这个view  就是一个gridview
                            EmoticonPageView pageView = new EmoticonPageView(container.getContext());
                            pageView.setNumColumns(pageEntity.getRow());
                            pageEntity.setRootView(pageView);
                            try {
                                EmoticonsAdapter adapter = new EmoticonsAdapter(container.getContext(), pageEntity, emoticonClickListener);
                                adapter.setItemHeightMaxRatio(1.8);
                                adapter.setOnDisPlayListener(getEmoticonDisplayListener(emoticonClickListener));
                                pageView.getEmoticonsGridView().setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return pageEntity.getRootView();
                    }
                })
                .setShowDelBtn(EmoticonPageEntity.DelBtnStatus.LAST)
                .build();
        pageSetAdapter.add(kaomojiPageSetEntity);
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    /**
     * 这个是adapter里面的bindview回调
     * 作用就是绑定数据用的
     *
     * @param emoticonClickListener 点击表情的回调
     * @return
     */
    public EmoticonDisplayListener<Object> getEmoticonDisplayListener(final EmoticonClickListener emoticonClickListener) {
        return new EmoticonDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, Object object, final boolean isDelBtn) {
                final EmojiconNew emoticonEntity = (EmojiconNew) object;
                if (emoticonEntity == null && !isDelBtn) {
                    return;
                }
                //每个表情的背景
//                viewHolder.ly_root.setBackgroundResource(getResDrawableId("sobot_bg_emoticon"));

                if (isDelBtn) {
                    viewHolder.iv_emoticon.setVisibility(View.VISIBLE);
                    viewHolder.tv_emoticon.setVisibility(View.GONE);
                    viewHolder.iv_emoticon.setImageResource(R.drawable.sobot_emoticon_del_selector);
                } else {
                    viewHolder.iv_emoticon.setVisibility(View.GONE);
                    viewHolder.tv_emoticon.setVisibility(View.VISIBLE);
                    viewHolder.tv_emoticon.setText(emoticonEntity.getEmojiCode());
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emoticonClickListener != null) {
                            emoticonClickListener.onEmoticonClick(emoticonEntity, isDelBtn);
                        }
                    }
                });
            }
        };
    }

    @Override
    public String getRootViewTag() {
        return "SobotChattingPanelEmoticonView";
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, boolean isDelBtn) {
            if (mListener != null) {
                if (isDelBtn) {
                    mListener.backspace();
                } else {
                    mListener.inputEmoticon((EmojiconNew) o);
                }
            }
        }
    };

    public interface SobotEmoticonClickListener extends SobotBasePanelListener {
        void backspace();

        void inputEmoticon(EmojiconNew item);
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {
//        mEmoticonsToolBarView.setToolBtnSelect(pageSetEntity.getUuid());
    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }
}
