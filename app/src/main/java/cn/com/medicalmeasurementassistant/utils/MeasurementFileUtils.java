package cn.com.medicalmeasurementassistant.utils;

import com.blankj.utilcode.util.FileIOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import cn.com.medicalmeasurementassistant.entity.Constant;

public class MeasurementFileUtils {

    public synchronized static void saveMeasurementFile(String fileName, List<String> rawData, List<String> filteredData) {
        LogUtils.i("originalDataSize=" + rawData.size());
        LogUtils.i("filterDataSize=" + filteredData.size());
        // 保存内容到文件
        String storePath = PathUtils.getMeasurementDataPath();
        String saveTime = "Saving DateTime: " + CalculateUtils.getTime() + "\n";
        String sampleFrequency = "Sampling frequency: 2000" + "\n";
        String channels = "Number of channels: " + Constant.DEFAULT_CHANNEL + "\n";
        String unit = "Unit: mV" + "\n";
        String title = "CH1,CH2,CH3,CH4,CH5,CH6,CH7,CH8,Capacitor/pF\n";
        LogUtils.i("storePath=" + storePath);
        if (rawData.size() > 0) {
            String originalFileName = fileName + "-RawData.csv";
            File originalFile = new File(storePath, originalFileName);
            String originDataNumber = "Number of data: " + rawData.size() + "\n";
            String originContent = rawData.toString().replace("[", "").replace("]", "").replace(" ", "")
                    .replace("\n,", "\n");
            String originContentWrite = saveTime + sampleFrequency + channels + originDataNumber + unit + title + originContent;
            FileIOUtils.writeFileFromString(originalFile, originContentWrite);
        }

        if (filteredData.size() > 0) {
            String filterFileName = fileName + "-FilteredData.csv";
            File filterFile = new File(storePath, filterFileName);
            String filterDataNumber = "Number of data: " + filteredData.size() + "\n";
            String filterContent = filteredData.toString().replace("[", "").replace("]", "").replace(" ", "")
                    .replace("\n,", "\n");
            String filterContentWrite = saveTime + sampleFrequency + channels + filterDataNumber + unit + title + filterContent;
            FileIOUtils.writeFileFromString(filterFile, filterContentWrite);
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
