package com.ripplesolutions.nfc;

import javax.smartcardio.*;
import java.util.Arrays;
import java.util.List;

public final class NFC {
    public static String readerName = "";
    public static boolean readerPluggedIn = false;
    public static boolean cardInRange = false;

    public static String read() throws Exception {
        /*if (!authenticated()) {
            throw new Exception("Card Authentication Failed, try another card");
        }*/
        // FF B0 00 04 10
        byte[] page4_16bits = new byte[]{(byte) 0xFF, (byte) 0xB0, (byte) 0x00, (byte) 0x04, (byte) 0x10};
        byte[] response = transmit(page4_16bits);
        String str = (new String(response));
        String toShow = str;
        if (str.length() < 16) {
            int remainingSpace = (16 - str.length());
            for (int strIndex = 0; strIndex < remainingSpace; strIndex++) {
                toShow = toShow.concat(" ");
            }
        }
        return toShow.substring(0, 16); // take only first 16 characters
    }

    public static boolean authenticated() {
        byte[] authenticationByte;
        byte sector = (byte) 0x61; // (byte) 0x60 -->sector A
        authenticationByte = new byte[]{(byte) 0xFF, (byte) 0x86, (byte) 0x00,
                (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x04,
                sector, (byte) 0x00};
        try {
            byte[] response = transmit(authenticationByte);
            if (response.length == 0) {
                return false;
            }
            return response[0] == 99;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean write(String data) throws Exception {
        if (data == null) {
            throw new Exception("Please set data and try again");
        }
        if (data.length() > 16) {
            throw new Exception("string data too long");
        }
        String toSave = data;
        if (data.length() < 16) {
            int remainingSpace = (16 - data.length());
            for (int strIndex = 0; strIndex < remainingSpace; strIndex++) {
                toSave = toSave.concat(" ");
            }
        }
        // split string into 4 parts
        String[] segments = new String[4];
        int segmentCounter = 0;
        for (int partIndex = 0; partIndex < 16; partIndex++) {
            boolean isFourthSegment = (partIndex + 1) % 4 == 0; // add 1 since index started at zero
            if (isFourthSegment) {
                String segment = toSave.substring(partIndex - 3, partIndex + 1);
                segments[segmentCounter] = segment;
                segmentCounter++;
            }
        }
        // authenticating here messes up response
        /*if (!authenticated()) {
            throw new Exception("Card Authentication Failed, try another card");
        }*/
        // save each part on its own page starting from page 4
        // FF D6 00 04 04 pg1
        // FF D6 00 05 04 pg2
        // FF D6 00 06 04 pg3
        // FF D6 00 07 04 pg4
        for (int pageNo = 0; pageNo < 4; pageNo++) {
            byte pageCode = (byte) (pageNo + 4);
            byte[] page4_16bits = new byte[]{(byte) 0xFF, (byte) 0xD6, (byte) 0x00, pageCode, (byte) 0x04};
            byte[] dataBytes = segments[pageNo].getBytes();
            byte[] both = Arrays.copyOf(page4_16bits, page4_16bits.length + dataBytes.length);
            System.arraycopy(dataBytes, 0, both, page4_16bits.length, dataBytes.length);
            byte[] response = transmit(both);
            if (response.length == 0) {
                throw new Exception("Writing segment: " + pageNo + " Failed, no response");
            }
            if (response[0] == 63) {
                System.out.println("Card Response: " + Arrays.toString(response));
                throw new Exception("Writing segment: " + pageNo + " Failed, code 0x" + response[0]);
            }
        }
        return true;
    }

    public static byte[] transmit(byte[] commands) throws Exception {
        if (commands.length == 0) {
            throw new Exception("Please Set Commands for the NFC reader and try again");
        }
        // show the list of available terminals
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals;
        try {
            terminals = factory.terminals().list();
        } catch (CardException e) {
            e.printStackTrace();
            readerPluggedIn = false;
            readerName = "";
            cardInRange = false;
            throw new Exception("Please Connect NFC reader and try again");
        }
        boolean hasTerminals = terminals.size() > 0;
        boolean hasManyTerminals = terminals.size() > 1;
        if (hasManyTerminals) {
            readerName = "";
            readerPluggedIn = true;
            cardInRange = false;
            throw new Exception("Please connect Only one NFC reader to PC and try again");
        }
        if (hasTerminals) {
            readerPluggedIn = true;
            // get the first terminal
            CardTerminal terminal = terminals.get(0);
            readerName = terminal.getName();
            // establish a connection with the card
            if (terminal.isCardPresent()) {
                cardInRange = true;
                Card card = terminal.connect("*"); // T=1
                System.out.println(card.getATR().toString());
                CardChannel channel = card.getBasicChannel();
                ResponseAPDU r = channel.transmit(new CommandAPDU(commands));
                // disconnect
                card.disconnect(false);
                // System.out.println("GD: " + Arrays.toString(r.getBytes()) + "  GD2: " + Arrays.toString(r.getData()));
                return r.getBytes();
            } else {
                cardInRange = false;
                throw new Exception("Please place loyalty card on the NFC reader and try again");
            }
        } else {
            readerPluggedIn = false;
            readerName = "";
            cardInRange = false;
            throw new Exception("Please connect NFC reader to PC and try again");
        }
    }
}
