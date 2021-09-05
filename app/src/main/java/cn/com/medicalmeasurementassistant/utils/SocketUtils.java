package cn.com.medicalmeasurementassistant.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * user: Created by jid on 2020/10/19
 * email: jid@hwtc.com.cn
 * description:
 */
public class SocketUtils {

    public static String getHostIp(String ipTag) {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.contains(ipTag)){
                            return hostAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
}

