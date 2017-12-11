package Server;

import org.springframework.context.annotation.ComponentScan;


/**
 * configures required packages for server
 */

@ComponentScan(basePackageClasses = {MasterNodeService.class, MinerService.class, RelayNodeService.class})
public class SpringConfig {

}
