module com.ripplesolutions.nfc.cardreader {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.base;
    requires google.cloud.firestore;
    requires com.google.auth.oauth2;
    requires com.google.auth;
    requires google.cloud.core;
    requires firebase.admin;
    requires com.jfoenix;
    requires java.smartcardio;
    opens com.ripplesolutions.nfc.cardreader to javafx.fxml, javafx.graphics, javafx.controls, javafx.base, com.jfoenix, google.cloud.firestore, google.cloud.core, com.google.auth.oauth2, com.google.auth;
    opens com.ripplesolutions.firebase to google.cloud.firestore, com.google.auth.oauth2, firebase.admin, com.google.auth, google.cloud.core;
    opens com.ripplesolutions.nfc to java.smartcardio;
    exports com.ripplesolutions.nfc.cardreader;
}