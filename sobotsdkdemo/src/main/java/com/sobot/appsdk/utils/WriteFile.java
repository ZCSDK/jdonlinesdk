package com.sobot.appsdk.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {
    /**
     * 写入content到fileName
     *
     * @param fileName 文件路径
     * @param content  写入的内容
     * @param append   是否为append
     */
    public static void writeStringToFile(String fileName, String content, boolean append) {
        try {
            checkFilePath(fileName);
            //使用true，即进行append file
            FileWriter fileWritter = new FileWriter(fileName, append);
            fileWritter.write(content);
            fileWritter.close();

            System.out.println("finish");

        } catch (IOException e) {

            e.printStackTrace();

        }
    }


    public static void checkFilePath(String fileName) {


        File file = new File(fileName.substring(0, fileName.lastIndexOf("/") + 1));

        if (!file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(fileName);

        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
