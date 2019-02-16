package org.bonbom;

import com.github.thorbenkuck.netcom2.network.server.ServerStart;
import lombok.extern.slf4j.Slf4j;
import org.bonbom.communication.RemoteAnswer;
import org.bonbom.communication.RemoteMethodCall;
import org.bonbom.communication.SessionRegistrationCall;

import java.util.List;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.22
 */

@Slf4j
public abstract class Server extends NetworkNode {

    private int port;
    private SessionManager sessionManager;
    private ServerStart serverStart;

    public Server(int port) {
        this.port = port;
        this.sessionManager = new SessionManager();
    }

    public void start() {
        new Thread(() -> {
            try {
                this.serverStart = ServerStart.at(port);
                this.serverStart.launch();

                this.serverStart.getCommunicationRegistration()
                        .register(SessionRegistrationCall.class)
                        .addFirst((session, o) -> {
                            sessionManager.register(o.getName(), session);
                            this.serverStart.clientList().getClient(session)
                                    .ifPresent(client ->
                                            client.addDisconnectedHandler(client1 -> {
                                                String name = sessionManager.get(client.getSession());
                                                sessionManager.unRegister(name);
                                                onDisconnect(name);
                                                    }));
                        });

                this.serverStart.getCommunicationRegistration()
                        .register(RemoteMethodCall.class)
                        .addFirst((session, o) -> onReceive(o));

                this.serverStart.getCommunicationRegistration()
                        .register(RemoteAnswer.class)
                        .addFirst((session, o) -> onReceive(o));

                log.info("Server is up and accepting clients");
                this.serverStart.acceptAllNextClients();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onDisconnect(String name) {
        log.info("Client {} disconnected", name);
    }

    public void stop() {
        serverStart.softStop();
    }

    @Override
    public void send(RemoteMethodCall remoteMethodCall) {
        log.debug("Sending RemoteMethodCall: {}", remoteMethodCall);

        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).send(remoteMethodCall);
            return;
        }
        throw new RuntimeException("No session for " + remoteMethodCall.getReceiverName());
    }

    @Override
    void send(RemoteAnswer remoteAnswer) {
        log.debug("Sending remoteAnswer: {}", remoteAnswer);

        if (sessionManager.contains(remoteAnswer.getReceiverName())) {
            sessionManager.get(remoteAnswer.getReceiverName()).send(remoteAnswer);
            return;
        }
        throw new RuntimeException("No session for " + remoteAnswer.getReceiverName());
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        log.debug("Sending RemoteMethodCall and waiting for answer: {}", remoteMethodCall);

        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).send(remoteMethodCall);
            return getReceiver().get(remoteMethodCall.hashCode());
        }
        throw new RuntimeException("No session for " + remoteMethodCall.getReceiverName());
    }

    public int getPort() {
        if (serverStart != null) return serverStart.getPort();
        return -1;
    }

    public List<String> getConnectedClients() {
        return sessionManager.getSessionNames();
    }

}
