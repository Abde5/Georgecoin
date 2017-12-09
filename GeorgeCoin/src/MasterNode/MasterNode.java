package MasterNode;

import Server.ServerCore;
import Client.Client;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MasterNode {

    private Client client;
    private ServerCore server;
    private String hostName;
    private int portServer;
    private int portClient;
    private ArrayList<String> transactionReceived;
    private ArrayList<String> relaysConnected;
    private static ArrayList<Block> blockChain;
    private String previousHash="0";
    private int difficulty = 7;

    public MasterNode(String hostnameServ,int portServer,int portClient) {
        this.hostName=hostnameServ;
        this.portServer=portServer;
        this.portClient=portClient;
        transactionReceived = new ArrayList<String>();
        relaysConnected=new ArrayList<String>();
        server = new ServerCore(this.hostName,this.portServer);


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
                        .put("difficulty",difficulty)
                        .put("previousHash",previousHash)
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
    		//tests pour le montant
            String jsonTransaction = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "")
                    	.put("amount", "50")
                    	.put("signature", "")
                    	.put("destinataire","address")).toString();
            String jsonTransaction1 = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "address")
                    	.put("amount", "20")
                    	.put("signature", "")
                    	.put("destinataire","address dest")).toString();
            String jsonTransaction2 = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "address")
                    	.put("amount", "10")
                    	.put("signature", "")
                    	.put("destinataire","address dest")).toString();
            String jsonTransaction3 = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "")
                    	.put("amount", "65")
                    	.put("signature", "")
                    	.put("destinataire","address")).toString();
            
        	Block firstBlock = new Block(previousHash, "currenthash", new Timestamp(System.currentTimeMillis()), 0, 
        			jsonTransaction, 
        			jsonTransaction1, 
        			jsonTransaction2, 
        			jsonTransaction3);
        	blockChain.add(firstBlock);
        	System.out.println("first block of the BLOCKCHAIN generated.");
    	}
    }

    public String acceptBlock(String block){
    	JSONObject jsonObj = new JSONObject(block);
    	Block newBlock = JSONtoBlock(jsonObj);
    	if (checkPreviousHash(newBlock)){
    		System.out.println("BLOCK accepted by master");
    		previousHash=newBlock.getHashBlock();
    		blockChain.add(newBlock);
    		return blockToJSON().toString();
    	}
    	System.out.println("BLOCK denied by master");
    	return "notAccepted";
    }
    
    public Boolean checkPreviousHash(Block block){
    	if(block.getPreviousHash().equals(blockChain.get(blockChain.size()-1).getPreviousHash())){
    		return true;
    	}
    	return false;
    }
    
    public JSONObject blockToJSON(){
    	JSONObject json = new JSONObject();
    	json.put("type", "BlockChain");
    	for(int i=0; i<blockChain.size(); i++){
    		json.put(Integer.toString(i), new JSONObject()
    				.put("previousHash", blockChain.get(i).getPreviousHash())
    				.put("blockHash", blockChain.get(i).getHashBlock())
    				.put("timestamp", blockChain.get(i).getTimestamp())
    				.put("nonce", blockChain.get(i).getNonce())
    				.put("TxO", blockChain.get(i).getTx0())
    				.put("Tx1", blockChain.get(i).getTx1())
    				.put("Tx2", blockChain.get(i).getTx2())
    				.put("Tx3", blockChain.get(i).getTx3()));
    	}
    	return json;
    }
    
    public Block JSONtoBlock(JSONObject jsonObj){
    	Block block = new Block(jsonObj.getJSONObject("block").getString("previousHash"), 
    							jsonObj.getJSONObject("block").getString("hashBlock"),
    							stringToTimestamp(jsonObj.getJSONObject("block").getString("timestamp")),
    							Integer.parseInt(jsonObj.getJSONObject("block").getString("nonce")),
                                jsonObj.getJSONObject("block").getString("Tx0"),
                                jsonObj.getJSONObject("block").getString("Tx1"),
                                jsonObj.getJSONObject("block").getString("Tx2"),
                                jsonObj.getJSONObject("block").getString("Tx3"));
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
    
    public Boolean checkEnoughMoney(JSONObject transaction){
        String address = transaction.getString("address");
        int amount = Integer.parseInt(transaction.getString("amount"));
        Block block;
        String Tx0;
        String Tx1;
        String Tx2;
        String Tx3;
       
        int currentAmount = 0;
        for(int i=0; i<blockChain.size(); i++){
            block = blockChain.get(i);
            Tx0 = block.getTx0();
            Tx1 = block.getTx1();
            Tx2 = block.getTx2();
            Tx3 = block.getTx3();
            
            currentAmount += checkTransaction(address, Tx0);
            currentAmount += checkTransaction(address, Tx1);
            currentAmount += checkTransaction(address, Tx2);
            currentAmount += checkTransaction(address, Tx3);
        }
        if(amount <= currentAmount){
        	return true;
        }
    	return false;
    }
    
    private int checkTransaction(String address, String transaction){
    	JSONObject jsonTransaction = new JSONObject(transaction);
        int amountReceived = 0;
        int amountSent = 0;
        
        if(jsonTransaction.getJSONObject("transaction").getString("destinataire").equals(address)){
        	amountReceived += Integer.parseInt(jsonTransaction.getJSONObject("transaction").getString("amount"));
        }
        if(jsonTransaction.getJSONObject("transaction").getString("address").equals(address)){
        	amountSent += Integer.parseInt(jsonTransaction.getJSONObject("transaction").getString("amount"));
        }
        return amountReceived - amountSent;
    }

    public void increaseDifficulty(){
        this.difficulty++;
    }

    public void decreaseDifficulty(){
        if(this.difficulty > 0){
            this.difficulty--;
        }
    }
}
