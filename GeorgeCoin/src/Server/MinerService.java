package Server;

import Miner.*;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableWebMvc
@Controller
@RequestMapping("/miner")

public class MinerService {
    private Miner miner= new Miner("localhost",8082);

    /**
     * Handles all possibles requests sent from the RelayNode to the Miner
     * @param msg
     * @return response to send to the RelayNode
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String minerServerSide(final @RequestBody(required = false)String msg) {
        System.out.println("Got a msg : " + msg);

        JSONObject jsonObj = new JSONObject(msg);
        String type = jsonObj.get("type").toString();
        if (type.equals("ForMining")) {
            String transactionsForMiners = jsonObj.get("alltransactions").toString();
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

