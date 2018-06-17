package io.mz.mp.websocket

import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import org.java_websocket.WebSocket as WS


internal actual class WebSocketServerTransport actual constructor(port: Int) {
    private val webSocketMap = mutableMapOf<WS, WebSocket>()
    private val wss = object: WebSocketServer(InetSocketAddress(port)){
        override fun onOpen(conn: WS, handshake: ClientHandshake?) {
            val webSocket = object : WebSocket {
                override var onMessage: (message: String) -> Unit = {}

                override fun send(message: String) {
                    conn.send(message)
                }
            }
            webSocketMap[conn] = webSocket

            onConnection(webSocket)
        }

        override fun onClose(conn: WS, code: Int, reason: String?, remote: Boolean) {
            webSocketMap.remove(conn)
        }

        override fun onMessage(conn: WS, message: String) {
            webSocketMap[conn]!!.onMessage(message)
        }

        override fun onStart() {
        }

        override fun onError(conn: org.java_websocket.WebSocket?, ex: Exception) {
            throw ex
        }
    }

    actual var onConnection: (webSocket: WebSocket) -> Unit = {}

    init {
        wss.start()
    }

    actual fun close() {
        wss.stop()
    }
}
