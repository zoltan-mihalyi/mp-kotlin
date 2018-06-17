package io.mz.mp.websocket

import io.mz.mp.serialization.SerializedServer

class WebSocketEndpoint(private val server: SerializedServer, port: Int) {
    private val transport = WebSocketServerTransport(port)

    init {
        transport.onConnection = ::connection
    }

    private fun connection(webSocket: WebSocket) {
        server.connect { channel ->
            webSocket.onMessage = channel::messageToServer
            channel.onMessage = webSocket::send
        }
    }

    fun close() = transport.close()
}
