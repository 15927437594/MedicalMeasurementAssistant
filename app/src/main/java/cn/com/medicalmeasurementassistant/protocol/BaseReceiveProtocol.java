package cn.com.medicalmeasurementassistant.protocol;

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
        List<Integer> crcData;
        boolean result;
        version = srcData.get(1);
        source = srcData.get(2);
        destination = srcData.get(3);
        action = CalculateUtils.highLowToInt(srcData.get(4), srcData.get(5));
        if (action == 0x0105) {
            crcData = new ArrayList<>(srcData.subList(0, 6));
        } else if (action == 0x0403) {
            length = CalculateUtils.highLowToInt(srcData.get(6), srcData.get(7));
            data.addAll(srcData.subList(8, length + 8 + 4));
            crcData = new ArrayList<>(srcData.subList(0, 6));
        } else {
            length = CalculateUtils.highLowToInt(srcData.get(6), srcData.get(7));
            data.addAll(srcData.subList(8, length + 8));
            crcData = new ArrayList<>(srcData.subList(0, length + 8));
        }
        crc2 = srcData.get(srcData.size() - 2);
        crc1 = srcData.get(srcData.size() - 1);
        byte[] crcDataBytes = CalculateUtils.integerListToBytes(crcData);
        if (action == 0x0403) {
            // action=0x0403的数据不用做crc校验
            result = true;
        } else {
            result = CalculateUtils.checkCrc(crcDataBytes, crc1, crc2);
        }

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
