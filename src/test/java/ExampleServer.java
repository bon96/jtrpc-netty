import commands.Command;
import commands.CommandImpl;
import org.bonbom.Server;


public class ExampleServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.start();
        server.registerMethods(Command.class, CommandImpl.class);
        while(server.getConnectedClients().size() == 0) {
            Thread.sleep(500);
        }

        Command command = server.createProxy(server.getConnectedClients().get(0), Command.class);
        for (int i = 0; i < 10000; i++) {
            System.out.println(command.sayHiToServer());
        }
    }
}


