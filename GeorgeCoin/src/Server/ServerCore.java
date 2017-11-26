package Server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Starts the server
 */
public class ServerCore {
    /**
     * Launches the server calls 
     * @param args recieves arguments when launching
     */
	ServerCore(int port){
    	
        final AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(SpringConfig.class);
        final ServletHolder servletHolder = new ServletHolder(new DispatcherServlet(appContext));
        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(servletHolder, "/*");
        final Server server = new Server(port);
        server.setHandler(context);
        try {
        	server.start();
        	server.join();
        } catch (Exception e) {
        	server.destroy();
        	System.out.println("Error in starting server.");
        }
	}
}
