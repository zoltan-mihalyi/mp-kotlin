package io.mz.mp

interface Server {
    fun connect(channelToClient: ChannelToClient)
}