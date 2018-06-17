package io.mz.mp.websocket

import io.mz.mp.serialization.SerializedChannel
import io.mz.mp.serialization.SerializedServer
import kotlin.test.Test
import kotlin.test.assertEquals

class WebSocketTest {
    @Test
    fun testEcho() = asyncTest { done ->
        val server = Echo()

        val endpoint = WebSocketEndpoint(server, 12345)

        val remote = RemoteWebSocketEndpoint("ws://localhost:12345");

        remote.connect { channel ->
            channel.onMessage = { message ->
                try {
                    assertEquals("Test", message)
                } finally {
                    endpoint.close()

                    done()
                }
            }

            channel.messageToServer("Test")
        }
    }

    class Echo : SerializedServer {
        override fun connect(callback: (channel: SerializedChannel) -> Unit) {
            callback(object : SerializedChannel.AbstractSerializedChannel() {
                override fun messageToServer(message: String) {
                    onMessage(message)
                }
            })
        }
    }
}