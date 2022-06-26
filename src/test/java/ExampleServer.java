import commands.Command;
import commands.CommandImpl;
import org.bonbom.Server;


public class ExampleServer {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080); //Initiate server parameters
        server.start(); //start server and begin accepting client connections

        System.setProperty("source", "server"); //set source to server for the client test example

        server.registerMethods(Command.class, CommandImpl.class); //register Command.class methods and link them to CommandImpl class
    }
}


