package Miner;

import Client.Client;
import Server.ServerCore;
import org.json.JSONObject;

public class Miner {
    private Client client;
    private ServerCore server;
    private int portServer;
    private int portClient;

    public Miner(int portServer,int portClient) {
        this.portServer = portServer;
        this.portClient = portClient;
        server = new ServerCore(portServer);
        client = new Client("localhost", portClient);


    }

    public void launchClient(){

        //client = new Client("localhost",portClient);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();
        String jsonString = new JSONObject()
                .put("type", "Miner")
                .put("Source", "localhost:8081")
                .put("message","ca fonctionne Miner").toString();
        client.sendMessage("relay",jsonString);
    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }
}
