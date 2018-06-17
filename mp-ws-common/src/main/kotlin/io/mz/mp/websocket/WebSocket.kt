package io.mz.mp.websocket

interface WebSocket{
    var onMessage:(message:String)->Unit

    fun send(message:String)
}