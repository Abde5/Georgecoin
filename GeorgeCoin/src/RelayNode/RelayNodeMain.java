package RelayNode;

public class RelayNodeMain {
	
	/**
	 * Creates a new RelayNode, gets a first copy of the block chain from the MasterNode and launches the server
	 * @param args
	 */
	public static void main(final String[] args) {
		RelayNode server = new RelayNode("localhost",8080,"localhost",8081);
		server.getBlockChainFromMaster();
		server.launchServer();
	}
}
