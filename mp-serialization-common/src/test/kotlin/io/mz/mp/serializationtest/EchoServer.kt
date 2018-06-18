package io.mz.mp.serializationtest

import io.mz.mp.*

class EchoServer : Server {
    override fun connect(callback: (channel: Channel) -> Unit) {
        val channel = object : Channel.AbstractChannel() {
            override fun messageToServer(message: MessageToServer) {
                onMessage(MessageToClient(message.message))
            }
        }
        callback(channel);

        channel.onAdd(object : GameChannel.AbstractGameChannel() {
            override fun messageToGame(message: MessageToGame) {
                onMessage(MessageFromGame(message.message))
                onRemove()
            }
        })
    }
}