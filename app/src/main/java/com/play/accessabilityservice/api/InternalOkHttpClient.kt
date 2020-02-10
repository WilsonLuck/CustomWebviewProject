package com.play.accessabilityservice.api

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.play.accessabilityservice.BuildConfig
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class InternalOkHttpClient {
    companion object {

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

        fun modifyRequest(originRequest: WebResourceRequest): WebResourceResponse {

            val oldUri = originRequest.url
            val oldSchem = oldUri.scheme //http or https
            val oldHost = oldUri.host// 192.168.1.1
            val oldPath = oldUri.path
            val queries = oldUri.queryParameterNames
            var newUrl = originRequest.url.toString()
            var oldPort = oldUri.port

            var newQueries = StringBuilder()
            var newRequest = Request.Builder().build()

            queries.forEachIndexed { index, key ->
                newQueries.append(key).append("=").append(oldUri.getQueryParameter(key) + "swl")
                if (index < queries.size - 1) {
                    newQueries.append("&")
                }
            }
            if (originRequest.method.toUpperCase() == "GET") {
                newUrl = "$oldSchem://$oldHost:$oldPort$oldPath?$newQueries"
                newRequest = newRequest.newBuilder().url(newUrl).get().build()
            } else {
                val requestBody = FormBody.Builder().build()
                newRequest = newRequest.newBuilder().url(newUrl).post(requestBody).build()
            }


            var response = getOkhttpClient().newCall(newRequest).execute()
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