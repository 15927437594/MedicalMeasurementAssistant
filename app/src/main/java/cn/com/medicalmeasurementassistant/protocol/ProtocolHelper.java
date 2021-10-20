package cn.com.medicalmeasurementassistant.protocol;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.manager.DeviceManager;

/**
 * user: Created by DuJi on 2021/8/25 22:03
 * email: 18571762595@13.com
 * description:
 */
public class ProtocolHelper extends Protocol {
    private static volatile ProtocolHelper sInstance = null;
    private final List<Integer> srcSocketData;

    private ProtocolHelper() {
        srcSocketData = new ArrayList<>();
    }

    public void analysisSocketProtocol(byte[] data, int bytesLength) {
//        LogUtils.i("data=" + CalculateUtils.bytesToHex(data));
        if (data[0] == (byte) 0xEB && data[1] == (byte) 0x01 && data[2] == (byte) 0x02 && data[3] == (byte) 0x01 && data[4] == (byte) 0x01 && data[5] == (byte) 0x05) {
            for (int i = 0; i < bytesLength; i++) {
                srcSocketData.add(data[i] & 0xff);
            }
            DeviceManager.getInstance().replyHandshake(srcSocketData);
        } else if (data[0] == (byte) 0xEB && data[1] == (byte) 0x01 && data[2] == (byte) 0x02 && data[3] == (byte) 0x01 && data[4] == (byte) 0x04 && data[5] == (byte) 0x02) {
            for (int i = 0; i < bytesLength; i++) {
                srcSocketData.add(data[i] & 0xff);
            }
            DeviceManager.getInstance().replyStartDataCollect(srcSocketData);
        } else if (data[0] == (byte) 0xEB && data[1] == (byte) 0x01 && data[2] == (byte) 0x02 && data[3] == (byte) 0x01 && data[4] == (byte) 0x04 && data[5] == (byte) 0x03) {
            for (int i = 8; i < bytesLength; i++) {
                srcSocketData.add(data[i] & 0xff);
            }
            if (srcSocketData.size() == 974 - 8) {
                DeviceManager.getInstance().replySampledData(srcSocketData);
            }
        } else if (data[0] == (byte) 0xEB && data[1] == (byte) 0x01 && data[2] == (byte) 0x02 && data[3] == (byte) 0x01 && data[4] == (byte) 0x04 && data[5] == (byte) 0x05) {
            for (int i = 0; i < bytesLength; i++) {
                srcSocketData.add(data[i] & 0xff);
            }
            DeviceManager.getInstance().replyStopDataCollect(srcSocketData);
        }
        srcSocketData.clear();
    }

    public static ProtocolHelper getInstance() {
        if (null == sInstance) {
            synchronized (ProtocolHelper.class) {
                if (null == sInstance) {
                    sInstance = new ProtocolHelper();
                }
            }
        }
        return sInstance;
    }
}
