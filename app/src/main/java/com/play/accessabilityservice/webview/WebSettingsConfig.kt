package com.play.accessabilityservice.webview

import com.play.accessabilityservice.APP
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

/**
 * Created by ChanHong on 2020-02-24
 *
 */
class WebSettingsConfig {
    companion object {
        fun setConfig(webview: WebView) {
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
            webSetting.setAppCachePath(APP.context4Application!!.getDir("appcache", 0).path)
            webSetting.databasePath = APP.context4Application!!.getDir("databases", 0).path
            webSetting.setGeolocationDatabasePath(
                APP.context4Application!!.getDir("geolocation", 0)
                    .path
            )
            webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        }
    }
}