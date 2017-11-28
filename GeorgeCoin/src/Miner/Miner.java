package Miner;

import Client.Client;
import Server.ServerCore;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONObject;

public class Miner {
    private Client client;
    private ServerCore server;
    private String hostName;
    private int portServer;

    private static String relayHostname;
    private static int relayPort;
    private ArrayList<String> allRelay;

    public Miner(String hostnameServer,int portServer) {
        this.hostName=hostnameServer;
        this.portServer = portServer;
        server = new ServerCore(this.hostName,this.portServer);
        //client = new Client("localhost", this.portClient);
        //wallet =new Wallet():
        allRelay= new ArrayList<String>(){{
            add("localhost:8080");
            //ajouter les autre relay
        }};

    }

    public Boolean sendWhoAMI(){
        String jsonString = new JSONObject()
                .put("type", "newMinerConnected")
                .put("source", "localhost:8082").toString();
        String resp=client.sendMessage("relay",jsonString);
        if (!resp.equals("NotPaired")) {
            String[] hostInfo = resp.split(":");
            this.relayHostname = hostInfo[0];
            this.relayPort = Integer.parseInt(hostInfo[1]);
            return true;
        }
        else{
            return false;
        }
    }

    public void launchClient(String hostname,int port){
        client = new Client(hostname,port);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();
    }

    public void connectToRelay(){
        Boolean foundRelay=false;
        int i=0;
        while (!foundRelay){
            String[] hostInfo=allRelay.get(i).split(":");
            launchClient(hostInfo[0],Integer.parseInt(hostInfo[1]));
            foundRelay=sendWhoAMI();
            i++;
            i=i%getNumerOfRelay();
        }
        System.out.println("Connected to : "+this.getRelayHostname()+":"+this.getRelayPort());
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
                .put("sourceMiner","localhost:8082")
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

    public String getRelayHostname(){
        return relayHostname;
    }

    public int getRelayPort(){
        return this.relayPort;
    }
    public int getNumerOfRelay(){
        return allRelay.size();
    }
}
