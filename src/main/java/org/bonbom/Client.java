package org.bonbom;

import com.github.thorbenkuck.netcom2.exceptions.StartFailedException;
import com.github.thorbenkuck.netcom2.network.client.ClientStart;
import com.github.thorbenkuck.netcom2.network.client.Sender;
import lombok.extern.slf4j.Slf4j;
import org.bonbom.communication.*;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.38
 */

@Slf4j
public class Client extends NetworkNode {

    private String host;
    private int port;
    private Sender sender;
    private ObjectReceiver receiver;
    private ClientStart clientStart;
    private String name;

    public Client(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        this.receiver = new ObjectReceiver();
        this.name = "client" + ThreadLocalRandom.current().nextInt();
    }

    public void start() {
        try {
            clientStart = ClientStart.at(host, port);

            clientStart.getCommunicationRegistration()
                    .register(RemoteAnswer.class)
                    .addFirst(this::onReceive);

            clientStart.getCommunicationRegistration()
                    .register(RemoteMethodCall.class)
                    .addFirst(this::onReceive);

            clientStart.addDisconnectedHandler(client -> onDisconnect());

            clientStart.launch();

            this.sender = Sender.open(clientStart);
            send(new SessionRegistrationCall(getName()));

            log.info("Client is up and connected to server");
        } catch (StartFailedException e) {
            e.printStackTrace();
        }
    }

    public void onDisconnect() {
        log.warn("Lost connection to server");
    }

    public void stop() {
        clientStart.softStop();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void send(RemoteMethodCall remoteMethodCall) {
        log.debug("Sending RemoteMethodCall: {}", remoteMethodCall);
        sender.objectToServer(remoteMethodCall);
    }

    @Override
    public void send(RemoteAnswer remoteAnswer) {
        log.debug("Sending remoteAnswer: {}", remoteAnswer);
        sender.objectToServer(remoteAnswer);
    }

    public void send(SessionRegistrationCall sessionRegistrationCall) {
        log.debug("Sending SessionRegistrationCall: {}", sessionRegistrationCall);
        sender.objectToServer(sessionRegistrationCall);
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        log.debug("Sending RemoteMethodCall and waiting for answer: {}", remoteMethodCall);
        sender.objectToServer(remoteMethodCall);
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
