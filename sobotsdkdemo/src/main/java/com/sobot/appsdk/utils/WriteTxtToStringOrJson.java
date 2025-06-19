package com.sobot.appsdk.utils;

import java.util.List;

public class WriteTxtToStringOrJson {
    public static void main(String[] args) {

        String path = "/Users/znw/Documents/sobot_app_sdk_project/app_yuyanbao/";
        String fileName = path + "sobotlocalizable_key.txt";
        List<String> arr = ReadFile.toArrayByFileReader(fileName);

//        writeJsonString(path, arr, "zh-Hans", "1.0.0");//简体中文
//        writeJsonString(path, arr, "zh-rtw", "1.0.0");//繁体中文

        writeXmlString(path, arr, "zh-rtw");//繁体中文
        writeXmlString(path, arr, "en");//繁体中文
    }


//    public static void writeString(String path, List<String> arr, String lproj) {
//        String fileName = path + "SobotLocalizable_" + lproj + ".txt";
//        StringBuffer sb = new StringBuffer();
//        List<String> arr_en = ReadFile.toArrayByFileReader(fileName);
//        for (int i = 0; i < arr.size(); i++) {
//            sb.append("\"" + arr.get(i) + "\" = \"" + arr_en.get(i) + "\";\n");
//        }
//        fileName = path + lproj + "_lproj/SobotLocalizable.strings";
//        System.out.println(sb.toString());
//        WriteFile.writeStringToFile(fileName, sb.toString(), true);
//    }


    public static void writeJsonString(String path, List<String> arr, String lproj, String sdkversion) {
        String fileName = path + "sobotlocalizable_value_" + lproj + ".txt";
        StringBuffer sb = new StringBuffer();
        List<String> arr_en = ReadFile.toArrayByFileReader(fileName);
        sb.append("{");
        for (int i = 0; i < arr.size(); i++) {
            if (i == (arr.size() - 1)) {
                sb.append("\"" + arr.get(i) + "\":\"" + arr_en.get(i) + "\"");
            } else {
                sb.append("\"" + arr.get(i) + "\":\"" + arr_en.get(i) + "\",");
            }
        }
        sb.append("}");
        fileName = path + "/2.9.0/sobot_android_strings_" + lproj + "_" + sdkversion + ".json";
        System.out.println(sb.toString());
        WriteFile.writeStringToFile(fileName, sb.toString(), true);
    }


    public static void writeXmlString(String path, List<String> arr, String lproj) {
        String fileName = path + "sobotlocalizable_value_" + lproj + ".txt";
        StringBuffer sb = new StringBuffer();
        List<String> arr_en = ReadFile.toArrayByFileReader(fileName);
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        for (int i = 0; i < arr.size(); i++) {
            sb.append("       <string name=\"" + arr.get(i) + "\">" + arr_en.get(i) + "</string>\n");

        }
        sb.append("</resources>");
        fileName = path + "values-" + lproj + "/strings.xml";
        System.out.println(sb.toString());
        WriteFile.writeStringToFile(fileName, sb.toString(), true);
    }
}
