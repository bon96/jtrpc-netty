import com.github.thorbenkuck.netcom2.network.client.ClientStart;
import com.github.thorbenkuck.netcom2.network.client.Sender;
import commands.Command;
import commands.Commands;
import communication.*;

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

    public void onReceive(RemoteMethodCall remoteMethodCall) {
        for (RemoteMethod remoteMethod : getRegisteredMethods()) {
            if (remoteMethod.hashCode() == remoteMethodCall.hashCode()) {
                try {
                    if (remoteMethod.getMethod().getReturnType().equals(void.class)) {
                        remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects());
                    } else {
                        RemoteAnswer answer = new RemoteAnswer(
                                remoteMethodCall.hashCode(),
                                remoteMethod.getMethod().invoke(remoteMethod.getClassInstance(), remoteMethodCall.getObjects()));
                        send(answer);
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

    public void send(Object object) {
        if (object instanceof RemoteMethodCall || object instanceof RemoteAnswer || object instanceof SessionRegistrationCall) {
            sender.objectToServer(object);
            return;
        }
        throw new IllegalArgumentException("Trying to send unsupported object type " + object.getClass().getName());
    }

    public Object sendAndWait(Object object) throws InterruptedException {
        if (object instanceof RemoteMethodCall) {
            sender.objectToServer(object);
            return getReceiver().get(object.hashCode());
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
        return "client001";
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static void main2(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.registerMethods(Commands.class);
        while (true) {
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) throws Exception {
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
