package com.mengka.springboot.netty_epoll_01;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.EpollSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

/**
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class MengkaServerHandler extends ChannelInboundHandlerAdapter {

    private static final int SPLICE_LEN = 1;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ctx.channel().config().setAutoRead(false);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);
        bootstrap.channel(EpollSocketChannel.class);
        bootstrap.group(ctx.channel().eventLoop()).handler(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext context) throws Exception {
                final EpollSocketChannel ch = (EpollSocketChannel) ctx.channel();
                final EpollSocketChannel ch2 = (EpollSocketChannel) context.channel();
                // We are splicing two channels together, at this point we have a tcp proxy which handles all
                // the data transfer only in kernel space!

                // Integer.MAX_VALUE will splice infinitly.
                ch.spliceTo(ch2, Integer.MAX_VALUE).addListener(new ChannelFutureListener() {

                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            future.channel().close();
                        }
                    }
                });
                // Trigger multiple splices to see if partial splicing works as well.
                ch2.spliceTo(ch, SPLICE_LEN).addListener(new ChannelFutureListener() {

                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            future.channel().close();
                        } else {
                            ch2.spliceTo(ch, SPLICE_LEN).addListener(this);
                        }
                    }
                });
                ctx.channel().config().setAutoRead(true);
            }

            @Override
            public void channelInactive(ChannelHandlerContext context) throws Exception {
                context.close();
            }
        });


        bootstrap.connect(new InetSocketAddress(Constant.SERVER_00_IP, Constant.SERVER_PORT)).addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    ctx.close();
                } else {
                    future.channel().closeFuture().addListener(new ChannelFutureListener() {

                        public void operationComplete(ChannelFuture future) throws Exception {
                            ctx.close();
                        }
                    });
                }
            }
        });
    }
}

