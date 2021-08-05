package cn.com.medicalmeasurementassistant.utils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MeasurementFileUtils {


    /**
     * 保存文件
     *
     * @param content
     */
    public synchronized static void saveMeasurementFile(String content) {
        saveMeasurementFile(content, null);
    }

    /**
     * 保存文件
     *
     * @param content
     */
    public synchronized static void saveMeasurementFile(String content, String fileName) {
        // 保存内容到文件
        String storePath = PathUtils.getMeasurementDataPath() + "/" + TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE));
        if (StringUtils.isEmpty(fileName)) {
            fileName = TimeUtils.getNowString(new SimpleDateFormat("HH_mm_ss", Locale.SIMPLIFIED_CHINESE)) + ".txt";
        } else {
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }
        }
        File file = new File(storePath, fileName);
        saveMeasurementFile(file, content);
    }

    public synchronized static void saveMeasurementFile(File file, String content) {
//        boolean orExistsFile = FileUtils.createOrExistsFile(file);
        FileIOUtils.writeFileFromString(file, content);
//        if (orExistsFile) {
//
//            addTxtToFileWrite(file, content);
//        }
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
