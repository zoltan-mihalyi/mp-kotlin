package io.mz.mp.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.internal.SerialClassDescImpl

abstract class SimpleSerializer<T>(name: String) : KSerializer<T> {
    override val serialClassDesc = SerialClassDescImpl(name)
}