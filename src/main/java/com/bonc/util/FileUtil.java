package com.bonc.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * 监控一个文件一定时间内是否不再变化
     *
     * @param dataFile
     * @param minute
     * @throws Exception
     */
    public static void monitorFile(File dataFile, int minute) throws Exception {

        final int waitingTime = minute * 60 * 1000;
        long fileSizeOld = 0;
        //文件更新的最后时刻
        long time1 = System.currentTimeMillis();

        long time2;

        while (true) {
            //取得文件大小
            long fileSizeNow = getFileSize(dataFile);
            boolean fileUpdate = fileSizeNow > fileSizeOld;
            if (fileUpdate) {
                fileSizeOld = fileSizeNow;
                time1 = System.currentTimeMillis();
            } else {
                time2 = System.currentTimeMillis();
                //文件到一定时间无变化
                if (time2 - time1 > waitingTime) {
                    countFile(dataFile, fileSizeNow);
                }
            }
            Thread.sleep(1000);
        }
    }

    /**
     * 输出文件行数，退出程序
     * @param dataFile
     * @param fileSizeNow
     * @throws IOException
     */
    private static void countFile(File dataFile, long fileSizeNow) throws IOException {
        if (fileSizeNow > 0) {
            String fileStr = FileUtils.readFileToString(dataFile);
            String[] lines = fileStr.split("\n");
            System.out.println(lines.length + " messages in queue");
        } else {
            System.out.println("No message in queue!");
        }
        System.exit(0);
    }

    /**
     * 获取文件大小
     *
     * @param file
     */
    public static long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return 0;
        }
    }
}
