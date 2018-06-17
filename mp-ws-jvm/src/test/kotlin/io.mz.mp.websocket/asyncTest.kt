package io.mz.mp.websocket


actual fun asyncTest(callback: (done: () -> Unit) -> Unit) {
    val lock = Object()
    callback {
        synchronized(lock) {
            lock.notify()
        }
    }
    synchronized(lock) {
        lock.wait()
    }
}