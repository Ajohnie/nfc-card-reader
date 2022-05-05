package com.ripplesolutions.nfc;

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

    public CardOptions(String customerId, String operation) {
        this.customerId = customerId;
        this.branch = "";
        this.id = this.getId();
        this.modified = 0;
        this.operation = operation;
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

    public boolean isSimilarTo(String existingId) {
        if (customerId.length() > existingId.length()) {
            return customerId.trim().contains(existingId.trim());
        }
        return existingId.trim().contains(customerId.trim());
    }
}
