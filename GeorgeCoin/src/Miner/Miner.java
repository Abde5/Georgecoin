package Miner;

import Client.Client;
import Server.ServerCore;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.util.Random;

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
        
        try {
			encryptTransactions(Tx0, Tx1, Tx2, Tx3);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String block= new JSONObject()
                .put("type","Block")
                .put("source","localhost:8082")
                .put("block",new JSONObject()
                        .put("header","header")
                    .put("Nonce","ici nonce")
                    .put("Transaction","transaction")).toString();
        return block;
    }

    public void sendBlock(String msg){
        client.sendMessage("relay",msg);
    }
    
    public String encryptTransactions(String trans1, String trans2, String trans3, String trans4) throws NoSuchAlgorithmException {
    	// ---------------------------------------
        // COMPUTATION OF THE BLOCK HERE
        // ---------------------------------------
    	MessageDigest md = MessageDigest.getInstance("SHA-256");
    	
    	// hash transactions two by two
    	byte[] hash1 = encryptTransactionPair(trans1, trans2, md);
    	byte[] hash2 = encryptTransactionPair(trans3, trans4, md);
    	System.out.println(hash1 + " " + hash2);
    	
    	String hex1 = String.format( "%064x", new BigInteger( 1, hash1) );
    	String hex2 = String.format( "%064x", new BigInteger( 1, hash2) );
        System.out.println(hex1 + "\n" +  hex2);
        
        // hash resulting hashs
        byte[] total_transaction_hash = encryptTransactionPair(hex1, hex2, md);
        System.out.println("\n" + total_transaction_hash);
        String total_hex = String.format( "%064x", new BigInteger( 1, total_transaction_hash) );
        System.out.println("\n" +  total_hex);
        
        // hash with nonce to obtain POW approved hash
        int size = 100000000;
        Random nonce_generator = new Random();
        int nonce = nonce_generator.nextInt(size);
        System.out.println("nonce : " + nonce);
                
        int current = nonce;
        boolean found_match = false;
        String match = "none";
        
        while(current <= size && !found_match){
        	String curr_try = tryBlockValidation(total_hex, current, md);
        	if(curr_try.startsWith("000000")){
        		System.out.println("try : " + curr_try);
        		found_match = true;
        		match = curr_try;
        	}
        	current++;
        }
        current = 0;
        while(current < nonce && !found_match){
        	String curr_try = tryBlockValidation(total_hex, current, md);
        	if(curr_try.startsWith("000000")){
        		System.out.println("try : " + curr_try);
        		found_match = true;
        		match = curr_try;
        	}
        	current++;
        }
        
        if(found_match){
        	System.out.println("found match : " + match);
        }
                
        return "mined";
    }
    
    public byte[] encryptTransactionPair(String trans1, String trans2, MessageDigest md) {
    	String hash = trans1 + trans2;
    	
    	md.update(hash.getBytes(StandardCharsets.UTF_8));
    	byte[] digest = md.digest();
    	
    	return digest;
    }
    
    public String tryBlockValidation(String block_hash, int nonce, MessageDigest md) {
    	String hash = block_hash + nonce;
    	md.update(hash.getBytes(StandardCharsets.UTF_8));
    	byte[] digest = md.digest();
    	
    	return String.format("%064x", new BigInteger( 1, digest));
    }
}
