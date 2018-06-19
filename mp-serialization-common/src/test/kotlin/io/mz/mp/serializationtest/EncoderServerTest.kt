package io.mz.mp.serializationtest

import io.mz.mp.*
import io.mz.mp.serialization.ActionToServer
import io.mz.mp.serialization.EncoderServer
import io.mz.mp.serialization.JSON
import io.mz.mp.serialization.MessageToGameAction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EncoderServerTest {

    @Test
    fun unknownId() {
        val server = EchoServer()

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        var finishedWithoutException = false
        encoded.connect { channel ->
            val message = MessageToGameAction(42, MessageToGame("!!!"))
            channel.messageToServer(JSON.stringify<ActionToServer>(message))
            finishedWithoutException = true
        }

        assertTrue(finishedWithoutException)
    }

    @Test
    fun removedTwice() {
        val server = EvilServer { channel, game ->
            channel.onAdd(game)
            game.onRemove()
            game.onRemove()
        }

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        var messages = 0;

        encoded.connect { channel ->
            channel.onMessage = {
                messages++;
            }
        }
        assertEquals(3, messages) // connected + add + remove = 3
    }

    @Test
    fun addedTwice() {
        val server = EvilServer { channel, game ->
            channel.onAdd(game)
            channel.onAdd(game)
        }

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        var actions = 0;

        encoded.connect { channel ->
            channel.onMessage = {
                actions++
            }
        }

        assertEquals(2, actions) // connected + add = 2
    }

    @Test
    fun messageFromNonExistent() {
        val server = EvilServer { channel, game ->
            game.onMessage(MessageFromGame("You are not added to this game!"))
        }

        val encoded = EncoderServer(server, JSON::stringify, JSON::parse)

        encoded.connect {}
    }

    private class EvilServer(
            private val handleGame: (channel: Channel, game: GameChannel) -> Unit
    ) : Server {
        override fun connect(callback: (channel: Channel) -> Unit) {
            val channel = object : Channel.AbstractChannel() {
                override fun messageToServer(message: MessageToServer) {
                }
            }
            callback(channel)
            val game = object : GameChannel.AbstractGameChannel() {
                override fun messageToGame(message: MessageToGame) {
                }
            }

            handleGame(channel, game)
        }

    }
}