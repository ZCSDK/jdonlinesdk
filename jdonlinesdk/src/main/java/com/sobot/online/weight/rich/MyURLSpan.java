package com.sobot.online.weight.rich;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;

import com.sobot.online.OnlineOption;
import com.sobot.online.activity.OnlineWebViewActivity;
import com.sobot.onlinecommon.utils.SobotOnlineLogUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyURLSpan extends URLSpan {

    private Context context;
    private int color;
    private boolean isShowLine;// 下划线

    public MyURLSpan(Context context, String url, int color) {
        this(context, url, color, false);
    }

    public MyURLSpan(Context context, String url, int color, boolean isShowLine) {
        super(url);
        this.context = context;
        this.color = context.getResources().getColor(color);
        this.isShowLine = isShowLine;
    }


    @Override
    public void onClick(View widget) {
        String url = getURL();
//        LogUtils.i("url:" + url);
        if (url.startsWith("sobot:")) {
            //不是超链接  而是自己内部的东西  例如留言
            if ("sobot:Sobot".equals(url)) {
//                sendLocalBroadcast(context, intent);
            }
        } else {
            if (url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls")
                    || url.endsWith(".txt") || url.endsWith(".ppt") || url.endsWith(".pptx")
                    || url.endsWith(".xlsx") || url.endsWith(".pdf") || url.endsWith(".rar")
                    || url.endsWith(".zip")) {// 内部浏览器不支持，所以打开外部
                if (OnlineOption.newHyperlinkListener != null) {
                    //如果返回true,拦截;false 不拦截
                    boolean isIntercept = OnlineOption.newHyperlinkListener.onUrlClick(url);
                    if (isIntercept) {
                        return;
                    }

                }
                url = fixUrl(url);
                // 外部浏览器
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri content = Uri.parse(url);
                intent.setData(content);
                context.startActivity(intent);
            } else if (url.startsWith("tel:")) {
                if (OnlineOption.newHyperlinkListener != null) {
                    boolean isIntercept = OnlineOption.newHyperlinkListener.onPhoneClick(url);
                    if (isIntercept) {
                        return;
                    }
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));// mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                context.startActivity(intent);
            } else {
                if (OnlineOption.newHyperlinkListener != null) {
                    //如果返回true,拦截;false 不拦截
                    boolean isIntercept = OnlineOption.newHyperlinkListener.onUrlClick(url);
                    if (isIntercept) {
                        return;
                    }
                }
                if (url.contains("https://maps.google.com")) {
                    String q = getValueFromUrl(url, "q");
                    if (!TextUtils.isEmpty(q)) {
                        Uri gmmIntentUri = Uri.parse("geo:" + q + ("?z=18&q=" + q));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mapIntent.addCategory("android.intent.category.DEFAULT");
                        mapIntent.setPackage("com.google.android.apps.maps");
                        try {
                            context.startActivity(mapIntent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(context, OnlineWebViewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("url", url);
                            context.startActivity(intent);
                        }
                    }
                } else if (url.contains("goo.gl")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory("android.intent.category.DEFAULT");
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // 处理未安装浏览器应用的情况
                        Intent intent = new Intent(context, OnlineWebViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("url", url);
                        context.startActivity(intent);
                    }
                } else {
                    url = fixUrl(url);
                    Intent intent = new Intent(context, OnlineWebViewActivity.class);
                    intent.putExtra("url", url);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }
    }

    private String getValueFromUrl(String url, String key) {
        if (TextUtils.isEmpty(key)) {
            return "";
        }
        Map<String, String> map = getUrlPramNameAndValue(url);
        if (map != null) {
            for (Map.Entry<String, String> entry :
                    map.entrySet()) {
                if (key.equals(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return "";
    }

    /**
     * 获取URL中的参数名和参数值的Map集合
     *
     * @param url
     * @return
     */
    private Map<String, String> getUrlPramNameAndValue(String url) {
        String regEx = "(\\?|&+)(.+?)=([^&]*)";//匹配参数名和参数值的正则表达式
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        // LinkedHashMap是有序的Map集合，遍历时会按照加入的顺序遍历输出
        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        while (m.find()) {
            String paramName = m.group(2);//获取参数名
            String paramVal = m.group(3);//获取参数值
            paramMap.put(paramName, paramVal);
        }
        return paramMap;
    }

    private String fixUrl(String url) {
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = "https://" + url;
            SobotOnlineLogUtils.i("url:" + url);
        }
        return url;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(isShowLine);
    }

    /**
     * 发送应用内广播
     *
     * @param context
     * @param intent
     */
    public static void sendLocalBroadcast(Context context, Intent intent) {
        String packageName = context.getPackageName();
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);
        }
        context.sendBroadcast(intent);
    }
}