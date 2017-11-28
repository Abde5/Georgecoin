package MasterNode;

import Server.ServerCore;
import Client.Client;
import org.json.JSONObject;

import java.util.ArrayList;

public class MasterNode {

    private Client client;
    private ServerCore server;
    private int portServer;
    private int portClient;
    private ArrayList<String> transactionReceived;
    private ArrayList<String> relayConnected;

    public MasterNode(int portServer,int portClient) {
        this.portServer=portServer;
        this.portClient=portClient;
        transactionReceived = new ArrayList<String>();
        relayConnected=new ArrayList<String>();
        server = new ServerCore(this.portServer);
        client = new Client("localhost",this.portClient);
    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }

    public void launchClient(){
        System.out.print("DemarageClient");
        Thread threadClient = new Thread(client);
        //threadClient.setDaemon(true);
        threadClient.start();
        //client.sendMessage("master","RN vers Master");
    }

    public void sendToRelay(String msg){
        System.out.println("OEnvoi au relay");
        client.sendMessage("relay",msg);
    }

    public void addTransaction(String message){
        //System.out.print(transactionReceived.size());
        transactionReceived.add(message);
        //System.out.print(transactionReceived.size());
    }

    public int getNumberOfTransaction(){
        //System.out.print(transactionReceived.get(0));
        return transactionReceived.size();
    }

    public String getTransactionsForMining(){
        String jsonString = new JSONObject()
                .put("type", "readyForMining")
                .put("alltransactions",new JSONObject()
                        .put("Tx0",transactionReceived.get(0))
                        .put("Tx1",transactionReceived.get(1))
                        .put("Tx2",transactionReceived.get(2))
                        .put("Tx3",transactionReceived.get(3))).toString();
        transactionReceived.clear();
        return jsonString;
    }

    public String acceptBlock(String block){

        //-----------------------------
        //Check a accepter et retrouner le blockchain
        //------------------------------
        String blockChain = new JSONObject()
                .put("type", "BlockChain")
                .put("BLOCK1",new JSONObject()
                        .put("Tx0","transact0")
                        .put("Tx1","transact1")
                        .put("Tx2","transact2")
                        .put("Tx3","transact3"))
                .put("BLOCK2",new JSONObject()
                        .put("Tx0","transact0")
                        .put("Tx1","transact1")
                        .put("Tx2","transact2")
                        .put("Tx3","transact3")).toString();

        return blockChain;
    }
}
