module com.ripplesolutions.nfc.cardreader {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires com.jfoenix;
    requires java.smartcardio;
    requires javax.servlet.api;
    requires org.slf4j;
    requires com.google.gson;
    requires org.eclipse.jetty.util;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.webapp;
    requires org.eclipse.jetty.servlet;
    opens com.ripplesolutions.nfc.cardreader to javafx.fxml, javafx.graphics, javafx.controls, javafx.base, com.jfoenix;
    opens com.ripplesolutions.nfc to java.smartcardio;
    exports com.ripplesolutions.nfc.cardreader;
    exports com.ripplesolutions.server to org.eclipse.jetty.server;
}