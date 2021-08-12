package cn.com.medicalmeasurementassistant.utils.communijava;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.com.medicalmeasurementassistant.utils.communication.MyServerSocketManager;

public class Channel {

    private Socket mSocket;
    private boolean isExe = true;
    //    private final Thread mThread;
    private InputStream is;
    private OutputStream os;
    private String addressId;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Channel(Socket mSocket) {
        try {
            is = mSocket.getInputStream();
            os = mSocket.getOutputStream();
            addressId = new String(mSocket.getInetAddress().getAddress());
            receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 接收消息
    private void receive() {
        try {
            byte[] bytes = new byte[1024];
            int len;
            while (isExe) {
                boolean connected = mSocket.isConnected()&&is.available() != 0;
                if(!connected){
                    MySocketManager.INSTANCE.moveSocket(this);
                    return;
                }
                //读取客户端请求信息
                while (connected && (len = is.read(bytes)) != -1) {
                    //接收到的消息
                    String s = new String(bytes);
                    int length = s.length();
                    if (length > len) {
                        s = s.substring(0, len);
                    }
                    MyServerSocketManager.INSTANCE.sendMsg(s);
                    //这里你可以根据你的实际消息内容做相应的逻辑处理
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送消息
    public void sendMsg(String string) {
        new Thread(() -> {
            try {
                byte[] bytes = (string).getBytes();
                os.write(bytes);
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void channelStart() {
//        mThread.start();
    }


    public void channelClose() {
        try {
            isExe = false;
            if (is != null) {
                is.close();
                is = null;
            }
            if (os != null) {
                os.close();
                os = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
//            if (mThread.isAlive()) {
//                mThread.interrupt();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
