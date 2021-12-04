module com.ripplesolutions.nfc.cardreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires firebase.admin;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.api.client.auth;
    requires com.google.auth.oauth2;
    requires com.google.auth;
    requires com.jfoenix;
    requires MaterialFX;
    requires google.api.client;
    opens com.ripplesolutions.nfc.cardreader to javafx.fxml;
    exports com.ripplesolutions.nfc.cardreader;
}