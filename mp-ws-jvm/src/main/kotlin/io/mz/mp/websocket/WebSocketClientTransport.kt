package io.mz.mp.websocket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

actual class WebSocketClientTransport actual constructor(url: String) {
    private val wsc = WebSocketClientImpl(url)

    inner class WebSocketClientImpl(url: String) : WebSocketClient (URI.create(url)) {
        private val webSocket = object: WebSocket {

            override var onMessage: (message: String) -> Unit = {}

            override fun send(message: String) {
                this@WebSocketClientImpl.send(message)
            }

        }

        override fun onOpen(handshakedata: ServerHandshake?) {
            onConnect(webSocket)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
        }

        override fun onMessage(message: String) {
            webSocket.onMessage(message)
        }

        override fun onError(ex: Exception) {
        }
    }

    init {
        wsc.connect()
    }

    actual var onConnect: (webSocket: WebSocket) -> Unit = {}
}
