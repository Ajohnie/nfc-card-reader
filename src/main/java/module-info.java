module com.ripplesolutions.nfc.cardreader {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.smartcardio;
    requires com.jfoenix;
    requires MaterialFX;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires firebase.admin;
    requires com.google.api.client.auth;
    requires com.google.auth.oauth2;
    requires com.google.auth;
    requires google.api.client;
    opens com.ripplesolutions.nfc.cardreader to javafx.fxml;
    exports com.ripplesolutions.nfc.cardreader;
}