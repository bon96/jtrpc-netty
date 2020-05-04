package org.bonbom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
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
import org.bonbom.communication.RemoteObject;
import org.bonbom.communication.SessionRegistrationCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.22
 */

public class Server extends NetworkNode {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public static LogLevel logLevel = LogLevel.INFO;
    private int port;
    private SessionManager<Channel> sessionManager;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ExecutorService executor;

    public Server() {
        this(0);
    }

    public Server(int port) {
        this.port = port;
        this.sessionManager = new SessionManager<>();
        logLevel = LogLevel.INFO;
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        executor = Executors.newFixedThreadPool(getThreads());

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(logLevel))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());
                        ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
                                if (o instanceof SessionRegistrationCall) {
                                    String name = ((SessionRegistrationCall) o).getName();
                                    sessionManager.register(name, ctx.channel());
                                    logger.debug("Registered " + ((SessionRegistrationCall) o).getName());

                                    ctx.channel().closeFuture().addListener((ChannelFutureListener) channelFuture ->
                                            onDisconnect(name));
                                }

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
                                ctx.channel().close();
                            }
                        });
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();

        running = true;

        if (port == 0) {
            port = ((InetSocketAddress) future.channel().localAddress()).getPort();
        }
    }

    public void onDisconnect(String name) {
        logger.info("Client {} disconnected", name);
        sessionManager.unRegister(name);
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        executor.shutdown();
        running = false;
    }

    @Override
    public void send(RemoteObject remoteObject) {
        logger.debug("Sending RemoteMethodCall: {}", remoteObject);

        if (sessionManager.contains(remoteObject.getReceiverName())) {
            sessionManager.get(remoteObject.getReceiverName()).writeAndFlush(remoteObject);
            return;
        }
        throw new RuntimeException("No session for client named \"" + remoteObject.getReceiverName() + "\"");
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        logger.debug("Sending RemoteMethodCall and waiting for answer: {}", remoteMethodCall);

        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).writeAndFlush(remoteMethodCall);
            return getReceiver().get(remoteMethodCall.getId());
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
