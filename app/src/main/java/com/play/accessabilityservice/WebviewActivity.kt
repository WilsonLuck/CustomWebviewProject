package com.play.accessabilityservice

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
import com.play.accessabilityservice.api.data.ProxyDTO
import com.play.accessabilityservice.api.data.RequestDTO
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.*
import kotlinx.android.synthetic.main.activity_webview.*
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

    val requestDTO: RequestDTO by lazy {
        intent.getSerializableExtra("requestDTO") as RequestDTO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webSetting = webview.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        webSetting.databaseEnabled = true
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        webSetting.setAppCachePath(this.getDir("appcache", 0).path)
        webSetting.databasePath = this.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(
            this.getDir("geolocation", 0)
                .path
        )
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webview.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
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
    }

    inner class WBChromeClient : WebChromeClient() {
        override fun onProgressChanged(p0: WebView?, p1: Int) {
            if (p1 == 100) {
                progressBar.visibility = View.GONE//加载完网页进度条消失
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
         * 拦截请求
         */
        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Logger.i("shouldInterceptRequest headers: ${request!!.requestHeaders}")
            Logger.i("shouldInterceptRequest urls : ${request.url}")

            if (requestDTO.blockXhrRequestPattern.isNotBlank()) {
                val pattern = Pattern.compile(requestDTO.blockXhrRequestPattern)
                val matecher = pattern.matcher(request.url.toString())
                if (matecher.find())
                    Logger.i("find blockXhrRequestPattern return the request")
                return null
            }

            //当webview请求的url和需求请求的url一致才会进行拦截
            if (requestDTO.url == request.url.toString()) {
                return try {
                    InternalOkHttpClient.modifyRequest(request, requestDTO) {
                        Logger.d("headers \n $it")
                    }
                } catch (e: java.lang.Exception) {
                    super.shouldInterceptRequest(view, request)
                }

            } else {
                return super.shouldInterceptRequest(view, request)
            }
        }

        /**
         * 渲染完成，需要拿到html代码
         */
        override fun onPageFinished(view: WebView?, url: String?) {
            view!!.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
            if (requestDTO.javascriptCode.isNotBlank()) {
                view.loadUrl("javascript:${requestDTO.javascriptCode}")
            }
            Logger.i("current url is :$url")
        }

        override fun shouldOverrideUrlLoading(p0: WebView?, p1: WebResourceRequest?): Boolean {
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
            Logger.i("shouldOverrideUrlLoading${p1!!.url}")
            try {
                if (!p1!!.url.toString().startsWith("http://") && !p1.url.toString().startsWith("https://")) {
                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(p1.url.toString()))
                    startActivity(intent)
                    return true
                }
            } catch (e: Exception) {//防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }

            // TODO Auto-generated method stub
            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
            p0!!.loadUrl(p1.url.toString())
            return true
        }

        override fun onLoadResource(p0: WebView?, p1: String?) {
            Logger.i("onLoadResource:$p1")
            /*
            拦截xhr请求
             */
            if (requestDTO.blockXhrRequestPattern.isNotBlank()) {
                val pattern = Pattern.compile(requestDTO.blockXhrRequestPattern)
                val matecher = pattern.matcher(p1)
                if (matecher.find())
                    Logger.i("find blockXhrRequestPattern return the request")
                return
            }
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

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processHTML(html: String) {
            Logger.i(html)
        }

        @JavascriptInterface
        fun changeA1() {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WebviewProxySetting.revertBackProxy(APP::class.java.name)
    }


}