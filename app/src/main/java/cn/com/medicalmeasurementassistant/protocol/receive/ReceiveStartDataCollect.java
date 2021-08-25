package cn.com.medicalmeasurementassistant.protocol.receive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.com.medicalmeasurementassistant.protocol.BaseReceiveProtocol;

/**
 * user: Created by DuJi on 2021/8/25 21:38
 * email: 18571762595@13.com
 * description:
 */
public class ReceiveStartDataCollect extends BaseReceiveProtocol {

    public ReceiveStartDataCollect(BaseReceiveProtocol protocol) {
        super();
        initParent(protocol);
    }

    @Override
    public List<Object> handleProtocolData() {
        int startState = data.get(0);
        return Collections.singletonList(startState);
    }
}
