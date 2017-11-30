package Wallet;

import Client.Client;

import java.nio.charset.StandardCharsets;
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
import org.json.JSONObject;

public class Wallet {

    private Client client;
    private String blockChain;
    private String address;
    private String passPhrase;
    private byte[] hashPhrase;
    private String publicKey;
    private String privateKey;
    

    public Wallet(int port) throws Exception{
        //client = new Client("localhost",port);
    	new PrivateKeys();
    	//DSAPrivateKey@fffd1a27
    	
    	//[B@75412c2f
    	//[B@6d06d69c
    	
        //walletClient();
        
        //Thread thread = new Thread(client);
        //thread.setDaemon(true);
        //thread.start();
        
    }
    
    public void walletClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Enter your sentence: ");
    	String sentence = br.readLine();
    	System.out.print("Enter your password: ");
    	String password = br.readLine();
    	passPhrase = password+sentence;
    	System.out.println(passPhrase.getBytes().length);
    	MessageDigest digest = MessageDigest.getInstance("MD5");
    	hashPhrase = digest.digest(passPhrase.getBytes(StandardCharsets.UTF_8));
    	System.out.println(hashPhrase.length);
    	checkExistingUser();

    }

    private void checkExistingUser() throws NoSuchAlgorithmException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException {
		String key_file_path = "keys.txt";
		File key_file = new File(key_file_path);
		if(key_file.exists() && !key_file.isDirectory()) {
			System.out.println("true");
		    getKeysFromFile(key_file_path);
		}
		else{
			setKeysInFile(key_file_path);
		}
	}

	private void setKeysInFile(String key_file_path) throws NoSuchAlgorithmException, FileNotFoundException, UnsupportedEncodingException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException {
		 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
         keyGen.initialize(1024, new SecureRandom());
         KeyPair pair = keyGen.generateKeyPair();
         PublicKey public_k = pair.getPublic();
         PrivateKey private_k = pair.getPrivate();
         System.out.println("writing keys " + public_k.toString() + private_k.toString());
         PrintWriter fileWriter = new PrintWriter(key_file_path, "UTF-8");
         //fileWriter.println(public_k.getEncoded());
         fileWriter.println(public_k);
         fileWriter.println(encodePrivate(private_k));
         fileWriter.close();
	}


	private String encodePrivate(PrivateKey private_k) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException {
		//DO NOT WORK
		SecretKey originalKey = new SecretKeySpec(hashPhrase, 0, hashPhrase.length, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, originalKey);
		System.out.println("private k len "+private_k.toString().getBytes().length);
		System.out.println("private key bytes : "+private_k.toString().getBytes());
		
		byte[] ciphertext = cipher.doFinal(private_k.toString().getBytes());
		System.out.println("ciphertext : "+ciphertext.toString());
		System.out.println(ciphertext.toString().getBytes());
		Cipher cipher2 = Cipher.getInstance("AES");
		
		cipher2.init(Cipher.DECRYPT_MODE, originalKey);
		byte[] plaintext = cipher2.doFinal(ciphertext.toString().getBytes());
		System.out.println("plaintext : "+plaintext);
		
		return ciphertext.toString();
	}

	private ArrayList<String> getKeysFromFile(String key_file_path) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException {
		ArrayList<String> keys = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(key_file_path));
		String public_k = br.readLine();
		String private_k = decodePrivate(br.readLine());  
		keys.add(public_k);
		keys.add(private_k);
		return keys;
	}

	private String decodePrivate(String private_k) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		//DO NOT WORK
		//byte[] encodedKey     = Base64.decodeBase64(hashPhrase);
		SecretKey originalKey = new SecretKeySpec(hashPhrase, 0, hashPhrase.length, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		
		cipher.init(Cipher.DECRYPT_MODE, originalKey);
		//byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		System.out.println("cipher "+cipher);
		System.out.println("private k "+private_k);
		System.out.println("private k len "+private_k.getBytes().length);
		byte[] plaintext = cipher.doFinal(private_k.getBytes());
		System.out.println("plaintext : "+plaintext.toString());
		return plaintext.toString();
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
    
    public void DSAsignature(){
    	//NOT IMPLEMENTED YET
    	try {
            /* generate a key pair */
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
            keyGen.initialize(1024, new SecureRandom());
            KeyPair pair = keyGen.generateKeyPair();
            System.out.println(pair);

            /* Verify the signature */

            /* Initialize the Signature object for verification */
            //PublicKey pub = pair.getPublic();
            //dsa.initVerify(pub);

            //boolean verifies = dsa.verify(sig);

            /*Signature dsa = Signature.getInstance("SHA1withDSA", "SUN"); 
            
            PrivateKey priv = pair.getPrivate();
            dsa.initSign(priv);
           
            //a la transaction
            dsa.update(data);
            byte[] realSig = dsa.sign();*/
            
            /* save the signature in a file 
            FileOutputStream sigfos = new FileOutputStream("Signatures.txt");
            sigfos.write(realSig);
            sigfos.close();*/
            
            /* save the public key in a file 
            PublicKey pub = pair.getPublic();
            byte[] key = pub.getEncoded();
            FileOutputStream keyfos = new FileOutputStream("PublicKeys.txt");
            keyfos.write(key);
            keyfos.close();*/


        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }
}


/*
 * Demander mot de passe + phrase au client
 * concatener les deux et hasher avec sha256
 * la mettre dans un fichier
 * 
 * génerer public et private key
 * 
 * public key avec RIPM va donner adresse
 * 
 * wallet est créé
 */

