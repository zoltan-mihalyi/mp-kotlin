package io.mz.mp

interface Channel {
    var onMessage: (message: MessageToClient) -> Unit
    var onAdd: (gameChannel: GameChannel) -> Unit

    fun messageToServer(message: MessageToServer)

    abstract class AbstractChannel : Channel {
        final override var onMessage: (message: MessageToClient) -> Unit = {}
        final override var onAdd: (gameChannel: GameChannel) -> Unit = {}
    }
}
