package com.sobot.appsdk.utils;

import java.util.List;

public class WriteTxtFromXml {
    public static void main(String[] args) {

        String path = "/Users/znw/Documents/sobot_app_sdk_project/app_yuyanbao/strings.xml";
        List<String> arr = ReadFile.toArrayByFileReader(path);

        writeXmlString(arr);
    }

    public static void writeXmlString(List<String> arr) {
        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < arr.size(); i++) {
//            String s1 = arr.get(i);
//            String[] arr1 = s1.split("\"");
//            if (arr1.length > 2) {
//                //打印string.xml 的key
//                System.out.println(arr1[1]);
//            }
//        }

        for (int i = 0; i < arr.size(); i++) {
            String s1 = arr.get(i);
            String[] arr1 = s1.split("\">");
            if (arr1.length > 1) {
                String[] arr2 = arr1[1].split("<");
                //打印string.xml 的value
                System.out.println(arr2[0]);
            }
        }

    }
}
