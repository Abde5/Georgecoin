package Client;


public class ClientMain {

	
	  public static void main(final String[] args) {
		  TestClient testclient = new TestClient(8080);
		  testclient.sendTest();
	  }
	  
}
