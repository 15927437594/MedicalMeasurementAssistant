package cn.com.medicalmeasurementassistant.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * user: Created by jid on 2020/11/3
 * email: jid@hwtc.com.cn
 * description:
 */
public class FileUtils {

    /**
     * 根据文件路径 递归创建文件
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            boolean mkdirs = parent.mkdirs();
            try {
                boolean newFile = file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param fileDir 文件夹路径
     * @return 该文件夹下的所有文件
     */
    public static List<String> GetLogFileName(String fileDir) {
        List<String> pathList = new ArrayList<>();
        File file = new File(fileDir);
        File[] subFiles = file.listFiles();

        if (subFiles == null) {
            LogUtils.w("subFiles==null");
            return null;
        }

        for (File subFile : subFiles) {
            // 判断是否为文件夹
            if (!subFile.isDirectory()) {
                String filename = subFile.getName();
                // 判断是否为log结尾
                if (filename.trim().toLowerCase().endsWith(".log")) {
                    pathList.add(filename);
                }
            }
        }
        return pathList;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        return dirFile.delete();
    }

    // 判断是否具有ROOT权限
    public static boolean isRoot() {
        boolean res = false;
        try {
            res = (new File("/system/bin/su").exists()) || (new File("/system/xbin/su").exists());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 文件设置最高权限 777 可读 可写 可执行
     *
     * @param file 文件
     * @return 权限修改是否成功
     */
    public static boolean chmod777(File file) {
        if (null == file || !file.exists()) {
            // 文件不存在
            return false;
        }
        try {
            // 获取ROOT权限
            Process su = Runtime.getRuntime().exec("/system/xbin/su");
            // 修改文件属性为 [可读 可写 可执行]
            String cmd = "chmod 777 " + file.getAbsolutePath() + "\n" + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
            if (0 == su.waitFor() && file.canRead() && file.canWrite() && file.canExecute()) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            // 没有ROOT权限
            e.printStackTrace();
        }
        return false;
    }

    public static String getFileSignature(File file, String type) {
        if (!file.exists()) {
            LogUtils.w("File doesn't exist!");
            return "";
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(type);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.e("Exception while getting FileInputStream");
            return "";
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e("Unable to process file for ");
            return "";
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("Exception on closing inputStream:");
            }
        }
    }

    public static byte[] readFileToByteArray(String path) {
        File file = new File(path);
        if (!file.exists()) {
            LogUtils.w("File doesn't exist!");
            return null;
        }
        FileInputStream in;
        try {

            in = new FileInputStream(file);
            long inSize = in.getChannel().size();//判断FileInputStream中是否有内容
            if (inSize == 0) {
                LogUtils.d("The FileInputStream has no content!");
                return null;
            }
            //in.available() 表示要读取的文件中的数据长度
            byte[] buffer = new byte[in.available()];
            //将文件中的数据读到buffer中
            int read = in.read(buffer);
            LogUtils.d("read=" + read);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.e("文件不存在!");
        }
        return size;
    }

    public static long getCRC32(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes, 0, bytes.length);
        return crc.getValue();
    }

    public static void writeInternal(String filePathName, String content) {
        File file = new File(filePathName);
        if (file.exists()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filePathName);
                byte[] bytes = content.getBytes();
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readInternal(String filePathName) {
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            FileInputStream fileInputStream = new FileInputStream(filePathName);
            byte[] buffer = new byte[1024];
            int len = fileInputStream.read(buffer);
            while (len > 0) {
                stringBuilder.append(new String(buffer, 0, len));
                len = fileInputStream.read(buffer);
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String getAssetsTxt(Context context, String path) {
        StringBuilder s = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String temp;
            while ((temp = br.readLine()) != null) {
                temp += "\n";
                s.append(temp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s.toString();
    }
}

