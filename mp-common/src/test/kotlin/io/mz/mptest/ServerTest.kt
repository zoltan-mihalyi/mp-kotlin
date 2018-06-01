package io.mz.mptest

import io.mz.mp.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTest {
    @Test
    fun testEcho() {
        val server: Server = EchoServer()

        var calledWith: MessageToClient? = null

        val channelToClient = object : ChannelToClient {
            override fun messageToClient(message: MessageToClient) {
                calledWith = message
            }
        }

        server.connect(channelToClient) { channelToServer ->
            channelToServer.messageToServer(MessageToServer("TEST"))
        }

        assertEquals(MessageToClient("TEST"), calledWith)
    }

    private class EchoServer : Server {
        override fun connect(channelToClient: ChannelToClient, callback: (channelToServer: ChannelToServer) -> Unit) {
            callback(object : ChannelToServer {
                override fun messageToServer(message: MessageToServer) {
                    channelToClient.messageToClient(MessageToClient(message.message))
                }
            })
        }
    }
}