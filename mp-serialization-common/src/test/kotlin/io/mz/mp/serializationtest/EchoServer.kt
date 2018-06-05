package io.mz.mp.serializationtest

import io.mz.mp.*

class EchoServer : Server {
    override fun connect(channelToClient: ChannelToClient) {
        channelToClient.connected(object : ChannelToServer {
            override fun messageToServer(message: MessageToServer) {
                channelToClient.messageToClient(MessageToClient(message.message))
            }
        })

        channelToClient.addedToGame(object : ChannelToServerMembership {
            override fun messageToGame(message: MessageToGame) {
                channelToClient.messageFromGame(this, MessageFromGame(message.message))
                channelToClient.removedFromGame(this)
            }
        })
    }
}