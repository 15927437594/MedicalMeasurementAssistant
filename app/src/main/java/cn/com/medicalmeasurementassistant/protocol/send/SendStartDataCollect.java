package cn.com.medicalmeasurementassistant.protocol.send;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.protocol.BaseSendProtocol;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;

/**
 * user: Created by DuJi on 2021/8/25 21:35
 * email: 18571762595@13.com
 * description:启动数据采集
 */
public class SendStartDataCollect extends BaseSendProtocol {

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
        return CalculateUtils.highLowToInt(0x04, 0x01);
    }

    @Override
    public List<Integer> protocolData() {
        return new ArrayList<>();
    }
}
