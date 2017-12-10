package Server;

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
	private RelayNode relay= new RelayNode("localhost",8080,"localhost",8081);
		
	@RequestMapping(value = "", method = RequestMethod.POST)
	public @ResponseBody String test(final @RequestBody(required = false)String msg) {
		JSONObject jsonObj = new JSONObject(msg);
		System.out.println("MSG : "+msg);

		String type=jsonObj.get("type").toString();
		if (type.equals("newTransaction")) {
			System.out.println("Got a Transaction : "+msg);
			relay.launchClientMaster();
			jsonObj.put("sourceRelay",relay.WhoAMI());
			relay.sendToMaster(jsonObj.toString());

		}
		else if (type.equals("newMinerConnected")){
			System.out.println("Got a Miner : "+msg);
			String source=jsonObj.get("source").toString();
			relay.addMiner(source);
			if (relay.getMinerNumber()<=10) {
				return relay.WhoAMI();
			}
			else{
				return "NotPaired";
			}
		}
		else if (type.equals("readyForMining")){
			if (relay.getMinerNumber()>0) {
				System.out.println("Got a BLOCK for the Miners, forwarding "+msg);
				String alltransactions= new JSONObject().put("type","ForMining").put("alltransactions",jsonObj.get("alltransactions")).toString();
				relay.sendToALLMiners(alltransactions);
			}
			else{
				System.out.print("NO MINERS CONNECTED");
			}
		}
		else if (type.equals("Block")){
			System.out.println("Got a computed BLOCK : "+msg);
			String source=jsonObj.get("sourceMiner").toString();
			System.out.println(source);
			relay.launchClientMaster();
			relay.sendToMaster(msg);
		}
		else if (type.equals("BlockChain")){
			System.out.print("Got an updated BLOCKCHAIN : "+ msg);
			relay.saveBlockChain(msg);

		}
		else if (type.equals("GetBlockChain")){
			relay.getBlockChainFromMaster();
			return relay.getBlockChain();
		}
		else if (type.equals("StopMining")){
			System.out.println("Send STOP Miners");
			relay.sendToALLMiners(msg);
		}
		return "OkFromRelay";
	}
}
