package com.play.accessabilityservice.api.data

/**
 * Created by ChanHong on 2020-02-16
 *
 */
data class ResponseDTO(
    //当前页面停留的url
    var url: String,
    //响应头
    var responseHeaders: Map<String, String>,
    //html片段
    var html: String,
    //截屏，base64编码的图片
    var screenshot: String
)