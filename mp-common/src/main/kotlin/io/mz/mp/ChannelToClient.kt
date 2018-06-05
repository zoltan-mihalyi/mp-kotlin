package io.mz.mp

interface ChannelToClient {
    fun connected(channelToServer: ChannelToServer) {}
    fun messageToClient(message: MessageToClient) {}
    fun addedToGame(channelToServerMembership: ChannelToServerMembership) {}
    fun removedFromGame(channelToServerMembership: ChannelToServerMembership) {}
    fun messageFromGame(channelToServerMembership: ChannelToServerMembership, message: MessageFromGame) {}
}
