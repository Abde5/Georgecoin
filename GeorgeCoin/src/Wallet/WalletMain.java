package Wallet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class WalletMain {

	
	  public static void main(final String[] args) throws IOException, NoSuchAlgorithmException {
		  Wallet wallet = new Wallet(8080);
		  wallet.makeTransaction(); // -> devrait etre JSON SOURCE,DEST,MONTANT
		  //wallet.requestBlockChain();
	  }
	  
}
