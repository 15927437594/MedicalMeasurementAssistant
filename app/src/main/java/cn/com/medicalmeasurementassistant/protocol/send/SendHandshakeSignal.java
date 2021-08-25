package cn.com.medicalmeasurementassistant.protocol.send;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.protocol.BaseSendProtocol;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;

/**
 * user: Created by DuJi on 2021/8/25 21:19
 * email: 18571762595@13.com
 * description:发送握手信号
 */
public class SendHandshakeSignal extends BaseSendProtocol {
    @Override
    public int protocolSource() {
        return Constant.SOURCE_APP;
    }

    @Override
    public int protocolDestination() {
        return Constant.SOURCE_MEASURE_DEVICE;
    }

    @Override
    public int protocolAction() {
        return CalculateUtils.highLowToInt(0x01, 0x04);
    }

    @Override
    public List<Integer> protocolData() {
        return new ArrayList<>();
    }
}
