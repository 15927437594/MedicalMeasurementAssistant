package cn.com.medicalmeasurementassistant.utils.communication;

import java.io.IOException;
import java.net.Socket;

public class MyClientTest {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("192.168.1.102", MyServerSocketManager.SERVER_SOCKET_PORT);
            String s = new String(clientSocket.getInetAddress().getAddress());
            System.out.println("连接成功 ip = "+s);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("连接失败 " + e.getMessage());
        }
    }
}
