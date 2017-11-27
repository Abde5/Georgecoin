package Miner;

import Client.Client;
import Server.ServerCore;

import java.sql.Timestamp;

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
        //wallet =new Wallet():


    }

    public void sendWhoAMI(){
        String jsonString = new JSONObject()
                .put("type", "newMinerConnected")
                .put("source", "localhost:8082").toString();
        client.sendMessage("relay",jsonString);
    }

    public void launchClient(){

        //client = new Client("localhost",portClient);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();
    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }

    public String computeBlock(String transactions){
        JSONObject jsonObj = new JSONObject(transactions);
        String Tx0=jsonObj.get("Tx0").toString();
        String Tx1=jsonObj.get("Tx1").toString();
        String Tx2=jsonObj.get("Tx2").toString();
        String Tx3=jsonObj.get("Tx3").toString();
        // ---------------------------------------
        // COMPUTATION OF THE BLOCK HERE
        // ---------------------------------------
        String block= new JSONObject()
                .put("type","Block")
                .put("source","localhost:8082")
                .put("block",new JSONObject()
                    .put("previousHash","0")
                    .put("hashBlock","hash block")
                    .put("timestamp", (new Timestamp(System.currentTimeMillis())).toString())
                    .put("nonce","1")).toString();
        return block;
    }

    public void sendBlock(String msg){
        client.sendMessage("relay",msg);
    }
}
