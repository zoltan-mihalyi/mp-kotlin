package io.mz.mp.serialization

interface SerializedServer {
    fun connect(callback: (channel: SerializedChannel) -> Unit)
}