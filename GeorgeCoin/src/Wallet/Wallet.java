package Wallet;

import Client.Client;
import org.json.JSONObject;

public class Wallet {

    private Client client;
    private String blockChain;

    public Wallet(int port){
        client = new Client("localhost",port);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();

    }

    public void makeTransaction(){
        String jsonString = new JSONObject()
                .put("type", "newTransaction")
                .put("source", "localhost:8080")
                .put("message","ca fonctionne").toString();
        client.sendMessage("relay",jsonString);
    }

    public void requestBlockChain(){
        String jsonString = new JSONObject()
                .put("type", "GetBlockChain")
                .put("source", "localhost:8080").toString();
        blockChain=client.sendMessage("relay",jsonString);
        System.out.println(blockChain);
    }
}

