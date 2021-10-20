package cn.com.medicalmeasurementassistant.manager;

import android.os.Handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.com.medicalmeasurementassistant.entity.Constant;
import cn.com.medicalmeasurementassistant.protocol.ProtocolHelper;
import cn.com.medicalmeasurementassistant.protocol.send.SendHandshakeSignal;
import cn.com.medicalmeasurementassistant.utils.CalculateUtils;
import cn.com.medicalmeasurementassistant.utils.LogUtils;

/**
 * user: Created by DuJi on 2021/8/25 22:31
 * email: 18571762595@13.com
 * description:
 */
public class ServerManager {
    private static volatile ServerManager sInstance = null;
    private ServerSocket mServerSocket;
    public ExecutorService mThreadPool;
    private Socket mClient;
    private final AtomicBoolean mIsServerSocketInterrupted = new AtomicBoolean(false);
    private final AtomicBoolean mIsClientInterrupted = new AtomicBoolean(false);
    private OutputStream mOutputStream;
    private final Handler mHandler;

    private ServerManager() {
        mThreadPool = Executors.newCachedThreadPool();
        mHandler = new Handler();
        try {
            mServerSocket = new ServerSocket(Constant.TCP_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void connectDevice() {
        LogUtils.i("connectDevice");
        mIsServerSocketInterrupted.set(false);
        mThreadPool.execute(() -> {
            try {
                while (!mIsServerSocketInterrupted.get()) {
                    if (!mServerSocket.isClosed()) {
                        mClient = mServerSocket.accept();
                        LogUtils.i("accept and add client");
                        mThreadPool.execute(new DeviceClient(mClient));
                        mHandler.postDelayed(() -> sendData(new SendHandshakeSignal().pack()), 1000L);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void disconnectDevice() {
        try {
            mIsServerSocketInterrupted.set(true);
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DeviceClient implements Runnable {
        DataInputStream dataInputStream;

        private DeviceClient(Socket socket) {
            try {
                InputStream inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (!mIsClientInterrupted.get()) {
                    byte[] buffer = new byte[1024];
                    if (dataInputStream != null) {
                        int read = dataInputStream.read(buffer);
                        LogUtils.d("read=" + read);
                        if (read > 0) {
                            ProtocolHelper.getInstance().analysisSocketProtocol(buffer, read);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendData(List<Integer> packData) {
        LogUtils.i(CalculateUtils.getHexStringList(packData).toString());
        int msgLen = packData.size();
        byte[] buf = new byte[msgLen];
        for (int i = 0; i < msgLen; i++) {
            buf[i] = packData.get(i).byteValue();
        }
        sendMsg(buf);
    }

    public void sendMsg(byte[] buf) {
        mThreadPool.execute(() -> {
            try {
                //发送数据到客户端
                if (mClient != null) {
                    mOutputStream = mClient.getOutputStream();
                    mOutputStream.write(buf);
                    mOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
