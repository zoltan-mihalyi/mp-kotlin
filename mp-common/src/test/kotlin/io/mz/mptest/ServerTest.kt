package io.mz.mptest

import io.mz.mp.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ServerTest {
    @Test
    fun testEcho() {
        val server: Server = EchoServer()

        var received: MessageToClient? = null

        val channelToClient = object : ChannelToClient {
            override fun connected(channelToServer: ChannelToServer) {
                channelToServer.messageToServer(MessageToServer("TEST"))
            }

            override fun messageToClient(message: MessageToClient) {
                received = message
            }
        }

        server.connect(channelToClient)

        assertEquals(MessageToClient("TEST"), received)
    }

    @Test
    fun addToGame() {
        val server = AddToGameServer()

        var added: ChannelToServerMembership? = null
        var receivedInGame: MessageFromGame? = null
        var channel: ChannelToServer? = null

        val channelToClient = object : ChannelToClient {
            override fun connected(channelToServer: ChannelToServer) {
                channel = channelToServer
            }

            override fun addedToGame(channelToServerMembership: ChannelToServerMembership) {
                added = channelToServerMembership
            }

            override fun messageFromGame(channelToServerMembership: ChannelToServerMembership, message: MessageFromGame) {
                receivedInGame = message
            }
        }

        run {
            server.connect(channelToClient)

            assertNotNull(channel)
            assertNull(added)
            assertNull(receivedInGame)
        }

        run {
            channel!!.messageToServer(MessageToServer("TEST"));

            assertNotNull(added)
            assertNull(receivedInGame)
        }

        run {
            added!!.messageToGame(MessageToGame("TEST 2"))
            assertEquals(MessageFromGame("TEST 2"), receivedInGame);
        }
    }

    private class EchoServer : Server {
        override fun connect(channelToClient: ChannelToClient) {
            channelToClient.connected(object : ChannelToServer {
                override fun messageToServer(message: MessageToServer) {
                    channelToClient.messageToClient(MessageToClient(message.message))
                }
            })
        }
    }

    private class AddToGameServer : Server {
        override fun connect(channelToClient: ChannelToClient) {
            channelToClient.connected(object : ChannelToServer {
                override fun messageToServer(message: MessageToServer) {
                    channelToClient.addedToGame(object : ChannelToServerMembership {
                        override fun messageToGame(message: MessageToGame) {
                            channelToClient.messageFromGame(this, MessageFromGame(message.message))
                        }
                    })
                }
            })
        }
    }
}