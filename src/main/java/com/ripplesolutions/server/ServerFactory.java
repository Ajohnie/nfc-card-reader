package com.ripplesolutions.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Simple factory for creating Jetty basic instances.
 *
 * @author Donato Rimenti
 */
public class ServerFactory {

    /**
     * Exposed context of the app.
     */
    public final static String APP_PATH = "/myApp";

    /**
     * The server port.
     */
    public final static int SERVER_PORT = 13133;
    public final static int SERVER_TIMEOUT = 30000;
    public final static String SERVER_HOST = "127.0.0.1";

    /**
     * Private constructor to avoid instantiation.
     */
    private ServerFactory() {
    }

    /**
     * Returns a simple server listening on port 80 with a timeout of 30 seconds
     * for connections and no handlers.
     *
     * @return a server
     */
    public static Server createBaseServer() {
        Server server = new Server();

        // Adds a connector for port 80 with a timeout of 30 seconds.
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(SERVER_PORT);
        connector.setHost(SERVER_HOST);
        connector.setIdleTimeout(SERVER_TIMEOUT);
        server.addConnector(connector);

        return server;
    }

    /**
     * Creates a server which delegates the request handling to a web
     * application.
     *
     * @return a server
     */
    public static Server createWebAppServer() {
        // Adds a handler to a server and returns it.
        Server server = createBaseServer();
        String webAppFolderPath = ServerFactory.class.getClassLoader().getResource("jetty-embedded-demo-app.war").getPath();
        Handler webAppHandler = new WebAppContext(webAppFolderPath, APP_PATH);
        server.setHandler(webAppHandler);

        return server;
    }

    /**
     * Creates a server which delegates the request handling to both a logging
     * handler and to a web application, in this order.
     *
     * @return a server
     */
    public static Server createMultiHandlerServer() {
        Server server = createBaseServer();

        // Creates the handlers and adds them to the server.
        HandlerCollection handlers = new HandlerCollection();

        String webAppFolderPath = ServerFactory.class.getClassLoader().getResource("jetty-embedded-demo-app.war").getPath();
        Handler customRequestHandler = new WebAppContext(webAppFolderPath, APP_PATH);
        handlers.addHandler(customRequestHandler);

        Handler loggingRequestHandler = new LoggingRequestHandler();
        handlers.addHandler(loggingRequestHandler);

        server.setHandler(handlers);

        return server;
    }

}
