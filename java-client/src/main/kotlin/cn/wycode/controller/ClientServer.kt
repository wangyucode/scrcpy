package cn.wycode.controller

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class ClientServer(private val port: Int) {

    fun run(){
        val eventLoopGroup = NioEventLoopGroup(4)
        try {
            val bootstrap = ServerBootstrap()
                    .group(eventLoopGroup)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .childHandler(ServerInitializer())
                    .channel(NioServerSocketChannel::class.java)

            val channelFeature = bootstrap.bind(port)
            channelFeature.channel().closeFuture().sync();
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            eventLoopGroup.shutdownGracefully()
        }
    }
}

class ServerInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        println("wy::Server-->ServerInitializer::initChannel")
        val p = ch.pipeline()
//        p.addLast(MessageDecoder())
//        p.addLast(MessageHandler())
    }
}