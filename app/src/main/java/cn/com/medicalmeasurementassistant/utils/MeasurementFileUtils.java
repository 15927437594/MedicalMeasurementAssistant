package cn.com.medicalmeasurementassistant.utils;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.blankj.utilcode.util.FileIOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import cn.com.medicalmeasurementassistant.entity.Constant;

public class MeasurementFileUtils {

    public synchronized static void saveMeasurementFile(String fileName, List<String> originalData, List<String> filterData) {
        String originalFileName = fileName + "-original.csv";
        String filterFileName = fileName + "-filter.csv";
        // 保存内容到文件
        String storePath = PathUtils.getMeasurementDataPath();
        LogUtils.i("storePath=" + storePath);
        LogUtils.i("originalDataSize=" + originalData.size());
        LogUtils.i("filterDataSize=" + filterData.size());
        File originalFile = new File(storePath, originalFileName);
        File filterFile = new File(storePath, filterFileName);
        String saveTime = "Saving DateTime: " + CalculateUtils.getTime() + "\n";
        String sampleFrequency = "Sampling frequency: 2000" + "\n";
        String channels = "Number of channels: " + Constant.DEFAULT_CHANNEL + "\n";
        String dataNumber = "Number of data: " + originalData.size() + "\n";
        String unit = "Unit: mV" + "\n";
        String title = "CH1,CH2,CH3,CH4,CH5,CH6,CH7,CH8,Capacitor/pF\n";
        String originContent = originalData.toString().replace("[", "").replace("]", "").replace(" ", "")
                .replace("\n,", "\n");
        String filterContent = filterData.toString().replace("[", "").replace("]", "").replace(" ", "")
                .replace("\n,", "\n");
        String originContentWrite = saveTime + sampleFrequency + channels + dataNumber + unit + title + originContent;
        String filterContentWrite = saveTime + sampleFrequency + channels + dataNumber + unit + title + filterContent;
        saveMeasurementFileForFile(originalFile, originContentWrite);
        saveMeasurementFileForFile(filterFile, filterContentWrite);
    }

    public synchronized static void saveMeasurementFileForFile(File file, String content) {
        FileIOUtils.writeFileFromString(file, content);
    }

    public void saveToCSVFile(Context context, Uri uri) {
        StringBuilder d = new StringBuilder();
        d.append("col1,col2,col3\n");
        d.append("1,2,3\n");
        d.append("2,3,4\n");
        String data = d.toString();

// Get a file output stream however is compatible with your app. Showing one of
// many ways here.
// once you have it, call outputStream.write(data.getBytes());
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "w");
            if (pfd != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                fileOutputStream.write(data.getBytes());
                fileOutputStream.close();
                pfd.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
