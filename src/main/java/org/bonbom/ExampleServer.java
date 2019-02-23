package org.bonbom;

import org.bonbom.commands.CommandImpl;
import org.bonbom.test.Command;

public class ExampleServer extends Server {

    public ExampleServer(int port) {
        super(port);
        this.registerMethods(this);
    }

    public static void main(String[] args) throws Exception {
        Server server = new ExampleServer(8080);
        server.start();
        server.registerMethods(Command.class, CommandImpl.class);
    }
}
