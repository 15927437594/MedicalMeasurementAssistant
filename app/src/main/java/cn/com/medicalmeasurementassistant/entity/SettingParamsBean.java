package cn.com.medicalmeasurementassistant.entity;

import java.util.ArrayList;
import java.util.List;

import static cn.com.medicalmeasurementassistant.entity.Constant.CHANNEL_STATUS;

public class SettingParamsBean {
    public static final int GLOBAL_SETTING = 1;
    public static final int CHANNEL_SETTING = 2;

    private final static SettingParamsBean mSettingBean = new SettingParamsBean();

    private List<SettingBean> settingBeans;
    private GlobalBean mGlobalBean;

    public static SettingParamsBean getInstance() {
        return mSettingBean;
    }


    private SettingParamsBean() {
        initSettingBean();
    }

    private void initSettingBean() {
        settingBeans = new ArrayList<>();
        mGlobalBean = new GlobalBean();
        settingBeans.add(mGlobalBean);

        for (int i = 0; i < 8; i++) {
            ChannelBean channelBean = new ChannelBean();
            channelBean.setChannelName("通道" + (i + 1));
            channelBean.setChannelStatus(CHANNEL_STATUS[i]);
            mChannelBeans.add(channelBean);
        }
        settingBeans.addAll(mChannelBeans);
        settingBeans.add(new ChannelBean());
    }

    private final List<ChannelBean> mChannelBeans = new ArrayList<>();

    public List<ChannelBean> getChannelBeans() {
        return mChannelBeans;
    }

    public GlobalBean getmGlobalBean() {
        return mGlobalBean;
    }

    public void setmGlobalBean(GlobalBean mGlobalBean) {
        this.mGlobalBean = mGlobalBean;
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
        private boolean channelStatus = true;
        // 高通虑波状态
        private boolean highpassFilterStatus = true;
        // 工频陷波状态
        private boolean frequencyNotchStatus = false;
        // REF电极状态
        private boolean electrodeStatus;
        // 通道量程
        private int angle = Constant.DEFAULT_RANGE;

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

        public boolean getHighpassFilterStatus() {
            return highpassFilterStatus;
        }

        public void setHighpassFilterStatus(boolean highpassFilterStatus) {
            this.highpassFilterStatus = highpassFilterStatus;
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
        private int channelAngle = Constant.DEFAULT_RANGE;
        // 电极状态  true 未脱落，false 脱落
        private boolean electrodeStatus = true;
        /**
         * 通道状态 true  开启，false 关闭
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
