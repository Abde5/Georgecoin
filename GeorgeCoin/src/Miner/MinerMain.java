package Miner;

public class MinerMain {

	/**
	 *  Creates a new Miner, connects to the Relay Node and launches the server
	 * @param args
	 */
    public static void main(final String[] args) {
        Miner miner = new Miner("localhost",8082);
        miner.connectToRelay();
        miner.launchServer();
    }
}
