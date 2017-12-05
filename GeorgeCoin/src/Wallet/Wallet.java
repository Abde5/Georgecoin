package Wallet;

import Client.Client;
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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import org.json.JSONObject;

public class Wallet {
    private Client client;
    private String blockChain;
    
    private String passPhrase;
    private byte[] hashPhrase;
    private PublicKey public_k;
    private PrivateKey private_k;
    private byte[] private_k_byte;
    private byte[] address;
    
    
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
    	//askAction();
    	makeTransaction();
    }
    
    private void askAction() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Make transaction (m) or Copy Blockchain (b): ");
    	String action = br.readLine();
    	while(!action.equals("b") && !action.equals("m")){
    		System.out.print("Make transaction (m) or Copy Blockchain (b): ");
    		action = br.readLine();
    	}
    	if (action.equals("b")){
    		requestBlockChain();
    	}
    	else{
    		makeTransaction();
    	}
    }
    
	public void makeTransaction() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException{
		KeyFactory kf = KeyFactory.getInstance("DSA");
		PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(private_k_byte));
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
		dsa.initSign(privateKey);
		
        String jsonString = new JSONObject()
                .put("type", "newTransaction")
                .put("sourceWallet", "localhost:8080")
                .put("address", address.toString())
                .put("amount", "50")
                .put("signature", dsa.toString())
                .put("destinataire","address dest").toString();
        System.out.println("Making a transaction : "+ jsonString);
        client.sendMessage("/relay",jsonString);
    }

    public void requestBlockChain(){
        String jsonString = new JSONObject()
                .put("type", "GetBlockChain")
                .put("source", "localhost:8080").toString();
        blockChain=client.sendMessage("/relay",jsonString);
        System.out.println(blockChain);
    }
    
    public static byte[] sha256digest16(String list) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(list.getBytes("UTF-8"));

        //32 bytes
        byte[]  b = digest.digest();
        //return 16 bytes
        return Arrays.copyOf(b, 16);
    }
    
	private void generateKeys() throws NoSuchAlgorithmException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        public_k = pair.getPublic();
        private_k = pair.getPrivate();
    }
	
    private Boolean checkExistingUser() throws Exception {
		File key_public_file = new File(key_public_path);
		File key_private_file = new File(key_private_path);
		File address_file = new File(address_path);
		
		if(key_public_file.exists() && key_private_file.exists() && address_file.exists()
				&& !key_public_file.isDirectory() && !key_private_file.isDirectory() && !address_file.isDirectory()){
			address = getAddress();
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
    
    private byte[] getAddress() throws IOException{
		Path myFile = Paths.get("",address_path);
		return Files.readAllBytes(myFile);
    }
}
