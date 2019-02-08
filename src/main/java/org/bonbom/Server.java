package org.bonbom;

import com.github.thorbenkuck.netcom2.network.server.ServerStart;
import lombok.extern.slf4j.Slf4j;
import org.bonbom.commands.Command;
import org.bonbom.commands.CommandImpl;
import org.bonbom.commands.ServerInfo;
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
public class Server extends NetworkNode {

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
                        .addFirst((session, o) -> sessionManager.register(o.getName(), session));

                this.serverStart.getCommunicationRegistration()
                        .register(RemoteMethodCall.class)
                        .addFirst((session, o) -> onReceive(o));

                this.serverStart.getCommunicationRegistration()
                        .register(RemoteAnswer.class)
                        .addFirst((session, o) -> onReceive(o));

                this.serverStart.acceptAllNextClients();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void send(RemoteMethodCall remoteMethodCall) {
        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).send(remoteMethodCall);
            return;
        }
        throw new RuntimeException("No session for " + remoteMethodCall.getReceiverName());
    }

    @Override
    void send(RemoteAnswer remoteAnswer) {
        if (sessionManager.contains(remoteAnswer.getReceiverName())) {
            sessionManager.get(remoteAnswer.getReceiverName()).send(remoteAnswer);
            return;
        }
        throw new RuntimeException("No session for " + remoteAnswer.getReceiverName());
    }

    @Override
    public Object sendAndWait(RemoteMethodCall remoteMethodCall) throws InterruptedException {
        if (sessionManager.contains(remoteMethodCall.getReceiverName())) {
            sessionManager.get(remoteMethodCall.getReceiverName()).send(remoteMethodCall);
            return getReceiver().get(remoteMethodCall.hashCode());
        }
        throw new RuntimeException("No session for " + remoteMethodCall.getReceiverName());
    }

    @Override
    public String getName() {
        return "server";
    }

    public int getPort() {
        return port;
    }

    public List<String> getConnectedClients() {
        return sessionManager.getSessionNames();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.registerMethods(Command.class, CommandImpl.class);
        server.registerMethods(new ServerInfo(server));
    }
}
