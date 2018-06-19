package io.mz.mp.serializationtest

import io.mz.mp.*
import io.mz.mp.serialization.*
import kotlin.test.*


class SerializedServerTest {

    @Test
    fun testSerializedEcho() {
        val server = EchoServer()

        val serializedServer = EncoderServer(server, JSON::stringify, JSON::parse)

        val deserializedServer = DecoderServer(serializedServer, JSON::stringify, JSON::parse)

        var received: MessageToClient? = null
        var receivedInGame: MessageFromGame? = null
        var removed = false

        deserializedServer.connect { channel ->
            channel.onMessage = { message ->
                received = message
            }

            channel.onAdd = { gameChannel ->
                gameChannel.onMessage = { message ->
                    receivedInGame = message
                }
                gameChannel.onRemove = {
                    removed = true;
                }
                gameChannel.messageToGame(MessageToGame("TEST 2"))
            }

            channel.messageToServer(MessageToServer("TEST"))
        }

        assertEquals(MessageToClient("TEST"), received)
        assertEquals(MessageFromGame("TEST 2"), receivedInGame)
        assertTrue(removed)
    }

    @Test
    fun noAccept() {
        val server = NoAccept()

        val serializedServer = EncoderServer(server, JSON::stringify, JSON::parse)

        val transport = FakeTransport(serializedServer)

        val deserializedServer = DecoderServer(transport, JSON::stringify, JSON::parse)

        deserializedServer.connect {
            fail("Should not connect")
        }
    }

    class NoAccept : Server {
        override fun connect(callback: (channel: Channel) -> Unit) {
            //No callback called
        }
    }

    class FakeTransport(private val server: SerializedServer) : SerializedServer {
        override fun connect(callback: (channel: SerializedChannel) -> Unit) {
            var forwardMessage: (String) -> Unit = {}

            server.connect { remoteChannel ->
                forwardMessage = remoteChannel::messageToServer
            }

            val channel = object : SerializedChannel.AbstractSerializedChannel() {
                override fun messageToServer(message: String) {
                    forwardMessage(message)
                }
            }

            callback(channel)
        }
    }
}