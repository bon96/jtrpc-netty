package org.bonbom;

import org.bonbom.commands.Command;
import org.bonbom.commands.Commands;
import org.bonbom.commands.ServerInfo;

public class ExampleClient extends Client {

    public ExampleClient(String host, int port) throws Exception {
        super(host, port);
    }

    @Override
    public String getName() {
        return "client001";
    }

    public static void main(String[] args) throws Exception {
        Client client = new ExampleClient("localhost", 8080);
        ServerInfo serverInfo = Enhancement.createProxy(client, ServerInfo.class);
        System.out.println(serverInfo.getConnectedClients());
    }

    public static void main5(String[] args) throws Exception {
        Client client = new ExampleClient("localhost", 8080);
        client.registerMethods(Commands.class);
        Command command = Enhancement.createProxy(client, Command.class);
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            System.out.println(command.output("test"));
        }
        System.out.println(System.currentTimeMillis() - time);
        while (true) {
            Thread.sleep(500);
        }
    }
}
