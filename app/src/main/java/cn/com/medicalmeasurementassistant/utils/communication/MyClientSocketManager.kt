package cn.com.medicalmeasurementassistant.utils.communication

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Log
import android.widget.TextView
import cn.com.medicalmeasurementassistant.app.ProjectApplication
import cn.com.medicalmeasurementassistant.utils.StringUtils
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket


object MyClientSocketManager {
    //
//    private static final int PORT = 12345;
//    private Socket mSocket = null;
    private var mOutStream: OutputStream? = null
    private var mInStream: InputStream? = null

    private var clientSocket: Socket? = null


    private var textView: TextView? = null

    // 初始化一个ServerSocket

    fun setTextView(textView: TextView) {
        this.textView = textView
        linkServerSocket()
    }

    private fun setText(text: String) {
        (textView?.context as Activity).runOnUiThread {
            textView?.text = text
        }
    }

    /**
     *  连接socket
     */
    private fun linkServerSocket() {
        Thread(Runnable {
            try {
//                clientSocket = Socket(getWifiRouteIPAddress(), MyServerSocketManager.SERVER_SOCKET_PORT)
//                clientSocket = Socket("127.0.0.1", MyServerSocketManager.SERVER_SOCKET_PORT)
                clientSocket = Socket("192.168.43.1", MyServerSocketManager.SERVER_SOCKET_PORT)
                setText("连接成功")
                mOutStream = clientSocket?.getOutputStream()
                mInStream = clientSocket?.getInputStream()
//                readMsg()
                readMsg2()
            } catch (e: Exception) {
                e.printStackTrace()
                setText("错误信息 ${e.message}")
            }
        }).start()
    }


    //发送数据
    fun sendMsg(msg: String) {
        if (StringUtils.isEmpty(msg))
            return
        Thread(Runnable {
            try {   //发送
                mOutStream?.write(msg.toByteArray())
                mOutStream?.flush()
                clientSocket?.shutdownOutput()
            } catch (e: Exception) {
                e.printStackTrace()
                setText("错误信息 ${e.message}")
            }

        }).start()

    }


    private fun readMsg2() {
        setText("开始接收消息")
        val inputBytes = ByteArray(1024)
        var len: Int?
        //监听输入流,持续接收
//        while (true) {
        len = mInStream?.read(inputBytes)
        while (mInStream?.available() != 0 && len != -1) {
            //消息体
            val s = String(inputBytes)
            setText("客户端接收到消息:$s")
            len = mInStream?.read(inputBytes)
            //下边可以对接收到的消息进行处理
        }
//        }

    }

    //接收数据
    private fun readMsg() {
        Thread(Runnable {
            try {
                clientSocket?.let {
                    while (true) {
                        if (it.isConnected) {
                            if (!it.isInputShutdown) {
                                val byteArray = ByteArray(1024)
                                //循环执行read，用来接收数据。
                                // 数据存在buffer中，count为读取到的数据长度。
                                val count = mInStream?.read(byteArray)
                                if (count != -1) {
                                    setText(String(byteArray))
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace();
            }
        }).start()
    }


    //关闭Socket
    fun closeConnection() {
        try {
            //关闭输出流
            mOutStream?.close()
            mOutStream = null
            //关闭输入流
            mInStream?.close()
            mInStream = null
            clientSocket?.close()
            clientSocket = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * wifi获取 已连接网络路由  路由ip地址
     * @param context
     * @return
     */
    fun getWifiRouteIPAddress(): String? {

        val wifiService = ProjectApplication.getApp().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiService.dhcpInfo
        //        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
        //        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        //        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        val routeIp: String = Formatter.formatIpAddress(dhcpInfo.gateway)

        Log.i("route ip", "wifi route ip：$routeIp")
        return routeIp
    }


}