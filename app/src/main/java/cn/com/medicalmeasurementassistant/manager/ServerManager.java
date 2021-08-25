package cn.com.medicalmeasurementassistant.manager;

import cn.com.medicalmeasurementassistant.server.TcpServer;

/**
 * user: Created by DuJi on 2021/8/25 22:31
 * email: 18571762595@13.com
 * description:
 */
public class ServerManager {
    private static volatile ServerManager sInstance = null;
    private TcpServer mTcpServer = null;

    private ServerManager() {
        mTcpServer = new TcpServer();
    }

    public static ServerManager getInstance() {
        if (null == sInstance) {
            synchronized (ServerManager.class) {
                if (null == sInstance) {
                    sInstance = new ServerManager();
                }
            }
        }
        return sInstance;
    }

    public void sendToDevice() {
        if (mTcpServer != null) {
            mTcpServer.sendToDevice();
        }
    }
}
