package io.mz.mp

interface ChannelToClient {
    fun messageToClient(message: MessageToClient)
}
