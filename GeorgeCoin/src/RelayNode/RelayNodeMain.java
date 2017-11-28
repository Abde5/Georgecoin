package RelayNode;

public class RelayNodeMain {

	  
	  public static void main(final String[] args) {
		  RelayNode server = new RelayNode("localhost",8080,"localhost",8081);
		  //TODO get previous hash from the Maser node
		  server.launchServer();
		  
	  }
}
