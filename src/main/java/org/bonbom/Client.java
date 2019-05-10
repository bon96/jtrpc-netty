package org.bonbom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.bonbom.communication.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.38
 */

public class Client extends NetworkNode {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private String host;
    private int port;
    private String name;

    private EventLoopGroup group;

    private Channel channel;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.name = "client" + ThreadLocalRandom.current().nextInt();
    }

    public void start() throws Exception {
        group = new NioEventLoopGroup();

        ExecutorService executor = Executors.newFixedThreadPool(getThreads());

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler() {

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(new SessionRegistrationCall(getName()));
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
                                if (o instanceof RemoteAnswer) {
                                    executor.submit(() -> onReceive((RemoteAnswer) o));
                                }

                                if (o instanceof RemoteMethodCall) {
                                    executor.submit(() -> onReceive((RemoteMethodCall) o));
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace();
                                ctx.close();
                            }
                        });

                        ch.closeFuture().addListener((ChannelFutureListener) channelFuture -> onDisconnect());
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port).sync();

        channel = future.channel();
        logger.info("Client is up and connected to server");

        new Thread(() -> {
            try {
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stop();
            }
        }).start();
    }

    public void onDisconnect() {
        logger.warn("Lost connection to server");
    }

    public void stop() {
        group.shutdownGracefully();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void send(RemoteObject remoteObject) {
        logger.debug("Sending remoteObject: {}", remoteObject);
        channel.writeAndFlush(remoteObject);
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        logger.debug("Sending RemoteMethodCall and waiting for answer: {}", remoteMethodCall);
        channel.writeAndFlush(remoteMethodCall);
        return getReceiver().get(remoteMethodCall.getId());
    }

    public <T> T createProxy(Class proxyClass) {
        return createProxy(proxyClass, true);
    }

    public <T> T createProxy(Class proxyClass, boolean ignorePath) {
        return createProxy( "server", proxyClass, ignorePath);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getTimeout() {
        return ObjectReceiver.TIMEOUT;
    }

    public void setTimeout(long ms) {
        ObjectReceiver.TIMEOUT = ms;
    }
}
