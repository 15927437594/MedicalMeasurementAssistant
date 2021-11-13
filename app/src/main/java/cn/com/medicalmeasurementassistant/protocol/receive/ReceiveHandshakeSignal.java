package cn.com.medicalmeasurementassistant.protocol.receive;

import java.util.List;

import cn.com.medicalmeasurementassistant.protocol.BaseReceiveProtocol;

/**
 * user: Created by DuJi on 2021/8/25 21:27
 * email: 18571762595@13.com
 * description:
 */
public class ReceiveHandshakeSignal extends BaseReceiveProtocol {

    public ReceiveHandshakeSignal(BaseReceiveProtocol protocol) {
        super();
        initParent(protocol);
    }

    @Override
    public List<Object> handleProtocolData() {
        return null;
    }
}
