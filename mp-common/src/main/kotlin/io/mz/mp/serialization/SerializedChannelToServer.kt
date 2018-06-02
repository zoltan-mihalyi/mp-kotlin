package io.mz.mp.serialization

interface SerializedChannelToServer {
    fun messageToServer(message: String)
}