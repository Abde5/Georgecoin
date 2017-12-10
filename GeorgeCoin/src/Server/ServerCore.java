package Server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Starts the server
 */
public class ServerCore implements Runnable {
    private String hostname;
    private int port;
    /**
     * Launches the server calls
     */
	public ServerCore(String hostname,int port){
	    this.hostname=hostname;
	    this.port=port;

	}
	public String getHostname(){
	    return hostname;
    }
    public int getPort(){
	    return port;
    }
    @Override
    public void run() {
        final AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(SpringConfig.class);
        final ServletHolder servletHolder = new ServletHolder(new DispatcherServlet(appContext));
        final ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(servletHolder, "/*");
        final Server server = new Server(this.port);
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
