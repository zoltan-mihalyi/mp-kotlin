package io.mz.mp.serialization

import io.mz.mp.*

class EncoderServer(
        private val server: Server,
        private val encode: (message: MessageToClient) -> String,
        private val decode: (message: String) -> MessageToServer
) : SerializedServer {
    override fun connect(channelToClient: SerializedChannelToClient, callback: (channelToServer: SerializedChannelToServer) -> Unit) {
        server.connect(EncoderChannelToClient(channelToClient, encode)) { channelToServer ->
            callback(DecoderChannelToServer(channelToServer, decode))
        }
    }

    private class EncoderChannelToClient(
            private val channelToClient: SerializedChannelToClient,
            private val encode: (message: MessageToClient) -> String
    ) : ChannelToClient {
        override fun messageToClient(message: MessageToClient) {
            channelToClient.messageToClient(encode(message))
        }
    }

    private class DecoderChannelToServer(
            private val channelToServer: ChannelToServer,
            private val decode: (message: String) -> MessageToServer
    ) : SerializedChannelToServer {
        override fun messageToServer(message: String) {
            channelToServer.messageToServer(decode(message))
        }
    }
}