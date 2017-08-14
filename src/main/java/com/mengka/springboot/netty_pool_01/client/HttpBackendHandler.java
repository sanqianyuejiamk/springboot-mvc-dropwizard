package com.mengka.springboot.netty_pool_01.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class HttpBackendHandler extends ChannelInboundHandlerAdapter {

    public HttpBackendHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Backend Handler is Active!");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("-----channelRead0----- msg = "+msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Backend Handler destroyed!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
