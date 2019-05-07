import commands.Command;
import commands.CommandImpl;
import org.bonbom.Client;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        client.registerMethods(Command.class, CommandImpl.class);
    }
}
