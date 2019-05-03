import org.bonbom.Client;
import org.bonbom.Enhancement;
import commands.ServerInfo;

public class ExampleClient extends Client {

    public ServerInfo serverInfo;

    public ExampleClient(String host, int port) throws Exception {
        super(host, port);
        this.start();
        serverInfo = Enhancement.createProxy(this, ServerInfo.class, false);
    }

    @Override
    public String getName() {
        return "client1233";
    }

    public static void main(String[] args) throws Exception {
        ExampleClient client = new ExampleClient("localhost", 8080);
        System.out.println(client.serverInfo.sendBack("test230", "test32"));
    }
}
