package MasterNode;

import Server.ServerCore;
import Wallet.WalletMaster;
import Client.Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;

public class MasterNode {

	private static String amount = "2";
    private Client client;
    private ServerCore server;
    private String hostName;
    private int portServer;
    private Queue<String> transactionReceived = new LinkedList<>();
    private ArrayList<String> relaysConnected = new ArrayList<>();
    private static ArrayList<Block> blockChain;
    private String previousHash="0";
    private int difficulty = 4;
    private WalletMaster georgeMillion = new WalletMaster();

    /**
     * MasterNode Constructor
     * @param hostnameServ
     * @param portServer	port to listen from RelayNode
     * @param portClient	port to communicate to RelayNode
     */
    public MasterNode(String hostnameServ,int portServer,int portClient) {
        hostName=hostnameServ;
        portServer=portServer;
        server = new ServerCore(hostName,portServer);
    }

    /**
     * Launches server in a thread
     */
    public void launchServer(){
        Thread threadServer = new Thread(server);
        threadServer.start();
    }

    /**
     * Launches client in a thread
     * @param hostname
     * @param port
     */
    public void launchClient(String hostname,int port){
        client = new Client(hostname,port);
        Thread threadClient = new Thread(client);
        threadClient.start();
    }

    /**
     * Sends message to RelayNode
     * @param msg
     * @return String response from RelayNode
     */
    public String sendToRelay(String msg){
        String resp=client.sendMessage("relay",msg);
        return resp;
    }
    
    /**
     * Sends information to all connected RelayNodes
     * @param msg
     */
    public void sendToALLRelays(String msg){
        for (int i=0;i<getNumberOfRelays();i++){
            String[] hostInfo=relaysConnected.get(i).split(":");
            launchClient(hostInfo[0],Integer.parseInt(hostInfo[1]));
            sendToRelay(msg);
        }

    }

    /**
     * Adds transaction in transactions list
     * @param message
     */
    public void addTransaction(String message){
        transactionReceived.add(message);
    }

    /**
     * Number of transactions getter
     * @return int size of transactions list
     */
    public int getNumberOfTransaction(){
        return transactionReceived.size();
    }
    
    /**
     * Gets 4 transactions for Miners
     * @return String json containing transactions
     */
    public String getTransactionsForMining(){
        String jsonString = new JSONObject()
                .put("type", "readyForMining")
                .put("alltransactions",new JSONObject()
                        .put("difficulty",difficulty)
                        .put("previousHash",previousHash)
                        .put("Tx0",transactionReceived.poll())
                        .put("Tx1",transactionReceived.poll())
                        .put("Tx2",transactionReceived.poll())
                        .put("Tx3",transactionReceived.poll())).toString();
        return jsonString;
    }
    
    /**
     * Creates a transaction to reward a miner & adds it in transactions list
     * @param destAddress
     * @throws JSONException
     * @throws Exception
     */
    public void rewardTransaction(String destAddress) throws JSONException, Exception{
        String jsonTransaction = new JSONObject()
				.put("transaction", new JSONObject()
                	.put("sourceWallet", "localhost")
                	.put("address", getGMAddress())
                	.put("amount", amount)
                	.put("signature", getGMSignature())
                	.put("destinataire",destAddress)).toString();
       	addTransaction(jsonTransaction);
    }
    
    /**
     * MasterNode signature (string form) getter
     * @return String signature of the MasterNode
     * @throws Exception
     */
    public String getGMSignature() throws Exception {
		return georgeMillion.DSASign().toString();
	}

    /**
     * MasterNode address (string form) getter
     * @return String address of the MasterNode
     * @throws Exception
     */
	private String getGMAddress() {
		return georgeMillion.getAddress().toString();
	}

	/**
	 * Generates first block when block chain is empty
	 */
	public void generateFirstBlock(){
        blockChain = new ArrayList<Block>();
    	if(blockChain.size() == 0){
            String jsonTransaction = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "")
                    	.put("amount", amount)
                    	.put("signature", "")
                    	.put("destinataire","address")).toString();
            String jsonTransaction1 = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "address")
                    	.put("amount", amount)
                    	.put("signature", "")
                    	.put("destinataire","address dest")).toString();
            String jsonTransaction2 = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "address")
                    	.put("amount", amount)
                    	.put("signature", "")
                    	.put("destinataire","address dest")).toString();
            String jsonTransaction3 = new JSONObject()
    				.put("transaction", new JSONObject()
                    	.put("sourceWallet", "localhost")
                    	.put("address", "")
                    	.put("amount", amount)
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

	/**
	 * Method accepting a block iff the block matches the current state of the block chain
	 * @param block
	 * @return String json of the block if accepted or "notAccepted" if not
	 */
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
    
    /**
     * Checks if the block matches the current states of the block chain
     * @param block
     * @return Boolean value
     */
    public Boolean checkPreviousHash(Block block){
    	if(block.getPreviousHash().equals(blockChain.get(blockChain.size()-1).getPreviousHash())){
    		return true;
    	}
    	return false;
    }
    
    /**
     * Creates a String json containing all the block chain
     * @return	String json of the whole block chain
     */
    public JSONObject blockToJSON(){
    	JSONObject json = new JSONObject();
    	json.put("type", "BlockChain");
    	for(int i=0; i<blockChain.size(); i++){
    		json.put(Integer.toString(i), new JSONObject()
    				.put("previousHash", blockChain.get(i).getPreviousHash())
    				.put("hashBlock", blockChain.get(i).getHashBlock())
    				.put("timestamp", blockChain.get(i).getTimestamp())
    				.put("nonce", blockChain.get(i).getNonce())
    				.put("Tx0", blockChain.get(i).getTx0())
    				.put("Tx1", blockChain.get(i).getTx1())
    				.put("Tx2", blockChain.get(i).getTx2())
    				.put("Tx3", blockChain.get(i).getTx3()));
    	}
    	return json;
    }
    
    /**
     * Converts a json form block into a Block object
     * @param jsonObj
     * @return created Block object
     */
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
    
    /**
     * Converts a given String into a TimeStamp object
     * @param time
     * @return TimeStamp object
     */
    public Timestamp stringToTimestamp(String time){
    	return Timestamp.valueOf(time);
    }

    /**
     * Adds RelayNode in the connected relays' list
     * @param source String json form of the a RelayNodeHostname:RelayNodePort
     */
    public void addRelay(String source){
        if (!relaysConnected.contains(source)){
            relaysConnected.add(source);
        }
    }

    /**
     * Number of connected Relay Nodes getter
     * @return size of connected relays' list
     */
    public int getNumberOfRelays(){
        return relaysConnected.size();
    }
    
    /**
     * Checks if the address of a given transaction has enough money to actually make this transaction
     * @param transaction
     * @return Boolean value
     */
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
    
    /**
     * Checks the amount of a given address in a given transaction if this transaction concerns that address
     * @param address
     * @param transaction
     * @return amount of a given address in a given transaction if this transaction concerns that address, otherwise 0
     */
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

    /**
     * Increases the difficulty to mine a block
     */
    public void increaseDifficulty(){
        difficulty++;
    }

    /**
     * Decreases the difficulty to mine a block
     */
    public void decreaseDifficulty(){
        if(difficulty > 0){
            difficulty--;
        }
    }
    
    /**
     * Checks the difficulty of mining a block, decreasing it or increasing it depending on the time taken for a miner to mine it.
     * @param totalTime corresponds to the time since miners started mining a new block
     */
    public void checkDifficulty(long totalTime){
		Integer min = 30000;
		long minValue = min.longValue();
		Integer max = 180000;
		long maxValue = max.longValue();
		if(totalTime < minValue){
			increaseDifficulty();
		}
		else if (totalTime > maxValue){
			decreaseDifficulty();	
		}
    }
}
