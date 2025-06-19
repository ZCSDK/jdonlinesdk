package com.sobot.online.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.SobotQuickReplyContentAdapter;
import com.sobot.online.adapter.SobotQuickReplyGroupAdapter;
import com.sobot.online.base.SobotOnlineBaseFragment;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.recyclerview.adapter.BaseRecyclerViewAdapter;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.model.ChatReplyGroupInfoModel;
import com.sobot.onlinecommon.model.ChatReplyInfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 快捷回复 我的页面
 * @Author: znw
 * @CreateDate: 2020/10/11 14:55
 * @Version: 1.0
 */
public class SobotMyReplyFragment extends SobotOnlineBaseFragment implements SobotRecyclerView.LoadingListener, View.OnClickListener {
    private View mRootView;
    private LinearLayout ll_online_my_reply_group_title;
    private TextView tv_online_my_reply_first_group_title;
    private TextView tv_online_my_reply_second_group_title;
    private TextView tv_online_my_reply_all_group_title;
    private EditText et_online_search_content;

    SobotSwipeMenuRecyclerView ssmrv_online_reply_first_group;//分组 第一层
    SobotSwipeMenuRecyclerView ssmrv_online_reply_second_group;//分组 第二层
    SobotSwipeMenuRecyclerView ssmrv_online_reply_content;//回复内容
    SobotQuickReplyGroupAdapter quickReplyGroupAdapter;
    SobotQuickReplyGroupAdapter quickReplySecondGroupAdapter;
    SobotQuickReplyContentAdapter quickReplyContentAdapter;
    List<ChatReplyGroupInfoModel> firstGroupInfoModelList = new ArrayList<>();
    List<ChatReplyGroupInfoModel> secondGroupInfoModelList = new ArrayList<>();
    List<ChatReplyInfoModel> replyInfoModelList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_my_reply, container, false);
        initView();
        initData();
        return mRootView;
    }

    protected void initView() {
        et_online_search_content = mRootView.findViewById(R.id.et_online_search_content);
        ll_online_my_reply_group_title = mRootView.findViewById(R.id.ll_online_my_reply_group_title);
        tv_online_my_reply_first_group_title = mRootView.findViewById(R.id.tv_online_my_reply_first_group_title);
        tv_online_my_reply_second_group_title = mRootView.findViewById(R.id.tv_online_my_reply_second_group_title);
        tv_online_my_reply_all_group_title = mRootView.findViewById(R.id.tv_online_my_reply_all_group_title);
        tv_online_my_reply_first_group_title.setOnClickListener(this);
        tv_online_my_reply_all_group_title.setOnClickListener(this);

        ssmrv_online_reply_first_group = mRootView.findViewById(R.id.ssmrv_online_reply_first_group);
        ssmrv_online_reply_second_group = mRootView.findViewById(R.id.ssmrv_online_reply_second_group);
        ssmrv_online_reply_content = mRootView.findViewById(R.id.ssmrv_online_reply_content);

        LinearLayoutManager firstlayoutManager = new LinearLayoutManager(getSobotActivity());
        firstlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_reply_first_group.setLayoutManager(firstlayoutManager);
        ssmrv_online_reply_first_group.setPullRefreshEnabled(false);
        ssmrv_online_reply_first_group.setLoadingMoreEnabled(false);
        ssmrv_online_reply_first_group.setLoadingListener(this);

        LinearLayoutManager secondLayoutManager = new LinearLayoutManager(getSobotActivity());
        secondLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_reply_second_group.setLayoutManager(secondLayoutManager);
        ssmrv_online_reply_second_group.setPullRefreshEnabled(false);
        ssmrv_online_reply_second_group.setLoadingMoreEnabled(false);
        ssmrv_online_reply_second_group.setLoadingListener(this);

        LinearLayoutManager contentLayoutManager = new LinearLayoutManager(getSobotActivity());
        contentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_reply_content.setLayoutManager(contentLayoutManager);
        ssmrv_online_reply_content.setPullRefreshEnabled(false);
        ssmrv_online_reply_content.setLoadingMoreEnabled(false);
        ssmrv_online_reply_content.setLoadingListener(this);
    }

    private void initData() {
        et_online_search_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    ll_online_my_reply_group_title.setVisibility(View.VISIBLE);
                    tv_online_my_reply_first_group_title.setVisibility(View.GONE);
                    tv_online_my_reply_second_group_title.setVisibility(View.GONE);
                    tv_online_my_reply_all_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
                    ssmrv_online_reply_first_group.setVisibility(View.VISIBLE);
                    ssmrv_online_reply_second_group.setVisibility(View.GONE);
                    ssmrv_online_reply_content.setVisibility(View.GONE);
                } else {
                    ssmrv_online_reply_first_group.setVisibility(View.GONE);
                    ssmrv_online_reply_second_group.setVisibility(View.GONE);
                    ssmrv_online_reply_content.setVisibility(View.VISIBLE);
                    searchReplyByKeyword(s.toString());
                    ll_online_my_reply_group_title.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        quickReplyGroupAdapter = new SobotQuickReplyGroupAdapter(getSobotActivity());
        quickReplyGroupAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<ChatReplyGroupInfoModel>() {
            @Override
            public void onItemClick(View view, ChatReplyGroupInfoModel groupInfoModel, int position) {
                if (groupInfoModel.getChildren() != null) {
                    if (groupInfoModel.getChildren().size() == 0) {
                        replyInfoModelList.clear();
                        quickReplyContentAdapter.setListAll(replyInfoModelList);
                        searchReplyByGroupId(groupInfoModel.getGroupId());
                        ssmrv_online_reply_first_group.setVisibility(View.GONE);
                        ssmrv_online_reply_second_group.setVisibility(View.GONE);
                        ssmrv_online_reply_content.setVisibility(View.VISIBLE);

                        tv_online_my_reply_first_group_title.setVisibility(View.GONE);
                        tv_online_my_reply_second_group_title.setVisibility(View.VISIBLE);
                        tv_online_my_reply_second_group_title.setText(groupInfoModel.getGroupName());
                        tv_online_my_reply_first_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
                        tv_online_my_reply_all_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
                    } else {
                        secondGroupInfoModelList.clear();
                        secondGroupInfoModelList.addAll(groupInfoModel.getChildren());
                        quickReplySecondGroupAdapter.setListAll(secondGroupInfoModelList);
                        ssmrv_online_reply_first_group.setVisibility(View.GONE);
                        ssmrv_online_reply_second_group.setVisibility(View.VISIBLE);
                        ssmrv_online_reply_content.setVisibility(View.GONE);

                        tv_online_my_reply_first_group_title.setVisibility(View.VISIBLE);
                        tv_online_my_reply_second_group_title.setVisibility(View.GONE);
                        tv_online_my_reply_first_group_title.setText(groupInfoModel.getGroupName());
                        tv_online_my_reply_all_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
                    }
                }
            }
        });
        ssmrv_online_reply_first_group.setAdapter(quickReplyGroupAdapter);
        getReplyGrouplist();

        quickReplySecondGroupAdapter = new SobotQuickReplyGroupAdapter(getSobotActivity());
        quickReplySecondGroupAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<ChatReplyGroupInfoModel>() {
            @Override
            public void onItemClick(View view, ChatReplyGroupInfoModel groupInfoModel, int position) {
                replyInfoModelList.clear();
                quickReplyContentAdapter.setListAll(replyInfoModelList);
                searchReplyByGroupId(groupInfoModel.getGroupId());
                ssmrv_online_reply_first_group.setVisibility(View.GONE);
                ssmrv_online_reply_second_group.setVisibility(View.GONE);
                ssmrv_online_reply_content.setVisibility(View.VISIBLE);

                tv_online_my_reply_first_group_title.setVisibility(View.VISIBLE);
                tv_online_my_reply_second_group_title.setVisibility(View.VISIBLE);
                tv_online_my_reply_second_group_title.setText(groupInfoModel.getGroupName());
                tv_online_my_reply_first_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
                tv_online_my_reply_all_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
            }
        });
        ssmrv_online_reply_second_group.setAdapter(quickReplySecondGroupAdapter);

        quickReplyContentAdapter = new SobotQuickReplyContentAdapter(getSobotActivity(), new SobotQuickReplyContentAdapter.OnSendReplyContentListener() {
            @Override
            public void onSendReplyContent(boolean isAutoSend, String sendContent) {
                Intent intent = new Intent();
                intent.putExtra("isAutoSend", isAutoSend);
                intent.putExtra("sendContent", sendContent);
                getSobotActivity().setResult(Activity.RESULT_OK, intent);
                getSobotActivity().finish();
            }
        });
        quickReplyContentAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener<ChatReplyInfoModel>() {
            @Override
            public void onItemClick(View view, ChatReplyInfoModel replyInfoModel, int position) {

            }
        });
        ssmrv_online_reply_content.setAdapter(quickReplyContentAdapter);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {
        if (v == tv_online_my_reply_all_group_title) {
            //点击全部分组
            tv_online_my_reply_first_group_title.setVisibility(View.GONE);
            tv_online_my_reply_second_group_title.setVisibility(View.GONE);
            tv_online_my_reply_all_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_common_gray2));
            ssmrv_online_reply_first_group.setVisibility(View.VISIBLE);
            ssmrv_online_reply_second_group.setVisibility(View.GONE);
            ssmrv_online_reply_content.setVisibility(View.GONE);
        }
        if (v == tv_online_my_reply_first_group_title) {
            //点击一级分组
            tv_online_my_reply_first_group_title.setVisibility(View.VISIBLE);
            tv_online_my_reply_second_group_title.setVisibility(View.GONE);
            tv_online_my_reply_all_group_title.setTextColor(ContextCompat.getColor(getSobotActivity(), R.color.sobot_online_color));
            ssmrv_online_reply_first_group.setVisibility(View.GONE);
            ssmrv_online_reply_second_group.setVisibility(View.VISIBLE);
            ssmrv_online_reply_content.setVisibility(View.GONE);
        }
    }

    //查询快捷回复分组
    public void getReplyGrouplist() {
        //adminFlag 0:客服查询  1:公用查询
        zhiChiApi.newReplyGrouplist(getSobotActivity(), "0", new SobotResultCallBack<List<ChatReplyGroupInfoModel>>() {

            @Override
            public void onSuccess(List<ChatReplyGroupInfoModel> replyGroupInfoModels) {
                if (replyGroupInfoModels != null) {
                    firstGroupInfoModelList.clear();
                    firstGroupInfoModelList.addAll(replyGroupInfoModels);
                    quickReplyGroupAdapter.setListAll(firstGroupInfoModelList);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }

    //查询快捷回复分组
    public void searchReplyByGroupId(String groupId) {
        zhiChiApi.searchReplyByGroupId(getSobotActivity(), groupId, new SobotResultCallBack<List<ChatReplyInfoModel>>() {

            @Override
            public void onSuccess(List<ChatReplyInfoModel> replyInfoModels) {
                if (replyInfoModels != null) {
                    replyInfoModelList.clear();
                    replyInfoModelList.addAll(replyInfoModels);
                    quickReplyContentAdapter.setListAll(replyInfoModelList);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }

    //根据关键字模糊查询快捷回复内容列表
    public void searchReplyByKeyword(String keyword) {
        zhiChiApi.vagueSearchReply(getSobotActivity(), "0", keyword, new SobotResultCallBack<List<ChatReplyInfoModel>>() {

            @Override
            public void onSuccess(List<ChatReplyInfoModel> replyInfoModels) {
                if (replyInfoModels != null) {
                    replyInfoModelList.clear();
                    replyInfoModelList.addAll(replyInfoModels);
                    quickReplyContentAdapter.setListAll(replyInfoModelList);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }
}