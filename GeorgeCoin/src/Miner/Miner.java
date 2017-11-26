package Miner;

import Client.Client;

public class Miner {
    private Client client;

    public Miner(int port){
        client = new Client("localhost",port);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();
        client.sendMessage("relay","Miner to RN");
    }
}
