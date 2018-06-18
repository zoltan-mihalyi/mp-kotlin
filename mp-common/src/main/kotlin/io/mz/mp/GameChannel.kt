package io.mz.mp

interface GameChannel {
    var onMessage: (message: MessageFromGame) -> Unit
    var onRemove: () -> Unit

    fun messageToGame(message: MessageToGame)

    abstract class AbstractGameChannel : GameChannel {
        final override var onMessage: (message: MessageFromGame) -> Unit = {}
        final override var onRemove: () -> Unit = {}
    }
}