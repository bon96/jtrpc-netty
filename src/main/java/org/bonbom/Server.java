package org.bonbom;

import com.github.thorbenkuck.netcom2.network.server.ServerStart;
import com.github.thorbenkuck.netcom2.network.shared.clients.Client;
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

        new Thread(() -> {
            try {
                this.serverStart = ServerStart.at(port);
                this.serverStart.launch();

                this.serverStart.getCommunicationRegistration()
                        .register(SessionRegistrationCall.class)
                        .addFirst((session, o) -> {
                            sessionManager.register(o.getName(), session);
                            this.serverStart.clientList().getClient(session)
                                    .ifPresent((Client client ) ->
                                            client.addDisconnectedHandler((Client client1) ->
                                                    sessionManager.unRegister(client.getSession())));
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
        return port;
    }

    public List<String> getConnectedClients() {
        return sessionManager.getSessionNames();
    }

}
