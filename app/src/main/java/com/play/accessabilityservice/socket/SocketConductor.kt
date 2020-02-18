package com.play.accessabilityservice.socket

import android.content.Context
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.play.accessabilityservice.api.data.RequestDTO
import com.play.accessabilityservice.system.SystemInfo
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

/**
 * Created by ChanHong on 2019/3/20
 *
 */
class SocketConductor {


    var serverAddress = "http://192.168.0.104:3000"
    var emmiter: Emitter? = null
    var socket: Socket? = null
    var context: Context? = null

    companion object {
        val instance: SocketConductor by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { SocketConductor() }
    }

    /**
     * build the connection between the c/s
     *  and post the phone information to server
     */
    fun connect2Server(
        context: Context,
        hookParamsReceiveListener: (requestDTO: RequestDTO) -> Unit
    ): Emitter {
        this.context = context


        this.socket = IO
            .socket(serverAddress)
            .apply {
                if (!connected()) {
                    connect()
                    emmiter = this.on(Socket.EVENT_CONNECT) {
                        Logger.i("connection_success")

                        val systemInfo = SystemInfo(
                            socket!!.id(),
                            ipAddress = SystemInfo.getIpAddress(context)!!
                        )
                        val data = Gson().toJson(systemInfo, SystemInfo::class.java)
                        Logger.json(data)
                        this.emit(Commands2Server.SYSTEM_INFO, data)
                    }
                        .on(Socket.EVENT_DISCONNECT) {
                            Logger.i("connection_disconnected")
                        }
                        .on("cmd") {
                            Logger.d(it)
                        }
                        .on(CmdFromServer.HOOK_PARAMS) {
                            val requestDTO =
                                Gson().fromJson(it[0].toString(), RequestDTO::class.java)
                            Logger.d("CmdFromServer.HOOK_PARAMS :\n $requestDTO")
                            hookParamsReceiveListener(requestDTO)
//                            GlobalScope.async {
//                                //                                delay(5000)
//                                socket!!.emit(requestDTO.uuid4socketEvent, data)
//                            }
                        }
                }
            }

        return emmiter!!
    }

}