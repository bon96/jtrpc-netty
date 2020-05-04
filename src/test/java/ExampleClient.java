import commands.Command2;
import org.bonbom.Client;

import java.util.HashMap;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        Command2 command2 = client.createProxy(Command2.class);
        System.setProperty("source", "client");
        command2.run(() -> System.out.println(System.getProperty("source")));
        client.stop();
    }
}
