package io.mz.mp.serialization

interface SerializedServer {
    fun connect(channelToClient: SerializedChannelToClient, callback: (channelToServer: SerializedChannelToServer) -> Unit)
}