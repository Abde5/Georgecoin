package RelayNode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RelayNodeMain {

	  
	  public static void main(final String[] args) {
		  RelayNode server = new RelayNode("localhost",8080,"localhost",8081);
		  server.getBlockChainFromMaster();
		  server.launchServer();
		  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		  System.out.print("Give the destination address for the transaction : ");
		  try {
			  String dest_address = br.readLine();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
		  System.out.print("Sending Stop");
		  String stopMinersTest=new JSONObject().put("type","StopMining").toString();
		  server.sendToALLMiners(stopMinersTest);


		  
	  }
}
