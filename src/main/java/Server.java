import com.github.thorbenkuck.netcom2.network.server.ServerStart;
import com.github.thorbenkuck.netcom2.network.shared.Session;
import commands.Command;
import commands.CommandImpl;
import communication.RemoteMethod;
import communication.RemoteMethodCall;
import communication.RpcAnswer;
import lombok.extern.slf4j.Slf4j;

/**
 * Tommi
 * Date: 13.1.2019
 * Time: 17.22
 */

@Slf4j
public class Server extends NetworkNode {

    private int port;

    public Server(int port) {
        new Thread(() -> {
            try {
                this.port = port;
                ServerStart serverStart = ServerStart.at(port);
                serverStart.launch();

                serverStart.getCommunicationRegistration()
                        .register(RemoteMethodCall.class)
                        .addFirst((session, o) -> receiveMethodCall(o, session));
                serverStart.acceptAllNextClients();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void receiveMethodCall(RemoteMethodCall remoteMethodCall, Session session) {
        for (RemoteMethod remoteMethod : getRegisteredMethods()) {
            if (remoteMethod.hashCode() == remoteMethodCall.hashCode()) {
                try {
                    RpcAnswer answer = new RpcAnswer(
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
