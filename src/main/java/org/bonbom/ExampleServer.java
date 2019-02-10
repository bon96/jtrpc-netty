package org.bonbom;

import org.bonbom.commands.Command;
import org.bonbom.commands.CommandImpl;
import org.bonbom.commands.ServerInfo;

public class ExampleServer extends Server {

    public ExampleServer(int port) {
        super(port);
        this.registerMethods(this);
    }

    @Override
    public String getName() {
        return "server";
    }

    public static void main(String[] args) throws Exception {
        Server server = new ExampleServer(8080);
        server.start();
        server.registerMethods(server);
    }
}
