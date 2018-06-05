package io.mz.mp.serialization

interface SerializedChannelToClient {
    fun connected(channelToServer: SerializedChannelToServer)
    fun actionToClient(serializedAction: String)
}