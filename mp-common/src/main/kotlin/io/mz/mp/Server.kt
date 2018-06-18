package io.mz.mp

interface Server {
    fun connect(callback: (channel: Channel) -> Unit)
}