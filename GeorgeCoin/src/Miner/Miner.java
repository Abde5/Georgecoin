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
    private int difficulty;
    private static boolean found_match = false;

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
    
    private void flagFoundMatch(){
    	this.found_match = true;
    }
    
    private void clearFoundMatch(){
    	this.found_match = false;
    }
    
    public boolean foundMatch(){
    	return this.found_match == true;
    }

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String block= new JSONObject()
                .put("type","Block")
                .put("sourceMiner",this.hostName+":"+this.portServer)
                .put("block",new JSONObject()
                    .put("previousHash","0") //TODO a changer
                    .put("hashBlock","hash block") //TODO a changer
                    .put("Tx0",Tx0)
                    .put("Tx1",Tx1)
                    .put("Tx2",Tx2)
                    .put("Tx3",Tx3)
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
    public int getNumerOfRelay() {
        return allRelay.size();
    }
    
    private void setMatch(String new_match){
    	this.match = new_match;
    }
    
    public String getMatch(){
    	return this.match;
    }
    
    private int getMaxNonceSize(){
    	/*
    	 * private : don't want people to know max nonce
    	 */
    	return this.max_nonce_size;
    }
    
    private int getCurrentNonceSize() {
    	return this.current_nonce_size;
    }
    
    private void setCurrentNonceSize(int new_nonce) {
    	this.current_nonce_size = new_nonce;
    }
    
    
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
    
    public String hashInformationPair(String info_1, String info_2, MessageDigest md){
    	byte[] hash = encryptInformationPair(info_1, info_2, md);
    	String hex = String.format( "%064x", new BigInteger( 1, hash) );
    	return hex;
    }

    public void setDifficulty(int difficulty){
        this.difficulty=difficulty;
        int max_nonce = 100;
        
        for(int i = 0; i < difficulty; i++){
        	if(max_nonce < getMaxNonceSize()){
        		max_nonce = max_nonce * max_nonce;
        	}
        }
        
        System.out.println(max_nonce);
                
        this.setCurrentNonceSize(max_nonce);
    }

    private int getDifficulty(){
    	return this.difficulty;
    }
    
    private String difficultyString(){
    	String diff_string = "";
    	for(int i=0; i< getDifficulty();i++){
    		diff_string += "0";
    	}
    	return diff_string;
    }
    
    public boolean findMatch(int nonce, String total_block_hex, MessageDigest md){
    	
    	this.clearFoundMatch();
        //TODO George a mettre ca en attribut -> et faire un set de Found et du coup la boucle s'arrete
    	
    	int current = nonce;
        String difficulty = difficultyString();
        
        while(current <= getCurrentNonceSize() && !this.foundMatch()){
        	String curr_try = tryBlockValidation(total_block_hex, current, md);
        	if(curr_try.startsWith(difficulty)){
        		this.flagFoundMatch();
        		this.setMatch(curr_try);
        	}
        	current++;
        }
        current = 0;
        while(current < nonce && !this.foundMatch()){
        	String curr_try = tryBlockValidation(total_block_hex, current, md);
        	if(curr_try.startsWith(difficulty)){
        		this.flagFoundMatch();
        		this.setMatch(curr_try);
        	}
        	current++;
        }
        
    	return this.foundMatch();
    }
    
    public byte[] encryptInformationPair(String trans1, String trans2, MessageDigest md) {
    	String hash = trans1 + trans2;
    	
    	md.update(hash.getBytes(StandardCharsets.UTF_8));
    	byte[] digest = md.digest();
    	
    	return digest;
    }
    
    public String tryBlockValidation(String block_hash, int nonce, MessageDigest md) {
        String hash = block_hash + nonce;
        md.update(hash.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        return String.format("%064x", new BigInteger(1, digest));
    }

    public void stopComputingBlock(){
        this.found_match=true;
    }
}