package RelayNode;

public class RelayNodeMain {

	  
	  public static void main(final String[] args) {
		  RelayNode server = new RelayNode(8080,8081);
		  server.launchServer();
		  
	  }
}
