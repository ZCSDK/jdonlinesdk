package com.sobot.online.dialog;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sobot.online.R;
import com.sobot.online.base.SobotOnlineDialogBaseActivity;
import com.sobot.online.weight.pickerview.builder.TimePickerBuilder;
import com.sobot.online.weight.pickerview.listener.OnTimeSelectListener;
import com.sobot.online.weight.pickerview.view.TimePickerView;
import com.sobot.online.weight.toast.SobotToastUtil;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;
import com.sobot.onlinecommon.utils.SobotTimeUtils;

import java.util.Date;

public class SobotOnlineScreenActivity extends SobotOnlineDialogBaseActivity implements View.OnClickListener {

    private TextView tv_online_pop_header_title;//头部标题
    private TextView tv_online_pop_header_cancle;//头部取消按钮
    private RadioGroup rg_online_pop_reception;//接待人
    private RadioGroup rg_online_pop_all_handle_status;//处理状态
    private RadioGroup rg_online_pop_all_summary;//服务总结状态
    private RadioGroup rg_online_pop_all_mark;//星标状态

    private TextView tv_online_pop_summary;
    private TextView tv_online_pop_mark;
    private TextView tv_online_pop_start_time;//开始时间
    private TextView tv_online_pop_end_time;//结束时间
    private TextView tv_online_pop_handle_status;

    private RadioButton rb_online_pop_my_reception;
    private RadioButton rb_online_pop_all_reception;
    private RadioButton rb_online_pop_all_summary;
    private RadioButton rb_online_pop_already_summary;
    private RadioButton rb_online_pop_no_summary;
    private RadioButton rb_online_pop_all_mark;
    private RadioButton rb_online_pop_already_mark;
    private RadioButton rb_online_pop_no_mark;

    private TextView tv_sobot_online_save;//保存返回

    private int isChatOrUser;//0筛选会话，1筛选用户

    private String startDate;//开始时间
    private String endDate;//结束时间
    private int receiveType;//0 自己接待   1全公司接待
    private String ismark = "0";//星标 1已星标  2未星标 0全部
    private String summaryStatus = "0"; //已总结 1 未总结; 2 ;0全部
    private String questionStatus = "0";//已处理 1 未处理; 2 ;0全部


    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_online_pop_screen;
    }


    @Override
    public void initView() {
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        receiveType = getIntent().getIntExtra("receiveType", 0);
        ismark = getIntent().getStringExtra("ismark");
        summaryStatus = getIntent().getStringExtra("summaryStatus");

        tv_sobot_online_save = findViewById(R.id.tv_sobot_online_save);
        tv_sobot_online_save.setOnClickListener(this);
        tv_online_pop_header_title = findViewById(R.id.tv_online_pop_header_title);
        tv_online_pop_header_title.setText(getString(R.string.sobot_screen));
        tv_online_pop_header_cancle = findViewById(R.id.tv_online_pop_header_cancle);
        tv_online_pop_header_cancle.setOnClickListener(this);
        tv_online_pop_summary = findViewById(R.id.tv_online_pop_summary);
        tv_online_pop_mark = findViewById(R.id.tv_online_pop_mark);
        tv_online_pop_handle_status = findViewById(R.id.tv_online_pop_handle_status);

        rb_online_pop_my_reception = findViewById(R.id.rb_online_pop_my_reception);
        rb_online_pop_all_reception = findViewById(R.id.rb_online_pop_all_reception);
        rb_online_pop_all_summary = findViewById(R.id.rb_online_pop_all_summary);
        rb_online_pop_already_summary = findViewById(R.id.rb_online_pop_already_summary);
        rb_online_pop_no_summary = findViewById(R.id.rb_online_pop_no_summary);
        rb_online_pop_all_mark = findViewById(R.id.rb_online_pop_all_mark);
        rb_online_pop_already_mark = findViewById(R.id.rb_online_pop_already_mark);
        rb_online_pop_no_mark = findViewById(R.id.rb_online_pop_no_mark);

        rg_online_pop_reception = findViewById(R.id.rg_online_pop_reception);
        rg_online_pop_all_handle_status = findViewById(R.id.rg_online_pop_all_handle_status);
        rg_online_pop_all_summary = findViewById(R.id.rg_online_pop_all_summary);
        rg_online_pop_all_mark = findViewById(R.id.rg_online_pop_all_mark);
        tv_online_pop_start_time = findViewById(R.id.tv_online_pop_start_time);
        tv_online_pop_end_time = findViewById(R.id.tv_online_pop_end_time);
        tv_online_pop_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(getSobotContext(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        tv_online_pop_start_time.setText(SobotTimeUtils.date2String(date));
                    }
                }).setType(new boolean[]{true, true, true, true, true, true}).setSubmitColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).setCancelColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).build();
                pvTime.show();
            }
        });
        startDate = TextUtils.isEmpty(startDate) ? SobotTimeUtils.getPastDate(7) : startDate;
        tv_online_pop_start_time.setText(startDate);
        tv_online_pop_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(getSobotContext(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        if (!TextUtils.isEmpty(endDate)) {
                            int result = SobotTimeUtils.timeCompare(SobotTimeUtils.date2String(date, "yyyy-MM-dd"), endDate, "yyyy-MM-dd");
                            if (result == 1) {
                                SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_time_start_cant_exceed_end));
                                return;
                            }
                        }
                        startDate = SobotTimeUtils.date2String(date, "yyyy-MM-dd");
                        tv_online_pop_start_time.setText(SobotTimeUtils.date2String(date, "yyyy-MM-dd"));

                    }
                }).setSubmitColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).setCancelColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).build();
                pvTime.show();
            }
        });
        endDate = TextUtils.isEmpty(endDate) ? SobotTimeUtils.date2String(new Date(), "yyyy-MM-dd") : endDate;
        tv_online_pop_end_time.setText(endDate);
        tv_online_pop_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(getSobotContext(), new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {

                        if (!TextUtils.isEmpty(startDate)) {
                            int result = SobotTimeUtils.timeCompare(startDate, SobotTimeUtils.date2String(date, "yyyy-MM-dd"), "yyyy-MM-dd");
                            if (result == 1) {
                                SobotToastUtil.showCustomToast(getSobotContext(), getString(R.string.online_time_end_cant_exceed_start));
                                return;
                            }
                        }
                        endDate = SobotTimeUtils.date2String(date, "yyyy-MM-dd");
                        tv_online_pop_end_time.setText(SobotTimeUtils.date2String(date, "yyyy-MM-dd"));

                    }
                }).setSubmitColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).setCancelColor(getSobotActivity().getResources().getColor(R.color.sobot_online_color)).build();
                pvTime.show();
            }
        });
        rg_online_pop_reception.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_online_pop_my_reception) {
                    receiveType = 0;
                } else if (checkedId == R.id.rb_online_pop_all_reception) {
                    receiveType = 1;
                }
            }
        });
        rg_online_pop_all_handle_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_online_pop_all_handle_status) {
                    questionStatus = "0";
                } else if (checkedId == R.id.rb_online_pop_already_handle_status) {
                    questionStatus = "1";
                } else if (checkedId == R.id.rb_online_pop_no_handle_status) {
                    questionStatus = "2";
                }
            }
        });
        rg_online_pop_all_summary.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_online_pop_all_summary) {
                    summaryStatus = "0";
                } else if (checkedId == R.id.rb_online_pop_already_summary) {
                    summaryStatus = "1";
                } else if (checkedId == R.id.rb_online_pop_no_summary) {
                    summaryStatus = "2";
                }
            }
        });
        rg_online_pop_all_mark.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_online_pop_all_mark) {
                    ismark = "0";
                } else if (checkedId == R.id.rb_online_pop_already_mark) {
                    ismark = "1";
                } else if (checkedId == R.id.rb_online_pop_no_mark) {
                    ismark = "2";
                }
            }
        });
        rg_online_pop_reception.clearCheck();
        if (receiveType == 0) {
            rb_online_pop_my_reception.setChecked(true);
        } else {
            rb_online_pop_all_reception.setChecked(true);
        }

        rg_online_pop_all_mark.clearCheck();
        if ("0".equals(ismark)) {
            rb_online_pop_all_mark.setChecked(true);
        } else if ("1".equals(ismark)) {
            rb_online_pop_already_mark.setChecked(true);
        } else if ("2".equals(ismark)) {
            rb_online_pop_no_mark.setChecked(true);
        }

        rg_online_pop_all_summary.clearCheck();
        if ("0".equals(summaryStatus)) {
            rb_online_pop_all_summary.setChecked(true);
        } else if ("1".equals(summaryStatus)) {
            rb_online_pop_already_summary.setChecked(true);
        } else if ("2".equals(summaryStatus)) {
            rb_online_pop_no_summary.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void initData() {
        isChatOrUser = getIntent().getIntExtra("isChatOrUser", 0);
        SobotOnlineLogUtils.i("isChatOrUser-------" + isChatOrUser);
        if (isChatOrUser == 0) {
            tv_online_pop_summary.setVisibility(View.VISIBLE);
            rg_online_pop_all_summary.setVisibility(View.VISIBLE);
            tv_online_pop_mark.setVisibility(View.GONE);
            rg_online_pop_all_mark.setVisibility(View.GONE);
            tv_online_pop_handle_status.setVisibility(View.GONE);
            rg_online_pop_all_handle_status.setVisibility(View.GONE);
        } else if (isChatOrUser == 1) {
            tv_online_pop_summary.setVisibility(View.GONE);
            tv_online_pop_mark.setVisibility(View.VISIBLE);
            rg_online_pop_all_summary.setVisibility(View.GONE);
            rg_online_pop_all_mark.setVisibility(View.VISIBLE);
            tv_online_pop_handle_status.setVisibility(View.GONE);
            rg_online_pop_all_handle_status.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_online_pop_header_cancle) {
            finish();
        }
        if (v.getId() == R.id.tv_sobot_online_save) {
            Intent intent = new Intent();
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("receiveType", receiveType);
            intent.putExtra("ismark", ismark);
            intent.putExtra("summaryStatus", summaryStatus);
            intent.putExtra("questionStatus", questionStatus);
            intent.putExtra("isChatOrUser", isChatOrUser);
            SobotOnlineLogUtils.i("--startDate--" + startDate + "--endDate--" + endDate + "--receiveType--" + receiveType + "--ismark--" + ismark + "--summaryStatus-" + summaryStatus);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}