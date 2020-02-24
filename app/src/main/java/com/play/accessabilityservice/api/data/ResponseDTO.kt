package com.play.accessabilityservice.api.data

/**
 * Created by ChanHong on 2020-02-16
 *
 */
data class ResponseDTO(
    //当前页面停留的url
    var url: String = "",
    //响应头
    var responseHeaders: MutableList<Header> = mutableListOf(),
    //html片段
    var html: String = "",
    //截屏，base64编码的图片
    var screenshot: String = "",
    var xhrInfo: MutableList<XhrDTO> = mutableListOf()
    //拦截的xhr请求
//    var xhr: List<String>
)

data class Header(
    var key: String,
    var value: String
)