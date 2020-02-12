package com.play.accessabilityservice.api.data

/**
 * Created by ChanHong on 2020-02-12
 *
 */
data class ProxyDTO(
    var proxyType:String ="HTTP",
    var serverAddress: String,
    var port: Int,
    var account: String = "",
    var password: String = ""
)