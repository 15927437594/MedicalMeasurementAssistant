package cn.com.medicalmeasurementassistant.protocol;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.utils.CalculateUtils;

public abstract class BaseSendProtocol extends Protocol {
    /**
     * @return Data sending source 1:APP 2:测量设备
     */
    public abstract int protocolSource();

    /**
     * @return Data sending destination 1:APP 2:测量设备
     */
    public abstract int protocolDestination();

    /**
     * @return 返回协议动作 action = categoryID + functionID
     */
    public abstract int protocolAction();

    /**
     * @return 返回协议数据
     */
    public abstract List<Integer> protocolData();


    /**
     * 协议数据封包,CRC使用ModbusCRC16
     */
    public List<Integer> pack() {
        raw = new ArrayList<>();
        source = protocolSource();
        destination = protocolDestination();
        action = protocolAction();
        data = protocolData();
        length = data.size();
        List<Integer> actionList = CalculateUtils.intToHighLow(action);
        raw.add(start);
        raw.add(version);
        raw.add(source);
        raw.add(destination);
        raw.addAll(actionList);
        if (length > 0) {
            List<Integer> lengthList = CalculateUtils.intToHighLow(length);
            raw.addAll(lengthList);
        }
        raw.addAll(data);
        byte[] bytes = CalculateUtils.integerListToBytes(raw);
        int crc16Check = CalculateUtils.crc16Check(bytes, bytes.length);
        List<Integer> crcResult = CalculateUtils.intToHighLow(crc16Check);
        crc1 = crcResult.get(1);
        crc2 = crcResult.get(0);
        raw.add(crc1);
        raw.add(crc2);
        return raw;
    }
}
