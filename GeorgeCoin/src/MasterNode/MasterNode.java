package MasterNode;

import Server.ServerCore;
import Client.Client;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MasterNode {

    private Client client;
    private ServerCore server;
    private int portServer;
    private int portClient;
    private ArrayList<String> transactionReceived;
    private ArrayList<String> relaysConnected;
    private static ArrayList<Block> blockChain;

    public MasterNode(int portServer,int portClient) {
        this.portServer=portServer;
        this.portClient=portClient;
        transactionReceived = new ArrayList<String>();
        relaysConnected=new ArrayList<String>();
        server = new ServerCore("localhost",this.portServer);


    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }

    public void launchClient(String hostname,int port){
        client = new Client(hostname,port);
        Thread threadClient = new Thread(client);
        //threadClient.setDaemon(true);
        threadClient.start();
        //client.sendMessage("master","RN vers Master");
    }

    public String sendToRelay(String msg){
        String resp=client.sendMessage("relay",msg);
        return resp;
    }
    public void sendToALLRelays(String msg){
        for (int i=0;i<getNumberOfRelays();i++){
            String[] hostInfo=relaysConnected.get(i).split(":");
            launchClient(hostInfo[0],Integer.parseInt(hostInfo[1]));
            sendToRelay(msg);
        }

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
    
    public void generateFirstBlock(){
        blockChain = new ArrayList<Block>();
    	if(blockChain.size() == 0){
        	Block firstBlock = new Block("0", "currenthash", new Timestamp(System.currentTimeMillis()), 0);
        	blockChain.add(firstBlock);
        	System.out.println("first block in blockchain + " + blockChain.size());
    	}
    }

    public String acceptBlock(String block){
    	JSONObject jsonObj = new JSONObject(block);
    	Block newBlock = JSONtoBlock(jsonObj);
    	if (checkPreviousHash(newBlock)){
    		System.out.println("BLOCK accepted by master");
    		blockChain.add(newBlock);
    		return blockToJSON().toString();
    	}
    	System.out.println("BLOCK denied by master");
    	return "notAccepted";
    }
    
    public Boolean checkPreviousHash(Block block){
    	if(block.previousHash.equals(blockChain.get(blockChain.size()-1).previousHash)){
    		return true;
    	}
    	return false;
    }
    
    public JSONObject blockToJSON(){
    	JSONObject json = new JSONObject();
    	json.put("type", "BlockChain");
    	for(int i=0; i<blockChain.size(); i++){
    		json.put(Integer.toString(i), new JSONObject()
    				.put("previousHash", blockChain.get(i).previousHash)
    				.put("blockHash", blockChain.get(i).hashBlock)
    				.put("timestamp", blockChain.get(i).timestamp)
    				.put("nonce", blockChain.get(i).nonce));
    	}
    	return json;
    }
    
    public Block JSONtoBlock(JSONObject jsonObj){
    	Block block = new Block(jsonObj.getJSONObject("block").getString("previousHash"), 
    							jsonObj.getJSONObject("block").getString("hashBlock"),
    							stringToTimestamp(jsonObj.getJSONObject("block").getString("timestamp")),
    							Integer.parseInt(jsonObj.getJSONObject("block").getString("nonce")));
    	return block;
    }
    
    public Timestamp stringToTimestamp(String time){
    	return Timestamp.valueOf(time);
    }

    public void addRelay(String source){
        if (!relaysConnected.contains(source)){
            relaysConnected.add(source);
        }
    }

    public int getNumberOfRelays(){
        //System.out.print(transactionReceived.get(0));
        return relaysConnected.size();
    }
}
