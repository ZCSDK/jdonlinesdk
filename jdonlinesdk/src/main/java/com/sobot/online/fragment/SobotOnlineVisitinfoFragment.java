package com.sobot.online.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.OnlineWebViewActivity;
import com.sobot.online.base.SobotOnlineBaseFragment;
import com.sobot.online.util.HtmlTools;
import com.sobot.onlinecommon.model.HistoryUserInfoModel;
import com.sobot.onlinecommon.model.UserConversationModel;
import com.sobot.onlinecommon.socket.SobotSocketConstant;

/**
 * @Description: 访问信息页面
 * @Author: znw
 * @CreateDate: 2020/09/7 19:58
 * @Version: 1.0
 */
public class SobotOnlineVisitinfoFragment extends SobotOnlineBaseFragment {
    private View mRootView;
    private TextView tv_visitor_search_engines;
    private TextView tv_visitor_loading_page;
    private TextView tv_visitor_launch_page;
    private TextView tv_visitor_access_channel;
    private TextView tv_visitor_user_skill_name;
    private TextView tv_visitor_user_line_up_time;
    private TextView tv_visitor_user_ip_address;
    private TextView tv_visitor_user_terminal;
    private TextView tv_visitor_user_system;
    private TextView tv_visitor_count;
    private TextView tv_visitor_search_keywords;
    private HistoryUserInfoModel userInfoModel;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_visit_info, container, false);
        initView();
        initData();
        return mRootView;
    }

    private void initData() {
        userInfoModel = (HistoryUserInfoModel) getSobotActivity().getIntent().getSerializableExtra("userinfo");
        zhiChiApi.queryConMsg(getSobotActivity(), userInfoModel.getId(), userInfoModel.getLastCid(), new SobotResultCallBack<UserConversationModel>() {
            @Override
            public void onSuccess(final UserConversationModel resultModel) {
                if (!isAdded()) {
                    return;
                }
                if (resultModel != null) {
                    if (!TextUtils.isEmpty(resultModel.getVisitLandPage())) {
                        tv_visitor_loading_page.setText(resultModel.getVisitLandPage());
                        tv_visitor_loading_page.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), OnlineWebViewActivity.class);
                                intent.putExtra("url", resultModel.getVisitLandPage());
                                startActivity(intent);
                            }
                        });
                    } else {
                        tv_visitor_loading_page.setText("--");
                    }

                    if (!TextUtils.isEmpty(resultModel.getConLaunchPage())) {
                        tv_visitor_launch_page.setText(resultModel.getVisitLandPage());
                        tv_visitor_launch_page.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), OnlineWebViewActivity.class);
                                intent.putExtra("url", resultModel.getConLaunchPage());
                                startActivity(intent);
                            }
                        });
                    } else {
                        tv_visitor_launch_page.setText("--");
                    }

                    tv_visitor_access_channel.setText(getSourceText(resultModel.getSource()+""));

                    tv_visitor_user_skill_name.setText(formatStr(resultModel.getGroupName()));
                    tv_visitor_user_line_up_time.setText(formatStr(resultModel.getRemainTime()));
                    tv_visitor_user_ip_address.setText(formatStr(resultModel.getIp()));
                    tv_visitor_user_terminal.setText(formatStr(resultModel.getTerminal()));
                    tv_visitor_user_system.setText(formatStr(resultModel.getOs()));

                    if (resultModel.getVisitMsg() != null) {
                        setVisitSourceType(getSobotActivity(), resultModel.getVisitSourceType(),
                                resultModel.getSearchSource(), tv_visitor_search_engines);
                    } else {
                        tv_visitor_search_engines.setText("--");
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    //显示客户来源
    private String getSourceText(String sourceType) {
        if (TextUtils.isEmpty(sourceType)) {
            return "";
        }
        String sourceName;
        switch (sourceType) {
            case "0":
                sourceName = getString(R.string.online_desktop_website);
                break;
            case "1":
                sourceName = getString(R.string.online_wechat);
                break;
            case "2":
                sourceName = "APP";
                break;
            case "3":
                sourceName = getString(R.string.online_micro_blog);
                break;
            case "4":
                sourceName = getString(R.string.online_mobile_website);
                break;
            case "5":
                sourceName = getString(R.string.online_fusion_cloud);
                break;
            case "6":
                sourceName = getString(R.string.online_call_center);
                break;
            case "7":
                sourceName = getString(R.string.online_work_order);
                break;
            case "8":
                sourceName = getString(R.string.online_customer_center);
                break;
            case "9":
                sourceName = getString(R.string.customer_source_wecom);
                break;
            case "10":
                sourceName = getString(R.string.customer_source_wechat_applet);
                break;
            case "11":
                sourceName = getString(R.string.customer_source_mail);
                break;
            case "12":
                sourceName = getString(R.string.customer_source_baidu_market);
                break;
            case "13":
                sourceName = getString(R.string.customer_source_toutiao);
                break;
            case "14":
                sourceName = getString(R.string.customer_source_360);
                break;
            case "15":
                sourceName = getString(R.string.customer_source_wolong);
                break;
            case "16":
                sourceName = getString(R.string.customer_source_sougou);
                break;
            case "17":
                sourceName = getString(R.string.customer_source_wechat_service);
                break;
            case "18":
                sourceName = getString(R.string.customer_source_interface);
                break;
            case "22":
                sourceName = getString(R.string.customer_source_facebook);
                break;
            case "23":
                sourceName = getString(R.string.customer_source_whatsapp);
                break;
            case "24":
                sourceName = getString(R.string.customer_source_instagram);
                break;
            case "25":
                sourceName = "Line";
                break;
            case "26":
                sourceName = "Discord";
                break;
            case "33":
                sourceName = "Telegram";
                break;

            default:
                sourceName = getString(R.string.online_desktop_website);
                break;
        }
        return sourceName;
    }


    protected void initView() {
        tv_visitor_search_engines = mRootView.findViewById(R.id.tv_visitor_search_engines);
        tv_visitor_loading_page = mRootView.findViewById(R.id.tv_visitor_loading_page);
        tv_visitor_launch_page = mRootView.findViewById(R.id.tv_visitor_launch_page);
        tv_visitor_access_channel = mRootView.findViewById(R.id.tv_visitor_access_channel);
        tv_visitor_user_skill_name = mRootView.findViewById(R.id.tv_visitor_user_skill_name);
        tv_visitor_user_line_up_time = mRootView.findViewById(R.id.tv_visitor_user_line_up_time);
        tv_visitor_user_ip_address = mRootView.findViewById(R.id.tv_visitor_user_ip_address);
        tv_visitor_user_terminal = mRootView.findViewById(R.id.tv_visitor_user_terminal);
        tv_visitor_user_system = mRootView.findViewById(R.id.tv_visitor_user_system);
        tv_visitor_count = mRootView.findViewById(R.id.tv_visitor_count);
        tv_visitor_search_keywords = mRootView.findViewById(R.id.tv_visitor_search_keywords);

    }

    /**
     * visitSourceType 如果存在 1 搜索引擎 2 站外网站 3直接访问
     * 如果不存在  searchSource =0 浏览网页  其他 由一下页面发起咨询
     *
     * @param tvVisitFromContent
     */
    public static String setVisitSourceType(Context context, String visitSourceType, String searchSource,
                                            TextView tvVisitFromContent) {
        String tmpStr = "";
        String type = "";
        if (!TextUtils.isEmpty(visitSourceType)) {
            switch (visitSourceType) {
                case SobotSocketConstant.TYPE_VISIT_SSOURCE_SEARCH_ENGINES:
                    //搜索引擎
                    tmpStr = context.getResources().getString(R.string.online_search_engines);
                    type = SobotSocketConstant.TYPE_VISIT_SSOURCE_SEARCH_ENGINES;
                    break;
                case SobotSocketConstant.TYPE_VISIT_SSOURCE_OUTSIDE_ACCESS:
                    //站外网站
                    tmpStr = context.getResources().getString(R.string.online_from_other_web);
                    type = SobotSocketConstant.TYPE_VISIT_SSOURCE_OUTSIDE_ACCESS;
                    break;
                case SobotSocketConstant.TYPE_VISIT_SSOURCE_DIRECT_ACCESS:
                    //直接访问
                    tmpStr = context.getResources().getString(R.string.online_direct_access);
                    type = SobotSocketConstant.TYPE_VISIT_SSOURCE_DIRECT_ACCESS;
                    break;
            }
        } else {
            if (!TextUtils.isEmpty(searchSource)) {
                switch (searchSource) {
                    case SobotSocketConstant.TYPE_VISIT_SSOURCE_BROWSE_PAGE:
                        //浏览网页
                        tmpStr = context.getResources().getString(R.string.online_browse_web);
                        type = SobotSocketConstant.TYPE_VISIT_SSOURCE_BROWSE_PAGE;
                        break;
                    default:
                        tmpStr = context.getResources().getString(R.string.online_from_following_page);
                        type = SobotSocketConstant.TYPE_VISIT_SSOURCE_OTHER;
                        break;
                }
            }
        }
        HtmlTools.getInstance(context).setRichText(tvVisitFromContent, tmpStr, R.color.sobot_online_color);
        return type;
    }

    private String formatStr(String str) {
        return TextUtils.isEmpty(str) ? "--" : str;
    }
}
