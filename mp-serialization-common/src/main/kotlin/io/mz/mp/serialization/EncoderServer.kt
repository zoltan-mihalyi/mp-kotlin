package io.mz.mp.serialization

import io.mz.mp.Channel
import io.mz.mp.GameChannel
import io.mz.mp.Server

class EncoderServer(
        private val server: Server,
        private val encode: (message: ActionToClient) -> String,
        private val decode: (message: String) -> ActionToServer
) : SerializedServer {
    override fun connect(callback: (channel: SerializedChannel) -> Unit) {
        server.connect { channel ->
            callback(EncoderChannel(channel))
        }
    }

    private inner class EncoderChannel(
            private val channel: Channel
    ) : SerializedChannel.AbstractSerializedChannel() {
        private var nextId = 0;
        private val gameIds: MutableMap<GameChannel, Int> = mutableMapOf() //todo bimap
        private val gamesById: MutableMap<Int, GameChannel> = mutableMapOf()

        init {
            channel.onAdd = ::add

            channel.onMessage = { message ->
                onMessage(encode(MessageToClientAction(message)))
            }
        }

        private fun add(gameChannel: GameChannel) {
            if (gameIds.containsKey(gameChannel)) {
                return
            }
            gameChannel.onRemove = onRemove@{
                val id = gameIds[gameChannel] ?: return@onRemove
                gameIds.remove(gameChannel)
                gamesById.remove(id)
                onMessage(encode(RemovedFromGameAction(id)))
            }
            gameChannel.onMessage = onMessage@{ message ->
                val id = gameIds[gameChannel] ?: return@onMessage
                onMessage(encode(MessageFromGameAction(id, message)))
            }

            val id = nextId
            nextId++
            gameIds[gameChannel] = id
            gamesById[id] = gameChannel;
            onMessage(encode(AddedToGameAction(id)))
        }

        override fun messageToServer(message: String) {
            val action = decode(message)
            return when (action) {
                is MessageToServerAction -> channel.messageToServer(action.message)
                is MessageToGameAction -> {
                    val game = gamesById[action.id]
                    if (game != null) game.messageToGame(action.message) else Unit
                }
            }
        }
    }
}