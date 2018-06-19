package io.mz.mp.serializationtest

import io.mz.mp.MessageFromGame
import io.mz.mp.serialization.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DecoderServerTest {

    @Test
    fun addedTwice() {
        val server = EvilSerializedServer { channel ->
            channel.onMessage(JSON.stringify<ActionToClient>(ConnectedAction.INSTANCE))

            val added = JSON.stringify<ActionToClient>(AddedToGameAction(1))
            channel.onMessage(added)
            channel.onMessage(added)
        }

        val decoderServer = DecoderServer(server, JSON::stringify, JSON::parse)

        var addedCount = 0;
        decoderServer.connect { channel ->
            channel.onAdd = {
                addedCount++
            }
        }

        assertEquals(1, addedCount)
    }

    @Test
    fun messageFromNonExistent() {
        val server = EvilSerializedServer { channel ->
            channel.onMessage(JSON.stringify<ActionToClient>(MessageFromGameAction(1, MessageFromGame("TEST"))))
        }

        val decoderServer = DecoderServer(server, JSON::stringify, JSON::parse)

        decoderServer.connect {}
    }

    @Test
    fun removedFromNonExistent() {
        val server = EvilSerializedServer { channel ->
            channel.onMessage(JSON.stringify<ActionToClient>(RemovedFromGameAction(1)))
        }

        val decoderServer = DecoderServer(server, JSON::stringify, JSON::parse)

        decoderServer.connect{}

    }

    private class EvilSerializedServer(
            private val onConnect: (channel: SerializedChannel) -> Unit
    ) : SerializedServer {
        override fun connect(callback: (channel: SerializedChannel) -> Unit) {
            val channel = object : SerializedChannel.AbstractSerializedChannel() {
                override fun messageToServer(message: String) {
                    throw UnsupportedOperationException("Not supported!");
                }
            }
            callback(channel)
            onConnect(channel)
        }
    }
}