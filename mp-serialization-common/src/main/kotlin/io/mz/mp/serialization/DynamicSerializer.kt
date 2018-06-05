package io.mz.mp.serialization

import kotlinx.serialization.*
import kotlin.reflect.KClass


class DynamicSerializer<T : Any>(private vararg val classes: KClass<out T>) : SimpleSerializer<T>("Dynamic") {
    override fun load(input: KInput): T {
        val dynamic = input.readSerializableValue(input.context.klassSerializer(Dynamic::class))

        val type = classes[dynamic.type];

        return JSON.parse(type.serializer(), dynamic.value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun save(output: KOutput, obj: T) {
        val type = obj::class as KClass<T>
        val dynamic = Dynamic(classes.indexOf(type), JSON.stringify(type.serializer(), obj))
        output.writeSerializableValue(Dynamic::class.serializer(), dynamic)
    }

    @Serializable
    private class Dynamic(@SerialName("t") val type: Int, @SerialName("v") val value: String)
}
