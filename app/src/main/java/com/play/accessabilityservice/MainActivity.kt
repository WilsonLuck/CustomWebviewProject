package com.play.accessabilityservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.play.accessabilityservice.api.data.RequestDTO
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
                    RequestDTO(
                        "http://192.168.0.103:3000/posttest",
                        method = "POST",
                        proxy = "{\"proxyType\":\"HTTP\"," +
                                "\"serverAddress\":\"192.168.0.103\"," +
                                "\"port\":8888}",
                        sendHeaders = "User-Agent=AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36&Origin=https://www.freelancer.com"
                    )
//                    "https://www.jb51.net/article/159778.htm",
                )
            )
        }

        btn_get.setOnClickListener {
            startActivity(
                WebviewActivity.newIntent(
                    this,
                    RequestDTO(
                        "http://192.168.0.103:3000/users",
//                        url = "https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9307631538385676634%22%7D&n_type=0&p_from=1",
//                        "https://www.v2ex.com/",
                        proxy = "{\"proxyType\":\"HTTP\"," +
                                "\"serverAddress\":\"192.168.0.103\"," +
                                "\"port\":8888}",
                        sendHeaders = "User-Agent=AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36&Origin=https://www.freelancer.com",
                        javascriptCode = "document.getElementsByClassName('a1')[0].text = \"111\""
                        )
//                    "https://www.v2ex.com/",
//                    "http://www.baidu.com",
//                    "https://blog.csdn.net/Crazy_zihao/article/details/51557425",
//                    "http://192.168.0.105:3000/getjson",
//                    "https://www.jb51.net/article/159778.htm",
                )
            )
        }
    }
}
