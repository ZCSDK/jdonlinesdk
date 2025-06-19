package com.sobot.online.weight.kpswitch.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.sobot.online.R;
import com.sobot.online.SobotOnlineUIConfig;
import com.sobot.online.weight.kpswitch.view.emoticon.EmoticonsFuncView;
import com.sobot.online.weight.kpswitch.view.emoticon.EmoticonsIndicatorView;
import com.sobot.online.weight.kpswitch.view.plus.SobotPlusPageView;
import com.sobot.online.weight.kpswitch.widget.adpater.PageSetAdapter;
import com.sobot.online.weight.kpswitch.widget.adpater.PlusAdapter;
import com.sobot.online.weight.kpswitch.widget.data.PageSetEntity;
import com.sobot.online.weight.kpswitch.widget.data.PlusPageEntity;
import com.sobot.online.weight.kpswitch.widget.data.PlusPageSetEntity;
import com.sobot.online.weight.kpswitch.widget.interfaces.PageViewInstantiateListener;
import com.sobot.online.weight.kpswitch.widget.interfaces.PlusDisplayListener;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天面板   更多菜单
 */
public class SobotChattingPanelUploadView extends BaseChattingPanelView implements  EmoticonsFuncView.OnEmoticonsPageViewListener {

    private static final String ONLINE_ACTION_PIC = "online_action_pic";
    private static final String ONLINE_ACTION_VIDEO = "online_action_video";
    private static final String ONLINE_ACTION_CAMERA = "online_action_camera";


    private List<SobotPlusEntity> operatorList = new ArrayList<>();

    private EmoticonsFuncView mEmoticonsFuncView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private PageSetAdapter pageSetAdapter;
    private SobotPlusClickListener mListener;

    public SobotChattingPanelUploadView(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        return View.inflate(context, R.layout.sobot_chat_bottom_meau_layout, null);
    }

    @Override
    public void initData() {
        mEmoticonsFuncView = (EmoticonsFuncView) getRootView().findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = ((EmoticonsIndicatorView) getRootView().findViewById(R.id.view_eiv));
        mEmoticonsFuncView.setOnIndicatorListener(this);
        //图片
        SobotPlusEntity picEntity = new SobotPlusEntity(R.drawable.sobot_tack_picture_button_selector, context.getResources().getString(R.string.sobot_bottom_picture_meau), ONLINE_ACTION_PIC);
        //视频
        SobotPlusEntity videoEntity = new SobotPlusEntity(R.drawable.sobot_tack_video_button_selector, context.getResources().getString(R.string.sobot_bottom_video_meau), ONLINE_ACTION_VIDEO);
        //拍照
        SobotPlusEntity cameraEntity = new SobotPlusEntity(R.drawable.sobot_tack_camera_button_selector, context.getResources().getString(R.string.sobot_bottom_camera_meau), ONLINE_ACTION_CAMERA);
        operatorList.clear();
        operatorList.add(picEntity);
        operatorList.add(videoEntity);
        operatorList.add(cameraEntity);
    }

    public static class SobotPlusEntity {
        public int iconResId;
        public String name;
        public String action;

        @Override
        public String toString() {
            return "SobotPlusEntity{" +
                    "iconResId=" + iconResId +
                    ", name='" + name + '\'' +
                    ", action='" + action + '\'' +
                    '}';
        }

        /**
         * 自定义菜单实体类
         *
         * @param iconResId 菜单图标
         * @param name      菜单名称
         * @param action    菜单动作 当点击按钮时会将对应action返回给callback
         *                  以此作为依据，判断用户点击了哪个按钮
         */
        public SobotPlusEntity(int iconResId, String name, String action) {
            this.iconResId = iconResId;
            this.name = name;
            this.action = action;
        }
    }

    private void setAdapter(List<SobotPlusEntity> datas) {
        if (pageSetAdapter == null) {
            pageSetAdapter = new PageSetAdapter();
        } else {
            pageSetAdapter.getPageSetEntityList().clear();
        }

        PlusPageSetEntity pageSetEntity
                = new PlusPageSetEntity.Builder()
                .setLine(context.getResources().getInteger(R.integer.sobot_plus_menu_line))
                .setRow(context.getResources().getInteger(R.integer.sobot_plus_menu_row))
                .setDataList(datas)
                .setIPageViewInstantiateItem(new PageViewInstantiateListener<PlusPageEntity>() {
                    @Override
                    public View instantiateItem(ViewGroup container, int position, PlusPageEntity pageEntity) {
                        if (pageEntity.getRootView() == null) {
                            //下面这个view  就是一个gridview
                            SobotPlusPageView pageView = new SobotPlusPageView(container.getContext());
                            pageView.setNumColumns(pageEntity.getRow());
                            pageEntity.setRootView(pageView);
                            try {
                                PlusAdapter adapter = new PlusAdapter(container.getContext(), pageEntity, mListener);
//                                adapter.setItemHeightMaxRatio(1.8);
                                adapter.setOnDisPlayListener(getPlusItemDisplayListener(mListener));
                                pageView.getGridView().setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            pageView.getGridView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (mListener != null) {
                                        View sobot_plus_menu = view.findViewById(R.id.sobot_plus_menu);
                                        String action = (String) sobot_plus_menu.getTag();
                                        if (ONLINE_ACTION_PIC.equals(action)) {
                                            //图库
                                            mListener.btnPicture();
                                        } else if (ONLINE_ACTION_VIDEO.equals(action)) {
                                            //视频
                                            mListener.btnVedio();
                                        } else if (ONLINE_ACTION_CAMERA.equals(action)) {
                                            //拍照
                                            mListener.btnCameraPicture();
                                        } else {
                                            if (SobotOnlineUIConfig.pulsMenu.sSobotPlusMenuListener != null) {
                                                SobotOnlineUIConfig.pulsMenu.sSobotPlusMenuListener.onClick(view, action);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        return pageEntity.getRootView();
                    }
                })
                .build();
        pageSetAdapter.add(pageSetEntity);
        mEmoticonsFuncView.setAdapter(pageSetAdapter);
    }

    /**
     * 这个是adapter里面的bindview回调
     * 作用就是绑定数据用的
     *
     * @param plusClickListener 点击表情的回调
     * @return
     */
    public PlusDisplayListener<Object> getPlusItemDisplayListener(final SobotChattingPanelUploadView.SobotPlusClickListener plusClickListener) {
        return new PlusDisplayListener<Object>() {
            @Override
            public void onBindView(int position, ViewGroup parent, PlusAdapter.ViewHolder viewHolder, Object object) {
                final SobotPlusEntity plusEntity = (SobotPlusEntity) object;
                if (plusEntity == null) {
                    return;
                }
                SobotOnlineLogUtils.i("--------" + plusEntity.toString());
                // 显示菜单
                //viewHolder.ly_root.setBackgroundResource(R.drawable.sobot_bg_emoticon);
                viewHolder.mMenu.setText(plusEntity.name);
                Drawable drawable = context.getResources().getDrawable(plusEntity.iconResId);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                viewHolder.mMenu.setCompoundDrawables(null, drawable, null, null);
                viewHolder.mMenu.setTag(plusEntity.action);


//                viewHolder.rootView.setOnClickListener(SobotChattingPanelUploadView.this);
            }
        };
    }

    @Override
    public void emoticonSetChanged(PageSetEntity pageSetEntity) {

    }

    @Override
    public void playTo(int position, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playTo(position, pageSetEntity);
    }

    @Override
    public void playBy(int oldPosition, int newPosition, PageSetEntity pageSetEntity) {
        mEmoticonsIndicatorView.playBy(oldPosition, newPosition, pageSetEntity);
    }

    public interface SobotPlusClickListener extends SobotBasePanelListener {
        void btnPicture();

        void btnVedio();

        void btnCameraPicture();
    }

    @Override
    public void setListener(SobotBasePanelListener listener) {
        if (listener != null && listener instanceof SobotChattingPanelUploadView.SobotPlusClickListener) {
            mListener = (SobotChattingPanelUploadView.SobotPlusClickListener) listener;
        }
    }

    @Override
    public String getRootViewTag() {
        return "SobotChattingPanelUploadView";
    }


    @Override
    public void onViewStart(Bundle bundle) {
        //在初次调用或者接待模式改变时修改view
        List<SobotPlusEntity> tmpList = new ArrayList<>();
        tmpList.addAll(operatorList);
        if (SobotOnlineUIConfig.pulsMenu.menus != null) {
            tmpList.addAll(SobotOnlineUIConfig.pulsMenu.menus);
        }
        setAdapter(tmpList);
    }
}