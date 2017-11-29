package Wallet;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class WalletMain {

	
	  public static void main(final String[] args) throws Exception {
		  Wallet wallet = new Wallet(8080);
		  //wallet.makeTransaction(); // -> devrait etre JSON SOURCE,DEST,MONTANT
		  //wallet.requestBlockChain();
	  }
	  
}
