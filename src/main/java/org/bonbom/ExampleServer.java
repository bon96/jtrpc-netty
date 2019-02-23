package org.bonbom;

import org.bonbom.commands.ServerInfo;

public class ExampleServer extends Server {

    public ExampleServer(int port) {
        super(port);
    }

    public static void main(String[] args) throws Exception {
        ExampleServer server = new ExampleServer(8080);
        server.start();
        server.registerMethods(new ServerInfo(server));
    }
}
