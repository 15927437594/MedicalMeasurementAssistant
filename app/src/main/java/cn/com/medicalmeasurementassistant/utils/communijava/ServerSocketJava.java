package cn.com.medicalmeasurementassistant.utils.communijava;

import android.app.Activity;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static cn.com.medicalmeasurementassistant.utils.communication.MyServerSocketManager.SERVER_SOCKET_PORT;

public class ServerSocketJava {

    private static TextView mTextView;
    private static InputStream inputStream;
    private static OutputStream outputStream;

    public static void startServerSocket(TextView textView) {
        mTextView = textView;
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(SERVER_SOCKET_PORT);
                Socket socket;
                setText("等待客户端连接:");
                //从请求队列中取出链接
                socket = serverSocket.accept();
                setText("客户端连接:" + Arrays.toString(socket.getInetAddress().getAddress()));
                //获取客户端信息
                inputStream = socket.getInputStream();
                //回复客户端
                outputStream = socket.getOutputStream();
                startThread();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }).start();

    }


    public static void startThread() {
        new Thread(() -> {
            try {
                //字节数组
                byte[] bytes = new byte[1024];
                //字节数组长度
                int len;

                while (true) {
                    //读取客户端请求信息
                    while (inputStream.available() != 0 && (len = inputStream.read(bytes)) != -1) {
                        //接收到的消息
                        String s = new String(bytes).trim();
                        //这里你可以根据你的实际消息内容做相应的逻辑处理
                        setText("客户端发送消息："+s);
                    }

                }

            } catch (Exception e) {
                setText(e.getMessage());
            }
        }).start();


    }

    private static int i = 1;

    public static synchronized void sendMsg(String string) {
        new Thread(()->{
            try {
                byte[] bytes = ("服务端发送消息" + string).getBytes();
                outputStream.write(bytes);
                outputStream.flush();
                setText(new String(bytes));
            } catch (Exception e) {
                setText(e.getMessage());
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
