package Wallet;

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

public class WalletMaster {
	
	protected String passPhrase;
	protected byte[] hashPhrase;
	protected PublicKey public_k;
	protected PrivateKey private_k;
	protected byte[] private_k_byte;
	protected byte[] address;
	
	public WalletMaster(){
        passPhrase = "passPhraseWalletMaster";
        try{
        	hashPhrase = sha256digest16(passPhrase);
        } catch (Exception e){
        	e.printStackTrace();
        }
    }
	
	protected static byte[] sha256digest16(String password_phrase) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(password_phrase.getBytes("UTF-8"));

        //32 bytes
        byte[]  b = digest.digest();
        //return 16 bytes
        return Arrays.copyOf(b, 16);
    }
    
	protected void generateKeys() throws NoSuchAlgorithmException{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        public_k = pair.getPublic();
        private_k = pair.getPrivate();
        private_k_byte = private_k.getEncoded();
    }
	
	protected byte[] generateAddress(byte[] pubKeyBytes) {
		return Ripemd160.getHash(pubKeyBytes);
	}
	
	public byte[] getAddress(){
		return address;
	}
	
	public Signature DSASign() throws Exception{
		KeyFactory kf = KeyFactory.getInstance("DSA");
		PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(private_k_byte));
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
		dsa.initSign(privateKey);
		return dsa;
	}
}
