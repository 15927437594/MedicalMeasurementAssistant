package cn.com.medicalmeasurementassistant.entity;

import java.util.ArrayList;
import java.util.List;

public class SettingParamsBean {
    public static final int GLOBAL_SETTING = 1;
    public static final int CHANNEL_SETTING = 2;

    private final static SettingParamsBean mSettingBean = new SettingParamsBean();

    private List<SettingBean> settingBeans;

    public static SettingParamsBean getInstance() {
        return mSettingBean;
    }


    private SettingParamsBean() {
        initSettingBean();
    }

    private void initSettingBean() {
        settingBeans = new ArrayList<>();
        GlobalBean globalBean = new GlobalBean();
        settingBeans.add(globalBean);

        for (int i = 0; i < 8; i++) {
            ChannelBean channelBean = new ChannelBean();
            channelBean.setChannelName("通道" + (i + 1));
            settingBeans.add(channelBean);
        }
        settingBeans.add(new ChannelBean());
    }


    public List<SettingBean> getSettingBeans() {
        return settingBeans;
    }

    public void setSettingBeans(List<SettingBean> settingBeans) {
        this.settingBeans = settingBeans;
    }

    public abstract static class SettingBean {
        public abstract int getType();

    }


    /**
     * 全局设置
     */
    public static class GlobalBean extends SettingBean {
        // 通道状态
        private boolean channelStatus;
        // 高通虑波状态
        private boolean highPassFilterStatus;
        // 工频陷波状态
        private boolean frequencyNotchStatus;
        // REF电极状态
        private boolean electrodeStatus;
        // 通道量程
        private int angle = Constant.DEFAULT_ANGLE;

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }

        public boolean getChannelStatus() {
            return channelStatus;
        }

        public void setChannelStatus(boolean channelStatus) {
            this.channelStatus = channelStatus;
        }

        public boolean getHighPassFilterStatus() {
            return highPassFilterStatus;
        }

        public void setHighPassFilterStatus(boolean highPassFilterStatus) {
            this.highPassFilterStatus = highPassFilterStatus;
        }

        public boolean getFrequencyNotchStatus() {
            return frequencyNotchStatus;
        }

        public void setFrequencyNotchStatus(boolean frequencyNotchStatus) {
            this.frequencyNotchStatus = frequencyNotchStatus;
        }

        public boolean getElectrodeStatus() {
            return electrodeStatus;
        }

        public void setElectrodeStatus(boolean electrodeStatus) {
            this.electrodeStatus = electrodeStatus;
        }

        @Override
        public int getType() {
            return GLOBAL_SETTING;
        }
    }

    /**
     * 通道设置
     */
    public static class ChannelBean extends SettingBean {
        //通道名称
        private String channelName;
        // 通道量程
        private int channelAngle = Constant.DEFAULT_ANGLE;
        // 电极状态  true 未脱落，false 脱落
        private boolean electrodeStatus = true;
        /**
         *  通道状态 true  开启，false 关闭
         */
        private boolean channelStatus = true;

        public boolean getChannelStatus() {
            return channelStatus;
        }

        public void setChannelStatus(boolean channelStatus) {
            this.channelStatus = channelStatus;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public int getChannelAngle() {
            return channelAngle;
        }

        public void setChannelAngle(int channelAngle) {
            this.channelAngle = channelAngle;
        }

        public boolean getElectrodeStatus() {
            return electrodeStatus;
        }

        public void setElectrodeStatus(boolean electrodeStatus) {
            this.electrodeStatus = electrodeStatus;
        }


        @Override
        public int getType() {
            return CHANNEL_SETTING;
        }
    }


}
