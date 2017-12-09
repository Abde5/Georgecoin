package RelayNode;

public class RelayNodeMain {

	  
	  public static void main(final String[] args) {
		  RelayNode server = new RelayNode("localhost",8080,"localhost",8081);
		  server.getBlockChainFromMaster();
		  server.launchServer();
	  }
}
