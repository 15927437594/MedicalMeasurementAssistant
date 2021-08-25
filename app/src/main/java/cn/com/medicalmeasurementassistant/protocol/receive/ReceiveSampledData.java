package cn.com.medicalmeasurementassistant.protocol.receive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.medicalmeasurementassistant.protocol.BaseReceiveProtocol;

/**
 * user: Created by DuJi on 2021/8/25 21:41
 * email: 18571762595@13.com
 * description:
 */
public class ReceiveSampledData extends BaseReceiveProtocol {

    public ReceiveSampledData(BaseReceiveProtocol protocol) {
        super();
        initParent(protocol);
    }

    @Override
    public List<Object> handleProtocolData() {
        List<Integer> adData = new ArrayList<>(data.subList(0, 960));
        List<Integer> capacitanceData = new ArrayList<>(data.subList(960, data.size()));
        return Arrays.asList(adData, capacitanceData);
    }
}
