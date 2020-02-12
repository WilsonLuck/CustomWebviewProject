package com.play.accessabilityservice.api.data

import java.io.Serializable

/**
 * Created by ChanHong on 2020-02-12
 *.putExtra("url", url)
.putExtra("get", get)
.putExtra("params", params) //和get的参数一样 用 &链接，类似：name=hc&age=12
.putExtra("needModifyRequest", needModifyRequest)//是否需要修改请求，例如修改请求头
.putExtra("injectJavascript", injectJavascript)//需要注入的js
.putExtra("clearCookies", clearCookies)//是否清除cookies再访问
.putExtra("headers",headers)//请求头
 */

data class RequestDTO(
    //需要webview打开的url
    var url:String,


    //{GET/POST/PUT}，请求服务器的方法，默认GET
    var method: String = "GET",

    //当method=POST的时候使用，默认 application/x-www-form-urlencoded
    var postContentType: String = "application/x-www-form-urlencoded",

    //当method=POST的时候使用，传入编码后的数组
    var formData: String = "",

    //让手机端的浏览器调用指定的代理服务器
    //1. 类型: HTTPS/SOCKS5
    //2. IP: 代理的IP地址
    //3. PORT: 代理的端口
    //4. PROXY_USERNAME: 代理的用户名，可为空
    //5. PROXY_PASSWORD: 代理的密码，可为空
    var proxy: String = "",

    //可同时传入多个headers，用于模拟其它设备或者 其它环境
    var sendHeaders: String = "",

    //用于控制返回结果的时候会同时返回网页请求 的头, 类似如下结果
    var getHeaders: Boolean = true,

    //即返回当前网页的URL。 因为某些网页会进行 javascript跳转到新的URL，网页最终停留的网址和请求的网址并不一样。默认总是返回 当前的URL
    var getUrl: Boolean = true,

    //即每次请求时浏览器会清空所有的本地 Cookie
    var clearCookie: Boolean = true,

    //传入base64编码后的javascript代码用于执行当 前网页操作
    var javascriptCode: String = "",

    //是否需要返回当前网页截图
    var screenshot: Boolean = false,

    //正则表达式， 如果被访问的页面进行某种形式的 跳转(javascript重定向，meta refresh等)，如果跳转的网页符合block_url_pattern ，则直接返回网址
    var blockUrlPattern: String = "",

    //正则表达式列表， 如果被访问的页面 有多个XHR请求，并且XHR请求的网址符合​block_xhr_request_pattern，则返回并且 拦截XHR的请求数据
    var blockXhrRequestPattern: String = ""
) : Serializable