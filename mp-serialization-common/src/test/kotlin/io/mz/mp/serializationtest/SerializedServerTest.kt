package io.mz.mp.serializationtest

import io.mz.mp.*
import io.mz.mp.serialization.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


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
            channel.onMessage = {message->
                received = message
            }

            channel.onAdd = {gameChannel ->
                gameChannel.onMessage = {message ->
                    receivedInGame = message
                }
                gameChannel.onRemove={
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
}