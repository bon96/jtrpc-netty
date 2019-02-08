package org.bonbom;

import com.github.thorbenkuck.netcom2.network.client.ClientStart;
import com.github.thorbenkuck.netcom2.network.client.Sender;
import org.bonbom.commands.Command;
import org.bonbom.commands.Commands;
import org.bonbom.commands.ServerInfo;
import org.bonbom.communication.ObjectReceiver;
import org.bonbom.communication.RemoteAnswer;
import org.bonbom.communication.RemoteMethodCall;
import org.bonbom.communication.SessionRegistrationCall;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.38
 */

public class Client extends NetworkNode {

    private String host;
    private int port;
    private Sender sender;
    private ObjectReceiver receiver;

    public Client(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        this.receiver = new ObjectReceiver();

        ClientStart clientStart = ClientStart.at(host, port);

        clientStart.getCommunicationRegistration()
                .register(RemoteAnswer.class)
                .addFirst(this::onReceive);

        clientStart.getCommunicationRegistration()
                .register(RemoteMethodCall.class)
                .addFirst(this::onReceive);

        clientStart.launch();

        this.sender = Sender.open(clientStart);
        send(new SessionRegistrationCall(getName()));
    }



    @Override
    public String getName() {
        return "client001";
    }

    @Override
    public void send(RemoteMethodCall remoteMethodCall) {
        sender.objectToServer(remoteMethodCall);
    }

    @Override
    public void send(RemoteAnswer remoteAnswer) {
        sender.objectToServer(remoteAnswer);
    }

    public void send(SessionRegistrationCall sessionRegistrationCall) {
        sender.objectToServer(sessionRegistrationCall);
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        sender.objectToServer(remoteMethodCall);
        return getReceiver().get(remoteMethodCall.hashCode());
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        ServerInfo serverInfo = Enhancement.createProxy(client, ServerInfo.class);
        System.out.println(serverInfo.getConnectedClients());
    }

    public static void main5(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.registerMethods(Commands.class);
        Command command = Enhancement.createProxy(client, Command.class);
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            System.out.println(command.output("test"));
        }
        System.out.println(System.currentTimeMillis() - time);
        while (true) {
            Thread.sleep(500);
        }
    }
}
