package com.play.accessabilityservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_btn_go_setting.setOnClickListener {
            //            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//            startActivity(intent)
            startActivity(WebviewActivity.newIntent(this,"http://192.168.0.105:3000/posttest"))
        }
    }
}
