package com.play.accessabilityservice.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

/**
 * Created by ChanHong on 2020-02-15
 *
 */
data class SystemInfo(
    var devicesID: String,
    var systemModel: String = getSystemModel(),
    var deviceBrand: String = getDeviceBrand(),
    var ipAddress: String
) {


    companion object {
        fun UUID(): String {
            return ""
        }

        /**
         * 获取当前手机系统版本号
         *
         * @return 系统版本号
         */
        fun getSystemVersion(): String {
            return android.os.Build.VERSION.RELEASE
        }

        /**
         * 获取手机型号
         *
         * @return 手机型号
         */
        fun getSystemModel(): String {
            return android.os.Build.MODEL
        }

        /**
         * 获取手机厂商
         *
         * @return 手机厂商
         */
        fun getDeviceBrand(): String {
            return android.os.Build.BRAND
        }


        fun getIpAddress(context: Context): String? {
            val info = (context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (info != null && info.isConnected) {
                // 3/4g网络
                if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    try {
                        val en = NetworkInterface.getNetworkInterfaces()
                        while (en.hasMoreElements()) {
                            val intf = en.nextElement()
                            val enumIpAddr = intf.inetAddresses
                            while (enumIpAddr.hasMoreElements()) {
                                val inetAddress = enumIpAddr.nextElement()
                                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                    return inetAddress.getHostAddress()
                                }
                            }
                        }
                    } catch (e: SocketException) {
                        e.printStackTrace()
                    }

                } else if (info.type == ConnectivityManager.TYPE_WIFI) {
                    //  wifi网络
                    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo
                    return intIP2StringIP(wifiInfo.ipAddress)
                } else if (info.type == ConnectivityManager.TYPE_ETHERNET) {
                    // 有限网络
                    return getLocalIp()
                }
            }
            return null
        }

        private fun intIP2StringIP(ip: Int): String {
            return (ip and 0xFF).toString() + "." +
                    (ip shr 8 and 0xFF) + "." +
                    (ip shr 16 and 0xFF) + "." +
                    (ip shr 24 and 0xFF)
        }


        // 获取有限网IP
        private fun getLocalIp(): String {
            try {
                val en = NetworkInterface
                    .getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf
                        .inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: SocketException) {

            }

            return "0.0.0.0"

        }
    }
}