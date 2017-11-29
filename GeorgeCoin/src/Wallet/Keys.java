package Wallet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;

public class Keys {
	
	private String passPhrase;
	private byte[] hashPhrase;
	private PrivateKey private_k;
	private PublicKey public_k;
	
	public Keys() throws Exception{
		walletClient();
	}

    public void walletClient() throws Exception{
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.print("Enter your sentence: ");
    	String sentence = br.readLine();
    	System.out.print("Enter your password: ");
    	String password = br.readLine();
    	
    	passPhrase = password+sentence;
    	System.out.println(passPhrase.getBytes().length);
    	
    	MessageDigest digest = MessageDigest.getInstance("MD5");
    	byte[] passbyte = passPhrase.getBytes(StandardCharsets.UTF_8);
    	String bytepass = new String(passbyte, StandardCharsets.UTF_8);
    	System.out.println("passbyte : " + passPhrase.getBytes(StandardCharsets.UTF_8));
    	System.out.println("bytepass : " + bytepass);
    	hashPhrase = digest.digest(passPhrase.getBytes(StandardCharsets.UTF_8));
    	
    	System.out.println(hashPhrase.length);
    	
    	genKeys();
    	checkExistingUser();
    }


    private void checkExistingUser() throws Exception {
		String key_public_path = "key_public.txt";
		String key_private_path = "key_private.txt";
		File key_public_file = new File(key_public_path);
		File key_private_file = new File(key_private_path);
		if(key_public_file.exists() && key_private_file.exists() && !key_public_file.isDirectory() && !key_private_file.isDirectory()) {
			System.out.println("true");
		    getKeysFromFile(key_public_path, key_private_path);
		}
		else{
			setKeysInFile(key_public_path, key_private_path);
		}
	}
    
    private void genKeys() throws NoSuchAlgorithmException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        public_k = pair.getPublic();
        private_k = pair.getPrivate();
        
    }
    
	private void setKeysInFile(String path_pub, String path_priv) throws Exception {
        OutputStream f_pub = new FileOutputStream(path_pub);
        //OutputStream f_priv = new FileOutputStream(path_priv);
        f_pub.write(public_k.getEncoded());
        //f_priv.write(encodePrivate());
        f_pub.close();
        //f_priv.close();
        System.out.println("e : " + private_k.getEncoded());
        //Path path = Paths.get(path_priv);
        byte[] bytes = encodePrivate();
        System.out.println("write : " + bytes);
        
        String text = Hex.encodeHexString(bytes);
        System.out.print("write text: " + text);
        PrintWriter fileWriter = new PrintWriter(path_priv);
        fileWriter.println(text);
        fileWriter.close();
	}
    
	private byte[] encodePrivate() throws Exception {
		System.out.println(private_k.getEncoded().length);
		System.out.println(hashPhrase.length);
		byte[] encryptionKey = hashPhrase;//"MZygpewJsCpRrfOr".getBytes(StandardCharsets.UTF_8);
		byte[] plainText = private_k.getEncoded();//"Hello world!".getBytes(StandardCharsets.UTF_8);
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        encryptionKey);
		byte[] cipherText = advancedEncryptionStandard.encrypt(plainText);
		
		System.out.println("cipher dans encrypt : " + cipherText);
		System.out.println("cipher dans encrypt : " + cipherText.length);
		//byte[] decryptedCipherText = advancedEncryptionStandard.decrypt(cipherText);

		//System.out.println(new String(plainText));
		//System.out.println(new String(cipherText));
		//System.out.println(new String(decryptedCipherText));
		return cipherText;
	}
	
	private void getKeysFromFile(String key_public_path, String key_private_path) throws Exception {
		AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard(
		        hashPhrase);
		ArrayList<String> keys = new ArrayList<String>();
		BufferedReader br_pub = new BufferedReader(new FileReader(key_public_path));
		BufferedReader br_priv = new BufferedReader(new FileReader(key_private_path));
		Path path = Paths.get(key_public_path);
		byte[] public_k = Files.readAllBytes(path);
		
		path = Paths.get(key_private_path);
		byte[] ciphertex = Files.readAllBytes(path);
		String cipher = br_priv.readLine();
		//String cipher = new String(cipher, StandardCharsets.UTF_8);
		
		//System.out.println("cipher dans decrypt : " + cipher.getBytes(StandardCharsets.UTF_8));
		//System.out.println("cipher dans decrypt : " + ciphertex);
		System.out.println("text read: " + cipher);
		System.out.println("back to bytes: " + hexStringToByteArray(cipher));
		
		byte[] decrypted = advancedEncryptionStandard.decrypt(ciphertex); 
		
		System.out.println("decrypt : " + decrypted);
		System.out.println("private dans decrypt : " + private_k.getEncoded());
		if(Arrays.equals(ciphertex, private_k.getEncoded())){
			System.out.println("same");
		}
		else{
			System.out.println("not same");
		}
	}
	
	public byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	    
	    //private key encoded =  [B@61bbe9ba =plaintext
	    
	    //hex = 5f73ac8f27c4c85766931a2e
	    //on ecrit dans le fichier cyphertext =  [B@13b6d03
	}
}
