package cn.com.medicalmeasurementassistant.utils.wifi_utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.PatternMatcher
import android.os.ResultReceiver
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import cn.com.medicalmeasurementassistant.app.ProjectApplication
import cn.com.medicalmeasurementassistant.utils.LogUtil
import cn.com.medicalmeasurementassistant.utils.PermissionHelper
import com.blankj.utilcode.util.NetworkUtils
import java.lang.reflect.Field
import java.lang.reflect.Method


object MyWifiManager {
    private val mWifiManager by lazy { initWifiManager(ProjectApplication.getApp()) }

    private fun initWifiManager(context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun openWifi(context: Activity) {


//        createWifiHotspot(context)

        val arrayOf = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
        }


//        val arrayOf = arrayOf("android.permission.CHANGE_WIFI_STATE", "android.permission.ACCESS_COARSE_LOCATION")
        val hasPermission = PermissionHelper.hasPermission(context, arrayOf)
//
        if (hasPermission) {
            if (mWifiManager.isWifiEnabled) {
                scan()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                setWifiApEnabledForAndroidO(context)
                NetworkUtils.setWifiEnabled(true)

            } else {
//                NetworkUtils.setWifiEnabled(true)
            }


//            connectAP_Q(ProjectApplication.getApp(), "1564558", "12121122")
//            mWifiManager.isWifiEnabled = true
        }
//        mWifiManager.isWifiEnabled = trumWifiManagere
//        NetworkUtils.setWifiEnabled(true)
    }

    fun connectAP(ssid: String, psd: String): Boolean {
//        mWifiManager.setTdlsEnabledWithMacAddress()
        return true
    }

    fun scan() {
        var scanResults = mWifiManager.scanResults
        var size = scanResults.size

        for (i in scanResults) {
            if ("12121122345" == i.SSID) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    connectAP_Q(ProjectApplication.getApp(), i.SSID, i.SSID)
                }
                break
            }

            Log.i("wifi_scan---", "  ssid = ${i.SSID}  level = ${i.level}")


        }

    }

//    private fun createWifiHotspot(activity: Activity) {
//        if (mWifiManager.isWifiEnabled) {
//            mWifiManager.isWifiEnabled = false
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.d("MainActivity", "Android 8.0及以上")
//            if (!Settings.System.canWrite(activity)) {
//                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + activity.application.packageName))
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                activity.applicationContext.startActivity(intent)
//            } else {
////                setWifiApEnabledForAndroid_O()
//            }
//            return
//        }
//        Log.d("MainActivity", "Android 8.0及以下")
////        setWifiApEnabledForAndroid()
//    }

    private fun setWifiApEnabledForAndroidO(activity: Activity) {
        val connManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return
        val iConnMgrField: Field
        try {
            iConnMgrField = connManager::class.java.getDeclaredField("mService")
            iConnMgrField.isAccessible = true
            val iConnMgr: Any = iConnMgrField.get(connManager)
            val iConnMgrClass = Class.forName(iConnMgr.javaClass.name)
            val startTethering: Method = iConnMgrClass.getMethod("startTethering", Int::class.javaPrimitiveType, ResultReceiver::class.java, Boolean::class.javaPrimitiveType)
            startTethering.invoke(iConnMgr, 0, null, true)
            Toast.makeText(ProjectApplication.getApp().applicationContext, "热点创建成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//

    @RequiresApi(api = Build.VERSION_CODES.Q)
//    @Throws(InterruptedException::class)
    private fun connectAP_Q(context: Context, ssid: String, pass: String): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val specifier: NetworkSpecifier = WifiNetworkSpecifier.Builder()
                .setSsidPattern(PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
                .setWpa2Passphrase(pass)
                .build()
        //创建一个请求
        val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //创建的是WIFI网络。
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED) //网络不受限
                .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED) //信任网络，增加这个连个参数让设备连接wifi之后还联网。
                .setNetworkSpecifier(specifier)
                .build()
        connectivityManager.requestNetwork(request, object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                //TODO 连接OK，做些啥
            }

            override fun onUnavailable() {
                //TODO 连接失败，或者被用户取消了，做些啥
            }
        })
        return true
    }


}