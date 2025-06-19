package com.sobot.online.activity;

import android.view.View;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.adapter.SobotViewPagerAdapter;
import com.sobot.online.base.SobotBaseFragment;
import com.sobot.online.base.SobotOnlineBaseActivity;
import com.sobot.online.fragment.SobotOnlineUserinfoFragment;
import com.sobot.online.fragment.SobotOnlineVisitinfoFragment;
import com.sobot.online.weight.SobotNoScrollViewPager;
import com.sobot.online.weight.SobotPagerSlidingTab;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 客户信息主页面
 * @Author: znw
 * @CreateDate: 2020/09/7 19:59
 * @Version: 1.0
 */
public class SobotUserInfoActivity extends SobotOnlineBaseActivity {
    private SobotNoScrollViewPager sobot_online_viewPager;
    private SobotViewPagerAdapter mAdapter;
    private SobotPagerSlidingTab sobot_online_tab_indicator;
    //头部返回按钮
    private TextView sobot_online_tab_back_tv;

    private List<SobotBaseFragment> mFragments;
    private SobotOnlineUserinfoFragment userinfoFragment;
    private SobotOnlineVisitinfoFragment visitinfoFragment;

    private HistoryUserInfoModel userInfoModel;

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_userinfo;
    }

    @Override
    protected void initView() {
        sobot_online_viewPager = findViewById(R.id.sobot_online_viewPager);
        sobot_online_tab_indicator = findViewById(R.id.sobot_online_tab_indicator);
        sobot_online_tab_back_tv = findViewById(R.id.sobot_online_tab_back_tv);
        initViewPager();
        //头部左侧返回按钮点击返回
        if (sobot_online_tab_back_tv != null) {
            sobot_online_tab_back_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void initData() {
        userInfoModel= (HistoryUserInfoModel) getIntent().getSerializableExtra("userinfo");
    }

    private void initViewPager() {
        userinfoFragment = new SobotOnlineUserinfoFragment();
        visitinfoFragment = new SobotOnlineVisitinfoFragment();
        mFragments = new ArrayList<SobotBaseFragment>();
        mFragments.clear();
        if (userinfoFragment != null) {
            mFragments.add(userinfoFragment);
        }
        if (visitinfoFragment != null) {
            mFragments.add(visitinfoFragment);
        }
        mAdapter = new SobotViewPagerAdapter(getSobotContext(), getSupportFragmentManager(), new String[]{getResString("online_user_info_tab"), getResString("online_visit_info_tab")}, mFragments);
        sobot_online_viewPager.setAdapter(mAdapter);
        sobot_online_tab_indicator.setViewPager(sobot_online_viewPager);

    }


}
