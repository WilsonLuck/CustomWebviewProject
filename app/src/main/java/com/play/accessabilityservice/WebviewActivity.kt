package com.play.accessabilityservice

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.play.accessabilityservice.api.InternalOkHttpClient
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.*
import kotlinx.android.synthetic.main.activity_webview.*


/**
 * Created by ChanHong on 2020-02-10
 *
 */
class WebviewActivity : AppCompatActivity() {

    companion object {
        fun newIntent(
            context: Context,
            url: String,
            get: Boolean = true,
            params: String = "",
            needModifyRequest: Boolean = false,
            injectJavascript: String = "",
            clearCookies: Boolean = true
        ): Intent {
            return Intent(context, WebviewActivity::class.java)
                .putExtra("url", url)
                .putExtra("get", get)
                .putExtra("params", params) //和get的参数一样 用 &链接，类似：name=hc&age=12
                .putExtra("needModifyRequest", needModifyRequest)//是否需要修改请求，例如修改请求头
                .putExtra("injectJavascript", injectJavascript)//需要注入的js
                .putExtra("clearCookies", clearCookies)//是否清除cookies再访问
        }
    }

    val needModifyRequest: Boolean by lazy {
        intent.getBooleanExtra(
            "needModifyRequest",
            false
        )
    }//是否需要修改请求，例如 请求头，请求参数等等
    val injectJavascript: String by lazy { intent.getStringExtra("injectJavascript") }//是否需要js注入
    val currentUrl: String by lazy { intent.getStringExtra("url") }//传进来的URL
    val get: Boolean by lazy { intent.getBooleanExtra("get", true) } //是否为get请求
    val params: String by lazy { intent.getStringExtra("params") } //如果是post请求，则需要传这个参数
    val clearCookies: Boolean by lazy {
        intent.getBooleanExtra(
            "clearCookies",
            true
        )
    }//是否清除cookies再访问

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
        // webSetting.setDatabaseEnabled(true);
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
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        webview.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webview.webViewClient = WBClient()
        webview.webChromeClient = WBChromeClient()
        if (clearCookies) {
            CookieManager.getInstance()
        }
        if (get) {
            webview.loadUrl(currentUrl)
        } else {
            webview.postUrl(currentUrl, params.toByteArray())
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
            Logger.e("onReceivedError" + error.toString())
            super.onReceivedError(view, request, error)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Logger.i("shouldInterceptRequest: ${request!!.url}")
            //当webview请求的url和需求请求的url一致才会进行拦截
            return if (currentUrl == request.url.toString()) {
                InternalOkHttpClient.modifyRequest(request, params) {
                    Logger.d(it)
                }
            } else {
                return super.shouldInterceptRequest(view, request)
            }

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (injectJavascript.isNotBlank()) {
                view!!.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
            }
            Logger.i("current url is :$url")
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Logger.i("shouldOverrideUrlLoading: ${request!!.url}")

            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Logger.i("shouldOverrideUrlLoading: $url")
            return super.shouldOverrideUrlLoading(view, url)
        }


    }

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processHTML(html: String) {
            Logger.i(html)
        }
    }


}