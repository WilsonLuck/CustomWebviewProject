package com.play.accessabilityservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_post.setOnClickListener {
            //            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//            startActivity(intent)
            startActivity(
                WebviewActivity.newIntent(
                    this,
                    "http://192.168.0.105:3000/posttest",
//                    "https://www.jb51.net/article/159778.htm",
                    get = false,
                    params = "name=hc&age=12"
                )
            )
        }

        btn_get.setOnClickListener {
            startActivity(
                WebviewActivity.newIntent(
                    this,
//                    "https://www.v2ex.com/",
                    "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9307631538385676634%22%7D&n_type=0&p_from=1",
//                    "http://www.baidu.com/",
//                    "https://blog.csdn.net/Crazy_zihao/article/details/51557425",
//                    "http://192.168.0.105:3000/getjson",
//                    "https://www.jb51.net/article/159778.htm",
                    injectJavascript = "asdas"
                )
            )
        }
    }
}
