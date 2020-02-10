package com.play.accessabilityservice

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.orhanobut.logger.Logger

/**
 * Created by ChanHong on 2020-02-08
 *
 */
class BookAccessAbilityService : AccessibilityService() {

    var firstOpen = true //是否第一次打开app
    var accountModel = false //当前是否为账号密码登录
    var editAccountCompleted = false //账号密码输入完成
    var btnLoginClicked = false//点击登录按钮
    var cuurentUserName = ""
    var currentPassword = ""
    override fun onInterrupt() {

    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        Logger.i(event.toString())

        if (firstOpen && event?.className == "com.tadu.android.ui.view.TDMainActivity" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val tabBook =
                rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tadu.android:id/main_tab_1")
            if (tabBook.isNotEmpty()) {
                tabBook.first().performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            firstOpen = false
        }


        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (event.className == "com.tadu.android.ui.theme.b.w") {
                    Logger.i(event.text.toString())
                }

                if (event.className == "com.tadu.android.ui.view.account.LoginActivity") {
                    /*
                  点击切换账号登录
                    */
                    val loginModel =
                        rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tadu.android:id/change_login_mode")
                    if (loginModel.isNotEmpty() && loginModel.first().text == "账号密码登录") {
                        loginModel.first().performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        rootInActiveWindow.recycle()
                    }
                }

                if (event.className == "com.tadu.android.ui.view.user.UserSpaceActivity") {
                    /*
                     * 进入登录界面
                    */
                    val btnChangeAccount =
                        rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tadu.android:id/change_account")
                    if (btnChangeAccount.isNotEmpty()) {
                        btnChangeAccount.first().performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        rootInActiveWindow.recycle()
                    }
                }


            }

            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                /*
                确认切换账号
                 */
                if (rootInActiveWindow.findAccessibilityNodeInfosByText("切换").isNotEmpty()) {
                    rootInActiveWindow.findAccessibilityNodeInfosByText("切换").first()
                        .performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }

            }

            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                /*
                 切换账号
                 */
                if (event.text.isNotEmpty() && event.text.first() == "书架") {
                    //打开右侧信息栏
                    rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tadu.android:id/layout_user_avatar")
                        .first().performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    //点击头像前往更换账号界面
                    rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tadu.android:id/user_avatar")
                        .first().performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    rootInActiveWindow.recycle()
                }

                if (event.className == "android.widget.TextView" && event.text.isNotEmpty() && event.text.first() == "验证码登录") {
                    Logger.i("当前为验证码登录")
                    loginByAccount("td647970251", "td4539842", rootInActiveWindow)
                }
            }

            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                if (event.className == "android.widget.EditText" && event.text.isNotEmpty() && event.text.first().length == currentPassword.length) {
                    Logger.i("账号密码输入完成 点击登录按钮")
                    val btn_login = "com.tadu.android:id/btn_login"
                    val btnLogin = rootInActiveWindow.findAccessibilityNodeInfosByViewId(btn_login)
                    if (btnLogin.isNotEmpty()) {
                        btnLogin.first().performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                    rootInActiveWindow.recycle()
                }
            }
        }

        return

    }

    fun loginByAccount(userName: String, password: String, currentNodeInfo: AccessibilityNodeInfo) {
        val et_userName = "com.tadu.android:id/et_username" //账号控件的id
        val et_password = "com.tadu.android:id/et_password"//密码控件的id
        val etAccount = currentNodeInfo.findAccessibilityNodeInfosByViewId(et_userName)
        val etPassword = currentNodeInfo.findAccessibilityNodeInfosByViewId(et_password)
        if (etAccount.isNotEmpty() && etPassword.isNotEmpty()) {
            val arguments4UserName = Bundle()
            arguments4UserName.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                userName
            )
            etAccount.first()
                .performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments4UserName)
            val args4Password = Bundle()
            args4Password.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                password
            )
            etPassword.first().performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args4Password)
            cuurentUserName = userName
            currentPassword = password
        }

        currentNodeInfo.recycle()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Logger.i("onServiceConnected")
    }

}