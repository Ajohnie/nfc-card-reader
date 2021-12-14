package com.ripplesolutions.firebase;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.ripplesolutions.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

public class FireBaseConnection {
    // put firebase service account key under /main/resources folder
    // private static final String SERVICE_ACCOUNT_KEY_PATH = "serviceAccountKey.json";
    private static final String SERVICE_ACCOUNT_KEY_PATH = "serviceAccountKey-dev.json";
    private final String dataBaseName;
    private Firestore db;
    private String collectionName;
    private GoogleCredentials credentials;

    /*pass arguments to customize connection*/
    public FireBaseConnection(String databaseName) throws Exception {
        collectionName = "none";
        dataBaseName = databaseName;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream accountKey;
        accountKey = loader.getResourceAsStream(SERVICE_ACCOUNT_KEY_PATH);
        if (accountKey == null) {
            throw new Exception("Set Service Account Key");
        }
        try {
            credentials = GoogleCredentials.fromStream(accountKey);
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore(app);
        } catch (Exception e) {
            System.out.println(Utils.getExceptionMessage(e));
        }
    }

    public void refreshConnection() throws IOException {
        if (credentials != null) {
            AccessToken accessToken = credentials.getAccessToken();
            // token is null using current auth scheme
            if (accessToken != null) {
                Date expirationTime = accessToken.getExpirationTime();
                boolean tokenExpired = (new Date()).after(expirationTime);
                if (tokenExpired) {
                    throw new IOException("Sever connection expired, Restart Application");
                }
            }
        } else {
            throw new IOException("Server Config Error !");
        }
    }

    public Firestore getDb() throws IOException {
        if (db == null) {
            return FirestoreClient.getFirestore();
        }
        refreshConnection();
        return db;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public DocumentReference observerDoc(String collectionName, String docName) throws IOException {
        this.collectionName = collectionName;
        return getCollection(collectionName).document(docName);
    }

    public DocumentReference observerDoc(String docName) throws IOException {
        return observerDoc(this.collectionName, docName);
    }

    public Future<DocumentReference> addDoc(Object data) throws IOException {
        return getCollection(this.collectionName).add(data);
    }

    public Future<WriteResult> editDoc(String docId, Map<String, Object> data) throws Exception {
        if (docId == null) {
            throw new Exception("Set Document ID");
        }
        System.out.println(data);
        return getCollection(this.collectionName).document(docId).set(data);
    }

    private CollectionReference getCollection(String collection) throws IOException {
        return getDoc().collection(collection);
    }

    private DocumentReference getDoc() throws IOException {
        return getDb().collection("databases").document(dataBaseName);
    }
}
