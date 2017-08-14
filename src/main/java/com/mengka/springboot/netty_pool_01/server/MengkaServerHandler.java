package com.mengka.springboot.netty_pool_01.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class MengkaServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * step02:
     *       channel建立
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("-----------, client active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("SERVER接收到消息:" + msg);

        /**
         * step03:
         *     接受client的消息，返回接收成功消息给client
         */
        ctx.channel().writeAndFlush("yes, server is accepted you ,nice !" + msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception is general");
        new Throwable(cause);
    }
}
