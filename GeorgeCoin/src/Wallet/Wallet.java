package Wallet;

import Client.Client;
import MasterNode.Block;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Signature;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONObject;

public class Wallet extends WalletMaster{
    private Client client;
    private String blockChain;
 
	private String key_public_path = "key_public.txt";
	private String key_private_path = "key_private.txt";
	private String address_path = "address.txt";

    public Wallet(int port) throws Exception{
        client = new Client("localhost",port);
        
        Thread thread = new Thread(client);
        thread.setDaemon(true);
        thread.start();
    }

    public void walletClient() throws Exception{
    	Boolean flag = false;
    	while (!flag){
    		System.out.println("Please enter your passPhrase and password: ");
	    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	System.out.print("Enter your passPhrase: ");
	    	String sentence = br.readLine();
	    	System.out.print("Enter your password: ");
	    	String password = br.readLine();
	    	
	    	passPhrase = password+sentence;
	    	hashPhrase = sha256digest16(passPhrase);
	    	flag = checkExistingUser();
    	}
    	askAction();
    }
    
    private void askAction() throws Exception{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Make transaction (m) or Copy Blockchain (b) or Check amount (c): ");
    	String action = br.readLine();
    	while(!action.equals("b") && !action.equals("m") && !action.equals("c")){
    		System.out.print("Make transaction (m) or Copy Blockchain (b) or Check amount (c): ");
    		action = br.readLine();
    	}
    	if (action.equals("b")){
    		requestBlockChain();
    	}
    	else if (action.equals("c")){
    		checkAmount();
    	}
    	else{
    		makeTransaction();
    	}
    }
    
	public void makeTransaction() throws Exception{
		Signature dsa = DSASign();
		ArrayList<String> transac_input = askTransaction(dsa.sign().toString());
        String jsonString = new JSONObject()
                .put("type", "newTransaction")
				.put("transaction", new JSONObject()
                	.put("sourceWallet", transac_input.get(0))
                	.put("address", transac_input.get(1)) // à remplacer par address.toString()
                	.put("amount", transac_input.get(2))
                	.put("signature", transac_input.get(3))
                	.put("destinataire",transac_input.get(4))).toString();
        System.out.println("Making a transaction : "+ jsonString);
        client.sendMessage("/relay",jsonString);
    }

    private ArrayList<String> askTransaction(String dsa_signature) throws IOException {
    	ArrayList<String> transaction = new ArrayList<String>();
    	Collections.addAll(transaction, "localhost:8080", address.toString(), askAmountTransac(),dsa_signature,askAddressDest());
		return transaction;
	}

	private String askAddressDest() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Give the destination address for the transaction : ");
		String dest_address = br.readLine();
		return dest_address;
	}

	private String askAmountTransac() throws IOException {
		boolean validAmount = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Amount for the transaction : ");
		String amount = br.readLine();
		while (!validAmount){
	    	if (amount.matches("[1-9][0-9]*")){	//Amount given is a number > 0
	    		validAmount = true;
	    	}
	    	else{
	    		System.out.println("The amount for the transaction must be a number > 0");
	    		System.out.print("Give a valid amount for the transaction : ");
	    		amount = br.readLine();
	    	}	
		}
		if (checkAmount() < Integer.parseInt(amount)){
			System.out.print("You don't have enough money to make this transaction");
			System.exit(0);
		}
		return amount;
	}

	public void requestBlockChain(){
        String jsonString = new JSONObject()
                .put("type", "GetBlockChain")
                .put("source", "localhost:8080").toString();
        blockChain=client.sendMessage("/relay",jsonString);
        System.out.println(blockChain);
    }
	
	private int checkAmount(){
        requestBlockChain();
        int amount = getAmount();
        System.out.println(amount);
        return amount;
	}
	
	public int getAmount(){
		Block block;
        String Tx0;
        String Tx1;
        String Tx2;
        String Tx3;
		int amount = 0;
        
        JSONObject blockchainJSON = new JSONObject(blockChain);
		Iterator keys = blockchainJSON.keys();
		while(keys.hasNext()) {
			String key = keys.next().toString();
			if (!key.equals("type")){
				JSONObject json = new JSONObject(blockchainJSON.get(key).toString());
				block=JSONtoBlock(json);
				Tx0 = block.getTx0();
				Tx1 = block.getTx1();
				Tx2 = block.getTx2();
				Tx3 = block.getTx3();

				amount += checkTransaction(address.toString(), Tx0);
				amount += checkTransaction(address.toString(), Tx1);
				amount += checkTransaction(address.toString(), Tx2);
				amount += checkTransaction(address.toString(), Tx3);
			}

		}
    	return amount;
    }
	
    public Block JSONtoBlock(JSONObject jsonObj){
    	Block block = new Block(jsonObj.getString("previousHash"),
    							jsonObj.getString("hashBlock"),
    							stringToTimestamp(jsonObj.getString("timestamp")),
    							jsonObj.getInt("nonce"),
                                jsonObj.getString("Tx0"),
                                jsonObj.getString("Tx1"),
                                jsonObj.getString("Tx2"),
                                jsonObj.getString("Tx3"));
    	return block;
    }
    
    public Timestamp stringToTimestamp(String time){
    	return Timestamp.valueOf(time);
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

    private Boolean checkExistingUser() throws Exception {
		File key_public_file = new File(key_public_path);
		File key_private_file = new File(key_private_path);
		File address_file = new File(address_path);
		
		if(key_public_file.exists() && key_private_file.exists() && address_file.exists()
				&& !key_public_file.isDirectory() && !key_private_file.isDirectory() && !address_file.isDirectory()){
			address = getAddressFromFile();
			return getKeysFromFile();
		}
		else{
			generateKeys();
			setKeysInFile();
			return true;
		}
	}
    
	private void setKeysInFile() throws Exception {
		byte[] privKeyBytes = encodePrivateKey();
		Path privFile = Paths.get("",key_private_path);
		Files.write(privFile, privKeyBytes, StandardOpenOption.CREATE_NEW);
		
		byte[] pubKeyBytes = public_k.getEncoded();
		Path pubFile = Paths.get("",key_public_path);
		Files.write(pubFile, pubKeyBytes, StandardOpenOption.CREATE_NEW);
		
		address = Ripemd160.getHash(pubKeyBytes);
		Path adress = Paths.get("",address_path);
		Files.write(adress, address, StandardOpenOption.CREATE_NEW);
	}
	
	private byte[] encodePrivateKey() throws Exception {
		byte[] plainText = private_k.getEncoded();
		return encrypt(plainText);
	}
    
	private Boolean getKeysFromFile() throws Exception {
		Path myFile = Paths.get("",key_private_path);
		byte[] ciphertext = Files.readAllBytes(myFile);
		return decodePrivate(ciphertext);
	}
	
	private Boolean decodePrivate(byte[] ciphertext) throws Exception{
		try{
			private_k_byte = decrypt(ciphertext);
		} catch (BadPaddingException error){
			return false;
		}
		return true;
	}
	
    public byte[] encrypt(byte[] plainText) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(hashPhrase, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }
    
    public byte[] decrypt(byte[] cipherText) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(hashPhrase, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cipherText);
    }
    
    private byte[] getAddressFromFile() throws IOException{
		Path myFile = Paths.get("",address_path);
		return Files.readAllBytes(myFile);
    }
}
