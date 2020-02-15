package com.play.accessabilityservice.socket

/**
 * Created by ChanHong on 2019/3/21
 *
 */

class Commands2Server {
    companion object {
        const val SYSTEM_INFO = "system_info"
        const val DATA_CALLBACK = "data_callback"//需要把获得的数据回传给服务器
    }
}