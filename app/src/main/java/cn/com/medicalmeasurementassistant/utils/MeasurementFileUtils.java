package cn.com.medicalmeasurementassistant.utils;

import com.blankj.utilcode.util.FileIOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MeasurementFileUtils {


//    /**
//     * 保存文件
//     *
//     * @param content
//     */
//    public synchronized static void saveMeasurementFile(String content) {
//        saveMeasurementFile(null, content);
//    }

    public synchronized static void saveMeasurementFile(String fileName, List<Float> originalData, List<Float> filterData) {
        String originalFileName = fileName + "-original.csv";
        String filterFileName = fileName + "-filter.csv";
        // 保存内容到文件
        String storePath = PathUtils.getMeasurementDataPath();
        LogUtils.i("storePath=" + storePath);
        LogUtils.i("originalDataSize=" + originalData.size());
        LogUtils.i("filterDataSize=" + filterData.size());
        File originalFile = new File(storePath, originalFileName);
        File filterFile = new File(storePath, filterFileName);
        saveMeasurementFileForFile(originalFile, originalData);
        saveMeasurementFileForFile(filterFile, filterData);
    }

    public synchronized static void saveMeasurementFileForFile(File file, List<Float> data) {
        FileIOUtils.writeFileFromString(file, data.toString());
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
