package com.zlyandroid.mycameraview.utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangliyang
 * dec   文件
 * data 2020/05/26
 *
 */
public class FileUtils {

    /**
     * @param customPath 自定义路径
     * @return 程序系统文件目录绝对路径
     */
    public static String getFileDir( String customPath) {
        String path =  formatPath(customPath);
        mkdir(path);
        return path;
    }

    /**
     * 创建文件夹
     *
     * @param DirPath 文件夹路径
     */
    public static void mkdir(String DirPath) {
        File file = new File(DirPath);
        if (!(file.exists() && file.isDirectory())) {
            file.mkdirs();
        }
    }
    /**
     * 删除文件夹
     *
     * @param DirPath 文件夹路径
     */
    public static boolean delete(File DirPath) {
        if (DirPath == null) {
            return false;
        }
        if (DirPath.isDirectory()) {
            String[] children = DirPath.list();
            for (String c : children) {
                boolean success = delete(new File(DirPath, c));
                if (!success) {
                    return false;
                }
            }
        }
        return DirPath.delete();
    }
    /**
     * 取文件夹下的所有子文件
     *
     * @param path 文件夹路径
     */
    public static List<String> getFilesAllName(String path) {
        List<String> s = new ArrayList<>();
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null) {
            Log.e("error","空目录");
            return s;
        }

        for(int i =0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }
    /**
     * 格式化文件路径
     * 示例：  传入 "sloop" "/sloop" "sloop/" "/sloop/"
     * 返回 "/sloop"
     */
    private static String formatPath(String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        while (path.endsWith("/"))
            path = new String(path.toCharArray(), 0, path.length() - 1);
        return path;
    }
}
