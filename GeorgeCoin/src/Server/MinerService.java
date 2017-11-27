package Server;

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

    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody String test(final @RequestBody(required = false)String msg) {
        System.out.println("Got a msg : "+msg);
        return "OkFromMiner";
    }

}

