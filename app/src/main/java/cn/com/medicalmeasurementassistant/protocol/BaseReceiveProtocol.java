package cn.com.medicalmeasurementassistant.protocol;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.utils.CalculateUtils;

public abstract class BaseReceiveProtocol extends Protocol {

    public int getProtocolSource() {
        return source;
    }

    public int getProtocolDestination() {
        return destination;
    }

    public int getProtocolAction() {
        return action;
    }

    public List<Integer> getProtocolData() {
        return data;
    }

    public abstract List<Object> handleProtocolData();

    /**
     * 协议数据解包
     *
     * @param srcData 完整协议数据包
     */
    public boolean unpack(List<Integer> srcData) {
        byte[] bytes = CalculateUtils.integerListToBytes(srcData);
        LogUtils.i(CalculateUtils.bytesToHex(bytes));

        boolean result;
        version = srcData.get(1);
        source = srcData.get(2);
        destination = srcData.get(3);
        action = CalculateUtils.highLowToInt(srcData.get(4), srcData.get(5));
        length = CalculateUtils.highLowToInt(srcData.get(6), srcData.get(7));
        data.addAll(srcData.subList(8, length + 8));
        crc1 = srcData.get(srcData.size() - 2);
        crc2 = srcData.get(srcData.size() - 1);
        List<Integer> crcData = new ArrayList<>(srcData.subList(0, length + 8));
        result = CalculateUtils.checkCrc(crcData, crc1, crc2);
        if (!result) {
            length = 0x0000;
            data.clear();
            crc1 = 0x00;
            crc2 = 0x00;
        } else {
            raw.addAll(srcData);
        }
        return result;
    }
}
