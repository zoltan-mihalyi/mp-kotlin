package io.mz.mp.serialization

import io.mz.mp.MessageToGame
import io.mz.mp.MessageToServer
import kotlinx.serialization.Serializable

sealed class ActionToServer

@Serializable
class MessageToServerAction(val message: MessageToServer) : ActionToServer()

@Serializable
class MessageToGameAction(val id: Int, val message: MessageToGame) : ActionToServer()
