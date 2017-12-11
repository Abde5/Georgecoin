package Wallet;

public class WalletMain {
	
	/**
	 * Creates a new Wallet and launches the client
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		Wallet wallet = new Wallet(8080);
		wallet.walletClient();
	}
}
