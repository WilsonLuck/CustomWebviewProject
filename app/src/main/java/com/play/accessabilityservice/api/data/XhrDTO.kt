package com.play.accessabilityservice.api.data

/**
 * Created by ChanHong on 2020-02-24
 *
 */
data class XhrDTO(
    var method: String = "",
    var url: String = "",
    var body: String = "",
    var requestHeaders: MutableList<Header> = mutableListOf()
)