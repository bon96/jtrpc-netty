import commands.Command;
import org.bonbom.Client;

public class ExampleClient {

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080); //initiate client parameters
        client.start(); //Connect client to server

        Command command = client.createProxy(Command.class); //create a proxy class of which all method calls are executed on server
        System.out.println(command.sayHiToServer("Client says hi!")); //prints message on server and server returns a value as response

        System.setProperty("source", "client"); //for testing if server or client executes the code below

        command.run(() -> System.out.println(System.getProperty("source"))); //execute lambda function on the server
        client.stop(); //stop the client
    }
}
