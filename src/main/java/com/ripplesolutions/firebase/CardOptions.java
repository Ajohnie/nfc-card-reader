package com.ripplesolutions.firebase;

import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CardOptions {
    public String id;
    public String branch;
    public String operation;
    public String message;
    private long modified;
    private String customerId;
    private String addedBy;
    private String modifiedBy;
    private String created;

    public CardOptions(String customerId, String branch, long modified) {
        this.customerId = customerId;
        this.branch = branch;
        this.id = this.getId();
        this.modified = modified;
        this.operation = "WRITE";
        this.message = "";
        this.addedBy = "card-reader";
        this.modifiedBy = "card-reader";
        this.created = new Date().toString();
    }

    public CardOptions(String customerId, String branch, long modified, String operation, String message) {
        this.customerId = customerId;
        this.branch = branch;
        this.id = this.getId();
        this.modified = modified;
        this.operation = operation;
        this.message = message;
        this.addedBy = "card-reader";
        this.modifiedBy = "card-reader";
        this.created = new Date().toString();
    }

    public CardOptions() {
        this.customerId = "";
        this.branch = "";
        this.modified = (new Date()).getTime();
        this.operation = "READ";
        this.message = "";
        this.addedBy = "card-reader";
        this.modifiedBy = "card-reader";
        this.created = new Date().toString();
    }

    public String getCustomerId() {
        return customerId;
    }

    /*we are truncating the customer id from firebase from 20
     * characters to 16 so that it can fit on the card reader in ascii format*/
    public void setCustomerId(String customerId) {
        if (customerId.length() > 16) {
            this.customerId = customerId.substring(0, 16);
        } else {
            this.customerId = customerId;
        }
    }

    String getId() {
        return "loyalty-program-" + this.branch;
    }

    public boolean isRead() {
        if (this.operation == null) {
            return true; // read by default
        }
        return this.operation.equalsIgnoreCase("READ");
    }

    public boolean wasModified(long now) {
        return wasModified(new Date(now));
    }

    public boolean wasModified(Date now) {
        Date then = new Date(this.modified);
        return then.compareTo(now) != 0;
    }

    public boolean wasModified() {
        return wasModified(new Date());
    }

    boolean isWrite() {
        if (this.operation == null) {
            return false; // don't overwrite useful data in case of wrong object set up
        }
        return this.operation.equalsIgnoreCase("WRITE");
    }

    public Map<String, Object> toFirebase() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("customerId", customerId);
        map.put("branch", branch);
        map.put("modified", modified);
        map.put("operation", operation);
        map.put("message", message);
        map.put("addedBy", addedBy);
        map.put("modifiedBy", modifiedBy);
        map.put("created", created);
        return map;
    }

    public CardOptions fromFirebase(DocumentSnapshot snapshot) {
        boolean configExists = snapshot != null && snapshot.exists();
        if (!configExists) {
            return this;
        }
        id = snapshot.getString("id");
        customerId = snapshot.getString("customerId");
        branch = snapshot.getString("branch");
        try {
            modified = snapshot.getLong("modified");
        } catch (Exception e) {
            modified = (new Date()).getTime();
        }
        operation = snapshot.getString("operation");
        message = snapshot.getString("message");
        addedBy = snapshot.getString("addedBy");
        modifiedBy = snapshot.getString("modifiedBy");
        created = snapshot.getString("created");
        return this;
    }

    public boolean hasId() {
        return this.customerId != null && this.customerId.length() > 0;
    }

    public void setModified() {
        this.setModified(new Date());
    }

    public void write() {
        this.operation = "WRITE";
        this.id = this.getId();
    }

    public void read() {
        this.operation = "READ";
        this.id = this.getId();
    }

    public long getModified() {
        return this.modified;
    }

    public void setModified(Date now) {
        this.modified = (now).getTime();
    }
}
