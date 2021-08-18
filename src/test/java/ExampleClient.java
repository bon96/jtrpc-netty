import commands.Command;
import org.bonbom.Client;
public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        Command command = client.createProxy(Command.class);
        //System.setProperty("source", "client");
        //command.run(() -> System.out.println(System.getProperty("source")));
        //client.stop();
        System.out.println(command.sayHiToServer());
    }
}
