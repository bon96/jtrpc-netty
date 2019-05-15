import commands.Command2;
import org.bonbom.Client;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.start();
        Command2 command2 = client.createProxy(Command2.class);
        for (int i = 0; i < 10000; i++) {
            System.out.println(command2.sayHiToServer());
        }
        client.stop();
    }
}
