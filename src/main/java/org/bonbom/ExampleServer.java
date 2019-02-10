package org.bonbom;

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
