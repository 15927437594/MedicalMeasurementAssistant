package cn.com.medicalmeasurementassistant.entity;

/**
 * 全局设置
 */
public class GlobalBean  {

    // 通道状态
    private int channelStatus;
    // 高通虑波状态
    private int highPassFilterStatus;
    // 工频陷波状态
    private int frequencyNotchStatus;
    // REF电极状态
    private int electrodeStatus;

    public int getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(int channelStatus) {
        this.channelStatus = channelStatus;
    }

    public int getHighPassFilterStatus() {
        return highPassFilterStatus;
    }

    public void setHighPassFilterStatus(int highPassFilterStatus) {
        this.highPassFilterStatus = highPassFilterStatus;
    }

    public int getFrequencyNotchStatus() {
        return frequencyNotchStatus;
    }

    public void setFrequencyNotchStatus(int frequencyNotchStatus) {
        this.frequencyNotchStatus = frequencyNotchStatus;
    }

    public int getElectrodeStatus() {
        return electrodeStatus;
    }

    public void setElectrodeStatus(int electrodeStatus) {
        this.electrodeStatus = electrodeStatus;
    }
}