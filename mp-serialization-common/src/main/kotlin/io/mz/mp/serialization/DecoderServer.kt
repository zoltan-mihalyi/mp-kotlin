package io.mz.mp.serialization

import io.mz.mp.*

class DecoderServer(
        private val server: SerializedServer,
        private val encode: (message: ActionToServer) -> String,
        private val decode: (message: String) -> ActionToClient
) : Server {
    override fun connect(callback: (channel: Channel) -> Unit) {
        server.connect { channel ->
            callback(DecoderChannel(channel))
        }
    }

    private inner class DecoderChannel(
            private val channel: SerializedChannel
    ) : Channel.AbstractChannel() {
        private val gamesById: MutableMap<Int, GameChannel> = mutableMapOf()

        init {
            channel.onMessage = ::msg
        }

        override fun messageToServer(message: MessageToServer) {
            channel.messageToServer(encode(MessageToServerAction(message)))
        }

        private fun msg(message: String) {
            val action = decode(message)
            return when (action) {
                is MessageToClientAction -> onMessage(action.message)
                is AddedToGameAction -> {
                    if (gamesById.containsKey(action.id)) {
                        return
                    }

                    val gameChannel = EncoderGameChannel(channel, action.id)
                    gamesById[action.id] = gameChannel
                    onAdd(gameChannel)
                }
                is RemovedFromGameAction -> {
                    val gameChannel = gamesById[action.id] ?: return
                    gamesById.remove(action.id)
                    gameChannel.onRemove()
                }
                is MessageFromGameAction -> {
                    val gameChannel = gamesById[action.id] ?: return
                    gameChannel.onMessage(action.message)
                }
            }
        }
    }

    private inner class EncoderGameChannel(
            private val serializedChannel: SerializedChannel,
            private val id: Int
    ) : GameChannel.AbstractGameChannel() {
        override fun messageToGame(message: MessageToGame) {
            serializedChannel.messageToServer(encode(MessageToGameAction(id, message)))
        }
    }
}