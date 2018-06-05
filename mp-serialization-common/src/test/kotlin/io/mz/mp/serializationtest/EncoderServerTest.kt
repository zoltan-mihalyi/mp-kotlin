package io.mz.mp.serializationtest

import io.mz.mp.*
import io.mz.mp.serialization.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EncoderServerTest {

    @Test
    fun unknownId() {
        val server = EchoServer()

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        var finishedWithoutException = false
        encoded.connect(object : SerializedChannelToClient {
            override fun connected(channelToServer: SerializedChannelToServer) {
                val message = MessageToGameAction(42, MessageToGame("!!!"))
                channelToServer.messageToServer(JSON.stringify<ActionToServer>(message))
                finishedWithoutException = true
            }

            override fun actionToClient(serializedAction: String) {
            }
        })

        assertTrue(finishedWithoutException)
    }

    @Test
    fun removedTwice() {
        val server = EvilServer { channelToClient, game ->
            channelToClient.removedFromGame(game)
            channelToClient.removedFromGame(game)
        }

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        encoded.connect(object : SerializedChannelToClient {
            override fun connected(channelToServer: SerializedChannelToServer) {
            }

            override fun actionToClient(serializedAction: String) {
            }
        })
    }

    @Test
    fun addedTwice() {
        val server = EvilServer { channelToClient, game ->
            channelToClient.addedToGame(game)
            channelToClient.addedToGame(game)
        }

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        var actions = 0;

        encoded.connect(object : SerializedChannelToClient {
            override fun connected(channelToServer: SerializedChannelToServer) {
            }

            override fun actionToClient(serializedAction: String) {
                actions++
            }
        })

        assertEquals(1, actions)
    }

    @Test
    fun messageFromNonExistent() {
        val server = EvilServer { channelToClient, game ->
            channelToClient.messageFromGame(game, MessageFromGame("You are not added to this game!"))
        }

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        encoded.connect(object : SerializedChannelToClient {
            override fun connected(channelToServer: SerializedChannelToServer) {
            }

            override fun actionToClient(serializedAction: String) {
            }
        })
    }

    private class EvilServer(private val onConnect: (channelToClient: ChannelToClient, game: ChannelToServerMembership) -> Unit) : Server {
        override fun connect(channelToClient: ChannelToClient) {
            val game = object : ChannelToServerMembership {
                override fun messageToGame(message: MessageToGame) {
                }
            }
            onConnect(channelToClient, game)
        }

    }
}