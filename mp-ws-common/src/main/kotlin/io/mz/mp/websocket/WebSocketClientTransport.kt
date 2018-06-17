package io.mz.mp.websocket

expect class WebSocketClientTransport(url:String){
    var onConnect: (webSocket:WebSocket)->Unit
}