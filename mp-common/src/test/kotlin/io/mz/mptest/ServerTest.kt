package io.mz.mptest

import io.mz.mp.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ServerTest {
    @Test
    fun testEcho() {
        val server = EchoServer()

        var received: MessageToClient? = null

        server.connect { channel ->
            channel.onMessage = { message ->
                received = message
            }
            channel.messageToServer(MessageToServer("TEST"))
        }

        assertEquals(MessageToClient("TEST"), received)
    }

    @Test
    fun addToGame() {
        val server = AddToGameServer()

        var added: GameChannel? = null
        var receivedInGame: MessageFromGame? = null
        var connectedChannel: Channel? = null

        run {
            server.connect { channel ->
                connectedChannel = channel
                channel.onAdd = { game ->
                    added = game
                    game.onMessage = { message ->
                        receivedInGame = message
                    }
                }
            }

            assertNotNull(connectedChannel)
            assertNull(added)
            assertNull(receivedInGame)
        }

        run {
            connectedChannel!!.messageToServer(MessageToServer("TEST"));

            assertNotNull(added)
            assertNull(receivedInGame)
        }

        run {
            added!!.messageToGame(MessageToGame("TEST 2"))
            assertEquals(MessageFromGame("TEST 2"), receivedInGame);
        }
    }

    private class EchoServer : Server {
        override fun connect(callback: (channel: Channel) -> Unit) {
            callback(object : Channel.AbstractChannel() {
                override fun messageToServer(message: MessageToServer) {
                    onMessage(MessageToClient(message.message))
                }
            })
        }
    }

    private class AddToGameServer : Server {
        override fun connect(callback: (channel: Channel) -> Unit) {
            callback(object : Channel.AbstractChannel() {
                override fun messageToServer(message: MessageToServer) {
                    onAdd(object : GameChannel.AbstractGameChannel() {
                        override fun messageToGame(message: MessageToGame) {
                            onMessage(MessageFromGame(message.message))
                        }
                    })
                }
            })
        }
    }
}