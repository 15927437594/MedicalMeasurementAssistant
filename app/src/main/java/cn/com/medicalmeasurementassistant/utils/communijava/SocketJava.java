package cn.com.medicalmeasurementassistant.utils.communijava;

import android.app.Activity;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static cn.com.medicalmeasurementassistant.utils.communication.MyServerSocketManager.SERVER_SOCKET_PORT;

public class SocketJava {
    private static TextView mTextView;
    private static InputStream is;
    private static OutputStream outputStream;

    public static void startSocket(TextView textView) {

        mTextView = textView;
        new Thread(()->{
            try {
                //你的ip，你的端口
                Socket socket = new Socket("192.168.43.1", SERVER_SOCKET_PORT);
                setText("连接成功");
                //获取输出流
                outputStream = socket.getOutputStream();
                //发送链接成功的消息
                sendMsg("客户端链接");
                //获取输入流
                is = socket.getInputStream();
                startThread();
            } catch (Exception e) {
                setText(e.getMessage());
            }
        }).start();

    }
    public static synchronized void sendMsg(String string) {
        new Thread(()->{
            try {
                byte[] bytes = ( string).getBytes();
                outputStream.write(bytes);
                outputStream.flush();
                setText(new String(bytes));
            } catch (Exception e) {
                setText(e.getMessage());
            }
        }).start();


    }

    public static void startThread() {
        new Thread(() -> {
            try {
                byte[] inputBytes = new byte[1024];
                int len;
                //监听输入流,持续接收
                while(true){
                    while (is.available() != 0 && (len = is.read(inputBytes)) != -1 ) {
                        //消息体
                        String s = new String(inputBytes, StandardCharsets.UTF_8);
                        setText(s);
                        //下边可以对接收到的消息进行处理
                    }
                }

            } catch (Exception e) {
                setText(e.getMessage());
            }
        }).start();


    }

    public static synchronized void setText(String sss) {
        if (mTextView == null) {
            return;
        }
        if (mTextView.getContext() == null) {
            return;
        }
        Activity context = (Activity) mTextView.getContext();
        context.runOnUiThread(() -> {
            mTextView.setText(sss);
        });

    }



}
