package com.play.accessabilityservice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.play.accessabilityservice.api.InternalOkHttpClient
import com.play.accessabilityservice.api.WebviewProxySetting
import com.play.accessabilityservice.api.data.*
import com.play.accessabilityservice.socket.SocketConductor
import com.play.accessabilityservice.system.ScreenShot
import com.play.accessabilityservice.util.StreamHelper
import com.play.accessabilityservice.webview.WebSettingsConfig
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import okhttp3.Headers
import org.jsoup.Jsoup
import java.io.IOException
import java.io.Serializable
import java.net.Proxy
import java.util.regex.Pattern


/**
 * Created by ChanHong on 2020-02-10
 *
 */
class WebviewActivity : AppCompatActivity() {

    companion object {
        fun newIntent(
            context: Context,
            requestDTO: RequestDTO
        ): Intent {
            return Intent(context, WebviewActivity::class.java).putExtra(
                "requestDTO",
                requestDTO as Serializable
            )

        }
    }

    private val MARKER = "AJAXINTERCEPT"

    //请求参数Map集
    private val ajaxRequestContents = mutableMapOf<String, XhrDTO>()
    //webhook的请求参数
    val requestDTO: RequestDTO by lazy {
        intent.getSerializableExtra("requestDTO") as RequestDTO
    }

    //当前加载的 url
    var currentLoadURL = ""

    //拦截后返回的 headers
    var responseHeaders = mutableMapOf<String, MutableList<String>>()
    //bitmap base64编码
    var img2Base64 = ""
    //渲染后的html代码
    var html = ""
    var myJavaScriptInterface = MyJavaScriptInterface()
    //返回给socket的具体信息
    var responseDTO = ResponseDTO()
    //被block的Xhr请求
    var xhrBlockList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        WebSettingsConfig.setConfig(webview)
        webview.addJavascriptInterface(myJavaScriptInterface, "HTMLOUT")
        webview.webViewClient = WBClient()
        webview.webChromeClient = WBChromeClient()


        if (requestDTO.clearCookie) {
            CookieManager.getInstance().removeAllCookie()
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestDTO.method == "GET") {
            webview.loadUrl(requestDTO.url)
        } else {
            webview.postUrl(requestDTO.url, requestDTO.formData.toByteArray())
        }
//        webview.loadDataWithBaseURL( null, myJavaScriptInterface.enableIntercept(this), "text/html", "utf-8", null );

    }

    inner class WBChromeClient : WebChromeClient() {

        override fun onProgressChanged(p0: WebView?, p1: Int) {
            if (p1 == 100) {
                Logger.i("onProgress Render Completed")
                progressBar.visibility = View.GONE//加载完网页进度条消失
                GlobalScope.async {
                    delay(3000 + requestDTO.pageWait.toLong())
                    /*
                    是否需要返回截屏
                     */
                    if (requestDTO.screenshot) {
                        img2Base64 =
                            ScreenShot.Bitmap2Base64(ScreenShot.activityShot(this@WebviewActivity))
                                .replace("\n", "")
                    }
                    val newResponseHeaders = mutableListOf<Header>()
                    /*
                    如果不需要返回 响应头
                     */
                    if (requestDTO.getHeaders) {
                        responseHeaders.forEach {
                            newResponseHeaders.add(
                                Header(
                                    it.key,
                                    it.value.toString().replace("[", "").replace("]", "")
                                )
                            )
                        }
                    }
                    /*
                    如果不需要返回URL
                     */
                    if (!requestDTO.getUrl) {
                        currentLoadURL = ""
                    }
                    ajaxRequestContents
                    val res = responseDTO.copy(
                        url = currentLoadURL,
                        responseHeaders = newResponseHeaders,
                        html = html,
                        screenshot = img2Base64
                    )

                    SocketConductor.instance.socket!!.emit(
                        requestDTO.uuid4socketEvent,
                        Gson().toJson(res)
                    )
                    val nextIntent = Intent()
                    nextIntent.putExtra("next", true)
                    this@WebviewActivity.setResult(Activity.RESULT_OK, nextIntent)
                    finish()
                }

            } else {
                progressBar.visibility = View.VISIBLE//开始加载网页时显示进度条
                progressBar.progress = p1//设置进度值
            }
            super.onProgressChanged(p0, p1)
        }

    }

    inner class WBClient : WebViewClient() {

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            Logger.i(errorResponse.toString())
            super.onReceivedHttpError(view, request, errorResponse)
        }


        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            Logger.e("onReceivedError" + error!!.description)
            super.onReceivedError(view, request, error)
        }
        /**
         ** This here is the "fixed" shouldInterceptRequest method that you should override.
         ** It receives a WriteHandlingWebResourceRequest instead of a WebResourceRequest.
         */

        /**
         * 拦截请求
         */
        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Logger.i("shouldInterceptRequest headers: ${request!!.requestHeaders}")
            Logger.i("shouldInterceptRequest urls : ${request.url}")


            //当webview请求的url和需求请求的url一致才会进行拦截
            if (requestDTO.url == request.url.toString()) {
                var response: WebResourceResponse
                try {
                    response = InternalOkHttpClient.modifyRequest(
                        request,
                        requestDTO,
                        this@WebviewActivity,
                        myJavaScriptInterface
                    ) {
                        responseHeaders.putAll((it[InternalOkHttpClient.KEY_RESPONSE_HEADERS] as Headers).toMultimap())
                    }

                    return response
                } catch (e: java.lang.Exception) {
                    Logger.e(e.toString())
                    return null
                }

            }
            val res = super.shouldInterceptRequest(view, request)
            if (requestDTO.blockXhrRequestPattern.isNotBlank()) {
                val pattern = Pattern.compile(requestDTO.blockXhrRequestPattern)
                val matecher = pattern.matcher(request.url.toString())
                if (matecher.find()) {
                    ajaxRequestContents.forEach {
                        if (request.url.toString().contains(it.value.url)
                        ) {
                            val newResponseHeaders = mutableListOf<Header>()
                            request.requestHeaders.forEach { entry ->
                                newResponseHeaders.add(Header(entry.key, entry.value))
                            }
                            responseDTO.xhrInfo.add(
                                XhrDTO(
                                    it.value.method,
                                    request.url.toString(),
                                    it.value.body, newResponseHeaders
                                )
                            )
                        }


                    }
                    Logger.i("find blockXhrRequestPattern return the request")
                }
                return null
            }
            return res
        }

        /**
         * 渲染完成，需要拿到html代码
         */
        override fun onPageFinished(view: WebView?, url: String?) {
            currentLoadURL = url!!
            if (requestDTO.javascriptCode.isNotBlank()) {
                view!!.loadUrl("javascript:${requestDTO.javascriptCode}")
            }
            Logger.i("current url is :$url")
            view!!.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(p0: WebView?, p1: WebResourceRequest?): Boolean {
            //正则 用于判断是否需要重定向
            if (requestDTO.blockUrlPattern.isNotBlank()) {
                val regex = requestDTO.blockUrlPattern
                val pattern = Pattern.compile(regex)
                val matchers = pattern.matcher(p1!!.url.toString())
                if (matchers.find()) {
                    Toast.makeText(
                        this@WebviewActivity,
                        "${p1.url} has been abandon",
                        Toast.LENGTH_SHORT
                    ).show()
                    return true
                }
            }

            Logger.i("shouldOverrideUrlLoading${p1!!.url}")
            try {
                if (!p1.url.toString().startsWith("http://") && !p1.url.toString().startsWith("https://")) {
                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(p1.url.toString()))
                    startActivity(intent)
                    return true
                }
            } catch (e: Exception) {//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }

            // TODO Auto-generated method stub
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            requestDTO.url = p1.url.toString()
            p0!!.loadUrl(p1.url.toString())
            return true
        }

        override fun onLoadResource(p0: WebView?, p1: String?) {
            Logger.i("onLoadResource:$p1")
            super.onLoadResource(p0, p1)
        }

        override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {

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

                WebviewProxySetting.setProxy(gson.serverAddress, gson.port, APP::class.java.name)
            }
            super.onPageStarted(p0, p1, p2)
        }

    }

    /**
     * js脚本
     */
    inner class MyJavaScriptInterface {
        private var interceptHeader: String? = null

        @Throws(IOException::class)
        fun enableIntercept(context: Context, data: ByteArray): String {
            if (interceptHeader == null) {
                interceptHeader = String(
                    StreamHelper.consumeInputStream(context.assets.open("interceptheader.html"))
                )
            }

            val doc = Jsoup.parse(String(data))
            doc.outputSettings().prettyPrint(true)

            // Prefix every script to capture submits
            // Make sure our interception is the first element in the
            // header
            val element = doc.getElementsByTag("head")
            if (element.size > 0) {
                element[0].prepend(interceptHeader)
            }

            return doc.toString()
        }

        @JavascriptInterface
        fun processHTML(html: String) {
            Logger.i(html)
            this@WebviewActivity.html = html
        }

        @JavascriptInterface
        fun xhrSend(requestID: String, body: String) {
            Logger.i("ajax xhrSend id:${requestID} body:$body")
            if (ajaxRequestContents[requestID] != null && body.isNotEmpty()) {
                ajaxRequestContents[requestID]!!.body = body
            }
        }

        /**
         * xhr请求 open
         */
        @JavascriptInterface
        fun xhrOpen(method: String, url: String, requestID: String) {
            ajaxRequestContents[requestID] = XhrDTO(method, url)
            Logger.i("ajax xhrOpen method:$method url:$url requestID:$requestID")
        }

        @JavascriptInterface
        fun onLoad(xhr: String) {
            Logger.i("ajax onLoad: ${xhr}")
        }

        @JavascriptInterface
        fun onreadystatechange(xhr: String) {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        WebviewProxySetting.revertBackProxy(APP::class.java.name)
    }


}