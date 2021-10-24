package cn.com.medicalmeasurementassistant.manager;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.List;

import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.utils.LogUtils;


/**
 * user: Created by jid on 2020/10/22
 * email: jid@hwtc.com.cn
 * description:OS服务和Python服务通过socket进行通信,将Python服务发送的协议通过serial通道转发给MCU
 */
public class MessageHandler {
    private static volatile MessageHandler sInstance = null;

    public MessageHandler() {

    }

    public static MessageHandler getInstance() {
        if (null == sInstance) {
            synchronized (MessageHandler.class) {
                if (null == sInstance) {
                    sInstance = new MessageHandler();
                }
            }
        }
        return sInstance;
    }

    public void createHandler() {
        HandlerThread handlerThread = new HandlerThread("message.distribute.handler");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler mDistributeHandler = new Handler(looper, msg -> {
            if (msg.what == Constant.MSG_WHAT_DISTRIBUTE_VOLTAGE) {
                int channel = msg.arg1;
                List<Double> data = (List<Double>) msg.obj;
                DeviceManager.getInstance().replyChannelVoltage(channel, data);
            }
            return false;
        });
        DeviceManager.getInstance().setHandler(mDistributeHandler);
    }
}

