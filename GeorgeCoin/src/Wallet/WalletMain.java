package Wallet;


public class WalletMain {

	
	  public static void main(final String[] args) {
		  Wallet wallet = new Wallet(8080);
		  wallet.makeTransaction(); // -> devrait etre JSON SOURCE,DEST,MONTANT
	  }
	  
}