package org.bonbom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.bonbom.communication.FutureReceive;
import org.bonbom.communication.RemoteAnswer;
import org.bonbom.communication.RemoteMethodCall;
import org.bonbom.communication.SessionRegistrationCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
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
                                        onReceive((RemoteAnswer) o, ctx);
                                    }

                                    if (o instanceof RemoteMethodCall) {
                                        onReceive((RemoteMethodCall) o, ctx);
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



            ChannelFuture f = b.connect(host, port).sync();

            channel = f.channel();

            logger.info("Client is up and connected to server");

            f.channel().closeFuture().sync();
        } finally {
            stop();
        }
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
    public void send(RemoteMethodCall remoteMethodCall) {
        logger.debug("Sending RemoteMethodCall: {}", remoteMethodCall);
        channel.writeAndFlush(remoteMethodCall);
    }

    @Override
    public void send(RemoteAnswer remoteAnswer) {
        logger.debug("Sending remoteAnswer: {}", remoteAnswer);
        channel.writeAndFlush(remoteAnswer);
    }

    public void send(SessionRegistrationCall sessionRegistrationCall) {
        logger.debug("Sending SessionRegistrationCall: {}", sessionRegistrationCall);
        channel.writeAndFlush(sessionRegistrationCall);
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        logger.debug("Sending RemoteMethodCall and waiting for answer: {}", remoteMethodCall);
        channel.writeAndFlush(remoteMethodCall);
        return getReceiver().get(remoteMethodCall.hashCode());
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getTimeout() {
        return FutureReceive.timeout;
    }

    public void setTimeout(long ms) {
        FutureReceive.timeout = ms;
    }
}
