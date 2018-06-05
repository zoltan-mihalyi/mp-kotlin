package io.mz.mp.serializationtest

import io.mz.mp.ChannelToClient
import io.mz.mp.ChannelToServerMembership
import io.mz.mp.MessageFromGame
import io.mz.mp.serialization.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DecoderServerTest {

    @Test
    fun addedTwice() {
        val server = EvilSerializedServer { channelToClient ->
            val added = JSON.stringify<ActionToClient>(AddedToGameAction(1))
            channelToClient.actionToClient(added)
            channelToClient.actionToClient(added)
        }

        val decoderServer = DecoderServer(server, JSON::stringify, JSON::parse)

        var addedCount = 0;
        decoderServer.connect(object : ChannelToClient {
            override fun addedToGame(channelToServerMembership: ChannelToServerMembership) {
                addedCount++
            }
        })

        assertEquals(1, addedCount)
    }

    @Test
    fun messageFromNonExistent(){
        val server = EvilSerializedServer { channelToClient ->
            channelToClient.actionToClient(JSON.stringify<ActionToClient>(MessageFromGameAction(1, MessageFromGame("TEST"))))
        }

        val decoderServer = DecoderServer(server, JSON::stringify, JSON::parse)

        decoderServer.connect(object : ChannelToClient {})
    }

    @Test
    fun removedFromNonExistent() {
        val server = EvilSerializedServer { channelToClient ->
            channelToClient.actionToClient(JSON.stringify<ActionToClient>(RemovedFromGameAction(1)))
        }

        val decoderServer = DecoderServer(server, JSON::stringify, JSON::parse)

        decoderServer.connect(object : ChannelToClient {})

    }

    private class EvilSerializedServer(private val onConnect: (channelToClient: SerializedChannelToClient) -> Unit) : SerializedServer {
        override fun connect(channelToClient: SerializedChannelToClient) {
            channelToClient.connected(object : SerializedChannelToServer {
                override fun messageToServer(message: String) {
                }
            })
            onConnect(channelToClient)
        }
    }
}