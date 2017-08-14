package com.mengka.springboot.netty_pool_01.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class TestChannelPoolHandler implements ChannelPoolHandler {
    public void channelReleased(Channel channel) throws Exception {
        log.info("-----TestChannelPoolHandler----- channelReleased");
    }

    public void channelAcquired(Channel channel) throws Exception {
        log.info("-----TestChannelPoolHandler----- channelAcquired");
    }

    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);

        //可以在此绑定channel的handler
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        pipeline.addLast("handler", new HttpBackendHandler());
    }
}
