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
    private MasterNode master= new MasterNode(8081,8080);

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String test(final @RequestBody(required = false)String msg) {
        System.out.println("Got a msg : "+msg);
        JSONObject jsonObj = new JSONObject(msg);

        String type=jsonObj.get("type").toString();
        if (type.equals("Wallet")) {
            master.addTransaction(msg);
            if (master.getNumberOfTransaction() == 4) {
                String messageForMiners=master.getTransactionsForMining();
                master.launchClient();
                master.sendToRelay(messageForMiners);
            }
        }
        else if (type.equals("Block")) {
            System.out.println("Got a BLOCK: "+msg);
        }
        return "OkFromMaster";
    }

}
