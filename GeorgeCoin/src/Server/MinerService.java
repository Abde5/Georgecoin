package Server;

import Miner.*;
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
@RequestMapping("/miner")

public class MinerService {
    private Miner miner= new Miner("localhost",8082);

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String test(final @RequestBody(required = false)String msg) {
        System.out.println("Got a msg : " + msg);

        JSONObject jsonObj = new JSONObject(msg);
        String type = jsonObj.get("type").toString();
        if (type.equals("ForMining")) {
            String transactionsForMiners = jsonObj.get("alltransactions").toString();
            //if (transactionsForMiners.length()>0){
            String block = miner.computeBlock(transactionsForMiners);
            miner.launchClient(miner.getRelayHostname(), miner.getRelayPort());
            System.out.println("Sending computed BLOCK");
            miner.sendBlock(block);
        }
        else if (type.equals("StopMining"))
            System.out.println("Got a Stop computing message : " + msg);
            miner.stopComputingBlock();
        return "OkFromMiner";
    }

}

