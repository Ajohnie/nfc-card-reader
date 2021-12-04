package com.ripplesolutions.utils;

import com.ripplesolutions.nfc.NFC;

public final class Utils {
    public static String getExceptionMessage(Exception exception) {
        exception.printStackTrace();
        // get last part of the exception message
        String rawMessage = exception.getMessage().toLowerCase();
        boolean isOfflineMessage = rawMessage.contains("unavailable");
        /* kindly find another way of doing this if your app is bigger, and you are likely
        to experience unexpected exceptions*/
        if (isOfflineMessage) {
            // System.out.println("rawMessage: " + rawMessage);
            return "Server is Unreachable, Check your internet connection and click refresh";
        }
        // exception messages usually go like ....blah blah...:message
        int startIndex = rawMessage.lastIndexOf(":");
        if (startIndex < 0) {
            return rawMessage;
        }
        String shortMessage = rawMessage.substring(startIndex);
        if (shortMessage.length() == 0) {
            return rawMessage;
        }
        if (shortMessage.contains(":")) {
            return shortMessage.replace(":", "");
        }
        return shortMessage;
    }

    public static void runInBackground(Runnable action) {
        if (action == null) {
            return;
        }
        Thread thread = new Thread(action);
        thread.start();
    }

    public static String getCustomerId() throws Exception {
        return NFC.read().trim();
    }

    public static boolean setCustomerId(String id) throws Exception {
        if (id == null) {
            throw new Exception("Please set customer card identifier and try again");
        }
        if (id.length() > 16) {
            throw new Exception("customer ID is too long");
        }
        return NFC.write(id);
    }
}
