package io.mz.mp.websocket

import new
import ws

actual class WebSocketClientTransport actual constructor(url: String) {
    private val wsc = new(ws, url)

    actual var onConnect: (webSocket: WebSocket) -> Unit = {}

    init {
        val webSocket = object : WebSocket {
            override var onMessage: (message: String) -> Unit = {}

            override fun send(message: String) {
                wsc.send(message)
            }
        }

        wsc.on("open") {
            onConnect(webSocket)
        }

        wsc.on("message") { message ->
            webSocket.onMessage(message)
        }
    }
}