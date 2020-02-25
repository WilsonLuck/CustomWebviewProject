package com.play.accessabilityservice

import android.app.Application
import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tencent.smtt.sdk.QbSdk

/**
 * Created by ChanHong on 2020-02-08
 *
 */
class APP : Application() {
    override fun onCreate() {
        super.onCreate()
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("My custom tag")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                Logger.i("onViewInitFinished")

            }

            override fun onViewInitFinished(p0: Boolean) {
                Logger.i("onViewInitFinished $p0")
            }

        })

        context4Application = applicationContext
    }

    companion object {
        var context4Application: Context? = null
    }


}