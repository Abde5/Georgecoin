package Wallet;

import Client.Client;
import org.json.JSONObject;

public class Wallet {

    private Client client;


    public Wallet(int port){
        client = new Client("localhost",port);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();

    }

    public void makeTransaction(){
        String jsonString = new JSONObject()
                .put("type", "Wallet")
                .put("Source", "localhost:8080")
                .put("message","ca fonctionne").toString();
        client.sendMessage("relay",jsonString);
    }
}
