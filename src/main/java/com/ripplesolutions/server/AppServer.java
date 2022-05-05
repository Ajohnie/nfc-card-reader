package com.ripplesolutions.server;

import javafx.beans.property.SimpleBooleanProperty;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.servlet.MultipartConfigElement;


public class AppServer {
    private int portNumber = 8090;
    private final SimpleBooleanProperty runningProperty;
    private Server server;

    public AppServer(SimpleBooleanProperty runningProperty, int portNumber) {
        this.runningProperty = runningProperty;
        this.portNumber = portNumber;
    }

    public void start() throws Exception {

        int maxThreads = 100;
        int minThreads = 10;
        int idleTimeout = 120;

        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

        server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(portNumber);
        server.setConnectors(new Connector[]{connector});

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(BlockingServlet.class, "/status");
        handler.addServletWithMapping(AsyncServlet.class, "/read-card");
        handler.addServletWithMapping(AsyncServlet.class, "/write-card")
                .getRegistration()
                .setMultipartConfig(new MultipartConfigElement("./tmp"));
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    /*return false for stopping server, true for starting the server*/
    public boolean toggle() throws Exception {
        if ((server != null) && server.isRunning()) {
            stop();
            return false;
        } else {
            start();
            return true;
        }
    }

    public boolean isRunning() {
        return server.isRunning();
    }
}
