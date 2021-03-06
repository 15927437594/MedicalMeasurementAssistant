package cn.com.medicalmeasurementassistant.listener;

import java.util.List;

/**
 * user: Created by DuJi on 2021/9/25 16:50
 * email: 18571762595@13.com
 * description:
 */
public interface DeviceInfoListener {

    void replyHandshake(List<Integer> data);

    void replyStartDataCollect(List<Integer> data);

    void replyVoltage(int channel, List<Double> data);

    void replyStopDataCollect(List<Integer> data);

    void replyCapacitance(double capacitance);

    void replyAngle(double angle);

    void replyDeviceStopped();
}
