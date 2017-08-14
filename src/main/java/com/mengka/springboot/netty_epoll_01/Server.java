package com.mengka.springboot.netty_epoll_01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

/**
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new EpollEventLoopGroup(1);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(EpollServerSocketChannel.class);
        bootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);
        bootstrap.group(group).childHandler(new MengkaServerHandler());
        Channel pc = bootstrap.bind(new InetSocketAddress(0)).syncUninterruptibly().channel();
    }
}
