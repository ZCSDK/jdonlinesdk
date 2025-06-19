package com.sobot.online.weight.emoji;

/**
 * @author kymjs (http://www.kymjs.com)
 */
public class Emojicon {
    private final String resName; // 图片资源名称
    private final int value; // 一个emoji对应唯一一个value
    private final String emojiStr; // emoji在互联网传递的字符串
    private final String remote;
    private final int resId;

    public Emojicon(String resName, int value, String name, String remote, int resId) {
        this.resName = resName;
        this.value = value;
        this.emojiStr = name;
        this.remote = remote;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public String getResName() {
        return resName;
    }

    public String getRemote() {
        return remote;
    }

    public int getValue() {
        return value;
    }

    public String getEmojiStr() {
        return emojiStr;
    }
}