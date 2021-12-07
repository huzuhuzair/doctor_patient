package com.aamir.icarepro.utils

import android.app.Activity
import android.util.Log
import com.aamir.icarepro.BuildConfig
import com.aamir.icarepro.data.models.login.LoginResponse
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class SocketManager {
//    val dataString = UserManager.getUserProfile()
    var socket: Socket? = null

    var activity:Activity?= null

    companion object {

        // Listen Events
        const val EVENT_ADD_MESSAGE_RESPONSE = "sendMessage"
        const val EVENT_TYPING_MESSAGE_RESPONSE = "typing"
        const val LISTENER_READALL = "message-read"
        const val LISTENER_ONLINE_STATUS = "online-status"

        //Emit Events
        const val EVENT_READ_ALL_MESSAGES = "message-read"
        const val EVENT_ADD_MESSAGE = "sendMessage"
        const val EVENT_TYPING_MESSAGE = "typing"
        const val EVENT_ONLINE_STATUS = "online-status"

        private var INSTANCE: SocketManager? = null

        fun getInstance() = INSTANCE
            ?: synchronized(SocketManager::class.java) {
                INSTANCE
                    ?: SocketManager()
                        .also { INSTANCE = it }
            }

        /**
         * Disconnects from current instance and also releases references to it
         * so that a new instance will be created next time.
         * */
        fun destroy() {
            Log.e("Socket", "Destroying socket instance")
            INSTANCE?.disconnect()
            INSTANCE = null
        }
    }

    fun connect(activity:Activity,user:LoginResponse,msgAck: OnMessageReceiver,status:Int) {
        this.activity = activity
        val options = IO.Options()
        options.forceNew = false
        options.reconnection = true
        /*options.query =
            "user_id=${dataString?.id.toString()}"*/
        socket = IO.socket(
            BuildConfig.SOCKET_BASE_URL + "?user_id=${user?.id}",
            options
        )
        socket?.connect()
        socket?.on(Socket.EVENT_CONNECT) {
            val arguments = JSONObject()
            arguments.putOpt("online_status", status)
            arguments.putOpt("user_id", user?.id)
            sendOnlineStatus(arguments, msgAck)
            Log.e("Socket", "Socket Connect ${socket?.id()}")
        }
        socket?.on(Socket.EVENT_DISCONNECT) {
            Log.e("Socket", "Socket Disconnect")
        }
        socket?.on(Socket.EVENT_CONNECT_TIMEOUT) {
            Log.e("Socket", "Socket timeout")
        }
        socket?.on(Socket.EVENT_ERROR) {
            Log.e("Socket", "Socket error")
        }
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            Log.e("Socket", "Socket error second$it")
        }


    }

    fun disconnect() {
        socketOff()
        socket?.off()
        socket?.disconnect()
        //  Timber.d("Disconnect")
    }

    fun on(event: String, listener: Emitter.Listener) {
        socket?.on(event, listener)
    }

    fun off(event: String, listener: Emitter.Listener) {
        socket?.off(event, listener)
    }

    fun emit(event: String, args: Any, acknowledge: Ack) {
        socket?.emit(event, args, acknowledge)
    }

    fun emit(event: String, args: Any) {
        socket?.emit(event, args)
    }


    fun sendTyping(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(
            EVENT_TYPING_MESSAGE, arg,
            Ack { args ->
                    println("------------------" + args[0])
                    msgAck.onMessageReceive(
                        args[0].toString(),
                        EVENT_TYPING_MESSAGE
                    )
            })
    }

    fun sendOnlineStatus(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(
            EVENT_ONLINE_STATUS, arg,
            Ack { args ->
                    println("------------------" + args[0])
                    msgAck.onMessageReceive(
                        args[0].toString(),
                        EVENT_ONLINE_STATUS
                    )
            })
    }


    //product price - shipping*12%
    //product price - 12%

    fun sendMessage(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(
            EVENT_ADD_MESSAGE, arg,
            Ack { args ->
                    println("-------sendMsg ACK-----------" + args[0])
                    msgAck.onMessageReceive(
                        args[0].toString(),
                        EVENT_ADD_MESSAGE
                    )
            })
    }

    fun readAllMessages(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(
            EVENT_READ_ALL_MESSAGES, arg,
            Ack { args ->
                    println("------------------" + args[0])
                    msgAck.onMessageReceive(
                        args[0].toString(),
                        EVENT_READ_ALL_MESSAGES
                    )
            })
    }


    // Listen Events
    fun onTyping(msgAck: OnMessageReceiver) {
        socket?.on(EVENT_TYPING_MESSAGE_RESPONSE) { args ->
            msgAck.onMessageReceive(
                args[0].toString(),
                EVENT_TYPING_MESSAGE_RESPONSE
            )
        }
    }

    fun addChatMessageListener(msgAck: OnMessageReceiver) {
        socket?.on(EVENT_ADD_MESSAGE_RESPONSE) { args ->
            activity?.runOnUiThread {
                msgAck.onMessageReceive(
                    args[0].toString(),
                    EVENT_ADD_MESSAGE_RESPONSE
                )
            }
        }
    }

    fun addChatMessageReadListener(msgAck: OnMessageReceiver) {
        socket?.on(LISTENER_READALL) { args ->
            msgAck.onMessageReceive(
                args[0].toString(),
                LISTENER_READALL
            )
        }
    }

    fun addOnlineStatusListener(msgAck: OnMessageReceiver) {
        socket?.on(LISTENER_ONLINE_STATUS) { args ->
            msgAck.onMessageReceive(
                args[0].toString(),
                LISTENER_ONLINE_STATUS
            )
        }
    }

    private fun socketOff() {
        socket?.off(EVENT_TYPING_MESSAGE_RESPONSE)
        socket?.off(EVENT_ADD_MESSAGE_RESPONSE)
        socket?.off(EVENT_TYPING_MESSAGE_RESPONSE)
        socket?.off(LISTENER_READALL)
        socket?.off(LISTENER_ONLINE_STATUS)
    }


    interface OnMessageReceiver {
        fun onMessageReceive(message: String, event: String)
    }
}