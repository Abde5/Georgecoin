package Miner;

import Client.Client;
import Server.ServerCore;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.util.Random;

public class Miner {
    private Client client;
    private ServerCore server;
    private String hostName;
    private int portServer;

    private static String relayHostname;
    private static int relayPort;
    private ArrayList<String> allRelay;
    
    private String match;
    private int current_nonce_size = 100;
    private int max_nonce_size = 100000000;
    private int valid_nonce;
    private int difficulty;
    private boolean found_match = false;
    
    /**
     * Constructor
     * @param hostnameServer
     * @param portServer
     */
    public Miner(String hostnameServer,int portServer) {
        hostName=hostnameServer;
        portServer = portServer;
        server = new ServerCore(hostName,portServer);
        allRelay= new ArrayList<String>(){{
            add("localhost:8080");
        }};
    }

    /**
     * Sends his own identity to a RelayNode
     * @return true if the RelayNode has not reached his limit amount of Miners connected, false otherwise
     */
    public Boolean sendWhoAMI(){
        String jsonString = new JSONObject()
                .put("type", "newMinerConnected")
                .put("source", "localhost:8082").toString();
        String resp=client.sendMessage("relay",jsonString);
        if (!resp.equals("NotPaired")) {
            String[] hostInfo = resp.split(":");
            relayHostname = hostInfo[0];
            relayPort = Integer.parseInt(hostInfo[1]);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Launches client in a thread
     * @param hostname
     * @param port
     */
    public void launchClient(String hostname,int port){
        client = new Client(hostname,port);
        Thread thread = new Thread(client);
        thread.start();
    }

    /**
     * Connects itself to a RelayNode
     */
    public void connectToRelay(){
        Boolean foundRelay=false;
        int i=0;
        while (!foundRelay){
            String[] hostInfo=allRelay.get(i).split(":");
            launchClient(hostInfo[0],Integer.parseInt(hostInfo[1]));
            foundRelay=sendWhoAMI();
            i++;
            i=i%getNumberOfRelay();
        }
        System.out.println("Connected to : "+getRelayHostname()+":"+getRelayPort());
    }

    /**
     * Launches server in a thread
     */
    public void launchServer(){
        Thread threadServer = new Thread(server);
        threadServer.start();
    }
    
    /**
     * found_match setter to true
     */
    private void flagFoundMatch(){
    	found_match = true;
    }
    
    /**
     * found_match setter to false
     */
    private void clearFoundMatch(){
    	found_match = false;
    }
    
    
    /**
     * value of found_match
     * @return found_match
     */
    public boolean foundMatch(){
    	return found_match == true;
    }
    
    /**
     * valid_nonce setter
     * @param valid
     */
    private void setValidNonce(int valid){
    	valid_nonce = valid;
    }
    
    /**
     * valid_nonce getter
     * @return int valid_nonce
     */
    private int getValidNonce(){
    	return valid_nonce;
    }

    /**
     * Gets a block (transaction parameters) and tries to mine it
     * @param transactions
     * @return String json -> the block mined
     */
    public String computeBlock(String transactions){
        JSONObject jsonObj = new JSONObject(transactions);
        String previousHash=jsonObj.get("previousHash").toString();
        setDifficulty(Integer.parseInt(jsonObj.get("difficulty").toString()));
        String Tx0=jsonObj.get("Tx0").toString();
        String Tx1=jsonObj.get("Tx1").toString();
        String Tx2=jsonObj.get("Tx2").toString();
        String Tx3=jsonObj.get("Tx3").toString();
        System.out.print("Starting computation");
        try {
			mineBlock(Tx0, Tx1, Tx2, Tx3, previousHash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        
        String block= new JSONObject()
                .put("type","Block")
                .put("sourceMiner",hostName+":"+portServer)
                .put("block",new JSONObject()
                    .put("previousHash",previousHash)
                    .put("hashBlock",getMatch())
                    .put("Tx0",Tx0)
                    .put("Tx1",Tx1)
                    .put("Tx2",Tx2)
                    .put("Tx3",Tx3)
                    .put("timestamp", (new Timestamp(System.currentTimeMillis())).toString())
                    .put("nonce",getValidNonce())).toString();
        return block;
    }

    /**
     * Sends a String block to the RelayNode
     * @param block
     */
    public void sendBlock(String block){
        client.sendMessage("relay",block);
    }

    /**
     * relayHostName getter
     * @return String relayHostname
     */
    public String getRelayHostname(){
        return relayHostname;
    }

    /**
     * relayPort getter
     * @return	int relayPort
     */
    public int getRelayPort(){
        return relayPort;
    }
    
    /**
     * Number of relay connected getter
     * @return int size of allRelay array
     */
    public int getNumberOfRelay() {
        return allRelay.size();
    }
    
    /**
     * match setter
     * @param new_match
     */
    private void setMatch(String new_match){
    	match = new_match;
    }
    
    /**
     * match getter
     * @return String match
     */
    public String getMatch(){
    	return match;
    }
    
    /**
     * max_nonce_size getter
     * @return int max_nonce_size
     */
    private int getMaxNonceSize(){
    	/*
    	 * private : don't want people to know max nonce
    	 */
    	return max_nonce_size;
    }
    
    /**
     * current_nonce_size getter
     * @return int current_nonce_size
     */
    private int getCurrentNonceSize() {
    	return current_nonce_size;
    }
    
    /**
     * current_nonce_size setter
     * @param new_nonce
     */
    private void setCurrentNonceSize(int new_nonce) {
    	current_nonce_size = new_nonce;
    }
    
    /**
     * Mines a block, given the transactions and the previous hash in it
     * @param trans1
     * @param trans2
     * @param trans3
     * @param trans4
     * @param previousHash
     * @throws NoSuchAlgorithmException
     */
    public void mineBlock(String trans1, String trans2, String trans3, String trans4, String previousHash) throws NoSuchAlgorithmException {
    	MessageDigest md = MessageDigest.getInstance("SHA-256");
    	
    	// hash transactions two by two
    	String hex1 = hashInformationPair(trans1, trans2, md);    	
    	String hex2 = hashInformationPair(trans3, trans4, md);
        
        // hash resulting hashs
        String total_trans_hex = hashInformationPair(hex1, hex2, md);
        
        // hash with previous hash
        String total_block_hex = hashInformationPair(total_trans_hex, previousHash, md);
        
        // hash with nonce to obtain POW approved hash
        Random nonce_generator = new Random();
        int nonce = nonce_generator.nextInt(getCurrentNonceSize());        
        boolean found_match = findMatch(nonce, total_block_hex, md);
        
        if(found_match){
        	System.out.println("found match : " + getMatch());
        	//
        	// send getMatch() to RN -> MN
        	//
        }
    }
    
    /**
     * Hashes info_1 and info_2, given a message digest
     * @param info_1
     * @param info_2
     * @param md
     * @return String value of hash
     */
    public String hashInformationPair(String info_1, String info_2, MessageDigest md){
    	byte[] hash = encryptInformationPair(info_1, info_2, md);
    	String hex = String.format( "%064x", new BigInteger( 1, hash) );
    	return hex;
    }

    /**
     * Mining difficulty setter
     * @param new_difficulty
     */
    public void setDifficulty(int new_difficulty){
        difficulty=new_difficulty;
        int max_nonce = 100;
        
        for(int i = 0; i < difficulty; i++){
        	if(max_nonce < getMaxNonceSize()){
        		max_nonce = max_nonce * max_nonce;
        	}
        }
        
        System.out.println(max_nonce);
                
        setCurrentNonceSize(max_nonce);
    }

    /**
     * Difficulty getter
     * @return int difficulty
     */
    public int getDifficulty(){
    	return difficulty;
    }
    
    /**
     * Transposes the difficulty into a string
     * @return String difficulty transposed
     */
    private String difficultyString(){
    	String diff_string = "";
    	for(int i=0; i< getDifficulty();i++){
    		diff_string += "0";
    	}
    	return diff_string;
    }
    
    /**
     * Finds the right nonce to get hash which corresponds to the difficulty
     * @param nonce
     * @param total_block_hex
     * @param md
     * @return Boolean value (found_match)
     */
    public boolean findMatch(int nonce, String total_block_hex, MessageDigest md){
    	
    	clearFoundMatch();
    	
    	int current = nonce;
        String difficulty = difficultyString();
        
        while(current <= getCurrentNonceSize() && !foundMatch()){
        	String curr_try = tryBlockValidation(total_block_hex, current, md);
        	if(curr_try.startsWith(difficulty)){
        		flagFoundMatch();
        		setMatch(curr_try);
        		setValidNonce(current);
        	}
        	current++;
        }
        current = 0;
        while(current < nonce && !foundMatch()){
        	String curr_try = tryBlockValidation(total_block_hex, current, md);
        	if(curr_try.startsWith(difficulty)){
        		flagFoundMatch();
        		setMatch(curr_try);
        		setValidNonce(current);
        	}
        	current++;
        }
        
    	return foundMatch();
    }
    
    /**
     * Concatenates the two given Strings trans1 & trans2 and encrypt them
     * @param trans1
     * @param trans2
     * @param md
     * @return encryption from the concatenation of the two Strings
     */
    public byte[] encryptInformationPair(String trans1, String trans2, MessageDigest md) {
    	String hash = trans1 + trans2;
    	
    	md.update(hash.getBytes(StandardCharsets.UTF_8));
    	byte[] digest = md.digest();
    	
    	return digest;
    }
    
    /**
     * Tries to make a valid block by mining it
     * @param block_hash
     * @param nonce
     * @param md
     * @return String value of digest
     */
    public String tryBlockValidation(String block_hash, int nonce, MessageDigest md) {
        String hash = block_hash + nonce;
        md.update(hash.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        return String.format("%064x", new BigInteger(1, digest));
    }

    
    /**
     * Forces the block computation to stop by setting found_match to true
     */
    public void stopComputingBlock(){
        found_match=true;
    }
}