package MasterNode;

import Server.ServerCore;

public class MasterNode {

    private ServerCore server;

    public MasterNode(int port) {
        server = new ServerCore(port);
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }
}
