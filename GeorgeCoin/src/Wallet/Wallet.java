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

	/**
	 * Constructor
	 * @param port
	 * @throws Exception
	 */
    public Wallet(int port) throws Exception{
        client = new Client("localhost",port);
        
        Thread thread = new Thread(client);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Asks, in command line, the credentials of the Wallet and, if they are right, asks the action the client want to make
     * @throws Exception
     */
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
    
    /**
     * Asks, in command line, what the client wants to do
     * @throws Exception
     */
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
    
    /**
     * Creates a new transaction to send to the RelayNode
     * @throws Exception
     */
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

	/**
	 * Creates the signatures by asking its information to the Wallet
	 * @param dsa_signature
	 * @return ArrayList<String> all transaction's information
	 * @throws IOException
	 */
    private ArrayList<String> askTransaction(String dsa_signature) throws IOException {
    	ArrayList<String> transaction = new ArrayList<String>();
    	Collections.addAll(transaction, "localhost:8080", address.toString(), askAmountTransac(),dsa_signature,askAddressDest());
		return transaction;
	}

    /**
     * Asks the destination address of the transaction to the client
     * @return String destination address
     * @throws IOException
     */
	private String askAddressDest() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Give the destination address for the transaction : ");
		String dest_address = br.readLine();
		return dest_address;
	}

	/**
	 *  Asks the amount of the transaction to the client
	 * @return String amount of the transaction
	 * @throws IOException
	 */
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

	/**
	 * Asks the current state of the block chain to the RelayNode and displays it.
	 */
	public void requestBlockChain(){
        String jsonString = new JSONObject()
                .put("type", "GetBlockChain")
                .put("source", "localhost:8080").toString();
        blockChain=client.sendMessage("/relay",jsonString);
        System.out.println(blockChain);
    }
	
	/**
	 * Asks for the block chain, to check to money this Wallet has
	 * @return int amount of money the Wallet has
	 */
	private int checkAmount(){
        requestBlockChain();
        int amount = getAmount();
        System.out.println(amount);
        return amount;
	}
	
	/**
	 * Checks the amount of money the Wallets has, in his copy of the block chain
	 * @return int amount of money the Wallet has
	 */
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
	
	/**
	 * Converts a JSONObject into a Block
	 * @param jsonObj
	 * @return the created Block
	 */
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
    
    /**
     * Converts a given String into a TimeStamp
     * @param time
     * @return	the created TimeStamp
     */
    public Timestamp stringToTimestamp(String time){
    	return Timestamp.valueOf(time);
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
     * Checks if the current user has already an account or is a new one. If he's a new user, it will generate the public and private keys, the address, and store them into their own file
     * @return Boolean value (true if everything went well)
     * @throws Exception
     */
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
    
    /**
     * Write the private key encoded, the public key and the address into their own files
     * @throws Exception
     */
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
	
	/**
	 * Encodes the private key
	 * @return byte[] private_k encoded
	 * @throws Exception
	 */
	private byte[] encodePrivateKey() throws Exception {
		byte[] plainText = private_k.getEncoded();
		return encrypt(plainText);
	}
    
	/**
	 * Gets the private key from the file, decodes it, stores it in private_k_bytes
	 * @return true if everything went well, otherwise false
	 * @throws Exception
	 */
	private Boolean getKeysFromFile() throws Exception {
		Path myFile = Paths.get("",key_private_path);
		byte[] ciphertext = Files.readAllBytes(myFile);
		return decodePrivate(ciphertext);
	}
	
	/**
	 * Decodes the given ciphertext and stores it in private_k_bytes
	 * @param ciphertext
	 * @return	true if everything went well, otherwise false
	 * @throws Exception
	 */
	private Boolean decodePrivate(byte[] ciphertext) throws Exception{
		try{
			private_k_byte = decrypt(ciphertext);
		} catch (BadPaddingException error){
			return false;
		}
		return true;
	}
	
	/**
	 * Encrypts the given plainText by using AES
	 * @param plainText
	 * @return byte[] the obtained cipherText
	 * @throws Exception
	 */
    public byte[] encrypt(byte[] plainText) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(hashPhrase, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }
    
    /**
     * Decrypts a cipherText by using AES
     * @param cipherText
     * @return byte[] the obtained plainText
     * @throws Exception
     */
    public byte[] decrypt(byte[] cipherText) throws Exception{
        SecretKeySpec secretKey = new SecretKeySpec(hashPhrase, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(cipherText);
    }
    
    /**
     * Retrieves the address of the Wallet from the address file
     * @return byte[] address from file
     * @throws IOException
     */
    private byte[] getAddressFromFile() throws IOException{
		Path myFile = Paths.get("",address_path);
		return Files.readAllBytes(myFile);
    }
}
