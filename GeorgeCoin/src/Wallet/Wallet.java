package Wallet;

import Client.Client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

public class Wallet {

    private Client client;
    private String blockChain;
    private String address;
    private String passPhrase;
    private byte[] hashPhrase;
    private PublicKey public_k;
    private static PrivateKey private_k;
    

    public Wallet(int port) throws Exception{
        //client = new Client("localhost",port);
        walletClient();
        
        //Thread thread = new Thread(client);
        //thread.setDaemon(true);
        //thread.start();
        
    }

	public void makeTransaction(){
        String jsonString = new JSONObject()
                .put("type", "newTransaction")
                .put("sourceWallet", "localhost:8080")
                .put("transaction","transaction ici en JSON").toString();
        System.out.println("Making a transaction : "+ jsonString);
        client.sendMessage("relay",jsonString);
    }

    public void requestBlockChain(){
        String jsonString = new JSONObject()
                .put("type", "GetBlockChain")
                .put("source", "localhost:8080").toString();
        blockChain=client.sendMessage("relay",jsonString);
        System.out.println(blockChain);
    }

    public void walletClient() throws Exception{
    	Boolean flag = false;
    	while (!flag){
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	System.out.print("Enter your sentence: ");
	    	String sentence = br.readLine();
	    	System.out.print("Enter your password: ");
	    	String password = br.readLine();
	    	
	    	passPhrase = password+sentence;
	    	hashPhrase = sha256digest16(passPhrase);
	    	flag = checkExistingUser();
    	}
    }
    
    public static byte[] sha256digest16(String list) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(list.getBytes("UTF-8"));

        // so you have 32 bytes here
        byte[]  b = digest.digest();

        // you can return it directly or you can cut it to 16 bytes
        return Arrays.copyOf(b, 16);
    }
    
	private void generateKeys() throws NoSuchAlgorithmException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        public_k = pair.getPublic();
        private_k = pair.getPrivate();
    }
	
    private Boolean checkExistingUser() throws Exception {
		String key_public_path = "key_public.txt";
		String key_private_path = "key_private.txt";
		File key_public_file = new File(key_public_path);
		File key_private_file = new File(key_private_path);
		if(key_public_file.exists() && key_private_file.exists() && !key_public_file.isDirectory() && !key_private_file.isDirectory()) {
		    System.out.println("true");
			return getKeysFromFile(key_public_path, key_private_path);
		}
		else{
			generateKeys();
			setKeysInFile(key_public_path, key_private_path);
			return true;
		}
	}
    
	private void setKeysInFile(String key_public_path, String key_private_path) throws Exception {
		byte[] encryptionKey = hashPhrase;
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        encryptionKey);
		byte[] plainText = private_k.getEncoded();
		String cle = new String(plainText, StandardCharsets.UTF_8);
	
		
		byte[] keyBytes = encodePrivate();
		
		Path myFile = Paths.get("",key_private_path);
		Files.write(myFile, keyBytes, StandardOpenOption.CREATE_NEW);
		
		Path myFile2 = Paths.get("",key_public_path);
		Files.write(myFile2, public_k.getEncoded(), StandardOpenOption.CREATE_NEW);

	}
	
	private byte[] encodePrivate() throws Exception {
		byte[] encryptionKey = hashPhrase;
		byte[] plainText = private_k.getEncoded();
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        encryptionKey);
		byte[] cipherText = advancedEncryptionStandard.encrypt(plainText);
		return cipherText;
	}
    
	private Boolean getKeysFromFile(String key_public_path, String key_private_path) throws Exception {
		byte[] encryptionKey = hashPhrase;
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        encryptionKey);
		Path myFile = Paths.get("",key_private_path);
		byte[] ciphertex = Files.readAllBytes(myFile);
		
		try{
			byte[] decrypted = advancedEncryptionStandard.decrypt(ciphertex);
		} catch (BadPaddingException name){
			return false;
		}
		return true;
	
	}
}









