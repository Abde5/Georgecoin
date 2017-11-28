package Server;

import Client.Client;
import RelayNode.*;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Provides services from Server to allow a user to log in
 */
@EnableWebMvc
@Controller
@RequestMapping("/relay")

public class RelayNodeService {
	private RelayNode relay= new RelayNode(8080,8081);
		
	@RequestMapping(value = "", method = RequestMethod.POST)
	public @ResponseBody String test(final @RequestBody(required = false)String msg) {
		System.out.println("Got a msg : "+msg);
		JSONObject jsonObj = new JSONObject(msg);

		String type=jsonObj.get("type").toString();
		if (type.equals("newTransaction")) {
			relay.launchClientMaster();
			relay.sendToMaster(msg);

		}
		else if (type.equals("newMinerConnected")){

			String source=jsonObj.get("source").toString();
			relay.addMiner(source);
		}
		else if (type.equals("readyForMining")){
			if (relay.getMinerNumber()>0) {
				relay.sendToALLMiners(msg);
				//relay.launchClientMiners();
				//relay.sendToMiners(msg);
			}
			else{
				System.out.print("NO MINERS CONNECTED");
			}
		}
		else if (type.equals("Block")){
			String source=jsonObj.get("source").toString();
			//------------------------------
			//STOP ALL OTHERS MINERS!!!!
			//------------------------------
			//String block=jsonObj.get("block").toString();
			relay.launchClientMaster();
			relay.sendToMaster(msg);
		}
		else if (type.equals("BlockChain")){
			System.out.print("Got an updated BLOCKCHAIN"+ msg);
			relay.saveBlockChain(msg);

		}
		else if (type.equals("GetBlockChain")){
			return relay.getBlockChain();
		}
		return "OkFromRelay";
	}
}
