package com.sobot.online.util;

import android.content.Context;
import android.text.TextUtils;

import com.sobot.online.R;

public class SobotFileTypeUtil {

//    文件类型：
//    DOC(0,"doc"),
//    PPT(1,"ppt"),
//    XLS(2,"xls"),
//    PDF(3,"pdf"),
//    MP3(4,"mp3"),
//    MP4(5,"mp4"),
//    RAR(6,"rar"),
//    TXT(7,"txt"),
//    OTHER(8,"other");

    public static final int MSGTYPE_FILE_DOC = 0;
    public static final int MSGTYPE_FILE_PPT = 1;
    public static final int MSGTYPE_FILE_XLS = 2;
    public static final int MSGTYPE_FILE_PDF = 3;
    public static final int MSGTYPE_FILE_MP3 = 4;
    public static final int MSGTYPE_FILE_MP4 = 5;
    public static final int MSGTYPE_FILE_RAR = 6;
    public static final int MSGTYPE_FILE_TXT = 7;
    public static final int MSGTYPE_FILE_OTHER = 8;


    public static int getFileType(String endWith) {
        if (TextUtils.isEmpty(endWith)) {
            return MSGTYPE_FILE_OTHER;
        }
        switch (endWith) {
            case "zip":
            case "rar":
                return MSGTYPE_FILE_RAR;
            case "doc":
            case "docx":
                return MSGTYPE_FILE_DOC;
            case "ppt":
            case "pptx":
                return MSGTYPE_FILE_PPT;
            case "xls":
            case "xlsx":
                return MSGTYPE_FILE_XLS;
            case "pdf":
                return MSGTYPE_FILE_PDF;
            case "mp3":
                return MSGTYPE_FILE_MP3;
            case "mp4":
                return MSGTYPE_FILE_MP4;
            case "txt":
                return MSGTYPE_FILE_TXT;
            default:
                return MSGTYPE_FILE_OTHER;
        }

    }

    /**
     * 根据文件类型获取文件icon
     *
     * @param context
     * @param fileType
     * @return
     */
    public static int getFileIcon(Context context, int fileType) {
        int tmpResId;
        if (context == null) {
            return 0;
        }
        switch (fileType) {
            case MSGTYPE_FILE_DOC:
                tmpResId = R.drawable.sobot_icon_file_doc;
                break;
            case MSGTYPE_FILE_PPT:
                tmpResId = R.drawable.sobot_icon_file_ppt;
                break;
            case MSGTYPE_FILE_XLS:
                tmpResId = R.drawable.sobot_icon_file_xls;
                break;
            case MSGTYPE_FILE_PDF:
                tmpResId = R.drawable.sobot_icon_file_pdf;
                break;
            case MSGTYPE_FILE_MP3:
                tmpResId = R.drawable.sobot_icon_file_mp3;
                break;
            case MSGTYPE_FILE_MP4:
                tmpResId = R.drawable.sobot_icon_file_mp4;
                break;
            case MSGTYPE_FILE_RAR:
                tmpResId = R.drawable.sobot_icon_file_rar;
                break;
            case MSGTYPE_FILE_TXT:
                tmpResId = R.drawable.sobot_icon_file_txt;
                break;
            case MSGTYPE_FILE_OTHER:
                tmpResId = R.drawable.sobot_icon_file_unknow;
                break;
            default:
                tmpResId = R.drawable.sobot_icon_file_unknow;
                break;
        }
        return tmpResId;
    }

}
