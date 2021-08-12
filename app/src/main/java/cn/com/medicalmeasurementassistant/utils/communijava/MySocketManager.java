package cn.com.medicalmeasurementassistant.utils.communijava;

import android.util.ArrayMap;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public enum MySocketManager {
    INSTANCE;
    private ArrayMap<String, Socket> socketArrayMap = new ArrayMap<>();
    private final List<Channel> socketArrayList = new ArrayList<>();


    public void addSocket(Channel channel) {
        boolean isAdd = false;
        for (Channel chan : socketArrayList) {
            if (chan.getAddressId().equalsIgnoreCase(channel.getAddressId())) {
                isAdd = true;
                break;
            }
        }
        if (!isAdd) {
            socketArrayList.add(channel);
        }
    }

    public void moveSocket(Channel channel) {
        socketArrayList.remove(channel);

    }

    public void closeSocket(Channel channel) {
        channel.channelClose();
        moveSocket(channel);
    }

    public void clearAllSocket() {
        for (Channel channel : socketArrayList) {
            channel.channelClose();
        }
        socketArrayList.clear();
    }


    public void sendMsgToClient() {
    }

    public void sendMsgToAll(Channel fromChannel, String msg) {
        for (Channel channel : socketArrayList) {
            if (fromChannel == channel) {
                continue;
            }
            channel.sendMsg(msg);
        }

    }

    public String getChannelDesc() {
        return "当前有" + socketArrayList.size() + "位用户连接";
    }


}
