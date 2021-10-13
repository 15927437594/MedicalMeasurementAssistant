package cn.com.medicalmeasurementassistant.utils;

import java.util.ArrayList;
import java.util.List;


/**
 * user: Created by DuJi on 2021/8/25 21:00
 * email: 18571762595@163.com
 * description:
 */
public class CalculateUtils {
    private static final int[] crcTab16 = new int[]{
            0x0000, 0x1189, 0x2312, 0x329b, 0x4624, 0x57ad, 0x6536, 0x74bf,
            0x8c48, 0x9dc1, 0xaf5a, 0xbed3, 0xca6c, 0xdbe5, 0xe97e, 0xf8f7,
            0x1081, 0x0108, 0x3393, 0x221a, 0x56a5, 0x472c, 0x75b7, 0x643e,
            0x9cc9, 0x8d40, 0xbfdb, 0xae52, 0xdaed, 0xcb64, 0xf9ff, 0xe876,
            0x2102, 0x308b, 0x0210, 0x1399, 0x6726, 0x76af, 0x4434, 0x55bd,
            0xad4a, 0xbcc3, 0x8e58, 0x9fd1, 0xeb6e, 0xfae7, 0xc87c, 0xd9f5,
            0x3183, 0x200a, 0x1291, 0x0318, 0x77a7, 0x662e, 0x54b5, 0x453c,
            0xbdcb, 0xac42, 0x9ed9, 0x8f50, 0xfbef, 0xea66, 0xd8fd, 0xc974,
            0x4204, 0x538d, 0x6116, 0x709f, 0x0420, 0x15a9, 0x2732, 0x36bb,
            0xce4c, 0xdfc5, 0xed5e, 0xfcd7, 0x8868, 0x99e1, 0xab7a, 0xbaf3,
            0x5285, 0x430c, 0x7197, 0x601e, 0x14a1, 0x0528, 0x37b3, 0x263a,
            0xdecd, 0xcf44, 0xfddf, 0xec56, 0x98e9, 0x8960, 0xbbfb, 0xaa72,
            0x6306, 0x728f, 0x4014, 0x519d, 0x2522, 0x34ab, 0x0630, 0x17b9,
            0xef4e, 0xfec7, 0xcc5c, 0xddd5, 0xa96a, 0xb8e3, 0x8a78, 0x9bf1,
            0x7387, 0x620e, 0x5095, 0x411c, 0x35a3, 0x242a, 0x16b1, 0x0738,
            0xffcf, 0xee46, 0xdcdd, 0xcd54, 0xb9eb, 0xa862, 0x9af9, 0x8b70,
            0x8408, 0x9581, 0xa71a, 0xb693, 0xc22c, 0xd3a5, 0xe13e, 0xf0b7,
            0x0840, 0x19c9, 0x2b52, 0x3adb, 0x4e64, 0x5fed, 0x6d76, 0x7cff,
            0x9489, 0x8500, 0xb79b, 0xa612, 0xd2ad, 0xc324, 0xf1bf, 0xe036,
            0x18c1, 0x0948, 0x3bd3, 0x2a5a, 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e,
            0xa50a, 0xb483, 0x8618, 0x9791, 0xe32e, 0xf2a7, 0xc03c, 0xd1b5,
            0x2942, 0x38cb, 0x0a50, 0x1bd9, 0x6f66, 0x7eef, 0x4c74, 0x5dfd,
            0xb58b, 0xa402, 0x9699, 0x8710, 0xf3af, 0xe226, 0xd0bd, 0xc134,
            0x39c3, 0x284a, 0x1ad1, 0x0b58, 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c,
            0xc60c, 0xd785, 0xe51e, 0xf497, 0x8028, 0x91a1, 0xa33a, 0xb2b3,
            0x4a44, 0x5bcd, 0x6956, 0x78df, 0x0c60, 0x1de9, 0x2f72, 0x3efb,
            0xd68d, 0xc704, 0xf59f, 0xe416, 0x90a9, 0x8120, 0xb3bb, 0xa232,
            0x5ac5, 0x4b4c, 0x79d7, 0x685e, 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a,
            0xe70e, 0xf687, 0xc41c, 0xd595, 0xa12a, 0xb0a3, 0x8238, 0x93b1,
            0x6b46, 0x7acf, 0x4854, 0x59dd, 0x2d62, 0x3ceb, 0x0e70, 0x1ff9,
            0xf78f, 0xe606, 0xd49d, 0xc514, 0xb1ab, 0xa022, 0x92b9, 0x8330,
            0x7bc7, 0x6a4e, 0x58d5, 0x495c, 0x3de3, 0x2c6a, 0x1ef1, 0x0f78};

    public static List<Integer> intToHighLow(int src) {
        List<Integer> result = new ArrayList<>();
        int high = ((src >> 8) & 0xff);
        int low = (src & 0xff);
        result.add(high);
        result.add(low);
        return result;
    }

    public static List<Integer> longToFourByte(long src) {
        List<Integer> result = new ArrayList<>();
        int first = (int) ((src >> 24) & 0xff);
        int second = (int) ((src >> 16) & 0xff);
        int third = (int) ((src >> 8) & 0xff);
        int forth = (int) (src & 0xff);
        result.add(first);
        result.add(second);
        result.add(third);
        result.add(forth);
        return result;
    }

    /**
     * @param str 原始字符串
     * @return ASCII数组
     */
    public static List<Integer> iterableStrToAsciiArray(String str) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            list.add((int) c);
        }
        return list;
    }

    /**
     * @param asciiList 原始ASCII数组
     * @return ASCII数组
     */
    public static StringBuffer iterableAsciiToString(List<Integer> asciiList) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < asciiList.size(); i++) {
            int a = asciiList.get(i);
            char c = (char) a;
            sb.append(c);
        }
        return sb;
    }

    public static List<Integer> calculateCrc(List<Integer> data) {
        int fcs = 0xFFFF;

        for (int datum : data) {
            fcs = ((fcs >> 8) ^ crcTab16[(fcs ^ datum) & 0xff]);
        }

        fcs = ~fcs;
        List<Integer> result = intToHighLow(fcs);

        if (result.get(0) == 0xE1 || result.get(0) == 0xFE) {
            result.set(0, 0x00);
        }
        if (result.get(1) == 0xE1 || result.get(1) == 0xFE) {
            result.set(1, 0x00);
        }
        return result;
    }

    public static int crc16Check(byte[] bytes, int length) {
        int regCRC = 0xFFFF;
        int temp;
        int i, j;

        for (i = 0; i < length; i++) {
            temp = bytes[i];
            if (temp < 0) temp += 256;
            temp &= 0xFF;
            regCRC ^= temp;

            for (j = 0; j < 8; j++) {
                if ((regCRC & 0x0001) == 0x0001)
                    regCRC = (regCRC >> 1) ^ 0xA001;
                else
                    regCRC >>= 1;
            }
        }
        return (regCRC & 0xFFFF);
    }

    public static boolean checkCrc(byte[] crcData, int crc1, int crc2) {
        boolean result = false;
        int crc = crc16Check(crcData, crcData.length);
        List<Integer> crcList = intToHighLow(crc);
        if (crc1 == crcList.get(0) && crc2 == crcList.get(1)) {
            result = true;
        }
        return result;
    }

    public static String replaceIpAddress(String oldIp, String newIpEnd) {
        int index = oldIp.lastIndexOf(".");
        if (index != -1) {
            return oldIp.substring(0, index) + "." + newIpEnd;
        }
        return "";
    }

    public static List<Integer> calculateFixtureIP(String fixtureIP) {
        List<Integer> data = new ArrayList<>();
        String[] split = fixtureIP.split("\\.");
        for (String s : split) {
            data.add(Integer.valueOf(s));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex.toUpperCase());
            sb.append(" ");
        }
        return sb.toString();
    }

    public static List<Integer> bytesToIntegerList(byte[] bytes) {
        List<Integer> list = new ArrayList<>();
        for (byte b : bytes) {
            int b1 = b & 0xFF;
            list.add(b1);
        }
        return list;
    }

    public static byte[] integerListToBytes(List<Integer> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = (byte) list.get(i).intValue();
        }
        return bytes;
    }

    public static List<String> getHexStringList(List<Integer> srcData) {
        List<String> dstList = new ArrayList<>();
        for (int i = 0; i < srcData.size(); i++) {
            String s = Integer.toHexString(srcData.get(i)).toUpperCase();
            if (s.length() < 2) {
                s = "0" + s;
            }
            dstList.add(s);
        }
        return dstList;
    }

    public static byte[] getBytesFromLong(long src) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (src >> 24);
        bytes[1] = (byte) (src >> 16);
        bytes[2] = (byte) (src >> 8);
        bytes[3] = (byte) (src);
        return bytes;
    }

    public static int highLowToInt(int high, int low) {
        return (low | (high << 8));
    }

    public static long threeLongCombine(long first, long second, long third) {
        return (third | (second << 8) | (first << 16));
    }

    public static long fourLongCombine(long first, long second, long third, long forth) {
        return (forth | (third << 8) | (second << 16) | (first << 24));
    }

    public static List<Integer> computeKeyFromSeed(List<Integer> srcList) {
        List<Integer> bufList = new ArrayList<>(srcList);
        List<Integer> keyList = new ArrayList<>();

        int crc0 = crc8(bufList);

        bufList.set(0, crc0);
        int crc1 = crc8(bufList);

        bufList.set(0, srcList.get(0));
        bufList.set(1, crc1);
        int crc2 = crc8(bufList);

        bufList.set(1, srcList.get(1));
        bufList.set(2, crc2);
        int crc3 = crc8(bufList);

        bufList.set(2, srcList.get(2));
        bufList.set(3, crc3);
        int crc4 = crc8(bufList);

        bufList.set(3, srcList.get(3));
        bufList.set(4, crc4);
        int crc5 = crc8(bufList);

        bufList.set(4, srcList.get(4));
        bufList.set(5, crc5);
        int crc6 = crc8(bufList);

        if (crc3 == 0 && crc4 == 0 && crc5 == 0 && crc6 == 0) {
            keyList.add(crc1);
            keyList.add(crc2);
            keyList.add(crc3);
            keyList.add(crc4);
        } else {
            keyList.add(crc3);
            keyList.add(crc4);
            keyList.add(crc5);
            keyList.add(crc6);
        }
        return keyList;
    }

    public static int crc8(List<Integer> list) {
        int tCrc = 0xFF;
        for (int i = 0; i < list.size(); i++) {
            tCrc ^= list.get(i);
            tCrc &= 0xFF;

            for (int j = 0; j < 8; j++) {
                if ((tCrc & 0x80) != 0) {
                    tCrc <<= 1;
                    tCrc &= 0xFF;
                    tCrc ^= 0x1D;
                } else {
                    tCrc <<= 1;
                }
            }
        }
        return 0xFF - tCrc;
    }
}

