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
		String source=jsonObj.get("type").toString();
		String message=jsonObj.get("type").toString();
		if (type.equals("Wallet")) {
			relay.addTransaction(message);
			relay.addWallet(source);

			if (relay.getNumberOfTransaction() == 4) {
				relay.launchClient();
				relay.sendToMiners();
			}
		}
		else if (type.equals("Miner")){
			relay.addMiner(source);
		}
		else{
			System.out.print("Erreur");
		}
		return "OkFromRelay";
	}
}
