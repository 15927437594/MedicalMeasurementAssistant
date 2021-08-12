package cn.com.medicalmeasurementassistant.utils.communijava;

import android.app.Activity;
import android.widget.TextView;

import java.net.ServerSocket;
import java.net.Socket;

import static cn.com.medicalmeasurementassistant.utils.communication.MyServerSocketManager.SERVER_SOCKET_PORT;

public class ServerSocketJava {

    private static TextView mTextView;
    private static ServerSocket serverSocket;
    private static Socket socket;

    public static void startServerSocket(TextView textView) {
        mTextView = textView;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(SERVER_SOCKET_PORT);
                setText("等待客户端连接:");
                while (true) {
                    socket = serverSocket.accept();
                    Channel channel = new Channel(socket);
                    MySocketManager.INSTANCE.addSocket(channel);
                    channel.channelStart();
                    setText("有新的客户端连接，当前连接数:" + MySocketManager.INSTANCE.getChannelDesc());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void setText(String sss) {
        if (mTextView == null) {
            return;
        }
        if (mTextView.getContext() == null) {
            return;
        }
        Activity context = (Activity) mTextView.getContext();
        context.runOnUiThread(() -> mTextView.setText(sss));

    }

}
