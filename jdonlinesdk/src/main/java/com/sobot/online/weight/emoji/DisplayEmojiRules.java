package com.sobot.online.weight.emoji;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 65个 Emoji
 * 参考 https://apps.timwhitlock.info/unicode/inspect?s=%F0%9F%98%83%20%F0%9F%98%84%20%F0%9F%98%81%20%F0%9F%98%86%20%F0%9F%98%85%20%F0%9F%A4%A3%20%F0%9F%98%82%20%F0%9F%99%82%20%F0%9F%98%89%20%F0%9F%98%8A%20%F0%9F%98%87%20%F0%9F%98%8D%20%F0%9F%A4%A9%20%F0%9F%98%98%20%F0%9F%98%9A%20%F0%9F%98%99%20%F0%9F%98%8B%20%F0%9F%98%9C%20%F0%9F%98%9D%20%F0%9F%A4%97%20%F0%9F%A4%AD%20%F0%9F%A4%94%20%F0%9F%A4%90%20%F0%9F%98%91%20%F0%9F%98%8F%20%F0%9F%98%92%20%F0%9F%98%8C%20%F0%9F%98%94%20%F0%9F%98%B7%20%F0%9F%A4%92%20%F0%9F%98%B5%20%F0%9F%A4%A0%20%F0%9F%98%8E%20%F0%9F%A4%93%20%F0%9F%98%B3%20%F0%9F%98%A8%20%F0%9F%98%B0%20%F0%9F%98%A5%20%F0%9F%98%A2%20%F0%9F%98%AD%20%F0%9F%98%B1%20%F0%9F%98%96%20%F0%9F%98%A3%20%F0%9F%98%93%20%F0%9F%98%A0%20%F0%9F%91%8B%20%F0%9F%91%8C%20%E2%9C%8C%20%F0%9F%A4%9F%20%F0%9F%91%8D%20%F0%9F%91%8F%20%F0%9F%A4%9D%20%F0%9F%99%8F%20%F0%9F%92%AA%20%F0%9F%99%87%20%F0%9F%90%AE%20%F0%9F%8C%B9%20%F0%9F%A5%80%20%F0%9F%92%8B%20%E2%9D%A4%EF%B8%8F%20%F0%9F%92%94%20%E2%AD%90%20%F0%9F%8E%89%20%F0%9F%8D%BA%20%F0%9F%8E%81
 */
public enum DisplayEmojiRules {
    QQBIAOQING0("\uD83D\uDE03", "SMILING FACE WITH OPEN MOUTH"),
    QQBIAOQING1("\uD83D\uDE04", "SPACE"),
    QQBIAOQING2("\uD83D\uDE01", "SMILING FACE WITH OPEN MOUTH AND SMILING EYES"),
    QQBIAOQING3("\uD83D\uDE06", "GRINNING FACE WITH SMILING EYES"),
    QQBIAOQING4("\uD83D\uDE05", "SMILING FACE WITH OPEN MOUTH AND TIGHTLY-CLOSED EYES"),
    QQBIAOQING5("\uD83D\uDE02", "SMILING FACE WITH OPEN MOUTH AND COLD SWEAT"),
    QQBIAOQING55("\uD83D\uDE09", "SMILING FACE WITH OPEN MOUTH AND COLD SWEAT"),
    QQBIAOQING6("\uD83D\uDE0A", "ROLLING ON THE FLOOR LAUGHING"),
    QQBIAOQING7("\uD83D\uDE07", "FACE WITH TEARS OF JOY"),
    QQBIAOQING8("\uD83D\uDE0D", "SLIGHTLY SMILING FACE"),

    QQBIAOQING9("\uD83D\uDE18", "WINKING FACE"),
    QQBIAOQING10("\uD83D\uDE1A", "SMILING FACE WITH SMILING EYES"),
    QQBIAOQING11("\uD83D\uDE19", "SMILING FACE WITH HALO"),
    QQBIAOQING12("\uD83D\uDE0B", "SMILING FACE WITH HEART-SHAPED EYES"),
    QQBIAOQING13("\uD83D\uDE1C", "GRINNING FACE WITH STAR EYES"),
    QQBIAOQING14("\uD83D\uDE1D", "FACE THROWING A KISS"),
    QQBIAOQING15("\uD83D\uDE11", "KISSING FACE WITH CLOSED EYES"),
    QQBIAOQING16("\uD83D\uDE0F", "KISSING FACE WITH SMILING EYES"),
    QQBIAOQING17("\uD83D\uDE12", "FACE SAVOURING DELICIOUS FOOD"),

    QQBIAOQING18("\uD83D\uDE0C", "FACE WITH STUCK-OUT TONGUE AND WINKING EYE"),
    QQBIAOQING19("\uD83D\uDE14", "FACE WITH STUCK-OUT TONGUE AND TIGHTLY-CLOSED EYES"),
    QQBIAOQING20("\uD83D\uDE37", "HUGGING FACE"),
    QQBIAOQING21("\uD83D\uDE35", "SMILING FACE WITH SMILING EYES AND HAND COVERING MOUTH"),
    QQBIAOQING22("\uD83D\uDE0E", "THINKING FACE"),
    QQBIAOQING23("\uD83D\uDE33", "ZIPPER-MOUTH FACE"),
    QQBIAOQING24("\uD83D\uDE28", "EXPRESSIONLESS FACE"),
    QQBIAOQING25("\uD83D\uDE30", "SMIRKING FACE"),
    QQBIAOQING26("\uD83D\uDE25", "UNAMUSED FACE"),

    QQBIAOQING27("\uD83D\uDE22", "RELIEVED FACE"),
    QQBIAOQING28("\uD83D\uDE2D", "PENSIVE FACE"),
    QQBIAOQING29("\uD83D\uDE31", "FACE WITH MEDICAL MASK"),
    QQBIAOQING30("\uD83D\uDE16", "FACE WITH THERMOMETER"),
    QQBIAOQING31("\uD83D\uDE23", "DIZZY FACE"),
    QQBIAOQING32("\uD83D\uDE13", "FACE WITH COWBOY HAT"),
    QQBIAOQING33("\uD83D\uDE20", "SMILING FACE WITH SUNGLASSES"),
    QQBIAOQING34("\uD83D\uDC4B", "NERD FACE"),
    QQBIAOQING35("\uD83D\uDC4C", "FLUSHED FACE"),

    QQBIAOQING36("✊", "FACE WITH OPEN MOUTH AND COLD SWEAT"),
    QQBIAOQING37("\uD83D\uDC4D", "DISAPPOINTED BUT RELIEVED FACE"),
    QQBIAOQING38("\uD83D\uDC4F", "CRYING FACE"),
    QQBIAOQING39("\uD83D\uDE4F", "LOUDLY CRYING FACE"),
    QQBIAOQING40("\uD83D\uDCAA", "FACE SCREAMING IN FEAR"),
    QQBIAOQING41("\uD83D\uDE47", "CONFOUNDED FACE"),
    QQBIAOQING42("\uD83D\uDC2E", "PERSEVERING FACE"),
    QQBIAOQING43("\uD83C\uDF39", "FACE WITH COLD SWEAT"),
    QQBIAOQING44("\uD83D\uDC8B", "ANGRY FACE"),

    QQBIAOQING45("\uD83D\uDC94", "WAVING HAND SIGN"),
    QQBIAOQING46("⭐", "OK HAND SIGN"),
    QQBIAOQING47("\uD83C\uDF89", "VICTORY HAND"),
    QQBIAOQING48("\uD83C\uDF7A", "I LOVE YOU HAND SIGN"),
    QQBIAOQING49("\uD83C\uDF81", "THUMBS UP SIGN");

    /*********************************
     * 操作
     **************************************/
    private String emojiCode;
    private String emojiDes;
    private static Map<String, String> sEmojiMap;

    private DisplayEmojiRules(String emojiCode, String emojiDes) {
        this.emojiDes = emojiDes;
        this.emojiCode = emojiCode;
    }

    public String getEmojiDes() {
        return emojiDes;
    }

    public void setEmojiDes(String emojiDes) {
        this.emojiDes = emojiDes;
    }

    public String getEmojiCode() {
        return emojiCode;
    }

    public void setEmojiCode(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    /**
     * 提高效率，忽略线程安全
     */
    public static Map<String, String> getMapAll(Context context) {
        if (sEmojiMap == null) {
            sEmojiMap = new HashMap<>();
            for (DisplayEmojiRules data : values()) {
                sEmojiMap.put(data.getEmojiCode(), data.getEmojiDes());
            }
        }
        return sEmojiMap;
    }

    public static ArrayList<EmojiconNew> getListAll(Context context) {
        ArrayList<EmojiconNew> sEmojiList = new ArrayList<>();
        for (DisplayEmojiRules data : values()) {
            sEmojiList.add(new EmojiconNew(data.getEmojiCode(), data.getEmojiDes()));
        }
        return sEmojiList;
    }
}