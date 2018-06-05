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

        deserializedServer.connect(object : ChannelToClient {
            override fun connected(channelToServer: ChannelToServer) {
                channelToServer.messageToServer(MessageToServer("TEST"))
            }

            override fun messageToClient(message: MessageToClient) {
                received = message
            }

            override fun addedToGame(channelToServerMembership: ChannelToServerMembership) {
                channelToServerMembership.messageToGame(MessageToGame("TEST 2"))
            }

            override fun removedFromGame(channelToServerMembership: ChannelToServerMembership) {
                removed = true
            }

            override fun messageFromGame(channelToServerMembership: ChannelToServerMembership, message: MessageFromGame) {
                receivedInGame = message
            }
        })

        assertEquals(MessageToClient("TEST"), received)
        assertEquals(MessageFromGame("TEST 2"), receivedInGame)
        assertTrue(removed)
    }
}