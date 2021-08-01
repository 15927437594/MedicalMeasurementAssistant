package cn.com.medicalmeasurementassistant.utils;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class MeasurementFileUtils {


    /**
     * 保存文件
     *
     * @param content
     */
    public synchronized static void saveMeasurementFile(String content) {
        // 保存log到文件
        String storePath = PathUtils.getMeasurementDataPath() + "/" + TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd"));
        String fileName = TimeUtils.getNowString(new SimpleDateFormat("HH_mm_ss")) + ".txt";
//        boolean orExistsFile = FileUtils.createOrExistsFile(storePath);
        File file = new File(storePath, fileName);
        saveMeasurementFile(file, content);
    }

    public synchronized static void saveMeasurementFile(File file, String content) {
//        if (file == null) {
//            return;
//        }
//        if (file.exists()) {
//            addTxtToFileWrite(file, content);
//            return;
//        }
//
//        boolean newFile = file.mkdirs();
//        if (newFile)
//            addTxtToFileWrite(file, content);
//        //
        boolean orExistsFile = FileUtils.createOrExistsFile(file);
        if (orExistsFile) {
            addTxtToFileWrite(file, content);
        }
    }

    /**
     * 使用FileWriter进行文本内容的追加
     *
     * @param file    file
     * @param content content
     */
    private static void addTxtToFileWrite(File file, String content) {
        FileWriter writer = null;
        try {
            //FileWriter(file, true),第二个参数为true是追加内容，false是覆盖
            writer = new FileWriter(file, true);
            writer.write("\r\n");//换行
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}