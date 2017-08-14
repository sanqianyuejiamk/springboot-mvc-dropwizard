package com.mengka.springboot.netty_pool_01;

import com.mengka.springboot.netty_pool_01.client.TestChannelPoolHandler;
import com.mengka.springboot.util.TimeUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CyclicBarrier;

/**
 *  netty客户端
 *  1.与多个服务端交互；
 *  2.与单个服务端建立连接池；
 *
 * @author mengka
 * @date 2017/08/14.
 */
@Slf4j
public class Client {

    InetSocketAddress addr1 = new InetSocketAddress(Constant.SERVER_00_IP, Constant.SERVER_PORT);
    InetSocketAddress addr2 = new InetSocketAddress(Constant.SERVER_01_IP, Constant.SERVER_PORT);

    final EventLoopGroup workerGroup = new NioEventLoopGroup();
    final Bootstrap bootstrap = new Bootstrap();

    /**
     *  ChannelPoolMap与多个服务端建立连接
     *
     */
    ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    public void build() throws Exception {
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
//                return new SimpleChannelPool(bootstrap.remoteAddress(key), new TestChannelPoolHandler());

                /**
                 *  设置maxConnections
                 */
                return new FixedChannelPool(bootstrap.remoteAddress(key), new TestChannelPoolHandler(),50);
            }
        };
    }

    public static void main(String[] args) throws Exception {

        final Client client = new Client();
        client.build();

        final CyclicBarrier aaBarrier = new CyclicBarrier(10);



        /**
         *  向服务端发送10条消息
         *
         */
        for (int i = 0; i < 10; i++) {
            //同时创建10个线程发送消息
            new Thread(new Runnable() {
                public void run() {
                    try {
                        aaBarrier.await();
                    }catch (Exception e){
                        log.info("CyclicBarrier error!",e);
                    }

                    //从连接池中获取连接
                    final SimpleChannelPool pool = client.poolMap.get(client.addr1);
                    Future<Channel> future = pool.acquire();
                    future.addListener(new FutureListener<Channel>() {

                        public void operationComplete(Future<Channel> f) {
                            if (f.isSuccess()) {
                                Channel channel = f.getNow();
                                // Do somethings
                                // ...
                                // ...
                                String message = "Just for Test[" + TimeUtil.toDate(new Date(), "yyyy-MM-dd HH:mm:ss") + "]" + "\r\n";
                                ChannelFuture lastWriteFuture = null;
                                lastWriteFuture = channel.writeAndFlush(message);

                                // Wait until all messages are flushed before closing the channel.
                                if (lastWriteFuture != null) {
                                    try {
                                        lastWriteFuture.sync();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                log.info("thread["+Thread.currentThread().getName()+"] send message = "+message);

                                // Release back to pool
                                pool.release(channel);
                            }
                        }
                    });
                }
            }).start();
        }

    }
}
