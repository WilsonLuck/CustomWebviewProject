package com.play.accessabilityservice.api

import android.content.Context
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.play.accessabilityservice.BuildConfig
import com.play.accessabilityservice.WebviewActivity
import com.play.accessabilityservice.api.data.ProxyDTO
import com.play.accessabilityservice.api.data.RequestDTO
import com.play.accessabilityservice.util.StreamHelper
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import okhttp3.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

class InternalOkHttpClient {

    companion object {

        const val KEY_REQUEST_HEADERS = "request_headers"//请求头
        const val KEY_RESPONSE_HEADERS = "response_headers"//服务器返回的headers
        const val KEY_FINAL_URL = "final_url" //网页最后停止的url
        var okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(SSLHandler.getSslSocketFactory())
            .hostnameVerifier(SSLHandler.getHostnameVerifier())
            .build().apply {
                if (BuildConfig.DEBUG) {//printf logs while  debug
                    this?.newBuilder()
                        ?.addInterceptor(PreIntercepet().setLevel(PreIntercepet.Level.BODY))
                        ?.build()
                }
            }

        fun modifyRequest(
            originRequest: WebResourceRequest,
            requestDTO: RequestDTO,
            context: Context,
            myJavaScriptInterface: WebviewActivity.MyJavaScriptInterface,
            action: (sourceMap: MutableMap<String, Any>) -> Unit = {}
        ): WebResourceResponse {
            Logger.i("start modifyRequest")
            var sourceMap = mutableMapOf<String, Any>()

            val oldUri = originRequest.url
            val oldSchem = oldUri.scheme //http or https
            val oldHost = oldUri.host// 192.168.1.1
            val oldPath = oldUri.path// /gettest
            val queries = oldUri.queryParameterNames //
            var newUrl = originRequest.url.toString()
            var oldPort = oldUri.port.let {
                return@let if (it <= 0) "" else ":$it"
            }
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
                    newQueries.append(key).append("=").append(oldUri.getQueryParameter(key))
                    if (index < queries.size - 1) {
                        newQueries.append("&")
                    }
                }
                if (newQueries.isNotBlank()) {
                    newQueries.insert(0, "?")
                }
                newUrl = "$oldSchem://$oldHost$oldPort$oldPath$newQueries"
                newRequest = newRequest.newBuilder().url(newUrl).get().build()
            }
            /**
             * 如果是POST请求，重新构造请求体
             */
            else {
                var formBody = FormBody.Builder()
                if (requestDTO.formData.isNotBlank()) {
                    val params = requestDTO.formData.split("&")
                    params.forEach {
                        val param = it.split("=")
                        if (param.size == 2) {
                            formBody.add(param[0], param[1])
                        }
                    }
                }
                newRequest = newRequest.newBuilder().url(newUrl).post(formBody.build()).build()
            }

            /**
             * 代理设置
             */
            if (requestDTO.proxy.isNotBlank()) {
                var gson = Gson().fromJson(requestDTO.proxy, ProxyDTO::class.java)
                var proxtTyp: Proxy.Type = Proxy.Type.DIRECT
                when (gson.proxyType
                    ) {
                    "HTTP" -> {
                        proxtTyp = Proxy.Type.HTTP
                    }
                    "SOCKS" -> {
                        proxtTyp = Proxy.Type.SOCKS
                    }
                }
                okHttpClient = okHttpClient.newBuilder()
                    .proxy(Proxy(proxtTyp, InetSocketAddress(gson.serverAddress, gson.port)))
                    .build()
            }else{
                okHttpClient = okHttpClient.newBuilder()
                    .proxy(null)
                    .build()
            }

            if (requestDTO.sendHeaders.isNotBlank()) {
                val headers = requestDTO.sendHeaders.split("&")
                headers.forEach {
                    val key = it.split("=")[0]
                    val value = it.split("=")[1]
                    newRequest =
                        newRequest.newBuilder().removeHeader(key).addHeader(key, value).build()
                }
            }
            response = okHttpClient.newCall(newRequest).execute()
            sourceMap[KEY_REQUEST_HEADERS] = newRequest.headers()
            response.headers().toMultimap()
            sourceMap[KEY_RESPONSE_HEADERS] = response.headers()
            action(sourceMap)
            okHttpClient = okHttpClient.newBuilder().proxy(Proxy.NO_PROXY).build()

            return injectIntercept(
                WebResourceResponse(
                    "text/html", // You can set something other as default content-type
                    response.header("content-encoding", "utf-8"),
                    response.body()!!.byteStream()
                ), context = context, myJavaScriptInterface = myJavaScriptInterface
            )
        }

        /**
         * 如果请求是网页，则html注入
         *
         * @param response
         * @param context
         * @return
         */
        private fun injectIntercept(
            response: WebResourceResponse,
            context: Context,
            myJavaScriptInterface: WebviewActivity.MyJavaScriptInterface

        ): WebResourceResponse {
            val encoding = response.encoding
            var mime = response.mimeType

            // WebResourceResponse的mime必须为"text/html",不能是"text/html; charset=utf-8"
            if (mime.contains("text/html")) {
                mime = "text/html"
            }

            val responseData = response.data
            val injectedResponseData = injectInterceptToStream(
                context,
                responseData,
                mime,
                myJavaScriptInterface
            )
            return WebResourceResponse(mime, encoding, injectedResponseData)
        }

        /**
         * 如果请求是网页，则html注入
         *
         * @param context
         * @param is
         * @param mime
         * @param charset
         * @return
         */
        private fun injectInterceptToStream(
            context: Context,
            `is`: InputStream,
            mime: String,
            myJavaScriptInterface: WebviewActivity.MyJavaScriptInterface
        ): InputStream {
            try {
                var pageContents = StreamHelper.consumeInputStream(`is`)
                if (mime.contains("text/html")) {
                    pageContents = myJavaScriptInterface
                        .enableIntercept(context, pageContents)
                        .toByteArray()
                }

                return ByteArrayInputStream(pageContents)
            } catch (e: Exception) {
                throw RuntimeException(e.message)
            }

        }
    }


}