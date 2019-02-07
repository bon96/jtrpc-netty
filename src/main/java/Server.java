import com.github.thorbenkuck.netcom2.network.server.ServerStart;
import com.github.thorbenkuck.netcom2.network.shared.Session;
import commands.Command;
import commands.CommandImpl;
import communication.RemoteAnswer;
import communication.RemoteMethod;
import communication.RemoteMethodCall;
import communication.SessionRegistrationCall;
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

    public Server(int port) {
        new Thread(() -> {
            try {
                this.port = port;
                this.sessionManager = new SessionManager();
                this.serverStart = ServerStart.at(port);
                this.serverStart.launch();

                this.serverStart.getCommunicationRegistration()
                        .register(SessionRegistrationCall.class)
                        .addFirst((session, o) -> sessionManager.register(o.getName(), session));

                this.serverStart.getCommunicationRegistration()
                        .register(RemoteMethodCall.class)
                        .addFirst((session, o) -> onReceive(o, session));

                this.serverStart.acceptAllNextClients();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void onReceive(RemoteMethodCall remoteMethodCall, Session session) {
        for (RemoteMethod remoteMethod : getRegisteredMethods()) {
            if (remoteMethod.hashCode() == remoteMethodCall.hashCode()) {
                try {
                    RemoteAnswer answer = new RemoteAnswer(
                            remoteMethodCall.hashCode(),
                            remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects()));
                    session.send(answer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.registerMethods(Command.class, CommandImpl.class);
    }

    @Override
    public void send(Object object) {

    }

    @Override
    public Object sendAndWait(Object object) throws InterruptedException {
        return null;
    }
}
