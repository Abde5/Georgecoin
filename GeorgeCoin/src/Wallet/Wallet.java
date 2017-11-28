package Wallet;

import Client.Client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.json.JSONObject;

public class Wallet {

    private Client client;
    private String blockChain;
    private String address;
    private String passPhrase;
    

    public Wallet(int port) throws IOException, NoSuchAlgorithmException{
        client = new Client("localhost",port);
        
        walletClient();
        
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();
        
    }
    
    public void walletClient() throws IOException, NoSuchAlgorithmException{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Enter your sentence: ");
    	String sentence = br.readLine();
    	System.out.print("Enter your password: ");
    	String password = br.readLine();
    	passPhrase = password+sentence;
    	checkExistingUser();

    }

    private void checkExistingUser() throws NoSuchAlgorithmException, FileNotFoundException, UnsupportedEncodingException {
		String key_file_path = "keys.txt";
		File key_file = new File(key_file_path);
		if(key_file.exists() && !key_file.isDirectory()) { 
		    getKeysFromFile(key_file_path);
		}
		else{
			setKeysInFile(key_file_path);
		}
		
	}

	private void setKeysInFile(String key_file_path) throws NoSuchAlgorithmException, FileNotFoundException, UnsupportedEncodingException {
		 KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
         keyGen.initialize(1024, new SecureRandom());
         KeyPair pair = keyGen.generateKeyPair();
         PublicKey public_k = pair.getPublic();
         PrivateKey private_k = pair.getPrivate();
         PrintWriter fileWriter = new PrintWriter(key_file_path, "UTF-8");
         fileWriter.println(public_k.toString());
         fileWriter.println(encodePrivate(private_k));
         fileWriter.close();
		
	}

	private char[] encodePrivate(PrivateKey private_k) {
		//Encore private key with aes-128
		return null;
	}

	private void getKeysFromFile(String key_file_path) {
		// TODO Auto-generated method stub
		
	}

	public void makeTransaction(){
        String jsonString = new JSONObject()
                .put("type", "newTransaction")
                .put("source", "localhost:8080")
                .put("message","ca fonctionne").toString();
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

