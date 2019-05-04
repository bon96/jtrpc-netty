package org.bonbom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.bonbom.communication.RemoteAnswer;
import org.bonbom.communication.RemoteMethodCall;
import org.bonbom.communication.SessionRegistrationCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.22
 */

public class Server extends NetworkNode {

    private static final Logger logger = LoggerFactory.getLogger(NetworkNode.class);

    private int port;
    private SessionManager<Channel> sessionManager;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Server(int port) {
        this.port = port;
        this.sessionManager = new SessionManager<>();
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
                                    if (o instanceof SessionRegistrationCall) {
                                        sessionManager.register(((SessionRegistrationCall) o).getName(), ctx.channel());
                                        logger.debug("Registered " + ((SessionRegistrationCall) o).getName());
                                    }

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

                            ch.closeFuture().addListener((ChannelFutureListener) channelFuture ->
                                    onDisconnect(sessionManager.get(ch)));
                        }
                    });
            ChannelFuture f = b.bind(port).sync();

            if (port == 0) {
                port = ((InetSocketAddress) f.channel().localAddress()).getPort();
            }

            f.channel().closeFuture().sync();
        } finally {
            stop();
        }
    }

    public void onDisconnect(String name) {
        logger.info("Client {} disconnected", name);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void send(RemoteMethodCall remoteMethodCall) {
        logger.debug("Sending RemoteMethodCall: {}", remoteMethodCall);

        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).writeAndFlush(remoteMethodCall);
            return;
        }
        throw new RuntimeException("No session for client named \"" + remoteMethodCall.getReceiverName() + "\"");
    }

    @Override
    void send(RemoteAnswer remoteAnswer) {
        logger.debug("Sending remoteAnswer: {}", remoteAnswer);

        if (sessionManager.contains(remoteAnswer.getReceiverName())) {
            sessionManager.get(remoteAnswer.getReceiverName()).writeAndFlush(remoteAnswer);
            return;
        }
        throw new RuntimeException("No session for " + remoteAnswer.getReceiverName());
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        logger.debug("Sending RemoteMethodCall and waiting for answer: {}", remoteMethodCall);

        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).writeAndFlush(remoteMethodCall);
            return getReceiver().get(remoteMethodCall.hashCode());
        }
        throw new RuntimeException("No session for " + remoteMethodCall.getReceiverName());
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getName() {
        return "server";
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public List<String> getConnectedClients() {
        return sessionManager.getSessionNames();
    }
}
