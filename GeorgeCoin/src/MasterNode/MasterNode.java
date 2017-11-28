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
    private ArrayList<String> relayConnected;
    private ArrayList<Block> blockChain;

    public MasterNode(int portServer,int portClient) {
        this.portServer=portServer;
        this.portClient=portClient;
        transactionReceived = new ArrayList<String>();
        relayConnected=new ArrayList<String>();
        server = new ServerCore(this.portServer);
        client = new Client("localhost",this.portClient);
        blockChain = new ArrayList<Block>();
    }

    public void launchServer(){
        Thread threadServer = new Thread(server);
        //threadServer.setDaemon(true);
        threadServer.start();
    }

    public void launchClient(){
        System.out.println("DemarageClient");
        generateFirstBlock();
        Thread threadClient = new Thread(client);
        //threadClient.setDaemon(true);
        threadClient.start();
        //client.sendMessage("master","RN vers Master");
    }

    public void sendToRelay(String msg){
        System.out.println("Envoi au relay");
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
    
    public void generateFirstBlock(){
    	if(blockChain.size() > 0){
        	Block firstBlock = new Block("0", "currenthash", new Timestamp(System.currentTimeMillis()), 0);
        	blockChain.add(firstBlock);
        	System.out.println("first block in blockchain + " + blockChain.size());
    	}
    }

    public String acceptBlock(String block){
    	JSONObject jsonObj = new JSONObject(block);
    	Block newBlock = JSONtoBlock(jsonObj);
    	
    	//check previousHash
    	if (checkPreviousHash(newBlock)){
    		System.out.println("block accepted by master");
    		blockChain.add(newBlock);
    		return blockToJSON().toString();
    	}
    	System.out.println("block not accepted by master");
    	return "notAccepted";
    	
        //-----------------------------
        //Check a accepter et retrouner le blockchain
        //------------------------------
        /*blockChain = new JSONObject()
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
                        .put("Tx3","transact3")).toString();*/
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
}
