import com.github.thorbenkuck.netcom2.network.client.ClientStart;
import com.github.thorbenkuck.netcom2.network.client.Sender;
import commands.Command;
import commands.Commands;
import communication.ObjectReceiver;
import communication.RpcAnswer;

import java.util.ArrayList;
import java.util.List;

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

    List<Class> commands = new ArrayList<>();

    public Object received = null;

    public Client(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
        ClientStart clientStart = ClientStart.at(host, port);

        clientStart.getCommunicationRegistration()
                .register(RpcAnswer.class)
                .addFirst(this::onReceive);

        clientStart.launch();
        this.sender = Sender.open(clientStart);
        this.receiver = new ObjectReceiver();
    }

    public void onReceive(RpcAnswer rpcAnswer) {
        receiver.onReceive(rpcAnswer);
    }

    public void send(Object object) {
        sender.objectToServer(object);
    }

    public Object sendAndWait(Object object) throws InterruptedException {
        sender.objectToServer(object);
        return receiver.get(object.hashCode());
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static void main3(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        Commands commands = Enhancement.createProxy(client, Commands.class);
        System.out.println(commands.sendBack("test23", "test24"));
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        Command command = Enhancement.createProxy(client, Command.class);
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            System.out.println(command.output("test"));
        }
        System.out.println(System.currentTimeMillis() - time);
    }
}
