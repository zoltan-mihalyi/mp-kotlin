package io.mz.mp.serialization

import io.mz.mp.*

class DecoderServer(
        private val server: SerializedServer,
        private val encode: (message: MessageToServer) -> String,
        private val decode: (message: String) -> MessageToClient
) : Server {
    override fun connect(channelToClient: ChannelToClient, callback: (channelToServer: ChannelToServer) -> Unit) {
        server.connect(DecoderChannelToClient(channelToClient, decode)) { channelToServer ->
            callback(EncoderChannelToServer(channelToServer, encode))
        }
    }

    private class DecoderChannelToClient(
            private val channelToClient: ChannelToClient,
            private val decode: (message: String) -> MessageToClient
    ) : SerializedChannelToClient {
        override fun messageToClient(message: String) {
            channelToClient.messageToClient(decode(message))
        }
    }

    private class EncoderChannelToServer(
            private val channelToServer: SerializedChannelToServer,
            private val encode: (message: MessageToServer) -> String
    ) : ChannelToServer {
        override fun messageToServer(message: MessageToServer) {
            channelToServer.messageToServer(encode(message))
        }
    }
}