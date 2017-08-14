package com.mengka.springboot.netty_pool_01;

import com.mengka.springboot.netty_pool_01.server.MengkaServerChannelInitializer;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws Exception {

        /**
         *  bossGroup用来接收客户端连接;
         *  workerGroup用来执行非阻塞的IO操作，主要是read，write;
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new MengkaServerChannelInitializer());
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            // 服务器绑定端口监听
            ChannelFuture channelFuture = bootstrap.bind(Constant.SERVER_PORT).sync();

            //Wait until the server socket is closed
            channelFuture.channel().closeFuture().sync();

            log.error("--------- , server start..");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
