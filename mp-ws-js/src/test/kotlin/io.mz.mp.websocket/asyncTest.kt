package io.mz.mp.websocket

actual fun asyncTest(callback: (done: () -> Unit) -> Unit): dynamic = js("new Promise(function(resolve, reject){callback(resolve)})")
