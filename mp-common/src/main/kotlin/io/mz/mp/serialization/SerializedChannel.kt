package io.mz.mp.serialization

interface SerializedChannel {
    var onMessage: (message: String) -> Unit

    fun messageToServer(message: String)

    abstract class AbstractSerializedChannel : SerializedChannel {
        final override var onMessage: (message: String) -> Unit = {}
    }
}