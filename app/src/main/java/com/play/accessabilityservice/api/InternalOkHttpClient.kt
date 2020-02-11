package com.play.accessabilityservice.api

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.play.accessabilityservice.BuildConfig
import okhttp3.*
import java.util.concurrent.TimeUnit

class InternalOkHttpClient {

    companion object {

        const val KEY_REQUEST_HEADERS = "request_headers"//请求头
        const val KEY_RESPONSE_HEADERS = "response_headers"//服务器返回的headers
        const val KEY_FINAL_URL = "final_url" //网页最后停止的url

        fun getOkhttpClient(): OkHttpClient {
            var okHttpClient: OkHttpClient? = null

            if (okHttpClient == null) {
                okHttpClient = OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                if (BuildConfig.DEBUG) {//printf logs while  debug
                    okHttpClient = okHttpClient?.newBuilder()
                        ?.addInterceptor(PreIntercepet().setLevel(PreIntercepet.Level.BODY))
                        ?.build()
                }
            }
            return okHttpClient!!
        }

        fun modifyRequest(
            originRequest: WebResourceRequest,
            newParams: String = "",
            action: (sourceMap: MutableMap<String, Any>) -> Unit = {}
        ): WebResourceResponse {

            var sourceMap = mutableMapOf<String, Any>()

            val oldUri = originRequest.url
            val oldSchem = oldUri.scheme //http or https
            val oldHost = oldUri.host// 192.168.1.1
            val oldPath = oldUri.path// /gettest
            val queries = oldUri.queryParameterNames //
            var newUrl = originRequest.url.toString()
            var oldPort = oldUri.port
            var oldHeader = originRequest.requestHeaders
            var newHeaders: Headers = Headers.of(oldHeader)


            var newRequest = Request.Builder().url(newUrl).headers(newHeaders).build()
            var response: Response?


            /**
             * 如果是GET请求，需要重新构造 url后面的请求参数
             */
            if (originRequest.method.toUpperCase() == "GET") {
                var newQueries = StringBuilder()
                //请求参数，get请求的参数在url的？ 后面
                queries.forEachIndexed { index, key ->
                    newQueries.append(key).append("=").append(oldUri.getQueryParameter(key) + "swl")
                    if (index < queries.size - 1) {
                        newQueries.append("&")
                    }
                }
                newUrl = "$oldSchem://$oldHost:$oldPort$oldPath?$newQueries"
                newRequest = newRequest.newBuilder().url(newUrl).get().build()
            }
            /**
             * 如果是POST请求，重新构造请求体
             */
            else {
                var formBody = FormBody.Builder()
                if (newParams.isNotBlank()) {
                    val params = newParams.split("&")
                    params.forEach {
                        val param = it.split("=")
                        if (param.size == 2) {
                            formBody.add(param[0], param[1])
                        }
                    }
                }
                newRequest = newRequest.newBuilder().url(newUrl).post(formBody.build()).build()
            }


            response = getOkhttpClient().newCall(newRequest).execute()
            sourceMap[KEY_REQUEST_HEADERS] = newRequest.headers()
            sourceMap[KEY_RESPONSE_HEADERS] = response.headers()
            action(sourceMap)

            return WebResourceResponse(
                response.header(
                    "text/html",
                    response.body()!!.contentType()!!.type()
                ), // You can set something other as default content-type
                response.header("content-encoding", "utf-8"),
                response.body()!!.byteStream()
            )
        }
    }
}