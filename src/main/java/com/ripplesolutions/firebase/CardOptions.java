package com.ripplesolutions.firebase;

import com.google.cloud.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class CardOptions {
    public String customerId;
    public String branch;
    public String functionName;
    public String operation;
    public String message;

    public CardOptions(String customerId, String branch, String functionName) {
        this.customerId = customerId;
        this.branch = branch;
        this.functionName = functionName;
        this.operation = "WRITE";
        this.message = "";
    }

    public CardOptions(String customerId,
                       String branch,
                       String functionName,
                       String operation,
                       String message) {
        this.customerId = customerId;
        this.branch = branch;
        this.functionName = functionName;
        this.operation = operation;
        this.message = message;
    }

    public CardOptions() {
        this.customerId = "";
        this.branch = "";
        this.functionName = "";
        this.operation = "READ";
        this.message = "";
    }

    public boolean isRead() {
        if (this.operation == null) {
            return true; // read by default
        }
        return this.operation.equalsIgnoreCase("READ");
    }

    boolean isWrite() {
        if (this.operation == null) {
            return false; // don't overwrite useful data in case of wrong object set up
        }
        return this.operation.equalsIgnoreCase("WRITE");
    }

    public Map<String, Object> toFirebase() {
        Map<String, Object> map = new HashMap<>();
        map.put("customerId", customerId);
        map.put("branch", branch);
        map.put("functionName", functionName);
        map.put("operation", operation);
        map.put("message", message);
        return map;
    }

    public CardOptions fromFirebase(DocumentSnapshot snapshot) {
        customerId = snapshot.getString("customerId");
        branch = snapshot.getString("branch");
        functionName = snapshot.getString("functionName");
        operation = snapshot.getString("operation");
        message = snapshot.getString("message");
        return this;
    }
}
