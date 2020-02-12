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
import com.play.accessabilityservice.api.data.RequestDTO
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.*
import kotlinx.android.synthetic.main.activity_webview.*
import java.io.IOException
import java.io.Serializable


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
            Logger.e("onReceivedError" + error.toString())
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
            Logger.i("shouldInterceptRequest: ${request!!.url}")
            //当webview请求的url和需求请求的url一致才会进行拦截
            try {
                return if (requestDTO.url == request.url.toString()) {

                    InternalOkHttpClient.modifyRequest(request, requestDTO) {
                        Logger.d("headers \n $it")
                    }
                } else {
                    return super.shouldInterceptRequest(view, request)
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
            return super.shouldInterceptRequest(view, request)
        }

        /**
         * 渲染完成，需要拿到html代码
         */
        override fun onPageFinished(view: WebView?, url: String?) {
            if (requestDTO.javascriptCode.isNotBlank()) {
                view!!.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
                view.loadUrl("javascript:${requestDTO.javascriptCode}")
            }
            Logger.i("current url is :$url")
        }

        /**
         * 重定向，需要根据正则判断判断是否继续加载
         */
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Logger.i("shouldOverrideUrlLoading: ${request!!.url}")

            return super.shouldOverrideUrlLoading(view, request)
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


}