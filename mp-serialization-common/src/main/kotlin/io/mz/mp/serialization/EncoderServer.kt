package io.mz.mp.serialization

import io.mz.mp.*

class EncoderServer(
        private val server: Server,
        private val encode: (message: ActionToClient) -> String,
        private val decode: (message: String) -> ActionToServer
) : SerializedServer {
    override fun connect(channelToClient: SerializedChannelToClient) {
        server.connect(EncoderChannelToClient(channelToClient))
    }

    private inner class EncoderChannelToClient(
            private val channelToClient: SerializedChannelToClient
    ) : ChannelToClient {
        private var nextId = 0;
        private val gameIds: MutableMap<ChannelToServerMembership, Int> = mutableMapOf() //todo bimap
        private val gamesById: MutableMap<Int, ChannelToServerMembership> = mutableMapOf()

        override fun connected(channelToServer: ChannelToServer) {
            channelToClient.connected(DecoderChannelToServer(channelToServer, gamesById))
        }

        override fun messageToClient(message: MessageToClient) {
            channelToClient.actionToClient(encode(MessageToClientAction(message)))
        }

        override fun addedToGame(channelToServerMembership: ChannelToServerMembership) {
            if (gameIds.containsKey(channelToServerMembership)) {
                return
            }

            val id = nextId
            nextId++
            gameIds[channelToServerMembership] = id
            gamesById[id] = channelToServerMembership;
            channelToClient.actionToClient(encode(AddedToGameAction(id)))
        }

        override fun removedFromGame(channelToServerMembership: ChannelToServerMembership) {
            val id = gameIds[channelToServerMembership] ?: return
            gameIds.remove(channelToServerMembership)
            gamesById.remove(id)
            channelToClient.actionToClient(encode(RemovedFromGameAction(id)))
        }

        override fun messageFromGame(channelToServerMembership: ChannelToServerMembership, message: MessageFromGame) {
            val id = gameIds[channelToServerMembership] ?: return
            channelToClient.actionToClient(encode(MessageFromGameAction(id, message)))
        }
    }

    private inner class DecoderChannelToServer(
            private val channelToServer: ChannelToServer,
            private val gamesById: Map<Int, ChannelToServerMembership>
    ) : SerializedChannelToServer {
        override fun messageToServer(message: String) {
            val action = decode(message)
            return when (action) {
                is MessageToServerAction -> channelToServer.messageToServer(action.message)
                is MessageToGameAction -> {
                    val game = gamesById[action.id]
                    if (game != null) game.messageToGame(action.message) else Unit
                }
            }
        }
    }
}