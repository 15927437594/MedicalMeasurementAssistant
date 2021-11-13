package cn.com.medicalmeasurementassistant.protocol.receive;

import java.util.Collections;
import java.util.List;

import cn.com.medicalmeasurementassistant.protocol.BaseReceiveProtocol;

/**
 * user: Created by DuJi on 2021/8/25 21:59
 * email: 18571762595@13.com
 * description:
 */
public class ReceiveStopDataCollect extends BaseReceiveProtocol {

    public ReceiveStopDataCollect(BaseReceiveProtocol protocol) {
        super();
        initParent(protocol);
    }

    @Override
    public List<Object> handleProtocolData() {
        int stopState = data.get(0);
        return Collections.singletonList(stopState);
    }
}
