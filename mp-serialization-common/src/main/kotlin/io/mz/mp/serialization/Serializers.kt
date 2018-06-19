package io.mz.mp.serialization

import io.mz.mp.MessageFromGame
import io.mz.mp.MessageToClient
import io.mz.mp.MessageToGame
import io.mz.mp.MessageToServer
import kotlinx.serialization.*
import kotlinx.serialization.json.JSON

inline fun <T> stringValueSerializer(
        crossinline wrap: (value: String) -> T,
        crossinline extract: (t: T) -> String, name: String
): SimpleSerializer<T> {
    return object : SimpleSerializer<T>(name) {
        override fun load(input: KInput): T {
            return wrap(input.readStringValue())
        }

        override fun save(output: KOutput, obj: T) {
            output.writeStringValue(extract(obj))
        }
    }
}

inline fun <reified T : Any> SerialContext.registerSerializer(serializer: KSerializer<T>) {
    registerSerializer(T::class, serializer);
}

fun SerialContext.registerMpSerializers(): SerialContext {
    registerSerializer(stringValueSerializer(::MessageToServer, MessageToServer::message, "MessageToServer"))
    registerSerializer(stringValueSerializer(::MessageToClient, MessageToClient::message, "MessageToClient"))
    registerSerializer(stringValueSerializer(::MessageToGame, MessageToGame::message, "MessageToGame"))
    registerSerializer(stringValueSerializer(::MessageFromGame, MessageFromGame::message, "MessageFromGame"))
    registerSerializer(DynamicSerializer(MessageToServerAction::class, MessageToGameAction::class))
    registerSerializer(DynamicSerializer(
            ConnectedAction::class,
            MessageToClientAction::class,
            AddedToGameAction::class,
            RemovedFromGameAction::class,
            MessageFromGameAction::class
    ))
    return this
}

val JSON = JSON(context = SerialContext().registerMpSerializers())