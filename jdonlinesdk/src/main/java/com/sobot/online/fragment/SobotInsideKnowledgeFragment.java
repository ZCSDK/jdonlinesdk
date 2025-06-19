package com.sobot.online.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.adapter.SobotRebotInsideKnoweledgeAdapter;
import com.sobot.online.api.ZhiChiOnlineApiFactory;
import com.sobot.online.base.SobotOnlineBaseFragment;
import com.sobot.online.weight.recyclerview.SobotRecyclerView;
import com.sobot.online.weight.recyclerview.swipemenu.SobotSwipeMenuRecyclerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.ChatMessageRichTextModel;
import com.sobot.onlinecommon.model.RebotSmartAnswerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 智能回复 内部知识库
 * @Author: znw
 * @CreateDate: 2020/10/12 18:37
 * @Version: 1.0
 */
public class SobotInsideKnowledgeFragment extends SobotOnlineBaseFragment implements SobotRecyclerView.LoadingListener, View.OnClickListener {
    private View mRootView;

    SobotSwipeMenuRecyclerView ssmrv_online_inside_knowledge;//分组 第一层
    SobotRebotInsideKnoweledgeAdapter rebotInsideKnoweledgeAdapter;
    List<RebotSmartAnswerModel> rebotSmartAnswerModelArrayList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_inside_knowledge, container, false);
        initView();
        initData();
        return mRootView;
    }

    protected void initView() {
        ssmrv_online_inside_knowledge = mRootView.findViewById(R.id.ssmrv_online_inside_knowledge);

        LinearLayoutManager firstlayoutManager = new LinearLayoutManager(getSobotActivity());
        firstlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ssmrv_online_inside_knowledge.setLayoutManager(firstlayoutManager);
        ssmrv_online_inside_knowledge.setPullRefreshEnabled(false);
        ssmrv_online_inside_knowledge.setLoadingMoreEnabled(false);
        ssmrv_online_inside_knowledge.setLoadingListener(this);
    }

    private void initData() {
        rebotInsideKnoweledgeAdapter = new SobotRebotInsideKnoweledgeAdapter(getSobotActivity(), rebotSmartAnswerModelArrayList,new SobotRebotInsideKnoweledgeAdapter.OnSendReplyContentListener() {
            @Override
            public void onSendReplyContent(boolean isAutoSend, String sendContent, ArrayList<ChatMessageRichTextModel.ChatMessageRichListModel> richListModelList) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("richList", richListModelList);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.putExtra("isAutoSend", isAutoSend);
                intent.putExtra("sendContent", sendContent);
                intent.putExtra("sendType", OnlineConstant.SOBOT_MSG_TYPE_RICH);
                getSobotActivity().setResult(Activity.RESULT_OK, intent);
                getSobotActivity().finish();
            }
        });
        ssmrv_online_inside_knowledge.setAdapter(rebotInsideKnoweledgeAdapter);
        rebotInsideKnoweledgeAdapter.setGroups(rebotSmartAnswerModelArrayList);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {

    }


    //根据关键字模糊查询快捷回复内容列表
    public void innerInternalChat(String keyword) {
        Activity activity = null;
        if (getSobotActivity() == null) {
            activity = SobotGlobalContext.getInstance().currentActivity();
        } else {
            activity = getSobotActivity();
        }
        if (zhiChiApi == null) {
            zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(activity);
        }
        zhiChiApi.innerInternalChat(activity, keyword, new SobotResultCallBack<List<RebotSmartAnswerModel>>() {

            @Override
            public void onSuccess(List<RebotSmartAnswerModel> rebotInsideAnswerModels) {
                if (rebotInsideAnswerModels != null) {
                    rebotInsideKnoweledgeAdapter.setGroups(rebotInsideAnswerModels);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotActivity(), des);
            }
        });
    }
}