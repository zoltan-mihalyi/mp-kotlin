package io.mz.mp.websocket

import new
import ws

internal actual class WebSocketServerTransport actual constructor(port: Int) {
    actual var onConnection:(webSocket:WebSocket)->Unit = {}

    private val wss = new(ws.Server, params(port))

    init {
        wss.on("connection") { wsConn ->
            val webSocket = object : WebSocket {
                override var onMessage: (message: String) -> Unit = {}

                override fun send(message: String) {
                    wsConn.send(message)
                }
            }

            wsConn.on("message") { message ->
                webSocket.onMessage(message as String)
            }

            onConnection(webSocket)
        }
    }

    actual fun close() {
        wss.close()
    }

    companion object {
        @Suppress("NOTHING_TO_INLINE")
        private inline fun params(@Suppress("UNUSED_PARAMETER") port:Int):dynamic{
            return js("{port: port}");
        }
    }
}