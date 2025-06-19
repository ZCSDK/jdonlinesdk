package com.sobot.online.model;


//whatspp 模板名字 枚举
public enum SobotTemplateName {
    LOCATION_CONFIRM_NONCOD("location_confirm_noncod"),
    LOCATION_CONFIRM_COD("location_confirm_cod"),
    TIME_REMINDER("time_reminder"),
    LOCATION_REMINDER("location_reminder"),
    REACH_REMINDER("reach_reminder"),
    REQUEST_REPLY("request_reply"),
    COD_CONFIRM("cod_confirm");

    private String templateName;

    SobotTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
