package cn.com.medicalmeasurementassistant.protocol;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.manager.ServerManager;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;
import cn.com.medicalmeasurementassistant.utils.LogUtils;

/**
 * user: Created by DuJi on 2021/8/25 22:03
 * email: 18571762595@13.com
 * description:
 */
public class ProtocolHelper extends Protocol {
    private static volatile ProtocolHelper sInstance = null;
    private final List<Integer> srcSocketData;
    private final List<Integer> socketProtocolData;
    private Handler mHandler;

    private ProtocolHelper() {
        srcSocketData = new ArrayList<>();
        socketProtocolData = new ArrayList<>();
    }

    /**
     * 从字节数据中解析出每帧协议
     * Start + Version + Source + Destination + Action
     * Data的长度=high_low_to_int(data[6],data[7])
     * CRC的长度固定为2
     *
     * @param data 原始数据
     */
    public void analysisSocketProtocol(byte[] data, int bytesLength) {
        LogUtils.i("data=" + CalculateUtils.bytesToHex(data));
        try {
            int dataLength = 0;
            int dataAction = 0;
            srcSocketData.clear();
            for (int i = 0; i < bytesLength; i++) {
                srcSocketData.add(data[i] & 0xff);
            }
            byte[] bytes = CalculateUtils.integerListToBytes(srcSocketData);
            if (srcSocketData.get(0) == 0x63 && srcSocketData.get(1) == 0x6F && srcSocketData.get(2) == 0x6E) {
                srcSocketData.clear();
                return;
            }
            if (srcSocketData.get(0) == 0x1B && srcSocketData.get(1) == 0x06 && srcSocketData.get(2) == 0x00) {
                srcSocketData.clear();
                return;
            }
//            LogUtils.i("bytes=" + CalculateUtils.bytesToHex(bytes));
            for (int i = 0; i < srcSocketData.size(); i++) {
                socketProtocolData.add(srcSocketData.get(i));
                int protocolLength = socketProtocolData.size();
                if (protocolLength == 6) {
                    dataAction = CalculateUtils.highLowToInt(socketProtocolData.get(4), socketProtocolData.get(5));
                    LogUtils.i("dataAction=" + dataAction);
                }
                if (dataAction == 0x0105) {
                    dataLength = 6 + 2;
                } else {
                    if (protocolLength >= 8) {
                        if (dataAction == 0x0403) {
                            dataLength = 8 + CalculateUtils.highLowToInt(socketProtocolData.get(6), socketProtocolData.get(7)) + 4 + 2;
                        } else {
                            dataLength = 8 + CalculateUtils.highLowToInt(socketProtocolData.get(6), socketProtocolData.get(7)) + 2;
                        }

                    }
                }
                if (protocolLength == dataLength) {
                    boolean result;
                    ReceiveProtocol protocol = new ReceiveProtocol();
                    result = protocol.unpack(socketProtocolData);
                    if (result) {
                        dispatchProtocol(protocol);
                    }
                    socketProtocolData.clear();
                }
            }
        } catch (Exception e) {
            LogUtils.e("exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 将解析出来的协议，分发给到业务层处理
     *
     * @param receiveProtocol 需要分发的协议
     */
    private void dispatchProtocol(ReceiveProtocol receiveProtocol) {
        LogUtils.i("dispatchProtocol");
        List<Integer> packData = receiveProtocol.raw;
        List<Integer> list = CalculateUtils.intToHighLow(receiveProtocol.action);
        int category = list.get(0);
        int function = list.get(1);
        LogUtils.i("handleMessage: " + "category=" + category + ", " + "function=" + function);
        switch (category) {
            case Constant.CATEGORY_COMMON:
                if (function == Constant.FUNCTION_REPLY_HANDSHAKE_SIGNAL) {
                    ServerManager.getInstance().setDeviceConnect(true);
                }
                break;
            case Constant.CATEGORY_DATA_COLLECT:
                if (function == Constant.FUNCTION_REPLY_START_DATA_COLLECT) {

                } else if (function == Constant.FUNCTION_REPLY_SAMPLED_DATA) {

                } else if (function == Constant.FUNCTION_REPLY_STOP_DATA_COLLECT) {

                }
                break;
            default:
                break;
        }
    }

    public static ProtocolHelper getInstance() {
        if (null == sInstance) {
            synchronized (ProtocolHelper.class) {
                if (null == sInstance) {
                    sInstance = new ProtocolHelper();
                }
            }
        }
        return sInstance;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

}
