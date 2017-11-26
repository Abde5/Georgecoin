package RelayNode;
import Server.*;
import Client.*;

public class RelayNode {

    private ServerCore server;

    public RelayNode(int portServer,int portClient) {

        server = new ServerCore(portServer);
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();


        Client client = new Client("localhost",portClient);
        Thread threadClient = new Thread(client);
        //threadClient.setDaemon(true);
        threadClient.start();
        client.sendMessage("master","RN vers Master");



        //threadServer.start();
        //threadClient.start();
        //threadClient.join();
        //System.out.println(client.sendTest());

        // new Thread(new ServerCore(port)).start();
        //new Thread(new Client(8081)).start();
        //ServerCore server = new ServerCore(port);

    }
}
