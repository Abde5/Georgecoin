package Wallet;

import Client.Client;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.json.JSONObject;

public class Wallet {

    private Client client;
    private String blockChain;
    private String address;
    

    public Wallet(int port){
        client = new Client("localhost",port);
        Thread thread = new Thread(client);
        //thread.setDaemon(true);
        thread.start();

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

