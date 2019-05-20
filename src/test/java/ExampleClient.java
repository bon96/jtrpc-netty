import commands.Command2;
import org.bonbom.Client;

import java.util.HashMap;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        Command2 command2 = client.createProxy(Command2.class);
        command2.sayHiToServer(new HashMap<>());
        client.stop();
    }
}
