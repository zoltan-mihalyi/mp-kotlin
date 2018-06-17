package io.mz.mp.websocket

import io.mz.mp.serialization.SerializedChannel
import io.mz.mp.serialization.SerializedServer

class RemoteWebSocketEndpoint(private val url: String) : SerializedServer {
    override fun connect(callback: (channel: SerializedChannel) -> Unit) {
        val transport = WebSocketClientTransport(url)

        transport.onConnect = { webSocket ->
            val channel = object : SerializedChannel.AbstractSerializedChannel() {
                override fun messageToServer(message: String) {
                    webSocket.send(message)
                }
            }

            webSocket.onMessage = { message ->
                channel.onMessage(message)
            }
            callback(channel)
        }
    }
}