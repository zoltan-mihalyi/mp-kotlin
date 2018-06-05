package io.mz.mp.serialization

import io.mz.mp.MessageFromGame
import io.mz.mp.MessageToClient
import kotlinx.serialization.Serializable


sealed class ActionToClient {
}

@Serializable
class MessageToClientAction(val message: MessageToClient) : ActionToClient()

@Serializable
class AddedToGameAction(val id: Int) : ActionToClient()

@Serializable
class RemovedFromGameAction(val id: Int) : ActionToClient()

@Serializable
class MessageFromGameAction(val id: Int, val message: MessageFromGame) : ActionToClient()
