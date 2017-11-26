package Wallet;

import Client.Client;

public class Wallet {

    private Client client;


    public Wallet(int port){
        client = new Client("localhost",port);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();

    }

    public void makeTransaction(){
        client.sendMessage("relay","Wallet to RN");
    }
}
