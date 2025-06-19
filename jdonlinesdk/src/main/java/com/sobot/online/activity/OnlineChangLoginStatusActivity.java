package com.sobot.online.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.api.apiutils.OnlineBaseCode;
import com.sobot.onlinecommon.utils.SobotTimeUtils;

//登录状态提交申请界面
public class OnlineChangLoginStatusActivity extends SobotOnlineDialogBaseActivity implements View.OnClickListener {

    private EditText et_online_pop_reason;//原因
    private TextView tv_online_pop_submit;//确定
    private TextView tv_online_pop_cancle;//取消
    private TextView tv_online_pop_header_title;//头部标题
    private TextView tv_online_pop_header_cancle;//头部取消按钮


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        win.setAttributes(lp);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_online_pop_apply;
    }


    @Override
    public void initView() {
        tv_online_pop_header_title = findViewById(R.id.tv_online_pop_header_title);
        tv_online_pop_header_title.setText(getString(R.string.online_change_kefu_status));
        tv_online_pop_cancle = findViewById(R.id.tv_online_pop_cancle);
        tv_online_pop_cancle.setOnClickListener(this);
        tv_online_pop_header_cancle = findViewById(R.id.tv_online_pop_header_cancle);
        tv_online_pop_header_cancle.setOnClickListener(this);
        tv_online_pop_submit = findViewById(R.id.tv_online_pop_submit);
        tv_online_pop_submit.setOnClickListener(this);
        et_online_pop_reason = findViewById(R.id.et_online_pop_reason);
        et_online_pop_reason.setHint(getString(R.string.online_change_kefu_status_reason_hint));

        displayInNotch(et_online_pop_reason);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_online_pop_cancle) {
            hideSoftInput();
            finish();
        }
        if (v.getId() == R.id.tv_online_pop_header_cancle) {
            hideSoftInput();
            finish();
        }
        if (v.getId() == R.id.tv_online_pop_submit) {
            serviceStatusChangeApply();
        }
    }

    //当前状态  Integer nowStatus;
    //申请状态 Integer cutStatus;
    //申请理由 String cutReason;
    //申请时间  String cutTime;
    //标记  1-通过 2-驳回 3-待审核 Integer cutFlag;
    //客服id String tid;
    public void serviceStatusChangeApply() {
        String nowStatus = getIntent().getStringExtra("nowStatus");
        String cutStatus = getIntent().getStringExtra("cutStatus");
        String cutReason = et_online_pop_reason.getText().toString();
        String tid = getIntent().getStringExtra("tid");
        String cutTime = SobotTimeUtils.getNowString();
        zhiChiApi.chopStatus(getSobotActivity(), nowStatus, cutStatus, cutReason, "3", tid, cutTime, new SobotResultCallBack<OnlineBaseCode>() {
            @Override
            public void onSuccess(OnlineBaseCode onlineBaseCode) {
                SobotToastUtil.showCustomToast(getSobotActivity(), getString(R.string.online_wait_apply_result));
                finish();
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getSobotContext(), des);
                finish();
            }
        });
    }
}