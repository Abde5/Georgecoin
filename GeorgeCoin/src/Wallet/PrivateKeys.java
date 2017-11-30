package Wallet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class PrivateKeys {
	private String passPhrase;
	private byte[] hashPhrase;
	private PrivateKey private_k;
	private PublicKey public_k;
	private KeyPair pair;
	
	public PrivateKeys() throws Exception{
		walletClient();
	}

    public void walletClient() throws Exception{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Enter your sentence: ");
    	String sentence = br.readLine();
    	System.out.print("Enter your password: ");
    	String password = br.readLine();
    	
    	passPhrase = password+sentence;
    	MessageDigest digest = MessageDigest.getInstance("MD5");
    	hashPhrase = digest.digest(passPhrase.getBytes(StandardCharsets.UTF_8));
    	
    	genKeys();
    	checkExistingUser();
    }

	private void genKeys() throws NoSuchAlgorithmException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom());
        pair = keyGen.generateKeyPair();
        public_k = pair.getPublic();
        private_k = pair.getPrivate();
    }
	
    private void checkExistingUser() throws Exception{
		String key_public_path = "key_public.txt";
		String key_private_path = "key_private.txt";
		setKeysInFile(key_public_path, key_private_path);
	}

	private void setKeysInFile(String key_public_path, String key_private_path) throws Exception {
		byte[] encryptionKey = hashPhrase;
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        encryptionKey);
		byte[] plainText = private_k.getEncoded();
		String cle = new String(plainText, StandardCharsets.UTF_8);
		System.out.println("key string : " +cle);
		
		String cleEncrypted = new String(encodePrivate(), StandardCharsets.UTF_8);
		System.out.println("encrypted key string : " +cleEncrypted);
		System.out.println("encrypted key byte : "+encodePrivate());
		
		FileOutputStream fos = new FileOutputStream(key_private_path);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(cleEncrypted);
		oos.close();
		
		FileInputStream fis = new FileInputStream(key_private_path);
		ObjectInputStream ois = new ObjectInputStream(fis);
		String str = (String) ois.readObject();
		
		System.out.println("encrypted key string from read : "+ str);	
		
		byte[] ciphertex= new BigInteger(str,16).toByteArray();
		//BigInteger cipher = new BigInteger(str.)
		//byte[] ciphertex = str.getBytes(StandardCharsets.UTF_8);
		byte[] decrypted = advancedEncryptionStandard.decrypt(ciphertex);
		ois.close();

		String decrypt = new String(decrypted, StandardCharsets.UTF_8);
		System.out.println("decrypted key string from read : "+ decrypt);	
	}
    
	private byte[] encodePrivate() throws Exception {
		byte[] encryptionKey = hashPhrase;
		byte[] plainText = private_k.getEncoded();
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        encryptionKey);
		byte[] cipherText = advancedEncryptionStandard.encrypt(plainText);
		return cipherText;
	}
}
