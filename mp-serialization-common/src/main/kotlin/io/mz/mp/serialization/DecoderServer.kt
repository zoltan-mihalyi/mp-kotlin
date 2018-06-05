package io.mz.mp.serialization

import io.mz.mp.*

class DecoderServer(
        private val server: SerializedServer,
        private val encode: (message: ActionToServer) -> String,
        private val decode: (message: String) -> ActionToClient
) : Server {
    override fun connect(channelToClient: ChannelToClient) {
        server.connect(DecoderChannelToClient(channelToClient))
    }

    private inner class DecoderChannelToClient(
            private val channelToClient: ChannelToClient
    ) : SerializedChannelToClient {
        private lateinit var channelToServer: SerializedChannelToServer;
        private val gamesById: MutableMap<Int, ChannelToServerMembership> = mutableMapOf()

        override fun connected(channelToServer: SerializedChannelToServer) {
            this.channelToServer = channelToServer;
            channelToClient.connected(EncoderChannelToServer(channelToServer))
        }

        override fun actionToClient(serializedAction: String) {
            val action = decode(serializedAction)
            return when (action) {
                is MessageToClientAction -> channelToClient.messageToClient(action.message)
                is AddedToGameAction -> {
                    if (gamesById.containsKey(action.id)) {
                        return
                    }

                    val channelToServerMembership = EncoderChannelToServerMembership(channelToServer, action.id)
                    gamesById[action.id] = channelToServerMembership
                    channelToClient.addedToGame(channelToServerMembership)
                }
                is RemovedFromGameAction -> {
                    val channelToServerMembership = gamesById[action.id] ?: return
                    gamesById.remove(action.id)
                    channelToClient.removedFromGame(channelToServerMembership)
                }
                is MessageFromGameAction -> {
                    val channelToServerMembership = gamesById[action.id] ?: return
                    channelToClient.messageFromGame(channelToServerMembership, action.message)
                }
            }
        }
    }

    private inner class EncoderChannelToServer(
            private val channelToServer: SerializedChannelToServer
    ) : ChannelToServer {
        override fun messageToServer(message: MessageToServer) {
            channelToServer.messageToServer(encode(MessageToServerAction(message)))
        }
    }

    private inner class EncoderChannelToServerMembership(
            private val serializedChannelToServer: SerializedChannelToServer,
            private val id: Int
    ) : ChannelToServerMembership {
        override fun messageToGame(message: MessageToGame) {
            serializedChannelToServer.messageToServer(encode(MessageToGameAction(id, message)))
        }
    }
}