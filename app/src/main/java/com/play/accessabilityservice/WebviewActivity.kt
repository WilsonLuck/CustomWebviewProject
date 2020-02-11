package com.play.accessabilityservice

import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.play.accessabilityservice.api.InternalOkHttpClient
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
            injectJavascript: String = ""

        ): Intent {
            return Intent(context, WebviewActivity::class.java)
                .putExtra("url", url)
                .putExtra("get", get)
                .putExtra("params", params) //和get的参数一样 用 &链接，类似：name=hc&age=12
                .putExtra("needModifyRequest", needModifyRequest)
                .putExtra("injectJavascript", injectJavascript)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        val webSetting = webview.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.loadWithOverviewMode = true
        webSetting.setAppCacheEnabled(true)
        webSetting.databaseEnabled = true
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        webSetting.loadsImagesAutomatically = true
        webSetting.allowContentAccess = true
        webview.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")

        webview.webViewClient = WBClient()
        if (get) {
            webview.loadUrl(currentUrl)
        } else {
            webview.postUrl(currentUrl, params.toByteArray())
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

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            Logger.e(error.toString())
            super.onReceivedSslError(view, handler, error)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            Logger.e(error.toString())
            super.onReceivedError(view, request, error)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            //当webview请求的url和需求请求的url一致才会进行拦截
            return if (currentUrl == request!!.url.toString()) {
                InternalOkHttpClient.modifyRequest(request,params) {
                    Logger.d(it)
                }
            } else {
                super.shouldInterceptRequest(view, request)
            }


        }


        override fun onPageFinished(view: WebView?, url: String?) {
            if (injectJavascript.isNotBlank()) {
                view!!.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);");
            }
        }
    }

    inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun processHTML(html: String) {
            Logger.i(html)
        }
    }


}