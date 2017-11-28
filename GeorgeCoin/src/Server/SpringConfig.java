package Server;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * configures required packages for server
 */
@Configuration
@ComponentScan(basePackages = { "src.Server.services, server" })
public class SpringConfig {

}
