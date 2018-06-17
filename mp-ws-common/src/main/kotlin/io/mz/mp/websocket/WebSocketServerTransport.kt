package io.mz.mp.websocket


internal expect class WebSocketServerTransport(port: Int) {
    var onConnection: (webSocket: WebSocket) -> Unit
    fun close()
}
