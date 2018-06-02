package io.mz.mp.serializationtest

import io.mz.mp.*
import io.mz.mp.serialization.DecoderServer
import io.mz.mp.serialization.EncoderServer
import io.mz.mp.serialization.JSON
import kotlin.test.Test
import kotlin.test.assertEquals


class SerializedServerTest {

    @Test
    fun test1() {
        val server = EchoServer()

        val serializedServer = EncoderServer(server, { JSON.stringify(it) }, { JSON.parse(it) });

        val deserializedServer = DecoderServer(serializedServer, { JSON.stringify(it) }, { JSON.parse(it) });

        var received: MessageToClient? = null

        deserializedServer.connect(object : ChannelToClient {
            override fun messageToClient(message: MessageToClient) {
                received = message
            }
        }) { channelToServer ->
            channelToServer.messageToServer(MessageToServer("TEST"))
        }

        assertEquals(MessageToClient("TEST"), received)
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
