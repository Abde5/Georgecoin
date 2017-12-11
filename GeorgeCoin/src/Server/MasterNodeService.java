package Server;

import MasterNode.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@Controller
@RequestMapping("/master")

public class MasterNodeService {
    private MasterNode master= new MasterNode("localhost",8081,8080);
    private long startTime;

    /**
     * Handles all possible requests sent from the RelayNode to the MasterNode
     * @param msg
     * @return response to send to the RelayNode
     * @throws JSONException
     * @throws Exception
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String masterServerSide(final @RequestBody(required = false)String msg) throws JSONException, Exception {
        //System.out.println("Got a msg : "+msg);
        JSONObject jsonObj = new JSONObject(msg);

        String type=jsonObj.get("type").toString();
        if (type.equals("newTransaction")) {
            System.out.println("Got a new transaction :"+ msg);
            String sourceRelay=jsonObj.get("sourceRelay").toString();
            if (master.checkEnoughMoney(jsonObj.getJSONObject("transaction"))){
                master.addTransaction(jsonObj.getJSONObject("transaction").toString());
                master.addRelay(sourceRelay);
                if (master.getNumberOfTransaction() == 4) {
                    Thread SendToMiners = new Thread(() -> {
                        System.out.println("Got 4 transactions!");
                        String messageForMiners = master.getTransactionsForMining();
                        System.out.println("Sending BLOCK to all relays");
                        master.sendToALLRelays(messageForMiners);
                    });
                    startTime = System.currentTimeMillis();
                    SendToMiners.start();
                }
            }
        }
        else if (type.equals("Block")) {	//accept block - reward miner - send to all relays
            System.out.println("Got a COMPUTED BLOCK : "+msg);
            long endTime = System.currentTimeMillis();
    		long totalTime = endTime - startTime;
    		master.checkDifficulty(totalTime);
            String newBlockChain=master.acceptBlock(msg);
            String stopMinersTest=new JSONObject().put("type","StopMining").toString();
            System.out.println("Send STOP Relay");
            master.sendToALLRelays(stopMinersTest);
            System.out.println("Rewarding the miner");
            System.out.println(jsonObj);
            master.rewardTransaction(jsonObj.get("sourceMiner").toString());
            System.out.println("Sending BLOCKCHAIN to all relays");
            master.sendToALLRelays(newBlockChain);
        }
        else if (type.equals("GiveMeTheBlockChain")) {	//Fetch blockchain
            System.out.println("Got a BLOCKCHAIN demand from a relay: "+msg);
            return master.blockToJSON().toString();
        }
        return "OkFromMaster";
    }

}
