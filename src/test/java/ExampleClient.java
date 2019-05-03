import commands.Command;
import org.bonbom.Client;
import org.bonbom.Enhancement;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        Command command = Enhancement.createProxy(client, Command.class, false);
        System.out.println(command.sayHiToServer());
    }
}
