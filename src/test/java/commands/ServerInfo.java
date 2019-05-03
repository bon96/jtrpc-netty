package commands;


import org.bonbom.Server;

import java.util.List;

/**
 * Tommi
 * Date: 8.2.2019
 * Time: 18.54
 */

public class ServerInfo extends Commands {

    private Server server;

    public ServerInfo(Server server) {
        this.server = server;
    }

    public List<String> getConnectedClients() {
        return server.getConnectedClients();
    }
}
