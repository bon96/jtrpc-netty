import com.github.thorbenkuck.netcom2.network.server.ServerStart;
import commands.Command;
import commands.CommandImpl;
import commands.Commands;
import communication.*;
import lombok.extern.slf4j.Slf4j;

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
    private ObjectReceiver receiver;

    public Server(int port) {
        this.port = port;
        this.sessionManager = new SessionManager();
        this.receiver = new ObjectReceiver();

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

    public void onReceive(RemoteMethodCall remoteMethodCall) {
        for (RemoteMethod remoteMethod : getRegisteredMethods()) {
            if (remoteMethod.hashCode() == remoteMethodCall.hashCode()) {
                try {
                    if (remoteMethod.getMethod().getReturnType().equals(void.class)) {
                        remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects());
                    } else {
                        RemoteAnswer answer = new RemoteAnswer(
                                remoteMethodCall.getSenderName(),
                                remoteMethodCall.hashCode(),
                                remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects()));
                        send(answer, remoteMethodCall.getSenderName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        throw new IllegalArgumentException("Method " + remoteMethodCall.getClassName() + "::" + remoteMethodCall.getMethodName()
                + " is not registered!");
    }

    public void send(Object object, String destination) {
        if (object instanceof RemoteMethodCall || object instanceof RemoteAnswer) {
            if (sessionManager.contains(destination)) {
                sessionManager.get(destination).send(object);
            }
        }
        throw new IllegalArgumentException("Trying to send unsupported object type " + object.getClass().getName());
    }

    public Object sendAndWait(Object object, String destination) throws InterruptedException {
        if (object instanceof RemoteMethodCall) {
            if (sessionManager.contains(destination)) {
                sessionManager.get(destination).send(object);
                return getReceiver().get(object.hashCode());
            } else {
                throw new RuntimeException("No session for NetworkNode " + destination);
            }
        }
        throw new IllegalArgumentException("Trying to send unsupported object type " + object.getClass().getName());
    }

    public void onReceive(RemoteAnswer remoteAnswer) {
        receiver.onReceive(remoteAnswer);
    }

    public ObjectReceiver getReceiver() {
        return receiver;
    }

    @Override
    String getName() {
        return "server";
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.registerMethods(Command.class, CommandImpl.class);
        while(!server.sessionManager.contains("client001")) {
            Thread.sleep(1000);
        }
        Commands commands = Enhancement.createProxy(server, "client001", Commands.class);
        System.out.println(commands.sendBack("test23", "test24"));
    }
}
