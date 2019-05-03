import commands.Command;
import commands.CommandImpl;
import lombok.extern.slf4j.Slf4j;
import org.bonbom.Server;


public class ExampleServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.start();
        server.registerMethods(Command.class, CommandImpl.class);
    }
}


