package Server;

import MasterNode.*;
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
@RequestMapping("/master")

public class MasterNodeService {
    private MasterNode master= new MasterNode("localhost",8081,8080);

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String test(final @RequestBody(required = false)String msg) {
        //System.out.println("Got a msg : "+msg);
        JSONObject jsonObj = new JSONObject(msg);

        String type=jsonObj.get("type").toString();
        if (type.equals("newTransaction")) {
            System.out.println("Got a new transaction : "+ msg);
            String sourceRelay=jsonObj.get("sourceRelay").toString();
            String transaction=jsonObj.get("transaction").toString();
            master.addTransaction(transaction);
            master.addRelay(sourceRelay);
            if (master.getNumberOfTransaction() == 4) {
                System.out.println("Got 4 transactions!");
                String messageForMiners=master.getTransactionsForMining();
                System.out.println("Sending BLOCK to all relays");
                master.sendToALLRelays(messageForMiners);

            }
        }
        else if (type.equals("Block")) {
            System.out.println("Got a COMPUTED BLOCK: "+msg);
            String newBlockChain=master.acceptBlock(msg);
            //TODO reward miner
            System.out.println("Sending BLOCKCHAIN to all relays");
            master.sendToALLRelays(newBlockChain);
        }
        return "OkFromMaster";
    }

}
