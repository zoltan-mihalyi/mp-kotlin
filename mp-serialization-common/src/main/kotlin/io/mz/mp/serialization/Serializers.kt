package io.mz.mp.serialization

import io.mz.mp.MessageToClient
import io.mz.mp.MessageToServer
import kotlinx.serialization.KInput
import kotlinx.serialization.KOutput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialContext
import kotlinx.serialization.json.JSON

private object MessageToServerSerializer : SimpleSerializer<MessageToServer>("MessageToServer") {
    override fun load(input: KInput): MessageToServer {
        return MessageToServer(input.readStringValue())
    }

    override fun save(output: KOutput, obj: MessageToServer) {
        output.writeStringValue(obj.message)
    }
}

private object MessageToClientSerializer : SimpleSerializer<MessageToClient>("MessageToClient") {
    override fun load(input: KInput): MessageToClient {
        return MessageToClient(input.readStringValue())
    }

    override fun save(output: KOutput, obj: MessageToClient) {
        output.writeStringValue(obj.message)
    }
}

inline fun <reified T : Any> SerialContext.registerSerializer(serializer: KSerializer<T>) {
    registerSerializer(T::class, serializer);
}

fun SerialContext.registerMpSerializers(): SerialContext {
    registerSerializer(MessageToServerSerializer)
    registerSerializer(MessageToClientSerializer)
    return this
}

val JSON = JSON(context = SerialContext().registerMpSerializers())