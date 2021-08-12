package cn.com.medicalmeasurementassistant.utils.communication

import android.app.Activity
import android.provider.SyncStateContract
import android.util.Log
import android.widget.TextView
import cn.com.medicalmeasurementassistant.utils.StringUtils
import cn.com.medicalmeasurementassistant.utils.communication.MyClientSocketManager.getWifiRouteIPAddress
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

/**
 * @author Mr SHI
 * created at 2021/8/9 0009 下午 15:53
 * desc:自定义Socket服务端
 */
object MyServerSocketManager {
    //有设备正在连接热点
    const val DEVICE_CONNECTING = 1

    //有设备连上热点
    const val DEVICE_CONNECTED = 2

    //发送消息成功
    const val SEND_MSG_SUCCESS = 3

    //发送消息失败
    const val SEND_MSG_ERROR = 4

    //获取新消息
    const val GET_MSG = 6

    const val SERVER_SOCKET_PORT = 54188
//    const val SERVER_SOCKET_PORT = 5554

    private var textView: TextView? = null
    private var os: OutputStream? = null
    private var pw: PrintWriter? = null

    // 初始化一个ServerSocket
    private var serverSocket: ServerSocket? = null

    fun setTextView(textView: TextView) {
        this.textView = textView
        startServerSocket()
    }

    private fun setText(text: String?) {
        (textView?.context as Activity).runOnUiThread {
            textView?.text = text
        }
    }

    private fun startServerSocket() {


        Thread(Runnable {
            try {
                serverSocket = ServerSocket(SERVER_SOCKET_PORT)
                var localSocketAddress = serverSocket?.localSocketAddress
                var toString = localSocketAddress.toString()
                // 获取本地host
                val address = InetAddress.getLocalHost()
                // 获取ip地址
                val ipAddress = address.hostAddress
                setText(ipAddress)
                setText("启动serverSocket,等待连接+$toString")
                val accept = serverSocket?.accept()
                accept ?: return@Runnable
                setText("连接成功，client ip :" + accept.inetAddress?.address)
                setText(accept.inetAddress.hostAddress)
                paresSocketMsg(accept)
            } catch (e: Exception) {
                e.printStackTrace()
                setText(e.message)
            }

        }).start()


    }

    private fun paresSocketMsg(accept: Socket) {
        val inputStream = accept.getInputStream()
        val isr = InputStreamReader(inputStream, "UTF-8")
        val br = BufferedReader(isr)
        os = accept.getOutputStream()
        //字节数组
        val bytes = ByteArray(1024)
        //字节数组长度
        var len = 0
        var i = 1

        while (true) {
            //读取客户端请求信息

            while (inputStream.available() != 0 && inputStream.read(bytes) != -1) {
                //接收到的消息
                val s = String(bytes)
                //这里你可以根据你的实际消息内容做相应的逻辑处理
                setText("服务端收到客户端消息:$s")
            }
            os?.write(("服务端发送消息$i".toByteArray()))
            i++
            Thread.sleep(5000)
        }
    }

    fun sendMsg(msg: String) {

        Thread(Runnable {
            if (!StringUtils.isEmpty(msg) && os != null){
                try {   //发送
                    os?.write(msg.toByteArray())
                    os?.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }).start()


    }


    private fun close() {
        serverSocket?.close()
        serverSocket = null
        //关闭输出流
        os?.close()
        os = null

    }


}