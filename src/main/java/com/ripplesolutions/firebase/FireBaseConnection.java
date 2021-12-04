package com.ripplesolutions.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.ripplesolutions.utils.Utils;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Future;

public class FireBaseConnection {
    private static final String SERVICE_ACCOUNT_KEY_PATH = "serviceAccountKey.json";
    private final String dataBaseName;
    private Firestore db;
    private String collectionName;

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
            GoogleCredentials credentials = GoogleCredentials.fromStream(accountKey);
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(credentials).build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore(app);
        } catch (Exception e) {
            System.out.println(Utils.getExceptionMessage(e));
        }
    }

    public Firestore getDb() {
        if (db == null) {
            return FirestoreClient.getFirestore();
        }
        return db;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public DocumentReference observerDoc(String collectionName, String docName) {
        this.collectionName = collectionName;
        return getCollection(collectionName).document(docName);
    }

    public DocumentReference observerDoc(String docName) {
        return observerDoc(this.collectionName, docName);
    }

    public Future<DocumentReference> addDoc(Object data) {
        return getCollection(this.collectionName).add(data);
    }

    public Future<WriteResult> editDoc(String docId, Map<String, Object> data) throws Exception {
        if (docId == null) {
            throw new Exception("Set Document ID");
        }
        System.out.println(data);
        return getCollection(this.collectionName).document(docId).set(data);
    }

    private CollectionReference getCollection(String collection) {
        return getDoc().collection(collection);
    }

    private DocumentReference getDoc() {
        return getDb().collection("databases").document(dataBaseName);
    }
}
