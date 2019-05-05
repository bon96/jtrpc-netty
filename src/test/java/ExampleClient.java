import commands.Command;
import org.bonbom.Client;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        Command command = client.createProxy(Command.class);
        for (int i = 0; i < 10000; i++) {
            String result = command.sayHiToServer();
            System.out.println(result);
            if (result == null) {
                break;
            }
        }
        client.stop();
    }
}
