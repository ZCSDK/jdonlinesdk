package com.sobot.online.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.activity.SobotCameraActivity;
import com.sobot.online.api.ZhiChiOnlineApiFactory;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.ZhiChiOnlineApi;
import com.sobot.onlinecommon.api.apiutils.OnlineConstant;
import com.sobot.onlinecommon.model.ConversationConfigModelResult;
import com.sobot.onlinecommon.utils.SobotSPUtils;
import com.sobot.utils.SobotSDCardUtils;
import com.sobot.widget.ui.base.SobotBaseActivity;
import com.sobot.widget.ui.permission.SobotPermissionListenerImpl;

/**
 * @Description: 在线客服基类 BaseActivity
 * @Author: znw
 * @CreateDate: 2020/09/1 11:05
 * @Version: 1.0
 */
public class SobotOnlineBaseActivity extends SobotBaseActivity {

    public ZhiChiOnlineApi zhiChiApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (zhiChiApi == null) {
            synchronized (SobotOnlineBaseActivity.class) {
                if (zhiChiApi == null) {
                    zhiChiApi = ZhiChiOnlineApiFactory.createZhiChiApi(SobotOnlineBaseActivity.this);
                }
            }
        }
        SobotGlobalContext.getInstance().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        SobotGlobalContext.getInstance().finishActivity(this);
        super.onDestroy();
    }

    @Override
    protected int getContentViewResId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    //返回整个头部
    public RelativeLayout getHearderView() {
        return findViewById(R.id.rl_sobot_online_base_header_root);
    }

    //返回头部左侧返回按钮控件
    public TextView getHearderLeftView() {
        return findViewById(R.id.tv_sobot_online_base_header_left);
    }

    //返回头部左侧返回按钮控件
    public TextView getHearderRightView() {
        return findViewById(R.id.tv_sobot_online_base_header_right);
    }

    //返回头部中间标题
    public TextView getHearderTitleView() {
        return findViewById(R.id.tv_sobot_online_base_header_title);
    }

    //设置头部中间标题
    public void setHearderTitle(String title) {
        TextView titleTV = getHearderTitleView();
        if (titleTV != null) {
            titleTV.setText(title);
        }
    }

    public SobotBaseActivity getSobotActivity() {
        return this;
    }

    public Context getSobotContext() {
        return this;
    }

    /**
     * 填充用户配置信息
     */
    public void fillUserConfig() {
        zhiChiApi.conversationConfig(this, new SobotResultCallBack<ConversationConfigModelResult.ConversationConfigModel>() {
            @Override
            public void onSuccess(ConversationConfigModelResult.ConversationConfigModel conversationConfigModel) {

                SobotSPUtils.getInstance().put(OnlineConstant
                        .SCAN_PATH_FLAG, conversationConfigModel.getScanPathFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .ISINVITE_FLAG, conversationConfigModel.getIsInvite() == 0);

                //服务总结
                SobotSPUtils.getInstance().put(OnlineConstant
                        .OPEN_SUMMARY_FLAG, conversationConfigModel.getOpenSummaryFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_OPERATION_FLAG, conversationConfigModel.getSummaryOperationFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_OPERATION_INPUT_FLAG, conversationConfigModel.getSummaryOperationInputFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_TYPE_FLAG, conversationConfigModel.getSummaryTypeFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_STATUS_FLAG, conversationConfigModel.getSummaryStatusFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .SUMMARY_STATUS_INPUT_FLAG, conversationConfigModel.getSummaryStatusInputFlag() == 1);
                SobotSPUtils.getInstance().put(OnlineConstant
                        .QDESCRIBE_SHOW_FLAG, conversationConfigModel.getQDescribeShowFlag() == 1);
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    /**
     * 通过照相上传图片
     */
    public void selectPicFromCamera() {

        if (!SobotSDCardUtils.isExitsSdcard()) {
            SobotToastUtil.showCustomToast(getApplicationContext(), getResString("sobot_app_sdcard_does_not_exist"),
                    Toast.LENGTH_SHORT);
            return;
        }
        permissionListener = new SobotPermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                //如果有拍照所需的权限，跳转到拍照界面
                startActivityForResult(SobotCameraActivity.newIntent(getSobotContext()), OnlineConstant.SOBOT_REQUEST_CODE_CAMERA);
            }
        };

        if (!checkCameraPermission()) {
            return;
        }
        // 打开拍摄页面
        startActivityForResult(SobotCameraActivity.newIntent(getSobotContext()), OnlineConstant.SOBOT_REQUEST_CODE_CAMERA);
    }
}
