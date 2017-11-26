package Client;

public class TestClient {
	
	private Client client;
	public TestClient(int port){
		client = new Client(port);
	}
	
	public void sendTest(){
		final String json = "test";
		final String response = client.post("/test", json);
		System.out.println(response);			
	}
}
