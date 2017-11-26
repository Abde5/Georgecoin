package Server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * configures required packages for server
 */
@Configuration
@ComponentScan(basePackages = { "be.ac.ulb.infof307.g05.server.services, server" })
public class SpringConfig {

}
